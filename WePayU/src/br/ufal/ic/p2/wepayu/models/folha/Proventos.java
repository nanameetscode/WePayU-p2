package br.ufal.ic.p2.wepayu.models.folha;

import java.math.BigDecimal;

/**
 * O que o empregado ganhou num período, antes dos descontos. Cada tipo de empregado
 * tem os seus detalhes (horas, vendas, comissão...), que o relatório mostra em
 * colunas próprias.
 */
public abstract class Proventos {

    /**
     * @return salário bruto do período
     */
    public abstract BigDecimal getBruto();

    /**
     * Entrega estes proventos ao visitante pelo método do seu tipo (padrão Visitor).
     */
    public abstract void aceitar(VisitanteDeContracheque visitante, Contracheque contracheque);
}
