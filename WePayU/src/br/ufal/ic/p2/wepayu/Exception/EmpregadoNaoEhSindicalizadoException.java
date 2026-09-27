package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando uma operação sindical é requisitada para um empregado não filiado ao sindicato
 */
public class EmpregadoNaoEhSindicalizadoException extends Exception {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoEhSindicalizadoException() {
        super("Empregado nao eh sindicalizado.");
    }
}
