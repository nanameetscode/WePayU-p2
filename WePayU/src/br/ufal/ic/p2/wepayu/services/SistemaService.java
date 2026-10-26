package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;
import br.ufal.ic.p2.wepayu.exceptions.sistema.ArquivoException;
import br.ufal.ic.p2.wepayu.historico.HistoricoDeComandos;
import br.ufal.ic.p2.wepayu.historico.FotoDoEstado;
import br.ufal.ic.p2.wepayu.models.Cadastro;
import br.ufal.ic.p2.wepayu.persistence.RepositorioXML;

/**
 * Ciclo de vida do sistema: carrega e grava os dados, executa cada comando que
 * altera o estado como uma transação e mantém o undo/redo (padrão Command).
 *
 * <p>Um comando que falha no meio não deixa rastro: o estado volta a ser o de antes
 * dele e nada entra no histórico. Depois de {@code encerrarSistema}, comandos e
 * consultas são recusados.
 */
public class SistemaService {

    /**
     * Comando que altera o estado e devolve um resultado.
     */
    @FunctionalInterface
    public interface Comando<T> {
        T executar() throws WePayUException;
    }

    /**
     * Comando que altera o estado sem devolver resultado.
     */
    @FunctionalInterface
    public interface ComandoSemRetorno {
        void executar() throws WePayUException;
    }

    private final Cadastro cadastro;
    private final RepositorioXML repositorio;
    private final HistoricoDeComandos historico = new HistoricoDeComandos();

    /**
     * Recupera o estado gravado pela execução anterior.
     *
     * @throws ArquivoException se o arquivo de dados existir e não puder ser lido
     */
    public SistemaService(Cadastro cadastro, RepositorioXML repositorio) throws ArquivoException {
        this.cadastro = cadastro;
        this.repositorio = repositorio;
        cadastro.carregar(repositorio.carregar());
    }

    /**
     * Responde uma consulta, que não altera o estado nem entra no histórico.
     *
     * @return a resposta da consulta
     */
    public <T> T consultar(Comando<T> consulta) throws WePayUException {
        historico.verificarAberto();
        return consulta.executar();
    }

    /**
     * Executa o comando como uma transação e o registra no histórico.
     *
     * @return o resultado do comando
     */
    public <T> T executar(Comando<T> comando) throws WePayUException {
        historico.verificarAberto();
        FotoDoEstado antes = new FotoDoEstado(cadastro);
        try {
            T resultado = comando.executar();
            historico.registrar(antes, new FotoDoEstado(cadastro));
            return resultado;
        } catch (WePayUException | RuntimeException erro) {
            antes.restaurarEm(cadastro);
            throw erro;
        }
    }

    /**
     * Executa o comando como uma transação e o registra no histórico.
     */
    public void executar(ComandoSemRetorno comando) throws WePayUException {
        executar(() -> {
            comando.executar();
            return null;
        });
    }

    /**
     * Remove todos os empregados e agendas criadas. Como qualquer outro comando, só vai
     * para o arquivo de dados no {@code encerrarSistema}; até lá, pode ser desfeito.
     */
    public void zerar() throws WePayUException {
        executar(cadastro::limpar);
    }

    public void desfazer() throws WePayUException {
        historico.desfazer().restaurarEm(cadastro);
    }

    public void refazer() throws WePayUException {
        historico.refazer().restaurarEm(cadastro);
    }

    /**
     * Grava o estado para a próxima execução; depois disso nenhum comando, nem consulta,
     * é aceito. Se a gravação falhar, o sistema continua aberto.
     */
    public void encerrar() throws WePayUException {
        historico.verificarAberto();
        repositorio.gravar(cadastro.exportar());
        historico.encerrar();
    }
}
