package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando a comissão não possui formato numérico válido
 */
public class ComissaoNaoNumericaException extends Exception {
    public ComissaoNaoNumericaException() {
        super("Comissao deve ser numerica.");
    }
}
