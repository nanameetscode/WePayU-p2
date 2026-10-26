package br.ufal.ic.p2.wepayu.models.folha;

import java.math.BigDecimal;

/**
 * Proventos do assalariado: o salário do mês.
 */
public class ProventosAssalariado extends Proventos {

    private final BigDecimal bruto;

    public ProventosAssalariado(BigDecimal bruto) {
        this.bruto = bruto;
    }

    @Override
    public BigDecimal getBruto() {
        return bruto;
    }

    @Override
    public void aceitar(VisitanteDeContracheque visitante, Contracheque contracheque) {
        visitante.visitarAssalariado(contracheque, this);
    }
}
