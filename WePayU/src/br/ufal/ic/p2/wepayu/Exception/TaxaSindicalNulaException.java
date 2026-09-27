package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando a taxa sindical não é informada
 */
public class TaxaSindicalNulaException extends Exception {
    private static final long serialVersionUID = 1L;

    public TaxaSindicalNulaException() {
        super("Taxa sindical nao pode ser nula.");
    }
}
