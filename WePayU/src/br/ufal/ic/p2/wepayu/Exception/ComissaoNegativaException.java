package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando a comissão informada é negativa
 */
public class ComissaoNegativaException extends Exception {
    private static final long serialVersionUID = 1L;

    public ComissaoNegativaException() {
        super("Comissao deve ser nao-negativa.");
    }
}
