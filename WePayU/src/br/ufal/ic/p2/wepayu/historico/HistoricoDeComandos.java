package br.ufal.ic.p2.wepayu.historico;

import br.ufal.ic.p2.wepayu.exceptions.sistema.ComandoDepoisDeEncerrarException;
import br.ufal.ic.p2.wepayu.exceptions.sistema.NaoHaComandoADesfazerException;
import br.ufal.ic.p2.wepayu.exceptions.sistema.NaoHaComandoARefazerException;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Histórico dos comandos que alteraram o sistema, para desfazer (undo) e refazer (redo).
 *
 * <p>Cada comando fica guardado como o par de fotos do estado antes e depois dele.
 * Um comando novo descarta o que podia ser refeito. Depois de encerrado, o histórico
 * recusa novos comandos.
 */
public class HistoricoDeComandos {

    private record Transacao(FotoDoEstado antes, FotoDoEstado depois) {
    }

    private final Deque<Transacao> desfaziveis = new ArrayDeque<>();
    private final Deque<Transacao> refaziveis = new ArrayDeque<>();
    private boolean encerrado;

    /**
     * @throws ComandoDepoisDeEncerrarException se o sistema já foi encerrado
     */
    public void verificarAberto() throws ComandoDepoisDeEncerrarException {
        if (encerrado) {
            throw new ComandoDepoisDeEncerrarException();
        }
    }

    /**
     * Guarda um comando concluído.
     *
     * @param antes  estado antes do comando
     * @param depois estado depois do comando
     */
    public void registrar(FotoDoEstado antes, FotoDoEstado depois) {
        desfaziveis.push(new Transacao(antes, depois));
        refaziveis.clear();
    }

    /**
     * @return o estado a restaurar para desfazer o último comando
     */
    public FotoDoEstado desfazer() throws ComandoDepoisDeEncerrarException, NaoHaComandoADesfazerException {
        verificarAberto();
        if (desfaziveis.isEmpty()) {
            throw new NaoHaComandoADesfazerException();
        }
        Transacao transacao = desfaziveis.pop();
        refaziveis.push(transacao);
        return transacao.antes();
    }

    /**
     * @return o estado a restaurar para refazer o último comando desfeito
     */
    public FotoDoEstado refazer() throws ComandoDepoisDeEncerrarException, NaoHaComandoARefazerException {
        verificarAberto();
        if (refaziveis.isEmpty()) {
            throw new NaoHaComandoARefazerException();
        }
        Transacao transacao = refaziveis.pop();
        desfaziveis.push(transacao);
        return transacao.depois();
    }

    /**
     * A partir daqui nenhum comando é aceito.
     */
    public void encerrar() {
        encerrado = true;
    }
}
