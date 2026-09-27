package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o método de pagamento informado não é válido
 */
public class MetodoPagamentoInvalidoException extends Exception {
    private static final long serialVersionUID = 1L;

    public MetodoPagamentoInvalidoException() {
        super("Metodo de pagamento invalido.");
    }
}
