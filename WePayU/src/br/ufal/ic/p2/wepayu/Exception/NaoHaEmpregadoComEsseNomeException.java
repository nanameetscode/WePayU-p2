package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando nenhum empregado é encontrado pelo nome e índice fornecidos
 */
public class NaoHaEmpregadoComEsseNomeException extends Exception {
    private static final long serialVersionUID = 1L;

    public NaoHaEmpregadoComEsseNomeException() {
        super("Nao ha empregado com esse nome.");
    }
}
