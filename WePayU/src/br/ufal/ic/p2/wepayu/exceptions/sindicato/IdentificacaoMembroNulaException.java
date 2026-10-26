package br.ufal.ic.p2.wepayu.exceptions.sindicato;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando a identificação do membro do sindicato não é informada.
 */
public class IdentificacaoMembroNulaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public IdentificacaoMembroNulaException() {
        super("Identificacao do membro nao pode ser nula.");
    }
}
