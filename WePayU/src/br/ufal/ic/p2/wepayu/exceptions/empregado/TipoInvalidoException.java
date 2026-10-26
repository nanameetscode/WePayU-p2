package br.ufal.ic.p2.wepayu.exceptions.empregado;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando o tipo de empregado informado não é reconhecido.
 */
public class TipoInvalidoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public TipoInvalidoException() {
        super("Tipo invalido.");
    }
}
