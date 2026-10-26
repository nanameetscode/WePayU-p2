package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.CampoData;
import br.ufal.ic.p2.wepayu.exceptions.validacao.HorasDevemSerPositivasException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorDeveSerPositivoException;
import br.ufal.ic.p2.wepayu.models.Periodo;
import br.ufal.ic.p2.wepayu.models.Cadastro;
import br.ufal.ic.p2.wepayu.models.empregado.CartaoDePonto;
import br.ufal.ic.p2.wepayu.models.empregado.Empregado;
import br.ufal.ic.p2.wepayu.models.empregado.ResultadoVenda;
import br.ufal.ic.p2.wepayu.models.sindicato.MembroSindicato;
import br.ufal.ic.p2.wepayu.models.sindicato.TaxaServico;
import br.ufal.ic.p2.wepayu.utils.Formatador;
import br.ufal.ic.p2.wepayu.utils.LeitorDeEntrada;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Supplier;

/**
 * Lançamentos do dia a dia (cartões de ponto, vendas e taxas de serviço) e as
 * consultas sobre eles.
 *
 * <p>As consultas usam o intervalo da data inicial até a véspera da data final. Em
 * todos os comandos, o empregado e o seu tipo são conferidos antes das datas e dos
 * valores informados.
 */
public class LancamentoService {

    @FunctionalInterface
    private interface Registro<T> {
        T criar(LocalDate dia, BigDecimal valor) throws WePayUException;
    }

    @FunctionalInterface
    private interface ExigenciaDeTipo {
        void verificar(Empregado empregado) throws WePayUException;
    }

    @FunctionalInterface
    private interface Consulta {
        String responder(Empregado empregado, Periodo periodo) throws WePayUException;
    }

    private final Cadastro cadastro;

    public LancamentoService(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public void lancaCartao(String emp, String data, String horas) throws WePayUException {
        Empregado empregado = cadastro.buscar(emp);
        empregado.exigirHorista();
        empregado.lancarCartao(lerRegistro(data, horas, HorasDevemSerPositivasException::new, CartaoDePonto::new));
    }

    public void lancaVenda(String emp, String data, String valor) throws WePayUException {
        Empregado empregado = cadastro.buscar(emp);
        empregado.exigirComissionado();
        empregado.lancarVenda(lerRegistro(data, valor, ValorDeveSerPositivoException::new, ResultadoVenda::new));
    }

    /**
     * @param membro identificação do empregado no sindicato
     */
    public void lancaTaxaServico(String membro, String data, String valor) throws WePayUException {
        Empregado empregado = cadastro.buscarMembro(membro);
        TaxaServico taxa = lerRegistro(data, valor, ValorDeveSerPositivoException::new, TaxaServico::new);
        empregado.alterarSindicalizacao(empregado.getSindicalizacao().comoMembro().comTaxaServico(taxa));
    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws WePayUException {

        return consultar(emp, dataInicial, dataFinal, Empregado::exigirHorista,
                (empregado, periodo) -> Formatador.horas(empregado.getHorasNormais(periodo)));
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws WePayUException {

        return consultar(emp, dataInicial, dataFinal, Empregado::exigirHorista,
                (empregado, periodo) -> Formatador.horas(empregado.getHorasExtras(periodo)));
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws WePayUException {
        return consultar(emp, dataInicial, dataFinal, Empregado::exigirComissionado,
                (empregado, periodo) -> Formatador.moeda(empregado.getVendasRealizadas(periodo)));
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws WePayUException {
        MembroSindicato membro = cadastro.buscar(emp).getSindicalizacao().comoMembro();
        Periodo periodo = LeitorDeEntrada.lerPeriodo(dataInicial, dataFinal);
        return Formatador.moeda(membro.getTaxasServico(periodo));
    }

    /**
     * Lê a data e o número de um lançamento e cria o registro, que valida o próprio valor.
     *
     * @param numeroInvalido erro do campo numérico quando o texto não é um número
     */
    private static <T, E extends WePayUException> T lerRegistro(String data, String numero,
            Supplier<E> numeroInvalido, Registro<T> registro) throws WePayUException {

        LocalDate dia = LeitorDeEntrada.lerData(data, CampoData.DATA);
        return registro.criar(dia, LeitorDeEntrada.lerNumero(numero, numeroInvalido));
    }

    /**
     * Localiza o empregado, confere o seu tipo, lê o intervalo e responde a consulta.
     */
    private String consultar(String emp, String dataInicial, String dataFinal, ExigenciaDeTipo tipo,
            Consulta consulta) throws WePayUException {

        Empregado empregado = cadastro.buscar(emp);
        tipo.verificar(empregado);
        return consulta.responder(empregado, LeitorDeEntrada.lerPeriodo(dataInicial, dataFinal));
    }
}
