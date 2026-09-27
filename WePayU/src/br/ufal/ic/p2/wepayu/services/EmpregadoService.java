package br.ufal.ic.p2.wepayu.services;

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
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhComissionadoException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhHoristaException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhSindicalizadoException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoRecebeEmBancoException;
import br.ufal.ic.p2.wepayu.Exception.EnderecoNuloException;
import br.ufal.ic.p2.wepayu.Exception.HorasDevemSerPositivasException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoEmpregadoNulaException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoMembroNulaException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoSindicatoDuplicadaException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoSindicatoNulaException;
import br.ufal.ic.p2.wepayu.Exception.MembroNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.MetodoPagamentoInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.NaoHaEmpregadoComEsseNomeException;
import br.ufal.ic.p2.wepayu.Exception.NomeNuloException;
import br.ufal.ic.p2.wepayu.Exception.SalarioNaoNumericoException;
import br.ufal.ic.p2.wepayu.Exception.SalarioNegativoException;
import br.ufal.ic.p2.wepayu.Exception.SalarioNuloException;
import br.ufal.ic.p2.wepayu.Exception.TaxaSindicalNaoNumericaException;
import br.ufal.ic.p2.wepayu.Exception.TaxaSindicalNegativaException;
import br.ufal.ic.p2.wepayu.Exception.TaxaSindicalNulaException;
import br.ufal.ic.p2.wepayu.Exception.TipoInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.TipoNaoAplicavelException;
import br.ufal.ic.p2.wepayu.Exception.ValorDeveSerPositivoException;
import br.ufal.ic.p2.wepayu.Exception.ValorTrueFalseException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoAssalariado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.EmpregadoHorista;
import br.ufal.ic.p2.wepayu.models.MetodoBanco;
import br.ufal.ic.p2.wepayu.models.MetodoCorreios;
import br.ufal.ic.p2.wepayu.models.MetodoEmMaos;
import br.ufal.ic.p2.wepayu.models.TipoEmpregado;
import br.ufal.ic.p2.wepayu.utils.Formatador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Regras de negócio do cadastro de empregados: valida os dados recebidos do script
 * de teste, delega a criação à {@link FabricaEmpregado} e guarda o resultado.
 *
 * <p>Este serviço não conhece subclasses concretas de {@link Empregado}: para
 * responder às consultas basta o mapa de atributos que o próprio empregado monta.
 */
public class EmpregadoService {

    /** Situações possíveis de um parâmetro numérico, na ordem em que são verificadas. */
    private enum StatusNumero {
        VALIDO, NULO, NAO_NUMERICO, NEGATIVO
    }

    /**
     * Resultado da análise de um parâmetro numérico recebido como texto.
     *
     * @param status se o texto é nulo, não numérico, negativo ou um valor válido
     * @param valor  o número convertido, significativo apenas quando o status é válido
     */
    private record Numero(StatusNumero status, double valor) { }

    private final Map<String, Empregado> empregados = new LinkedHashMap<>();
    private final FabricaEmpregado fabrica = new FabricaEmpregado();
    private int ultimoId;

    /**
     * Cadastra um empregado que não recebe comissão.
     *
     * <p>Variante utilizada pelos scripts que informam apenas nome, endereço, tipo e
     * salário. A comissão chega à validação como ausente, e é por isso que o tipo que
     * exige comissão é rejeitado com {@code Tipo nao aplicavel.}.
     *
     * @return a identificação do empregado, escolhida automaticamente pelo sistema
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario)
            throws NomeNuloException, EnderecoNuloException, TipoInvalidoException,
            SalarioNuloException, SalarioNaoNumericoException, SalarioNegativoException,
            ComissaoNulaException, ComissaoNaoNumericaException, ComissaoNegativaException,
            TipoNaoAplicavelException {

        return criarEmpregado(nome, endereco, tipo, salario, null);
    }

    /**
     * Cadastra um empregado, com comissão ou sem ela.
     *
     * <p>Os parâmetros chegam como texto porque é assim que o EasyAccept invoca a
     * fachada. A verificação segue a ordem em que os scripts esperam as mensagens:
     * nome, endereço, tipo, salário, comissão e, por último, a compatibilidade entre
     * o tipo e a comissão informada.
     *
     * @param comissao texto da comissão, ou {@code null} se o script não informou o parâmetro
     * @return a identificação do empregado, escolhida automaticamente pelo sistema
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
            throws NomeNuloException, EnderecoNuloException, TipoInvalidoException,
            SalarioNuloException, SalarioNaoNumericoException, SalarioNegativoException,
            ComissaoNulaException, ComissaoNaoNumericaException, ComissaoNegativaException,
            TipoNaoAplicavelException {

        if (nome == null || nome.isBlank()) {
            throw new NomeNuloException();
        }
        if (endereco == null || endereco.isBlank()) {
            throw new EnderecoNuloException();
        }

        TipoEmpregado tipoDoEmpregado = TipoEmpregado.fromRotulo(tipo);

        double salarioNumerico = validarSalario(salario);
        Double comissaoNumerica = comissao == null ? null : validarComissao(comissao);

        Empregado empregado = fabrica.criar(tipoDoEmpregado, nome, endereco, salarioNumerico, comissaoNumerica);

        String identificacao = String.valueOf(++ultimoId);
        empregados.put(identificacao, empregado);
        return identificacao;
    }

    /**
     * Recupera um atributo do empregado, já formatado como o script espera.
     *
     * @param emp      identificação do empregado
     * @param atributo nome do atributo consultado
     * @return o valor do atributo
     */
    public String getAtributoEmpregado(String emp, String atributo)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, EmpregadoNaoEhSindicalizadoException,
            EmpregadoNaoEhComissionadoException, EmpregadoNaoRecebeEmBancoException {

        Empregado empregado = buscar(emp);
        return empregado.getValorAtributo(atributo);
    }

    /**
     * Localiza um empregado pelo nome, contando as ocorrências a partir de um índice.
     *
     * A busca é por substring, e não por igualdade: pode haver mais de um empregado
     * cujo nome contenha o texto informado, e o índice é o que permite escolher qual
     * deles.
     *
     * @param nome   texto procurado no nome do empregado
     * @param indice posição da ocorrência, começando em 1
     * @return a identificação do empregado encontrado
     */
    public String getEmpregadoPorNome(String nome, int indice) throws NaoHaEmpregadoComEsseNomeException {
        List<String> identificacoes = new ArrayList<>();
        for (Map.Entry<String, Empregado> entry : empregados.entrySet()) {
            if (entry.getValue().getNome().contains(nome)) {
                identificacoes.add(entry.getKey());
            }
        }
        if (indice < 1 || indice > identificacoes.size()) {
            throw new NaoHaEmpregadoComEsseNomeException();
        }
        return identificacoes.get(indice - 1);
    }

    /**
     * Remove um empregado do sistema pela sua identificação.
     *
     * @param emp identificação do empregado a remover
     * @throws IdentificacaoEmpregadoNulaException se a identificação for nula ou vazia
     * @throws EmpregadoNaoExisteException         se o empregado não for encontrado
     */
    public void removerEmpregado(String emp)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException {
        buscar(emp);
        empregados.remove(emp);
    }

    /**
     * Registra um cartão de ponto para um empregado horista.
     *
     * @param emp   identificação do empregado
     * @param data  data do ponto no formato d/M/yyyy
     * @param horas total de horas trabalhadas no dia
     */
    public void lancaCartao(String emp, String data, String horas)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhHoristaException, DataInvalidaException, HorasDevemSerPositivasException {

        Empregado empregado = buscar(emp);

        LocalDate dataConvertida = Formatador.converterData(data);
        if (dataConvertida == null) {
            throw new DataInvalidaException();
        }

        Double horasConvertidas = Formatador.converterNumero(horas);
        if (horasConvertidas == null || horasConvertidas <= 0) {
            throw new HorasDevemSerPositivasException();
        }

        empregado.lancarCartao(data, horasConvertidas);
    }

    /**
     * Calcula as horas normais trabalhadas por um empregado horista em um intervalo.
     *
     * @param emp         identificação do empregado
     * @param dataInicial início do intervalo
     * @param dataFinal   fim do intervalo (exclusivo)
     * @return quantidade de horas normais formatada
     */
    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhHoristaException, DataInicialInvalidaException, DataFinalInvalidaException,
            DataInicialPosteriorDataFinalException {

        Empregado empregado = buscar(emp);
        LocalDate[] periodo = validarPeriodo(dataInicial, dataFinal);
        double horas = empregado.getHorasNormais(periodo[0], periodo[1]);
        return Formatador.formatarHoras(horas);
    }

    /**
     * Calcula as horas extras trabalhadas por um empregado horista em um intervalo.
     *
     * @param emp         identificação do empregado
     * @param dataInicial início do intervalo
     * @param dataFinal   fim do intervalo (exclusivo)
     * @return quantidade de horas extras formatada
     */
    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhHoristaException, DataInicialInvalidaException, DataFinalInvalidaException,
            DataInicialPosteriorDataFinalException {

        Empregado empregado = buscar(emp);
        LocalDate[] periodo = validarPeriodo(dataInicial, dataFinal);
        double horas = empregado.getHorasExtras(periodo[0], periodo[1]);
        return Formatador.formatarHoras(horas);
    }

    private LocalDate[] validarPeriodo(String dataInicial, String dataFinal)
            throws DataInicialInvalidaException, DataFinalInvalidaException,
            DataInicialPosteriorDataFinalException {

        LocalDate inicio = Formatador.converterData(dataInicial);
        if (inicio == null) {
            throw new DataInicialInvalidaException();
        }

        LocalDate fim = Formatador.converterData(dataFinal);
        if (fim == null) {
            throw new DataFinalInvalidaException();
        }

        if (inicio.isAfter(fim)) {
            throw new DataInicialPosteriorDataFinalException();
        }

        return new LocalDate[]{inicio, fim};
    }

    /**
     * Registra o resultado de uma venda para um empregado comissionado.
     *
     * @param emp   identificação do empregado
     * @param data  data da venda no formato d/M/yyyy
     * @param valor valor monetário da venda
     */
    public void lancaVenda(String emp, String data, String valor)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhComissionadoException, DataInvalidaException, ValorDeveSerPositivoException {

        Empregado empregado = buscar(emp);

        LocalDate dataConvertida = Formatador.converterData(data);
        if (dataConvertida == null) {
            throw new DataInvalidaException();
        }

        Double valorConvertido = Formatador.converterNumero(valor);
        if (valorConvertido == null || valorConvertido <= 0) {
            throw new ValorDeveSerPositivoException();
        }

        empregado.lancarVenda(data, valorConvertido);
    }

    /**
     * Calcula o total de vendas realizadas por um empregado comissionado em um intervalo.
     *
     * @param emp         identificação do empregado
     * @param dataInicial início do intervalo
     * @param dataFinal   fim do intervalo (exclusivo)
     * @return valor total das vendas formatado com duas casas decimais
     */
    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhComissionadoException, DataInicialInvalidaException, DataFinalInvalidaException,
            DataInicialPosteriorDataFinalException {

        Empregado empregado = buscar(emp);
        LocalDate[] periodo = validarPeriodo(dataInicial, dataFinal);
        double total = empregado.getVendasRealizadas(periodo[0], periodo[1]);
        return Formatador.formatarMoeda(total);
    }

    /**
     * Altera atributos simples do empregado (3 parâmetros).
     */
    public void alteraEmpregado(String emp, String atributo, String valor)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, NomeNuloException, EnderecoNuloException,
            TipoInvalidoException, SalarioNuloException, SalarioNaoNumericoException,
            SalarioNegativoException, ComissaoNulaException, ComissaoNaoNumericaException,
            ComissaoNegativaException, EmpregadoNaoEhComissionadoException,
            MetodoPagamentoInvalidoException, ValorTrueFalseException,
            IdentificacaoSindicatoNulaException {

        Empregado empregado = buscar(emp);
        if ("nome".equals(atributo)) {
            if (valor == null || valor.isBlank()) {
                throw new NomeNuloException();
            }
            empregado.setNome(valor);
        } else if ("endereco".equals(atributo)) {
            if (valor == null || valor.isBlank()) {
                throw new EnderecoNuloException();
            }
            empregado.setEndereco(valor);
        } else if ("tipo".equals(atributo)) {
            if ("assalariado".equals(valor)) {
                Empregado novo = new EmpregadoAssalariado(empregado.getNome(), empregado.getEndereco(), empregado.getSalario());
                copiarDados(empregado, novo);
                empregados.put(emp, novo);
            } else {
                throw new TipoInvalidoException();
            }
        } else if ("salario".equals(atributo)) {
            double sal = validarSalario(valor);
            empregado.setSalario(sal);
        } else if ("comissao".equals(atributo)) {
            empregado.verificarComissionado();
            double com = validarComissao(valor);
            empregado.setComissao(com);
        } else if ("metodoPagamento".equals(atributo)) {
            if ("emMaos".equals(valor)) {
                empregado.setMetodoPagamento(new MetodoEmMaos());
            } else if ("correios".equals(valor)) {
                empregado.setMetodoPagamento(new MetodoCorreios());
            } else {
                throw new MetodoPagamentoInvalidoException();
            }
        } else if ("sindicalizado".equals(atributo)) {
            if ("false".equals(valor)) {
                empregado.setDadosSindicato(false, null, 0.0);
            } else if ("true".equals(valor)) {
                throw new IdentificacaoSindicatoNulaException();
            } else {
                throw new ValorTrueFalseException();
            }
        } else {
            throw new AtributoNaoExisteException();
        }
    }

    /**
     * Altera tipo do empregado com parâmetro adicional (salário ou comissão - 4 parâmetros).
     */
    public void alteraEmpregado(String emp, String atributo, String valor, String salOuComissao)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, TipoInvalidoException, ComissaoNulaException,
            ComissaoNaoNumericaException, ComissaoNegativaException, SalarioNuloException,
            SalarioNaoNumericoException, SalarioNegativoException {

        Empregado empregado = buscar(emp);
        if ("tipo".equals(atributo)) {
            if ("comissionado".equals(valor)) {
                double comissao = validarComissao(salOuComissao);
                Empregado novo = new EmpregadoComissionado(empregado.getNome(), empregado.getEndereco(), empregado.getSalario(), comissao);
                copiarDados(empregado, novo);
                empregados.put(emp, novo);
            } else if ("horista".equals(valor)) {
                double salario = validarSalario(salOuComissao);
                Empregado novo = new EmpregadoHorista(empregado.getNome(), empregado.getEndereco(), salario);
                copiarDados(empregado, novo);
                empregados.put(emp, novo);
            } else if ("assalariado".equals(valor)) {
                double salario = validarSalario(salOuComissao);
                Empregado novo = new EmpregadoAssalariado(empregado.getNome(), empregado.getEndereco(), salario);
                copiarDados(empregado, novo);
                empregados.put(emp, novo);
            } else {
                throw new TipoInvalidoException();
            }
        } else {
            throw new AtributoNaoExisteException();
        }
    }

    /**
     * Altera filiação sindical para sindicalizado com id de sindicato e taxa sindical (5 parâmetros).
     */
    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, ValorTrueFalseException, IdentificacaoSindicatoNulaException,
            IdentificacaoSindicatoDuplicadaException, TaxaSindicalNulaException,
            TaxaSindicalNaoNumericaException, TaxaSindicalNegativaException {

        Empregado empregado = buscar(emp);
        if (!"sindicalizado".equals(atributo)) {
            throw new AtributoNaoExisteException();
        }
        if (!"true".equals(valor) && !"false".equals(valor)) {
            throw new ValorTrueFalseException();
        }
        if ("false".equals(valor)) {
            empregado.setDadosSindicato(false, null, 0.0);
            return;
        }
        if (idSindicato == null || idSindicato.isBlank()) {
            throw new IdentificacaoSindicatoNulaException();
        }
        for (Map.Entry<String, Empregado> entry : empregados.entrySet()) {
            if (!entry.getKey().equals(emp) && entry.getValue().isSindicalizado() && idSindicato.equals(entry.getValue().getIdSindicato())) {
                throw new IdentificacaoSindicatoDuplicadaException();
            }
        }
        if (taxaSindical == null || taxaSindical.isBlank()) {
            throw new TaxaSindicalNulaException();
        }
        Double taxa = Formatador.converterNumero(taxaSindical);
        if (taxa == null) {
            throw new TaxaSindicalNaoNumericaException();
        }
        if (taxa < 0) {
            throw new TaxaSindicalNegativaException();
        }
        empregado.setDadosSindicato(true, idSindicato, taxa);
    }

    /**
     * Altera método de pagamento para depósito bancário (6 parâmetros).
     */
    public void alteraEmpregado(String emp, String atributo, String valor, String banco, String agencia, String contaCorrente)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, MetodoPagamentoInvalidoException,
            BancoNuloException, AgenciaNulaException, ContaCorrenteNulaException {

        Empregado empregado = buscar(emp);
        if (!"metodoPagamento".equals(atributo)) {
            throw new AtributoNaoExisteException();
        }
        if (!"banco".equals(valor)) {
            throw new MetodoPagamentoInvalidoException();
        }
        if (banco == null || banco.isBlank()) {
            throw new BancoNuloException();
        }
        if (agencia == null || agencia.isBlank()) {
            throw new AgenciaNulaException();
        }
        if (contaCorrente == null || contaCorrente.isBlank()) {
            throw new ContaCorrenteNulaException();
        }
        empregado.setMetodoPagamento(new MetodoBanco(banco, agencia, contaCorrente));
    }

    private void copiarDados(Empregado origem, Empregado destino) {
        destino.setDadosSindicato(origem.isSindicalizado(), origem.getIdSindicato(), origem.getTaxaSindical());
        destino.setTaxasServico(origem.getTaxasServico());
        destino.setMetodoPagamento(origem.getMetodoPagamento());
    }

    /**
     * Registra taxa de serviço cobrada a um membro do sindicato.
     */
    public void lancaTaxaServico(String membro, String data, String valor)
            throws IdentificacaoMembroNulaException, MembroNaoExisteException,
            DataInvalidaException, ValorDeveSerPositivoException {

        if (membro == null || membro.isBlank()) {
            throw new IdentificacaoMembroNulaException();
        }

        Empregado empregado = null;
        for (Empregado e : empregados.values()) {
            if (e.isSindicalizado() && membro.equals(e.getIdSindicato())) {
                empregado = e;
                break;
            }
        }
        if (empregado == null) {
            throw new MembroNaoExisteException();
        }

        LocalDate dataConvertida = Formatador.converterData(data);
        if (dataConvertida == null) {
            throw new DataInvalidaException();
        }

        Double valorConvertido = Formatador.converterNumero(valor);
        if (valorConvertido == null || valorConvertido <= 0) {
            throw new ValorDeveSerPositivoException();
        }

        empregado.lancarTaxaServico(data, valorConvertido);
    }

    /**
     * Calcula o total de taxas de serviço cobradas a um empregado sindicalizado em um intervalo.
     */
    public String getTaxasServico(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhSindicalizadoException, DataInicialInvalidaException, DataFinalInvalidaException,
            DataInicialPosteriorDataFinalException {

        Empregado empregado = buscar(emp);
        if (!empregado.isSindicalizado()) {
            throw new EmpregadoNaoEhSindicalizadoException();
        }
        LocalDate[] periodo = validarPeriodo(dataInicial, dataFinal);
        double total = empregado.getTaxasServico(periodo[0], periodo[1]);
        return Formatador.formatarMoeda(total);
    }

    /**
     * Remove todos os empregados cadastrados, reiniciando a numeração.
     */
    public void zerarSistema() {
        empregados.clear();
        ultimoId = 0;
    }

    /**
     * @return os empregados cadastrados, na ordem em que foram criados
     */
    public Map<String, Empregado> getEmpregados() {
        return empregados;
    }

    /**
     * Restaura a numeração de identificações após recarregar o sistema do disco.
     *
     * @param maiorId maior identificação presente no estado recuperado
     */
    public void restaurarUltimoId(int maiorId) {
        this.ultimoId = maiorId;
    }

    public int getUltimoId() {
        return ultimoId;
    }

    private Empregado buscar(String emp)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException {

        if (emp == null || emp.isBlank()) {
            throw new IdentificacaoEmpregadoNulaException();
        }
        Empregado empregado = empregados.get(emp);
        if (empregado == null) {
            throw new EmpregadoNaoExisteException();
        }
        return empregado;
    }

    private double validarSalario(String valor)
            throws SalarioNuloException, SalarioNaoNumericoException, SalarioNegativoException {

        Numero numero = classificar(valor);
        return switch (numero.status()) {
            case NULO -> throw new SalarioNuloException();
            case NAO_NUMERICO -> throw new SalarioNaoNumericoException();
            case NEGATIVO -> throw new SalarioNegativoException();
            case VALIDO -> numero.valor();
        };
    }

    private double validarComissao(String valor)
            throws ComissaoNulaException, ComissaoNaoNumericaException, ComissaoNegativaException {

        Numero numero = classificar(valor);
        return switch (numero.status()) {
            case NULO -> throw new ComissaoNulaException();
            case NAO_NUMERICO -> throw new ComissaoNaoNumericaException();
            case NEGATIVO -> throw new ComissaoNegativaException();
            case VALIDO -> numero.valor();
        };
    }

    /**
     * Analisa um parâmetro numérico recebido do script de teste.
     *
     * <p>Salário e comissão obedecem exatamente às mesmas três regras, mudando apenas
     * a exceção informada ao chamador. Classificar o texto uma única vez e deixar cada
     * campo responsável por traduzir o resultado em sua própria exceção evita duplicar
     * a análise sem precisar lançar exceção genérica alguma.
     */
    private Numero classificar(String valor) {
        if (valor == null || valor.isBlank()) {
            return new Numero(StatusNumero.NULO, 0);
        }
        Double convertido = Formatador.converterNumero(valor);
        if (convertido == null) {
            return new Numero(StatusNumero.NAO_NUMERICO, 0);
        }
        if (convertido < 0) {
            return new Numero(StatusNumero.NEGATIVO, 0);
        }
        return new Numero(StatusNumero.VALIDO, convertido);
    }
}
