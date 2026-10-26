package br.ufal.ic.p2.wepayu.exceptions.sistema;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando o undo é pedido sem haver comando no histórico.
 */
public class NaoHaComandoADesfazerException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public NaoHaComandoADesfazerException() {
        super("Nao ha comando a desfazer.");
    }
}
