package br.ufal.ic.p2.wepayu.exceptions.empregado;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando uma operação exclusiva de comissionados é pedida para outro tipo de empregado.
 */
public class EmpregadoNaoEhComissionadoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoEhComissionadoException() {
        super("Empregado nao eh comissionado.");
    }
}
