package br.ufal.ic.p2.wepayu.exceptions.empregado;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando o atributo consultado ou alterado não existe para empregados.
 */
public class AtributoNaoExisteException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public AtributoNaoExisteException() {
        super("Atributo nao existe.");
    }
}
