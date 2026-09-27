package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando a identificação sindical informada já pertence a outro empregado
 */
public class IdentificacaoSindicatoDuplicadaException extends Exception {
    private static final long serialVersionUID = 1L;

    public IdentificacaoSindicatoDuplicadaException() {
        super("Ha outro empregado com esta identificacao de sindicato");
    }
}
