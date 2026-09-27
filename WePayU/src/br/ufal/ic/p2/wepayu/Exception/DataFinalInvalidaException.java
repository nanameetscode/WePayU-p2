package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando a data final informada para consulta de período é inválida
 */
public class DataFinalInvalidaException extends Exception {
    private static final long serialVersionUID = 1L;

    public DataFinalInvalidaException() {
        super("Data final invalida.");
    }
}
