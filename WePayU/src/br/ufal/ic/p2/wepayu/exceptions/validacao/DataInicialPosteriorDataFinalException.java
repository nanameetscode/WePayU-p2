package br.ufal.ic.p2.wepayu.exceptions.validacao;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando o início de um intervalo de consulta vem depois do seu fim.
 */
public class DataInicialPosteriorDataFinalException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public DataInicialPosteriorDataFinalException() {
        super("Data inicial nao pode ser posterior aa data final.");
    }
}
