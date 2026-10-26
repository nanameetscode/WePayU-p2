package br.ufal.ic.p2.wepayu.exceptions.sindicato;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando a identificação sindical já pertence a outro empregado.
 */
public class IdentificacaoSindicatoDuplicadaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public IdentificacaoSindicatoDuplicadaException() {
        super("Ha outro empregado com esta identificacao de sindicato");
    }
}
