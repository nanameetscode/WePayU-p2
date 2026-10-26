package br.ufal.ic.p2.wepayu.utils;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.CampoData;
import br.ufal.ic.p2.wepayu.exceptions.validacao.CampoNumerico;
import br.ufal.ic.p2.wepayu.exceptions.validacao.DataInicialPosteriorDataFinalException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.DataInvalidaException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNaoNumericoException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNegativoException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNuloException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorTrueFalseException;
import br.ufal.ic.p2.wepayu.models.Periodo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Converte os parâmetros de texto dos scripts em valores do sistema, lançando o
 * erro específico de cada campo quando o texto não serve.
 *
 * <p>Os scripts usam o padrão brasileiro: vírgula decimal e datas d/M/aaaa.
 */
public final class LeitorDeEntrada {

    private static final DateTimeFormatter FORMATO_DATA = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NEVER)
            .appendLiteral('/')
            .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NEVER)
            .appendLiteral('/')
            .appendValue(ChronoField.YEAR, 4, 4, SignStyle.NEVER)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);

    private static final Pattern FORMATO_NUMERO = Pattern.compile("[+-]?\\d+([.,]\\d+)?");

    private LeitorDeEntrada() {
    }

    /**
     * Aceita só números escritos por extenso, com vírgula ou ponto decimal (ex.: 23,
     * -1, 2300,45); notação científica como "1e3" não é aceita.
     *
     * @param texto número recebido do script
     * @param erro  fábrica do erro do campo, criado se o texto não for um número
     * @return o número lido
     */
    public static <E extends WePayUException> BigDecimal lerNumero(String texto, Supplier<E> erro) throws E {
        if (texto == null || !FORMATO_NUMERO.matcher(texto.trim()).matches()) {
            throw erro.get();
        }
        return new BigDecimal(texto.trim().replace(',', '.'));
    }

    /**
     * Lê um campo que precisa estar preenchido, ser numérico e não ser negativo,
     * verificando nessa ordem.
     *
     * @param texto texto recebido do script
     * @param campo campo lido, que define as mensagens de erro
     * @return o valor lido
     */
    public static BigDecimal lerValorNaoNegativo(String texto, CampoNumerico campo)
            throws ValorNuloException, ValorNaoNumericoException, ValorNegativoException {

        Validacao.exigirPreenchido(texto, () -> new ValorNuloException(campo));
        BigDecimal valor = lerNumero(texto, () -> new ValorNaoNumericoException(campo));
        return Validacao.exigirNaoNegativo(valor, campo);
    }

    /**
     * @param texto data no formato d/M/aaaa
     * @param campo campo lido, que define a mensagem de erro
     * @return a data lida
     * @throws DataInvalidaException se a data faltar, estiver fora do formato ou não existir
     */
    public static LocalDate lerData(String texto, CampoData campo) throws DataInvalidaException {
        if (texto == null) {
            throw new DataInvalidaException(campo);
        }
        try {
            return LocalDate.parse(texto.trim(), FORMATO_DATA);
        } catch (DateTimeParseException formatoInvalido) {
            throw new DataInvalidaException(campo);
        }
    }

    /**
     * Lê o intervalo de uma consulta: começa na data inicial e termina na véspera
     * da data final.
     *
     * @return o intervalo lido, vazio quando as duas datas coincidem
     */
    public static Periodo lerPeriodo(String dataInicial, String dataFinal)
            throws DataInvalidaException, DataInicialPosteriorDataFinalException {

        LocalDate inicio = lerData(dataInicial, CampoData.DATA_INICIAL);
        LocalDate fim = lerData(dataFinal, CampoData.DATA_FINAL);
        if (inicio.isAfter(fim)) {
            throw new DataInicialPosteriorDataFinalException();
        }
        return Periodo.semiAberto(inicio, fim);
    }

    /**
     * @param texto "true" ou "false"
     * @return o valor lido
     * @throws ValorTrueFalseException para qualquer outro texto
     */
    public static boolean lerBooleano(String texto) throws ValorTrueFalseException {
        if ("true".equals(texto)) {
            return true;
        }
        if ("false".equals(texto)) {
            return false;
        }
        throw new ValorTrueFalseException();
    }
}
