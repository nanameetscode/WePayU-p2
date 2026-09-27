package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando uma data fornecida é inválida
 */
public class DataInvalidaException extends Exception {
    private static final long serialVersionUID = 1L;

    public DataInvalidaException() {
        super("Data invalida.");
    }
}
