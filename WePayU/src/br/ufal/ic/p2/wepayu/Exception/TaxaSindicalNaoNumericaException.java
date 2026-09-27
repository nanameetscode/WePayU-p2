package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando a taxa sindical não é numérica
 */
public class TaxaSindicalNaoNumericaException extends Exception {
    private static final long serialVersionUID = 1L;

    public TaxaSindicalNaoNumericaException() {
        super("Taxa sindical deve ser numerica.");
    }
}
