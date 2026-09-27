package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o endereço informado é nulo ou vazio
 */
public class EnderecoNuloException extends Exception {
    public EnderecoNuloException() {
        super("Endereco nao pode ser nulo.");
    }
}
