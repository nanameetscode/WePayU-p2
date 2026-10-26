package br.ufal.ic.p2.wepayu.exceptions.pagamento;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando o depósito em banco é escolhido sem informar a conta corrente.
 */
public class ContaCorrenteNulaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public ContaCorrenteNulaException() {
        super("Conta corrente nao pode ser nulo.");
    }
}
