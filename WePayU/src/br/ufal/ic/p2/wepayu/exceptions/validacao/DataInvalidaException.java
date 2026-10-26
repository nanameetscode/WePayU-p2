package br.ufal.ic.p2.wepayu.exceptions.validacao;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando uma data não existe no calendário ou não está no formato d/M/aaaa.
 */
public class DataInvalidaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    /**
     * @param campo campo de data que causou o erro; define a mensagem
     */
    public DataInvalidaException(CampoData campo) {
        super(campo.getMensagemInvalida());
    }
}
