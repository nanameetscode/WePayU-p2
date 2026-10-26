package br.ufal.ic.p2.wepayu.exceptions.sistema;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando um comando chega depois de o sistema ter sido encerrado.
 */
public class ComandoDepoisDeEncerrarException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public ComandoDepoisDeEncerrarException() {
        super("Nao pode dar comandos depois de encerrarSistema.");
    }
}
