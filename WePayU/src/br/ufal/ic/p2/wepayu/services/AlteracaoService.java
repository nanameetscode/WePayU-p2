package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;
import br.ufal.ic.p2.wepayu.exceptions.empregado.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.exceptions.pagamento.MetodoPagamentoInvalidoException;
import br.ufal.ic.p2.wepayu.exceptions.sindicato.IdentificacaoSindicatoNulaException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.CampoNumerico;
import br.ufal.ic.p2.wepayu.models.Cadastro;
import br.ufal.ic.p2.wepayu.models.empregado.Empregado;
import br.ufal.ic.p2.wepayu.models.pagamento.MetodoBanco;
import br.ufal.ic.p2.wepayu.models.pagamento.MetodoCorreios;
import br.ufal.ic.p2.wepayu.models.pagamento.MetodoEmMaos;
import br.ufal.ic.p2.wepayu.models.pagamento.MetodoPagamento;
import br.ufal.ic.p2.wepayu.models.remuneracao.Remuneracao;
import br.ufal.ic.p2.wepayu.models.remuneracao.RemuneracaoComissionada;
import br.ufal.ic.p2.wepayu.models.sindicato.NaoSindicalizado;
import br.ufal.ic.p2.wepayu.models.remuneracao.TipoEmpregado;
import br.ufal.ic.p2.wepayu.utils.LeitorDeEntrada;
import br.ufal.ic.p2.wepayu.utils.Validacao;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Alteração dos dados de um empregado ({@code alteraEmpregado}).
 *
 * <p>O script usa de 3 a 6 parâmetros conforme o atributo; cada quantidade tem o seu
 * método. Com 3 parâmetros, cada atributo tem a sua alteração numa tabela.
 * Trocar o tipo troca a remuneração e volta à agenda padrão do novo tipo; o empregado
 * continua o mesmo objeto, com cartões, vendas e situação no sindicato.
 */
public class AlteracaoService {

    @FunctionalInterface
    private interface Alteracao {
        void aplicar(Empregado empregado, String valor) throws WePayUException;
    }

    private static final Map<String, Supplier<MetodoPagamento>> METODOS_SEM_DADOS = Map.of(
            "emMaos", MetodoEmMaos::new,
            "correios", MetodoCorreios::new);

    private final Cadastro cadastro;
    private final Map<String, Alteracao> alteracoes = Map.of(
            "nome", Empregado::setNome,
            "endereco", Empregado::setEndereco,
            "tipo", AlteracaoService::alterarTipoMantendoSalario,
            "salario", AlteracaoService::alterarSalario,
            "comissao", AlteracaoService::alterarComissao,
            "metodoPagamento", AlteracaoService::alterarMetodoSemDados,
            "sindicalizado", AlteracaoService::desfiliar,
            "agendaPagamento", this::alterarAgenda);

    public AlteracaoService(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    /**
     * Altera um atributo que precisa só de um valor.
     */
    public void alteraEmpregado(String emp, String atributo, String valor) throws WePayUException {
        Empregado empregado = cadastro.buscar(emp);
        Alteracao alteracao = atributo == null ? null : alteracoes.get(atributo);
        if (alteracao == null) {
            throw new AtributoNaoExisteException();
        }
        alteracao.aplicar(empregado, valor);
    }

    /**
     * Troca o tipo do empregado. O último parâmetro é a comissão, se o novo tipo for
     * comissionado, ou o novo salário, nos demais casos.
     */
    public void alteraEmpregado(String emp, String atributo, String valor, String salarioOuComissao)
            throws WePayUException {

        Empregado empregado = cadastro.buscar(emp);
        exigirAtributo(atributo, "tipo");
        TipoEmpregado novoTipo = TipoEmpregado.doRotulo(valor);
        BigDecimal valorDaTroca = LeitorDeEntrada.lerValorNaoNegativo(salarioOuComissao, novoTipo.getCampoDaTroca());
        empregado.trocarTipo(novoTipo.trocar(empregado.getRemuneracao(), valorDaTroca));
    }

    /**
     * Filia o empregado ao sindicato ({@code valor=true}) ou o desfilia ({@code valor=false}).
     */
    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical)
            throws WePayUException {

        Empregado empregado = cadastro.buscar(emp);
        exigirAtributo(atributo, "sindicalizado");
        if (!LeitorDeEntrada.lerBooleano(valor)) {
            empregado.alterarSindicalizacao(new NaoSindicalizado());
            return;
        }
        Validacao.exigirPreenchido(idSindicato, IdentificacaoSindicatoNulaException::new);
        cadastro.verificarIdentificacaoSindicalLivre(idSindicato, empregado);
        BigDecimal taxa = LeitorDeEntrada.lerValorNaoNegativo(taxaSindical, CampoNumerico.TAXA_SINDICAL);
        empregado.alterarSindicalizacao(empregado.getSindicalizacao().filiar(idSindicato, taxa));
    }

    /**
     * Passa o empregado a receber por depósito bancário.
     */
    public void alteraEmpregado(String emp, String atributo, String valor, String banco, String agencia,
            String contaCorrente) throws WePayUException {

        Empregado empregado = cadastro.buscar(emp);
        exigirAtributo(atributo, "metodoPagamento");
        if (!"banco".equals(valor)) {
            throw new MetodoPagamentoInvalidoException();
        }
        empregado.alterarMetodoPagamento(new MetodoBanco(banco, agencia, contaCorrente));
    }

    private static void exigirAtributo(String atributo, String esperado) throws AtributoNaoExisteException {
        if (!esperado.equals(atributo)) {
            throw new AtributoNaoExisteException();
        }
    }

    /**
     * Troca o tipo sem informar valor: o salário atual é mantido. O comissionado não
     * pode ser escolhido assim, pois precisaria da comissão.
     */
    private static void alterarTipoMantendoSalario(Empregado empregado, String tipo) throws WePayUException {
        Remuneracao atual = empregado.getRemuneracao();
        empregado.trocarTipo(TipoEmpregado.doRotulo(tipo).criar(atual.getSalario(), Optional.empty()));
    }

    private static void alterarSalario(Empregado empregado, String salario) throws WePayUException {
        BigDecimal novoSalario = LeitorDeEntrada.lerValorNaoNegativo(salario, CampoNumerico.SALARIO);
        empregado.alterarRemuneracao(empregado.getRemuneracao().comSalario(novoSalario));
    }

    private static void alterarComissao(Empregado empregado, String comissao) throws WePayUException {
        RemuneracaoComissionada comissionado = empregado.getRemuneracao().comoComissionado();
        BigDecimal novaComissao = LeitorDeEntrada.lerValorNaoNegativo(comissao, CampoNumerico.COMISSAO);
        empregado.alterarRemuneracao(comissionado.comComissao(novaComissao));
    }

    /**
     * Escolhe uma forma de pagamento sem dados extras; o banco precisa do comando de 6 parâmetros.
     */
    private static void alterarMetodoSemDados(Empregado empregado, String metodo) throws WePayUException {
        Supplier<MetodoPagamento> criarMetodo = metodo == null ? null : METODOS_SEM_DADOS.get(metodo);
        if (criarMetodo == null) {
            throw new MetodoPagamentoInvalidoException();
        }
        empregado.alterarMetodoPagamento(criarMetodo.get());
    }

    /**
     * Escolhe uma das agendas de pagamento que a empresa oferece (US9 e US10).
     */
    private void alterarAgenda(Empregado empregado, String agenda) throws WePayUException {
        empregado.alterarAgenda(cadastro.getAgendas().buscar(agenda));
    }

    /**
     * Com um só valor, só é possível desfiliar: filiar exige identificação e taxa.
     */
    private static void desfiliar(Empregado empregado, String valor) throws WePayUException {
        if (LeitorDeEntrada.lerBooleano(valor)) {
            throw new IdentificacaoSindicatoNulaException();
        }
        empregado.alterarSindicalizacao(new NaoSindicalizado());
    }
}
