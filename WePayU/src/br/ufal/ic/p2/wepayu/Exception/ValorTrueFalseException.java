package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o valor de sindicalizado não é true nem false
 */
public class ValorTrueFalseException extends Exception {
    private static final long serialVersionUID = 1L;

    public ValorTrueFalseException() {
        super("Valor deve ser true ou false.");
    }
}
