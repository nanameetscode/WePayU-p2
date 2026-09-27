package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando a data inicial é posterior à data final informada
 */
public class DataInicialPosteriorDataFinalException extends Exception {
    private static final long serialVersionUID = 1L;

    public DataInicialPosteriorDataFinalException() {
        super("Data inicial nao pode ser posterior aa data final.");
    }
}
