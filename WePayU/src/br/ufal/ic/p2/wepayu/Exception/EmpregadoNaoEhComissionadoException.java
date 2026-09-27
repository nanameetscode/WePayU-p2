package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando uma operação exclusiva de comissionado é invocada
 * para um empregado de outro tipo
 */
public class EmpregadoNaoEhComissionadoException extends Exception {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoEhComissionadoException() {
        super("Empregado nao eh comissionado.");
    }
}
