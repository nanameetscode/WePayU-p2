package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o nome informado é nulo ou vazio
 */
public class NomeNuloException extends Exception {
    private static final long serialVersionUID = 1L;

    public NomeNuloException() {
        super("Nome nao pode ser nulo.");
    }
}
