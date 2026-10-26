package br.ufal.ic.p2.wepayu.exceptions;

/**
 * Classe-mãe de todos os erros de negócio do WePayU.
 *
 * <p>Cada subclasse representa uma única situação de erro e carrega a mensagem
 * exata que os scripts do EasyAccept esperam. Ter uma raiz comum permite que a
 * fachada e os serviços declarem e tratem "qualquer erro do sistema" de uma vez,
 * sem listar cada caso na assinatura.
 */
public abstract class WePayUException extends Exception {
    private static final long serialVersionUID = 1L;

    protected WePayUException(String mensagem) {
        super(mensagem);
    }

    protected WePayUException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
