package br.ufal.ic.p2.wepayu.exceptions.pagamento;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando o depósito em banco é escolhido sem informar o banco.
 */
public class BancoNuloException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public BancoNuloException() {
        super("Banco nao pode ser nulo.");
    }
}
