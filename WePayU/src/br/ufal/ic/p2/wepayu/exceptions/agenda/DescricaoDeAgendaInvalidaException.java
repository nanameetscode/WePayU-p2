package br.ufal.ic.p2.wepayu.exceptions.agenda;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando a descrição de uma nova agenda não segue o formato "semanal [semanas] dia" ou "mensal dia".
 */
public class DescricaoDeAgendaInvalidaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public DescricaoDeAgendaInvalidaException() {
        super("Descricao de agenda invalida");
    }
}
