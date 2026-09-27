package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando uma operação exclusiva de horista é invocada
 * para um empregado de outro tipo
 */
public class EmpregadoNaoEhHoristaException extends Exception {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoEhHoristaException() {
        super("Empregado nao eh horista.");
    }
}
