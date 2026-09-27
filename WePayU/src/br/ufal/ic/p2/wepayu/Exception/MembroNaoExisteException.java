package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando nenhum membro do sindicato é encontrado com a identificação informada
 */
public class MembroNaoExisteException extends Exception {
    private static final long serialVersionUID = 1L;

    public MembroNaoExisteException() {
        super("Membro nao existe.");
    }
}
