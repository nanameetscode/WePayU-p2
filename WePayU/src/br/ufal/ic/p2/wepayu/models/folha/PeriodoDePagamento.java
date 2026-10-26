package br.ufal.ic.p2.wepayu.models.folha;

import br.ufal.ic.p2.wepayu.models.Centavos;
import br.ufal.ic.p2.wepayu.models.Periodo;

import java.math.BigDecimal;

/**
 * Os dias de um pagamento: o período inteiro da agenda e a parte dele que ainda não
 * foi paga. Os dois só diferem quando o empregado já recebeu no meio do período,
 * porque mudou de agenda ou de tipo.
 *
 * @param daAgenda período inteiro definido pela agenda
 * @param aPagar   dias do período que ainda não foram pagos
 */
public record PeriodoDePagamento(Periodo daAgenda, Periodo aPagar) {

    /**
     * @param valorDoPeriodo valor fixo que corresponde ao período inteiro da agenda
     * @return o valor proporcional aos dias a pagar, truncado em centavos
     */
    public BigDecimal proporcional(BigDecimal valorDoPeriodo) {
        if (aPagar.dias() == daAgenda.dias()) {
            return valorDoPeriodo;
        }
        return Centavos.dividir(valorDoPeriodo.multiply(BigDecimal.valueOf(aPagar.dias())),
                BigDecimal.valueOf(daAgenda.dias()));
    }
}
