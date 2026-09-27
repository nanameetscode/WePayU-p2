package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.AgenciaNulaException;
import br.ufal.ic.p2.wepayu.Exception.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.BancoNuloException;
import br.ufal.ic.p2.wepayu.Exception.ComissaoNaoNumericaException;
import br.ufal.ic.p2.wepayu.Exception.ComissaoNegativaException;
import br.ufal.ic.p2.wepayu.Exception.ComissaoNulaException;
import br.ufal.ic.p2.wepayu.Exception.ContaCorrenteNulaException;
import br.ufal.ic.p2.wepayu.Exception.DataFinalInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.DataInicialInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.DataInicialPosteriorDataFinalException;
import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhHoristaException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoRecebeEmBancoException;
import br.ufal.ic.p2.wepayu.Exception.MetodoPagamentoInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.EnderecoNuloException;
import br.ufal.ic.p2.wepayu.Exception.HorasDevemSerPositivasException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoEmpregadoNulaException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoMembroNulaException;
import br.ufal.ic.p2.wepayu.Exception.MembroNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.NaoHaEmpregadoComEsseNomeException;
import br.ufal.ic.p2.wepayu.Exception.NomeNuloException;
import br.ufal.ic.p2.wepayu.Exception.SalarioNaoNumericoException;
import br.ufal.ic.p2.wepayu.Exception.SalarioNegativoException;
import br.ufal.ic.p2.wepayu.Exception.SalarioNuloException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhComissionadoException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhSindicalizadoException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoSindicatoDuplicadaException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoSindicatoNulaException;
import br.ufal.ic.p2.wepayu.Exception.TaxaSindicalNaoNumericaException;
import br.ufal.ic.p2.wepayu.Exception.TaxaSindicalNegativaException;
import br.ufal.ic.p2.wepayu.Exception.TaxaSindicalNulaException;
import br.ufal.ic.p2.wepayu.Exception.TipoInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.TipoNaoAplicavelException;
import br.ufal.ic.p2.wepayu.Exception.ValorDeveSerPositivoException;
import br.ufal.ic.p2.wepayu.Exception.ValorTrueFalseException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.persistence.RepositorioXML;
import br.ufal.ic.p2.wepayu.services.EmpregadoService;

import java.io.IOException;

/**
 * Ponto de entrada unico do sistema, invocado por reflexao pelo EasyAccept.
 */
public class Facade {

    private final RepositorioXML repositorio = new RepositorioXML();
    private final EmpregadoService empregados = new EmpregadoService();

    public Facade() {
        repositorio.carregar();
        empregados.getEmpregados().putAll(repositorio.getEmpregados());
        empregados.restaurarUltimoId(maiorIdentificacao());
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario)
            throws NomeNuloException, EnderecoNuloException, TipoInvalidoException,
            SalarioNuloException, SalarioNaoNumericoException, SalarioNegativoException,
            ComissaoNulaException, ComissaoNaoNumericaException, ComissaoNegativaException,
            TipoNaoAplicavelException {

        return empregados.criarEmpregado(nome, endereco, tipo, salario);
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
            throws NomeNuloException, EnderecoNuloException, TipoInvalidoException,
            SalarioNuloException, SalarioNaoNumericoException, SalarioNegativoException,
            ComissaoNulaException, ComissaoNaoNumericaException, ComissaoNegativaException,
            TipoNaoAplicavelException {

        return empregados.criarEmpregado(nome, endereco, tipo, salario, comissao);
    }

    public String getAtributoEmpregado(String emp, String atributo)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, EmpregadoNaoEhSindicalizadoException,
            EmpregadoNaoEhComissionadoException, EmpregadoNaoRecebeEmBancoException {

        return empregados.getAtributoEmpregado(emp, atributo);
    }

    public String getEmpregadoPorNome(String nome, int indice) throws NaoHaEmpregadoComEsseNomeException {
        return empregados.getEmpregadoPorNome(nome, indice);
    }

    public void removerEmpregado(String emp)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException {
        empregados.removerEmpregado(emp);
    }

    public void lancaCartao(String emp, String data, String horas)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhHoristaException, DataInvalidaException, HorasDevemSerPositivasException {
        empregados.lancaCartao(emp, data, horas);
    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhHoristaException, DataInicialInvalidaException, DataFinalInvalidaException,
            DataInicialPosteriorDataFinalException {
        return empregados.getHorasNormaisTrabalhadas(emp, dataInicial, dataFinal);
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhHoristaException, DataInicialInvalidaException, DataFinalInvalidaException,
            DataInicialPosteriorDataFinalException {
        return empregados.getHorasExtrasTrabalhadas(emp, dataInicial, dataFinal);
    }

    public void lancaVenda(String emp, String data, String valor)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhComissionadoException, DataInvalidaException, ValorDeveSerPositivoException {
        empregados.lancaVenda(emp, data, valor);
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhComissionadoException, DataInicialInvalidaException, DataFinalInvalidaException,
            DataInicialPosteriorDataFinalException {
        return empregados.getVendasRealizadas(emp, dataInicial, dataFinal);
    }

    public void lancaTaxaServico(String membro, String data, String valor)
            throws IdentificacaoMembroNulaException, MembroNaoExisteException,
            DataInvalidaException, ValorDeveSerPositivoException {
        empregados.lancaTaxaServico(membro, data, valor);
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhSindicalizadoException, DataInicialInvalidaException, DataFinalInvalidaException,
            DataInicialPosteriorDataFinalException {
        return empregados.getTaxasServico(emp, dataInicial, dataFinal);
    }

    public void alteraEmpregado(String emp, String atributo, String valor)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, NomeNuloException, EnderecoNuloException,
            TipoInvalidoException, SalarioNuloException, SalarioNaoNumericoException,
            SalarioNegativoException, ComissaoNulaException, ComissaoNaoNumericaException,
            ComissaoNegativaException, EmpregadoNaoEhComissionadoException,
            MetodoPagamentoInvalidoException, ValorTrueFalseException,
            IdentificacaoSindicatoNulaException {
        empregados.alteraEmpregado(emp, atributo, valor);
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String salOuComissao)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, TipoInvalidoException, ComissaoNulaException,
            ComissaoNaoNumericaException, ComissaoNegativaException, SalarioNuloException,
            SalarioNaoNumericoException, SalarioNegativoException {
        empregados.alteraEmpregado(emp, atributo, valor, salOuComissao);
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, ValorTrueFalseException, IdentificacaoSindicatoNulaException,
            IdentificacaoSindicatoDuplicadaException, TaxaSindicalNulaException,
            TaxaSindicalNaoNumericaException, TaxaSindicalNegativaException {
        empregados.alteraEmpregado(emp, atributo, valor, idSindicato, taxaSindical);
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String banco, String agencia, String contaCorrente)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, MetodoPagamentoInvalidoException,
            BancoNuloException, AgenciaNulaException, ContaCorrenteNulaException {
        empregados.alteraEmpregado(emp, atributo, valor, banco, agencia, contaCorrente);
    }

    public void zerarSistema() throws IOException {
        empregados.zerarSistema();
        repositorio.limpar();
    }

    public void encerrarSistema() throws IOException {
        repositorio.getEmpregados().clear();
        repositorio.getEmpregados().putAll(empregados.getEmpregados());
        repositorio.gravar();
    }

    private int maiorIdentificacao() {
        int maior = 0;
        for (String identificacao : repositorio.getEmpregados().keySet()) {
            try {
                maior = Math.max(maior, Integer.parseInt(identificacao));
            } catch (NumberFormatException ignorada) {
            }
        }
        return maior;
    }
}
