package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando a conta corrente não é informada
 */
public class ContaCorrenteNulaException extends Exception {
    private static final long serialVersionUID = 1L;

    public ContaCorrenteNulaException() {
        super("Conta corrente nao pode ser nulo.");
    }
}
