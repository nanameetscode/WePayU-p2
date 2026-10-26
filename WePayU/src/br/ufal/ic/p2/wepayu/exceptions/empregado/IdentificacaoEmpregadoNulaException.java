package br.ufal.ic.p2.wepayu.exceptions.empregado;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando a identificação do empregado não é informada.
 */
public class IdentificacaoEmpregadoNulaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public IdentificacaoEmpregadoNulaException() {
        super("Identificacao do empregado nao pode ser nula.");
    }
}
