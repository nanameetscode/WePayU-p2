package br.ufal.ic.p2.wepayu.exceptions.empregado;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando nenhum empregado corresponde ao nome e ao índice procurados.
 */
public class NaoHaEmpregadoComEsseNomeException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public NaoHaEmpregadoComEsseNomeException() {
        super("Nao ha empregado com esse nome.");
    }
}
