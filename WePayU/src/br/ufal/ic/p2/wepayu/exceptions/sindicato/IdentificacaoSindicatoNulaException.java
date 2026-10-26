package br.ufal.ic.p2.wepayu.exceptions.sindicato;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando a filiação ao sindicato é pedida sem identificação sindical.
 */
public class IdentificacaoSindicatoNulaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public IdentificacaoSindicatoNulaException() {
        super("Identificacao do sindicato nao pode ser nula.");
    }
}
