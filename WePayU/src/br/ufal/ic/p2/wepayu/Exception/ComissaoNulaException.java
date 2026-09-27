package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando a comissão informada é nula ou vazia
 */
public class ComissaoNulaException extends Exception {
    private static final long serialVersionUID = 1L;

    public ComissaoNulaException() {
        super("Comissao nao pode ser nula.");
    }
}
