package br.ufal.ic.p2.wepayu.models.agenda;

import br.ufal.ic.p2.wepayu.exceptions.agenda.DescricaoDeAgendaInvalidaException;

import java.time.DayOfWeek;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interpreta a descrição textual de uma agenda de pagamento (US10):
 * <ul>
 *   <li>{@code semanal D}: toda semana no dia D (1 = segunda ... 7 = domingo);</li>
 *   <li>{@code semanal N D}: a cada N semanas (1 a 52) no dia D;</li>
 *   <li>{@code mensal D}: no dia D de cada mês (1 a 28);</li>
 *   <li>{@code mensal $}: no último dia útil de cada mês.</li>
 * </ul>
 */
public final class DescricaoDeAgenda {

    private static final Pattern SEMANAL = Pattern.compile("semanal (?:(\\d{1,2}) )?(\\d{1,2})");
    private static final Pattern MENSAL_DIA_FIXO = Pattern.compile("mensal (\\d{1,2})");
    private static final String MENSAL_ULTIMO_DIA_UTIL = "mensal $";
    private static final int SEMANAS_SE_OMITIDO = 1;

    private DescricaoDeAgenda() {
    }

    /**
     * @param descricao descrição da agenda, como "semanal 2 5" ou "mensal 7"
     * @return a agenda descrita
     * @throws DescricaoDeAgendaInvalidaException se a descrição não seguir o formato
     */
    public static AgendaDePagamento interpretar(String descricao) throws DescricaoDeAgendaInvalidaException {
        if (descricao == null) {
            throw new DescricaoDeAgendaInvalidaException();
        }
        if (MENSAL_ULTIMO_DIA_UTIL.equals(descricao)) {
            return AgendasDisponiveis.MENSAL;
        }
        Matcher mensal = MENSAL_DIA_FIXO.matcher(descricao);
        if (mensal.matches()) {
            return new AgendaDiaDoMes(Integer.parseInt(mensal.group(1)));
        }
        Matcher semanal = SEMANAL.matcher(descricao);
        if (semanal.matches()) {
            int semanas = semanal.group(1) == null ? SEMANAS_SE_OMITIDO : Integer.parseInt(semanal.group(1));
            return new AgendaSemanal(semanas, diaDaSemana(Integer.parseInt(semanal.group(2))));
        }
        throw new DescricaoDeAgendaInvalidaException();
    }

    private static DayOfWeek diaDaSemana(int numero) throws DescricaoDeAgendaInvalidaException {
        if (numero < 1 || numero > DayOfWeek.values().length) {
            throw new DescricaoDeAgendaInvalidaException();
        }
        return DayOfWeek.of(numero);
    }
}
