package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando atributos bancários são consultados para empregado que não recebe em banco
 */
public class EmpregadoNaoRecebeEmBancoException extends Exception {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoRecebeEmBancoException() {
        super("Empregado nao recebe em banco.");
    }
}
