package br.ufal.ic.p2.wepayu.Exception;

/**
 * exceção lançada quando um comando é solicitado após encerrar o sistema
 */
public class ComandoDepoisDeEncerrarException extends Exception {
    private static final long serialVersionUID = 1L;

    public ComandoDepoisDeEncerrarException() {
        super("Nao pode dar comandos depois de encerrarSistema.");
    }
}
