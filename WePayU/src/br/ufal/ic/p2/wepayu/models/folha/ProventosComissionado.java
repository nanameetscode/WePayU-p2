package br.ufal.ic.p2.wepayu.models.folha;

import java.math.BigDecimal;

/**
 * Proventos do comissionado: parte fixa da quinzena mais a comissão sobre as vendas.
 */
public class ProventosComissionado extends Proventos {

    private final BigDecimal fixo;
    private final BigDecimal vendas;
    private final BigDecimal comissao;

    /**
     * @param fixo     parte fixa do salário no período
     * @param vendas   total vendido no período
     * @param comissao comissão sobre as vendas
     */
    public ProventosComissionado(BigDecimal fixo, BigDecimal vendas, BigDecimal comissao) {
        this.fixo = fixo;
        this.vendas = vendas;
        this.comissao = comissao;
    }

    public BigDecimal getFixo() {
        return fixo;
    }

    public BigDecimal getVendas() {
        return vendas;
    }

    public BigDecimal getComissao() {
        return comissao;
    }

    @Override
    public BigDecimal getBruto() {
        return fixo.add(comissao);
    }

    @Override
    public void aceitar(VisitanteDeContracheque visitante, Contracheque contracheque) {
        visitante.visitarComissionado(contracheque, this);
    }
}
