package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando a taxa sindical é negativa
 */
public class TaxaSindicalNegativaException extends Exception {
    private static final long serialVersionUID = 1L;

    public TaxaSindicalNegativaException() {
        super("Taxa sindical deve ser nao-negativa.");
    }
}
