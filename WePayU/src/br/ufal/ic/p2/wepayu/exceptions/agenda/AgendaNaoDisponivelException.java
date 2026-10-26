package br.ufal.ic.p2.wepayu.exceptions.agenda;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando o empregado escolhe uma agenda de pagamento que a empresa não oferece.
 */
public class AgendaNaoDisponivelException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public AgendaNaoDisponivelException() {
        super("Agenda de pagamento nao esta disponivel");
    }
}
