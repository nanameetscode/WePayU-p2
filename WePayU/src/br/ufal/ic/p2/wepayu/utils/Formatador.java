package br.ufal.ic.p2.wepayu.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;

/**
 * Utilitário de conversão entre números e o texto usado pelo EasyAccept.
 *
 * <p>Os scripts de teste escrevem os valores monetários no padrão brasileiro, com
 * vírgula como separador decimal, e o framework compara o texto devolvido
 * literalmente. Esta classe é o único ponto do sistema que conhece esse padrão,
 * de modo que a conversão aconteça uma única vez em cada sentido.
 */
public class Formatador {

    private static final DateTimeFormatter FORMATADOR_DATA = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NEVER)
            .appendLiteral('/')
            .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NEVER)
            .appendLiteral('/')
            .appendValue(ChronoField.YEAR, 4, 4, SignStyle.NEVER)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Formata um valor numérico com duas casas decimais e separador vírgula,
     * sem separador de milhar (ex: 23,00 ou 2300,45).
     *
     * @param valor valor a formatar
     * @return o valor no padrão esperado pelo EasyAccept
     */
    public static String formatarMoeda(double valor) {
        return String.format(Locale.US, "%.2f", valor).replace('.', ',');
    }

    /**
     * Converte o texto de um parâmetro numérico em número.
     *
     * <p>A vírgula é o separador decimal dos scripts, enquanto {@code Double} só
     * reconhece o ponto. A troca é feita aqui para que a validação dos parâmetros
     * receba sempre um texto no formato esperado pela biblioteca.
     *
     * @param texto valor recebido do script, possivelmente com vírgula decimal
     * @return o número convertido, ou {@code null} se o texto não for numérico
     */
    public static Double converterNumero(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException naoEhNumero) {
            return null;
        }
    }

    /**
     * Converte o texto de uma data no formato d/M/yyyy em {@link LocalDate}.
     *
     * @param texto texto da data recebido do script
     * @return a data convertida, ou {@code null} se a data for inválida
     */
    public static LocalDate converterData(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim(), FORMATADOR_DATA);
        } catch (DateTimeParseException erro) {
            return null;
        }
    }

    /**
     * Formata quantidade de horas. Valores inteiros saem sem casas decimais (ex: 8, 16),
     * e valores fracionários saem com vírgula (ex: 1,5).
     *
     * @param horas quantidade de horas
     * @return texto formatado
     */
    public static String formatarHoras(double horas) {
        if (horas == (long) horas) {
            return String.valueOf((long) horas);
        }
        String formatado = String.format(Locale.US, "%.2f", horas).replace('.', ',');
        if (formatado.endsWith(",00")) {
            return formatado.substring(0, formatado.length() - 3);
        }
        if (formatado.endsWith("0")) {
            return formatado.substring(0, formatado.length() - 1);
        }
        return formatado;
    }
}
