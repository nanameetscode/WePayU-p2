package br.ufal.ic.p2.wepayu.exceptions.validacao;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando as horas de um cartão de ponto não são um número positivo.
 */
public class HorasDevemSerPositivasException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public HorasDevemSerPositivasException() {
        super("Horas devem ser positivas.");
    }
}
