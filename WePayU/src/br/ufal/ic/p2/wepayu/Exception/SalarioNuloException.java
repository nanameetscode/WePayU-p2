package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o salário informado é nulo ou vazio
 */
public class SalarioNuloException extends Exception {
    private static final long serialVersionUID = 1L;

    public SalarioNuloException() {
        super("Salario nao pode ser nulo.");
    }
}
