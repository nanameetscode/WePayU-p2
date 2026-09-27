package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o salário informado é nulo ou vazio
 */
public class SalarioNuloException extends Exception {
    public SalarioNuloException() {
        super("Salario nao pode ser nulo.");
    }
}
