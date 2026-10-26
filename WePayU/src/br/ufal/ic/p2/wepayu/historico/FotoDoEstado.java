package br.ufal.ic.p2.wepayu.historico;

import br.ufal.ic.p2.wepayu.models.Cadastro;

/**
 * Fotografia do estado do sistema num instante (padrão Memento).
 *
 * <p>A cópia é independente nos dois sentidos: alterar o sistema depois da foto não
 * muda a foto, e restaurar a foto não a consome, de modo que ela pode ser usada de
 * novo por um redo.
 */
public final class FotoDoEstado {

    private final Cadastro estado;

    /**
     * @param cadastro estado atual do sistema, que é copiado
     */
    public FotoDoEstado(Cadastro cadastro) {
        this.estado = cadastro.copiar();
    }

    /**
     * Devolve o cadastro ao estado desta foto.
     */
    public void restaurarEm(Cadastro cadastro) {
        cadastro.restaurar(estado);
    }
}
