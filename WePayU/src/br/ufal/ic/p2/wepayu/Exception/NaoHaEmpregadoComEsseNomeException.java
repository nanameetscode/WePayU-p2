package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando nenhum empregado é encontrado pelo nome e índice fornecidos
 */
public class NaoHaEmpregadoComEsseNomeException extends Exception {
    public NaoHaEmpregadoComEsseNomeException() {
        super("Nao ha empregado com esse nome.");
    }
}
