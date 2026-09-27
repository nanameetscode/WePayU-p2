package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o nome do banco não é informado
 */
public class BancoNuloException extends Exception {
    private static final long serialVersionUID = 1L;

    public BancoNuloException() {
        super("Banco nao pode ser nulo.");
    }
}
