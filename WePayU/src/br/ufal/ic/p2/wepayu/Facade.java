package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;
import br.ufal.ic.p2.wepayu.exceptions.sistema.ArquivoException;
import br.ufal.ic.p2.wepayu.models.Cadastro;
import br.ufal.ic.p2.wepayu.persistence.RepositorioXML;
import br.ufal.ic.p2.wepayu.services.AgendaService;
import br.ufal.ic.p2.wepayu.services.AlteracaoService;
import br.ufal.ic.p2.wepayu.services.EmpregadoService;
import br.ufal.ic.p2.wepayu.services.FolhaService;
import br.ufal.ic.p2.wepayu.services.LancamentoService;
import br.ufal.ic.p2.wepayu.services.SistemaService;

/**
 * Fachada do sistema, o único ponto alcançado pelos scripts do EasyAccept (padrão Façade).
 *
 * <p>Cada método só encaminha o pedido ao serviço responsável. Os comandos que alteram
 * o estado passam por {@link SistemaService#executar}, que cuida da transação e do
 * undo/redo; as consultas passam por {@link SistemaService#consultar}, que só confere
 * se o sistema ainda está aberto.
 *
 * <p>O EasyAccept instancia a fachada pelo construtor sem argumentos e passa os
 * parâmetros por posição, todos como texto (exceto o índice de
 * {@link #getEmpregadoPorNome}). Os erros chegam aos scripts pela mensagem da
 * {@link WePayUException} lançada.
 */
public class Facade {

    private final SistemaService sistema;
    private final EmpregadoService empregados;
    private final AlteracaoService alteracoes;
    private final LancamentoService lancamentos;
    private final FolhaService folha;
    private final AgendaService agendas;

    /**
     * Monta os serviços sobre o estado gravado pela execução anterior.
     *
     * @throws ArquivoException se o arquivo de dados existir e não puder ser lido
     */
    public Facade() throws ArquivoException {
        Cadastro cadastro = new Cadastro();
        this.sistema = new SistemaService(cadastro, new RepositorioXML());
        this.empregados = new EmpregadoService(cadastro);
        this.alteracoes = new AlteracaoService(cadastro);
        this.lancamentos = new LancamentoService(cadastro);
        this.folha = new FolhaService(cadastro);
        this.agendas = new AgendaService(cadastro);
    }

    // ---- US1, US2: cadastro ----

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws WePayUException {
        return sistema.executar(() -> empregados.criarEmpregado(nome, endereco, tipo, salario));
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
            throws WePayUException {

        return sistema.executar(() -> empregados.criarEmpregado(nome, endereco, tipo, salario, comissao));
    }

    public void removerEmpregado(String emp) throws WePayUException {
        sistema.executar(() -> empregados.removerEmpregado(emp));
    }

    public String getAtributoEmpregado(String emp, String atributo) throws WePayUException {
        return sistema.consultar(() -> empregados.getAtributoEmpregado(emp, atributo));
    }

    public String getEmpregadoPorNome(String nome, int indice) throws WePayUException {
        return sistema.consultar(() -> empregados.getEmpregadoPorNome(nome, indice));
    }

    public int getNumeroDeEmpregados() throws WePayUException {
        return sistema.consultar(() -> empregados.getNumeroDeEmpregados());
    }

    // ---- US3, US4, US5: lançamentos ----

    public void lancaCartao(String emp, String data, String horas) throws WePayUException {
        sistema.executar(() -> lancamentos.lancaCartao(emp, data, horas));
    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws WePayUException {

        return sistema.consultar(() -> lancamentos.getHorasNormaisTrabalhadas(emp, dataInicial, dataFinal));
    }

    /**
     * Nome com que o enunciado lista a consulta de horas normais na linguagem de script.
     */
    public String getHorasTrabalhadas(String emp, String dataInicial, String dataFinal) throws WePayUException {
        return sistema.consultar(() -> lancamentos.getHorasNormaisTrabalhadas(emp, dataInicial, dataFinal));
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws WePayUException {

        return sistema.consultar(() -> lancamentos.getHorasExtrasTrabalhadas(emp, dataInicial, dataFinal));
    }

    public void lancaVenda(String emp, String data, String valor) throws WePayUException {
        sistema.executar(() -> lancamentos.lancaVenda(emp, data, valor));
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws WePayUException {
        return sistema.consultar(() -> lancamentos.getVendasRealizadas(emp, dataInicial, dataFinal));
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws WePayUException {
        sistema.executar(() -> lancamentos.lancaTaxaServico(membro, data, valor));
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws WePayUException {
        return sistema.consultar(() -> lancamentos.getTaxasServico(emp, dataInicial, dataFinal));
    }

    // ---- US6: alteração ----

    public void alteraEmpregado(String emp, String atributo, String valor) throws WePayUException {
        sistema.executar(() -> alteracoes.alteraEmpregado(emp, atributo, valor));
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String salarioOuComissao)
            throws WePayUException {

        sistema.executar(() -> alteracoes.alteraEmpregado(emp, atributo, valor, salarioOuComissao));
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical)
            throws WePayUException {

        sistema.executar(() -> alteracoes.alteraEmpregado(emp, atributo, valor, idSindicato, taxaSindical));
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String banco, String agencia,
            String contaCorrente) throws WePayUException {

        sistema.executar(() -> alteracoes.alteraEmpregado(emp, atributo, valor, banco, agencia, contaCorrente));
    }

    // ---- US7: folha ----

    public String totalFolha(String data) throws WePayUException {
        return sistema.consultar(() -> folha.totalFolha(data));
    }

    public void rodaFolha(String data, String saida) throws WePayUException {
        sistema.executar(() -> folha.rodaFolha(data, saida));
    }

    // ---- US9, US10: agendas de pagamento ----

    public void criarAgendaDePagamentos(String descricao) throws WePayUException {
        sistema.executar(() -> agendas.criarAgendaDePagamentos(descricao));
    }

    // ---- US8 e ciclo de vida ----

    public void undo() throws WePayUException {
        sistema.desfazer();
    }

    public void redo() throws WePayUException {
        sistema.refazer();
    }

    public void zerarSistema() throws WePayUException {
        sistema.zerar();
    }

    public void encerrarSistema() throws WePayUException {
        sistema.encerrar();
    }
}
