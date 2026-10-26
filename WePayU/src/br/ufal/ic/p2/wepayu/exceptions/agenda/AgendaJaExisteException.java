package br.ufal.ic.p2.wepayu.exceptions.agenda;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando a empresa já oferece uma agenda igual à que se quer criar.
 */
public class AgendaJaExisteException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public AgendaJaExisteException() {
        super("Agenda de pagamentos ja existe");
    }
}
