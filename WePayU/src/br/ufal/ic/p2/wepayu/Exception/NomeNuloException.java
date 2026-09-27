package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o nome informado é nulo ou vazio
 */
public class NomeNuloException extends Exception {
    public NomeNuloException() {
        super("Nome nao pode ser nulo.");
    }
}
