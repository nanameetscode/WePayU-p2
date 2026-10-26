package br.ufal.ic.p2.wepayu.exceptions.empregado;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando não há empregado cadastrado com a identificação informada.
 */
public class EmpregadoNaoExisteException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoExisteException() {
        super("Empregado nao existe.");
    }
}
