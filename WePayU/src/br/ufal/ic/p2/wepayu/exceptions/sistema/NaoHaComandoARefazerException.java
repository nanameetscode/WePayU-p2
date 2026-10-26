package br.ufal.ic.p2.wepayu.exceptions.sistema;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando o redo é pedido sem haver comando desfeito.
 */
public class NaoHaComandoARefazerException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public NaoHaComandoARefazerException() {
        super("Nao ha comando a refazer.");
    }
}
