package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o tipo de empregado informado não é reconhecido
 */
public class TipoInvalidoException extends Exception {
    public TipoInvalidoException() {
        super("Tipo invalido.");
    }
}
