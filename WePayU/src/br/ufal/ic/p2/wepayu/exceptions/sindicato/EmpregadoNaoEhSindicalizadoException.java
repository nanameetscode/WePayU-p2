package br.ufal.ic.p2.wepayu.exceptions.sindicato;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando uma operação do sindicato é pedida para quem não é filiado.
 */
public class EmpregadoNaoEhSindicalizadoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoEhSindicalizadoException() {
        super("Empregado nao eh sindicalizado.");
    }
}
