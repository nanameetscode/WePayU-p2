package br.ufal.ic.p2.wepayu.models.agenda;

import br.ufal.ic.p2.wepayu.models.Periodo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Pagamento uma vez por mês. Cada pagamento cobre os dias desde o pagamento do mês
 * anterior e vale o salário mensal inteiro; as subclasses dizem em que dia do mês ele cai.
 */
public abstract class AgendaMensal extends AgendaDePagamento {

    /**
     * @return o dia de pagamento no mês informado
     */
    protected abstract LocalDate diaDePagamento(YearMonth mes);

    @Override
    public boolean ehDiaDePagamento(LocalDate dia, LocalDate contratacao) {
        return !dia.isBefore(contratacao) && dia.equals(diaDePagamento(YearMonth.from(dia)));
    }

    @Override
    public Periodo periodoDePagamento(LocalDate dia) {
        LocalDate pagamentoAnterior = diaDePagamento(YearMonth.from(dia).minusMonths(1));
        return new Periodo(pagamentoAnterior.plusDays(1), dia);
    }

    @Override
    public BigDecimal parteDoSalarioMensal(BigDecimal salarioMensal) {
        return salarioMensal;
    }
}
