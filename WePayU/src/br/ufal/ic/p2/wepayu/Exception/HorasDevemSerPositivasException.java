package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o número de horas informado não é positivo (> 0)
 */
public class HorasDevemSerPositivasException extends Exception {
    private static final long serialVersionUID = 1L;

    public HorasDevemSerPositivasException() {
        super("Horas devem ser positivas.");
    }
}
