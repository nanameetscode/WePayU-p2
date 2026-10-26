package br.ufal.ic.p2.wepayu.models.agenda;

import br.ufal.ic.p2.wepayu.exceptions.agenda.DescricaoDeAgendaInvalidaException;
import br.ufal.ic.p2.wepayu.models.Centavos;
import br.ufal.ic.p2.wepayu.models.Periodo;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

/**
 * Pagamento num dia da semana, a cada tantas semanas ("semanal 5", "semanal 2 5",
 * "semanal 3 3"...).
 *
 * <p>O primeiro pagamento cai na última semana do primeiro intervalo de trabalho, contado
 * da contratação: para "semanal 2 5", contratado em 1/1/2005 (sábado), é a sexta-feira
 * 14/1/2005. Como o ano
 * tem 52 semanas, cada pagamento vale {@code salario * 12 * semanas / 52} do salário
 * mensal, truncado em centavos como no resultado esperado.
 */
public class AgendaSemanal extends AgendaDePagamento {

    private static final int DIAS_POR_SEMANA = 7;
    private static final BigDecimal MESES_POR_ANO = BigDecimal.valueOf(12);
    private static final BigDecimal SEMANAS_POR_ANO = BigDecimal.valueOf(52);

    private static final int MAXIMO_DE_SEMANAS = 52;

    /** Toda sexta-feira ("semanal 5"). */
    public static final AgendaSemanal TODA_SEXTA = new AgendaSemanal(DayOfWeek.FRIDAY, 1);

    /** A cada duas sextas-feiras ("semanal 2 5"). */
    public static final AgendaSemanal A_CADA_DUAS_SEXTAS = new AgendaSemanal(DayOfWeek.FRIDAY, 2);

    private final int semanas;
    private final DayOfWeek diaDaSemana;

    /**
     * @param semanas     intervalo entre pagamentos, de 1 a 52 semanas
     * @param diaDaSemana dia da semana em que o pagamento cai
     * @throws DescricaoDeAgendaInvalidaException se o intervalo estiver fora dessa faixa
     */
    @ConstructorProperties({"semanas", "diaDaSemana"})
    public AgendaSemanal(int semanas, DayOfWeek diaDaSemana) throws DescricaoDeAgendaInvalidaException {
        this(Objects.requireNonNull(diaDaSemana), validarSemanas(semanas));
    }

    /**
     * Cria as agendas padrão, cujos valores já se sabe que são válidos.
     */
    private AgendaSemanal(DayOfWeek diaDaSemana, int semanas) {
        this.semanas = semanas;
        this.diaDaSemana = diaDaSemana;
    }

    private static int validarSemanas(int semanas) throws DescricaoDeAgendaInvalidaException {
        if (semanas < 1 || semanas > MAXIMO_DE_SEMANAS) {
            throw new DescricaoDeAgendaInvalidaException();
        }
        return semanas;
    }

    /**
     * O dia da semana sai como número, de 1 (segunda) a 7 (domingo); o intervalo só
     * aparece quando passa de uma semana.
     */
    @Override
    public String getDescricao() {
        String intervalo = semanas == 1 ? "" : semanas + " ";
        return "semanal " + intervalo + diaDaSemana.getValue();
    }

    @Override
    public boolean ehDiaDePagamento(LocalDate dia, LocalDate contratacao) {
        LocalDate primeiroPagamento = contratacao
                .with(TemporalAdjusters.nextOrSame(diaDaSemana))
                .plusWeeks(semanas - 1L);
        return !dia.isBefore(primeiroPagamento)
                && ChronoUnit.DAYS.between(primeiroPagamento, dia) % diasDoPeriodo() == 0;
    }

    @Override
    public Periodo periodoDePagamento(LocalDate dia) {
        return Periodo.terminadoEm(dia, diasDoPeriodo());
    }

    @Override
    public BigDecimal parteDoSalarioMensal(BigDecimal salarioMensal) {
        return Centavos.dividir(salarioMensal.multiply(MESES_POR_ANO).multiply(BigDecimal.valueOf(semanas)),
                SEMANAS_POR_ANO);
    }

    public int getSemanas() {
        return semanas;
    }

    public DayOfWeek getDiaDaSemana() {
        return diaDaSemana;
    }

    private int diasDoPeriodo() {
        return semanas * DIAS_POR_SEMANA;
    }
}
