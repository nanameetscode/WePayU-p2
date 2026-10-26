package br.ufal.ic.p2.wepayu.exceptions.validacao;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando o campo numérico não contém um número.
 */
public class ValorNaoNumericoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    /**
     * @param campo campo que causou o erro; define a mensagem
     */
    public ValorNaoNumericoException(CampoNumerico campo) {
        super(campo.getMensagemNaoNumerico());
    }
}
