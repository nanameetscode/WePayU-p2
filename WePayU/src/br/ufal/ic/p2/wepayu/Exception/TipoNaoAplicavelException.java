package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando o tipo é válido mas os parâmetros fornecidos
 * não são compatíveis (ex: comissão para horista, ou comissionado sem comissão)
 */
public class TipoNaoAplicavelException extends Exception {
    public TipoNaoAplicavelException() {
        super("Tipo nao aplicavel.");
    }
}
