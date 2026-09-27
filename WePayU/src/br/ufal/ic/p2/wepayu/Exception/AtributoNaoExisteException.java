package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o atributo solicitado não existe no empregado
 */
public class AtributoNaoExisteException extends Exception {
    private static final long serialVersionUID = 1L;

    public AtributoNaoExisteException() {
        super("Atributo nao existe.");
    }
}
