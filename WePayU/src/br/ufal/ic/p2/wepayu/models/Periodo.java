package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Intervalo fechado de datas, com início e fim incluídos.
 *
 * <p>Serve tanto às consultas dos scripts, que usam intervalos semiabertos
 * (veja {@link #semiAberto}), quanto aos períodos de pagamento da folha.
 *
 * @param inicio primeiro dia do intervalo
 * @param fim    último dia do intervalo; quando anterior ao início, o intervalo é vazio
 */
public record Periodo(LocalDate inicio, LocalDate fim) {

    /**
     * @param inicio       primeiro dia, incluído
     * @param fimExclusivo dia seguinte ao último dia do intervalo
     * @return o intervalo [inicio, fimExclusivo)
     */
    public static Periodo semiAberto(LocalDate inicio, LocalDate fimExclusivo) {
        return new Periodo(inicio, fimExclusivo.minusDays(1));
    }

    /**
     * @param fim  último dia do intervalo
     * @param dias quantidade de dias do intervalo, contando o último
     * @return o intervalo de {@code dias} dias terminado em {@code fim}
     */
    public static Periodo terminadoEm(LocalDate fim, int dias) {
        return new Periodo(fim.minusDays(dias - 1L), fim);
    }

    /**
     * @return intervalo sem nenhum dia, terminado em {@code fim}
     */
    public static Periodo vazioEm(LocalDate fim) {
        return new Periodo(fim.plusDays(1), fim);
    }

    /**
     * @return se a data está dentro do intervalo
     */
    public boolean contem(LocalDate data) {
        return !data.isBefore(inicio) && !data.isAfter(fim);
    }

    /**
     * @return quantidade de dias do intervalo
     */
    public long dias() {
        return ChronoUnit.DAYS.between(inicio, fim) + 1;
    }
}
