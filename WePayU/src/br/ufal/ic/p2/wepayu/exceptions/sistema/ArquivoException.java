package br.ufal.ic.p2.wepayu.exceptions.sistema;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando um arquivo do sistema (dados gravados ou relatório da folha)
 * não pode ser lido ou escrito. A causa original fica preservada.
 */
public class ArquivoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    /**
     * @param mensagem descrição do que falhou, incluindo o nome do arquivo
     * @param causa    erro de entrada e saída que originou a falha
     */
    public ArquivoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
