package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando a identificação do membro do sindicato é nula ou vazia
 */
public class IdentificacaoMembroNulaException extends Exception {
    private static final long serialVersionUID = 1L;

    public IdentificacaoMembroNulaException() {
        super("Identificacao do membro nao pode ser nula.");
    }
}
