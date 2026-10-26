package br.ufal.ic.p2.wepayu.exceptions.pagamento;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando o método de pagamento informado não é reconhecido.
 */
public class MetodoPagamentoInvalidoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public MetodoPagamentoInvalidoException() {
        super("Metodo de pagamento invalido.");
    }
}
