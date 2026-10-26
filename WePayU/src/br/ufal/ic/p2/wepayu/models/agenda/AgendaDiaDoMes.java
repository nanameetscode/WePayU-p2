package br.ufal.ic.p2.wepayu.models.agenda;

import br.ufal.ic.p2.wepayu.exceptions.agenda.DescricaoDeAgendaInvalidaException;

import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Pagamento num dia fixo de cada mês ("mensal 7"), caia ele em dia útil ou não (US10).
 * O dia vai até 28, para existir em todos os meses.
 */
public class AgendaDiaDoMes extends AgendaMensal {

    private static final int ULTIMO_DIA_PERMITIDO = 28;

    private final int dia;

    /**
     * @param dia dia do mês, de 1 a 28
     * @throws DescricaoDeAgendaInvalidaException se o dia estiver fora dessa faixa
     */
    @ConstructorProperties({"dia"})
    public AgendaDiaDoMes(int dia) throws DescricaoDeAgendaInvalidaException {
        if (dia < 1 || dia > ULTIMO_DIA_PERMITIDO) {
            throw new DescricaoDeAgendaInvalidaException();
        }
        this.dia = dia;
    }

    @Override
    public String getDescricao() {
        return "mensal " + dia;
    }

    @Override
    protected LocalDate diaDePagamento(YearMonth mes) {
        return mes.atDay(dia);
    }

    public int getDia() {
        return dia;
    }
}
