package br.ufal.ic.p2.wepayu.exceptions.sindicato;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando não há filiado ao sindicato com a identificação informada.
 */
public class MembroNaoExisteException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public MembroNaoExisteException() {
        super("Membro nao existe.");
    }
}
