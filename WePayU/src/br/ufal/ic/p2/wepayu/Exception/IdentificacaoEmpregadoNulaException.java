package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o ID do empregado informado é nulo ou vazio
 */
public class IdentificacaoEmpregadoNulaException extends Exception {
    private static final long serialVersionUID = 1L;

    public IdentificacaoEmpregadoNulaException() {
        super("Identificacao do empregado nao pode ser nula.");
    }
}
