package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando a agência bancária não é informada
 */
public class AgenciaNulaException extends Exception {
    private static final long serialVersionUID = 1L;

    public AgenciaNulaException() {
        super("Agencia nao pode ser nulo.");
    }
}
