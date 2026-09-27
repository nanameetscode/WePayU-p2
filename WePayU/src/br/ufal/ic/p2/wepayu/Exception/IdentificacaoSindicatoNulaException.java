package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando a identificação do sindicato não é informada
 */
public class IdentificacaoSindicatoNulaException extends Exception {
    private static final long serialVersionUID = 1L;

    public IdentificacaoSindicatoNulaException() {
        super("Identificacao do sindicato nao pode ser nula.");
    }
}
