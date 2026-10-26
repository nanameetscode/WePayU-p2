package br.ufal.ic.p2.wepayu.models.folha;

import java.math.BigDecimal;

/**
 * Proventos do horista: horas normais e extras da semana.
 */
public class ProventosHorista extends Proventos {

    private final BigDecimal horasNormais;
    private final BigDecimal horasExtras;
    private final BigDecimal bruto;

    public ProventosHorista(BigDecimal horasNormais, BigDecimal horasExtras, BigDecimal bruto) {
        this.horasNormais = horasNormais;
        this.horasExtras = horasExtras;
        this.bruto = bruto;
    }

    public BigDecimal getHorasNormais() {
        return horasNormais;
    }

    public BigDecimal getHorasExtras() {
        return horasExtras;
    }

    @Override
    public BigDecimal getBruto() {
        return bruto;
    }

    @Override
    public void aceitar(VisitanteDeContracheque visitante, Contracheque contracheque) {
        visitante.visitarHorista(contracheque, this);
    }
}
