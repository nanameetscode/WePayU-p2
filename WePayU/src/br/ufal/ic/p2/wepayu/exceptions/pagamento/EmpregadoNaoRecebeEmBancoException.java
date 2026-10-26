package br.ufal.ic.p2.wepayu.exceptions.pagamento;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando dados bancários são consultados para quem não recebe por depósito.
 */
public class EmpregadoNaoRecebeEmBancoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoRecebeEmBancoException() {
        super("Empregado nao recebe em banco.");
    }
}
