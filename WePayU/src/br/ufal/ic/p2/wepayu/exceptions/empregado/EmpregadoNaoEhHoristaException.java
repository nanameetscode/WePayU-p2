package br.ufal.ic.p2.wepayu.exceptions.empregado;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando uma operação exclusiva de horistas é pedida para outro tipo de empregado.
 */
public class EmpregadoNaoEhHoristaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoEhHoristaException() {
        super("Empregado nao eh horista.");
    }
}
