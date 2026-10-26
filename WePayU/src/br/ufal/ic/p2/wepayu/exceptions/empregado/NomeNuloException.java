package br.ufal.ic.p2.wepayu.exceptions.empregado;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando o nome do empregado não é informado.
 */
public class NomeNuloException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public NomeNuloException() {
        super("Nome nao pode ser nulo.");
    }
}
