package br.ufal.ic.p2.wepayu.models.agenda;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Pagamento no último dia útil de cada mês ("mensal $"), sem considerar feriados.
 */
public class AgendaUltimoDiaUtil extends AgendaMensal {

    @Override
    public String getDescricao() {
        return "mensal $";
    }

    @Override
    protected LocalDate diaDePagamento(YearMonth mes) {
        LocalDate dia = mes.atEndOfMonth();
        while (dia.getDayOfWeek() == DayOfWeek.SATURDAY || dia.getDayOfWeek() == DayOfWeek.SUNDAY) {
            dia = dia.minusDays(1);
        }
        return dia;
    }
}
