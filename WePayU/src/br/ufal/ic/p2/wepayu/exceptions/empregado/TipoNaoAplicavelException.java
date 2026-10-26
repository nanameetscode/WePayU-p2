package br.ufal.ic.p2.wepayu.exceptions.empregado;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando a comissão é informada para um tipo que não a aceita, ou omitida para o comissionado.
 */
public class TipoNaoAplicavelException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public TipoNaoAplicavelException() {
        super("Tipo nao aplicavel.");
    }
}
