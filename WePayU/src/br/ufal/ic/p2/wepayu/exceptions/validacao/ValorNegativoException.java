package br.ufal.ic.p2.wepayu.exceptions.validacao;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando o campo numérico recebe um valor negativo.
 */
public class ValorNegativoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    /**
     * @param campo campo que causou o erro; define a mensagem
     */
    public ValorNegativoException(CampoNumerico campo) {
        super(campo.getMensagemNegativo());
    }
}
