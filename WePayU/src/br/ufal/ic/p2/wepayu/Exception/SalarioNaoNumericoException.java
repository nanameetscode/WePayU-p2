package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o salário não possui formato numérico válido
 */
public class SalarioNaoNumericoException extends Exception {
    private static final long serialVersionUID = 1L;

    public SalarioNaoNumericoException() {
        super("Salario deve ser numerico.");
    }
}
