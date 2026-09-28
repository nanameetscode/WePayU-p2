package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando não há comando a refazer
 */
public class NaoHaComandoARefazerException extends Exception {
    private static final long serialVersionUID = 1L;

    public NaoHaComandoARefazerException() {
        super("Nao ha comando a refazer.");
    }
}
