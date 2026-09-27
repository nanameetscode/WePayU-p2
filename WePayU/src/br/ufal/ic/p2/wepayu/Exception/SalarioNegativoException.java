package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o salário informado é negativo
 */
public class SalarioNegativoException extends Exception {
    public SalarioNegativoException() {
        super("Salario deve ser nao-negativo.");
    }
}
