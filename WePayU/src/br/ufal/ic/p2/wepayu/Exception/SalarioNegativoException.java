package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o salário informado é negativo
 */
public class SalarioNegativoException extends Exception {
    private static final long serialVersionUID = 1L;

    public SalarioNegativoException() {
        super("Salario deve ser nao-negativo.");
    }
}
