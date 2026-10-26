package br.ufal.ic.p2.wepayu.exceptions.validacao;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;

/**
 * Lançada quando o valor de uma venda ou de uma taxa de serviço não é um número positivo.
 */
public class ValorDeveSerPositivoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public ValorDeveSerPositivoException() {
        super("Valor deve ser positivo.");
    }
}
