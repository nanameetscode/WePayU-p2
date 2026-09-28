package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.Exception.ComandoDepoisDeEncerrarException;
import br.ufal.ic.p2.wepayu.Exception.NaoHaComandoADesfazerException;
import br.ufal.ic.p2.wepayu.Exception.NaoHaComandoARefazerException;
import br.ufal.ic.p2.wepayu.models.SistemaSnapshot;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Gerenciador de histórico de comandos e transações do sistema (Padrões Command e Memento).
 *
 * <p>Mantém as pilhas de desfazer (undo) e refazer (redo), garantindo consistência
 * e bloqueando execuções caso o sistema já tenha sido encerrado.
 */
public class CommandManager {

    private record Transacao(SistemaSnapshot antes, SistemaSnapshot depois) {}

    private final Deque<Transacao> undoStack = new ArrayDeque<>();
    private final Deque<Transacao> redoStack = new ArrayDeque<>();
    private boolean sistemaEncerrado = false;

    /**
     * Registra uma transação bem-sucedida no histórico de undo e limpa a pilha de redo.
     *
     * @param antes  estado do sistema imediatamente antes da execução
     * @param depois estado do sistema imediatamente após a execução
     * @throws ComandoDepoisDeEncerrarException se o sistema já foi encerrado
     */
    public void registrarComando(SistemaSnapshot antes, SistemaSnapshot depois) throws ComandoDepoisDeEncerrarException {
        verificarNaoEncerrado();
        undoStack.push(new Transacao(antes, depois));
        redoStack.clear();
    }

    /**
     * Desfaz o último comando executado, retornando o snapshot anterior.
     *
     * @return o snapshot a ser restaurado
     * @throws NaoHaComandoADesfazerException se a pilha de undo estiver vazia
     * @throws ComandoDepoisDeEncerrarException se o sistema já foi encerrado
     */
    public SistemaSnapshot undo() throws NaoHaComandoADesfazerException, ComandoDepoisDeEncerrarException {
        verificarNaoEncerrado();
        if (undoStack.isEmpty()) {
            throw new NaoHaComandoADesfazerException();
        }
        Transacao transacao = undoStack.pop();
        redoStack.push(transacao);
        return transacao.antes();
    }

    /**
     * Refaz o último comando desfeito, retornando o snapshot posterior.
     *
     * @return o snapshot a ser restaurado
     * @throws NaoHaComandoARefazerException se a pilha de redo estiver vazia
     * @throws ComandoDepoisDeEncerrarException se o sistema já foi encerrado
     */
    public SistemaSnapshot redo() throws NaoHaComandoARefazerException, ComandoDepoisDeEncerrarException {
        verificarNaoEncerrado();
        if (redoStack.isEmpty()) {
            throw new NaoHaComandoARefazerException();
        }
        Transacao transacao = redoStack.pop();
        undoStack.push(transacao);
        return transacao.depois();
    }

    /**
     * Marca o sistema como encerrado.
     */
    public void encerrar() {
        this.sistemaEncerrado = true;
    }

    /**
     * Valida que o sistema ainda se encontra aberto para novos comandos.
     *
     * @throws ComandoDepoisDeEncerrarException se o sistema já foi encerrado
     */
    public void verificarNaoEncerrado() throws ComandoDepoisDeEncerrarException {
        if (sistemaEncerrado) {
            throw new ComandoDepoisDeEncerrarException();
        }
    }

    /**
     * Reinicia o estado das pilhas de comandos.
     */
    public void limpar() {
        undoStack.clear();
        redoStack.clear();
        sistemaEncerrado = false;
    }
}
