package br.ufal.ic.p2.wepayu.models.agenda;

import br.ufal.ic.p2.wepayu.models.Periodo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Quando um empregado recebe e que dias cada pagamento cobre (US9).
 *
 * <p>A agenda é independente do tipo do empregado: um horista pode receber por mês e
 * um assalariado por semana. O tipo decide quanto se ganha no período; a agenda decide
 * qual é o período. As implementações são imutáveis.
 */
public abstract class AgendaDePagamento {

    /**
     * @return a descrição usada pelos scripts, como "semanal 2 5" ou "mensal $"
     */
    public abstract String getDescricao();

    /**
     * @param contratacao dia em que o empregado foi contratado; as agendas semanais
     *                    contam as semanas a partir dele
     * @return se o empregado recebe no dia informado
     */
    public abstract boolean ehDiaDePagamento(LocalDate dia, LocalDate contratacao);

    /**
     * @param dia dia de pagamento
     * @return os dias cobertos pelo pagamento, terminando no próprio dia
     */
    public abstract Periodo periodoDePagamento(LocalDate dia);

    /**
     * @param salarioMensal salário fixo do mês
     * @return a parte do salário mensal que cabe a um pagamento desta agenda
     */
    public abstract BigDecimal parteDoSalarioMensal(BigDecimal salarioMensal);
}
