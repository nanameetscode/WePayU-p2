package br.ufal.ic.p2.wepayu.models.folha;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Folha de pagamento de um dia: os contracheques de todos que recebem nesse dia,
 * em ordem alfabética.
 */
public class Folha {

    private final LocalDate dia;
    private final List<Contracheque> contracheques;

    public Folha(LocalDate dia, List<Contracheque> contracheques) {
        this.dia = dia;
        this.contracheques = List.copyOf(contracheques);
    }

    public LocalDate getDia() {
        return dia;
    }

    /**
     * @return soma dos salários brutos
     */
    public BigDecimal getTotalBruto() {
        return contracheques.stream().map(Contracheque::getBruto).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Entrega cada contracheque ao visitante, na ordem da folha.
     */
    public void registrarEm(VisitanteDeContracheque visitante) {
        contracheques.forEach(contracheque -> contracheque.registrarEm(visitante));
    }

    /**
     * Efetiva todos os pagamentos.
     */
    public void efetivar() {
        contracheques.forEach(Contracheque::efetivar);
    }
}
