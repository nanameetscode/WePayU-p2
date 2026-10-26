package br.ufal.ic.p2.wepayu.exceptions.validacao;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando um atributo booleano recebe valor diferente de true ou false.
 */
public class ValorTrueFalseException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public ValorTrueFalseException() {
        super("Valor deve ser true ou false.");
    }
}
