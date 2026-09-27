package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.Exception.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ComissaoNaoNumericaException;
import br.ufal.ic.p2.wepayu.Exception.ComissaoNegativaException;
import br.ufal.ic.p2.wepayu.Exception.ComissaoNulaException;
import br.ufal.ic.p2.wepayu.Exception.DataFinalInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.DataInicialInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.DataInicialPosteriorDataFinalException;
import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhHoristaException;
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
import br.ufal.ic.p2.wepayu.models.TipoEmpregado;
import br.ufal.ic.p2.wepayu.utils.Formatador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Regras de negocio do cadastro de empregados e registro de cartao de ponto.
 */
public class EmpregadoService {

    private enum StatusNumero {
        VALIDO, NULO, NAO_NUMERICO, NEGATIVO
    }

    private record Numero(StatusNumero status, double valor) { }

    private final Map<String, Empregado> empregados = new LinkedHashMap<>();
    private final FabricaEmpregado fabrica = new FabricaEmpregado();
    private int ultimoId;

    public String criarEmpregado(String nome, String endereco, String tipo, String salario)
            throws NomeNuloException, EnderecoNuloException, TipoInvalidoException,
            SalarioNuloException, SalarioNaoNumericoException, SalarioNegativoException,
            ComissaoNulaException, ComissaoNaoNumericaException, ComissaoNegativaException,
            TipoNaoAplicavelException {

        return criarEmpregado(nome, endereco, tipo, salario, null);
    }

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

    public String getAtributoEmpregado(String emp, String atributo)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException {

        Empregado empregado = buscar(emp);
        String valor = empregado.getAtributos().get(atributo);
        if (valor == null) {
            throw new AtributoNaoExisteException();
        }
        return valor;
    }

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

    public void removerEmpregado(String emp)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException {
        buscar(emp);
        empregados.remove(emp);
    }

    public void lancaCartao(String emp, String data, String horas)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhHoristaException, DataInvalidaException, HorasDevemSerPositivasException {

        Empregado empregado = buscar(emp);
        LocalDate dataPonto = Formatador.converterData(data);
        if (dataPonto == null) {
            throw new DataInvalidaException();
        }
        Double horasNumericas = Formatador.converterNumero(horas);
        if (horasNumericas == null || horasNumericas <= 0) {
            throw new HorasDevemSerPositivasException();
        }
        empregado.lancarCartao(data, horasNumericas);
    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhHoristaException, DataInicialInvalidaException, DataFinalInvalidaException,
            DataInicialPosteriorDataFinalException {

        Empregado empregado = buscar(emp);
        LocalDate[] periodo = validarPeriodo(dataInicial, dataFinal);
        double horas = empregado.getHorasNormais(periodo[0], periodo[1]);
        return Formatador.formatarHoras(horas);
    }

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

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhComissionadoException, DataInicialInvalidaException, DataFinalInvalidaException,
            DataInicialPosteriorDataFinalException {

        Empregado empregado = buscar(emp);
        LocalDate[] periodo = validarPeriodo(dataInicial, dataFinal);
        double total = empregado.getVendasRealizadas(periodo[0], periodo[1]);
        return Formatador.formatarMoeda(total);
    }

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

    public void alteraEmpregado(String emp, String atributo, String valor)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, ValorTrueFalseException, IdentificacaoSindicatoNulaException {

        Empregado empregado = buscar(emp);
        if ("sindicalizado".equals(atributo)) {
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

    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, ValorTrueFalseException,
            IdentificacaoSindicatoNulaException, IdentificacaoSindicatoDuplicadaException,
            TaxaSindicalNulaException, TaxaSindicalNaoNumericaException, TaxaSindicalNegativaException {

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

    public void zerarSistema() {
        empregados.clear();
        ultimoId = 0;
    }

    public Map<String, Empregado> getEmpregados() {
        return empregados;
    }

    public void restaurarUltimoId(int maiorId) {
        this.ultimoId = maiorId;
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
