package br.ufal.ic.p2.wepayu.exceptions.pagamento;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando o depósito em banco é escolhido sem informar a agência.
 */
public class AgenciaNulaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public AgenciaNulaException() {
        super("Agencia nao pode ser nulo.");
    }
}
