package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o valor informado não é positivo (> 0)
 */
public class ValorDeveSerPositivoException extends Exception {
    private static final long serialVersionUID = 1L;

    public ValorDeveSerPositivoException() {
        super("Valor deve ser positivo.");
    }
}
