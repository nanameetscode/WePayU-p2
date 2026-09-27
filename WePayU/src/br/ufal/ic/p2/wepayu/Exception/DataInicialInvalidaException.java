package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando a data inicial informada para consulta de período é inválida
 */
public class DataInicialInvalidaException extends Exception {
    private static final long serialVersionUID = 1L;

    public DataInicialInvalidaException() {
        super("Data inicial invalida.");
    }
}
