package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o endereço informado é nulo ou vazio
 */
public class EnderecoNuloException extends Exception {
    private static final long serialVersionUID = 1L;

    public EnderecoNuloException() {
        super("Endereco nao pode ser nulo.");
    }
}
