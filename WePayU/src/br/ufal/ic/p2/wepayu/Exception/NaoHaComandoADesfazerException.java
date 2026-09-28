package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando não há comando a desfazer
 */
public class NaoHaComandoADesfazerException extends Exception {
    private static final long serialVersionUID = 1L;

    public NaoHaComandoADesfazerException() {
        super("Nao ha comando a desfazer.");
    }
}
