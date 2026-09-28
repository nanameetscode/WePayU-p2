package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.AgenciaNulaException;
import br.ufal.ic.p2.wepayu.Exception.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.BancoNuloException;
import br.ufal.ic.p2.wepayu.Exception.ComissaoNaoNumericaException;
import br.ufal.ic.p2.wepayu.Exception.ComissaoNegativaException;
import br.ufal.ic.p2.wepayu.Exception.ComissaoNulaException;
import br.ufal.ic.p2.wepayu.Exception.ContaCorrenteNulaException;
import br.ufal.ic.p2.wepayu.Exception.DataFinalInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.DataInicialInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.DataInicialPosteriorDataFinalException;
import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhComissionadoException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhHoristaException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhSindicalizadoException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoRecebeEmBancoException;
import br.ufal.ic.p2.wepayu.Exception.EnderecoNuloException;
import br.ufal.ic.p2.wepayu.Exception.HorasDevemSerPositivasException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoEmpregadoNulaException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoMembroNulaException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoSindicatoDuplicadaException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoSindicatoNulaException;
import br.ufal.ic.p2.wepayu.Exception.MembroNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.MetodoPagamentoInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.NaoHaEmpregadoComEsseNomeException;
import br.ufal.ic.p2.wepayu.Exception.NomeNuloException;
import br.ufal.ic.p2.wepayu.Exception.SalarioNaoNumericoException;
import br.ufal.ic.p2.wepayu.Exception.SalarioNegativoException;
import br.ufal.ic.p2.wepayu.Exception.SalarioNuloException;
import br.ufal.ic.p2.wepayu.Exception.TaxaSindicalNaoNumericaException;
import br.ufal.ic.p2.wepayu.Exception.TaxaSindicalNegativaException;
import br.ufal.ic.p2.wepayu.Exception.TaxaSindicalNulaException;
import br.ufal.ic.p2.wepayu.Exception.TipoInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.TipoNaoAplicavelException;
import br.ufal.ic.p2.wepayu.Exception.ValorDeveSerPositivoException;
import br.ufal.ic.p2.wepayu.Exception.ValorTrueFalseException;
import br.ufal.ic.p2.wepayu.Exception.ComandoDepoisDeEncerrarException;
import br.ufal.ic.p2.wepayu.Exception.NaoHaComandoADesfazerException;
import br.ufal.ic.p2.wepayu.Exception.NaoHaComandoARefazerException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.SistemaSnapshot;
import br.ufal.ic.p2.wepayu.persistence.RepositorioXML;
import br.ufal.ic.p2.wepayu.services.CommandManager;
import br.ufal.ic.p2.wepayu.services.EmpregadoService;
import br.ufal.ic.p2.wepayu.services.FolhaService;

import java.io.IOException;
import java.util.Map;

/**
 * Ponto de entrada único do sistema, invocado por reflexão pelo EasyAccept.
 *
 * <p>Esta classe é a <strong>fachada</strong> do sistema: não contém regra de
 * negócio, apenas traduz o texto dos scripts em chamadas de serviço e delega o
 * trabalho. Nenhum outro ponto do sistema é alcançado pelos testes.
 *
 * <p><strong>Contrato com o EasyAccept.</strong> O framework localiza os métodos
 * públicos desta classe pelo nome e pela quantidade de parâmetros, e passa os
 * argumentos <em>por posição</em>, na ordem em que aparecem no script. Por isso:
 * <ul>
 *   <li>todo parâmetro é declarado como {@code String}, exceto o índice de
 *       {@code getEmpregadoPorNome}, que é {@code int};</li>
 *   <li>existem duas sobrecargas de {@code criarEmpregado}, porque o script
 *       informa comissão apenas para o comissionado;</li>
 *   <li>o valor devolvido é comparado pelo {@code toString()}, de modo que os
 *       atributos numéricos já saem formatados em
 *       {@link br.ufal.ic.p2.wepayu.utils.Formatador}.</li>
 * </ul>
 *
 * <p>A fachada precisa de construtor sem argumentos, pois o EasyAccept a instancia
 * por reflexão. É nele que o cadastro é recuperado do arquivo XML, o que permite
 * a um script continuar o estado deixado pelo script anterior.
 */
public class Facade {

    private final RepositorioXML repositorio;
    private final EmpregadoService empregados;
    private final FolhaService folhaService;
    private final CommandManager commandManager;

    /**
     * Cria a fachada e recupera o cadastro gravado na execução anterior.
     */
    public Facade() {
        this.repositorio = new RepositorioXML();
        this.repositorio.carregar();
        this.empregados = new EmpregadoService();
        this.empregados.getEmpregados().putAll(repositorio.getEmpregados());
        this.empregados.restaurarUltimoId(maiorIdentificacao());
        this.folhaService = new FolhaService(this.empregados);
        this.commandManager = new CommandManager();
    }

    /**
     * Cadastra um empregado que não recebe comissão.
     *
     * @return a identificação do empregado, escolhida automaticamente pelo sistema
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario)
            throws NomeNuloException, EnderecoNuloException, TipoInvalidoException,
            SalarioNuloException, SalarioNaoNumericoException, SalarioNegativoException,
            ComissaoNulaException, ComissaoNaoNumericaException, ComissaoNegativaException,
            TipoNaoAplicavelException, ComandoDepoisDeEncerrarException {

        commandManager.verificarNaoEncerrado();
        SistemaSnapshot antes = criarSnapshot();
        String id = empregados.criarEmpregado(nome, endereco, tipo, salario);
        SistemaSnapshot depois = criarSnapshot();
        commandManager.registrarComando(antes, depois);
        return id;
    }

    /**
     * Cadastra um empregado, com comissão ou sem ela.
     *
     * @return a identificação do empregado, escolhida automaticamente pelo sistema
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
            throws NomeNuloException, EnderecoNuloException, TipoInvalidoException,
            SalarioNuloException, SalarioNaoNumericoException, SalarioNegativoException,
            ComissaoNulaException, ComissaoNaoNumericaException, ComissaoNegativaException,
            TipoNaoAplicavelException, ComandoDepoisDeEncerrarException {

        commandManager.verificarNaoEncerrado();
        SistemaSnapshot antes = criarSnapshot();
        String id = empregados.criarEmpregado(nome, endereco, tipo, salario, comissao);
        SistemaSnapshot depois = criarSnapshot();
        commandManager.registrarComando(antes, depois);
        return id;
    }

    /**
     * Recupera um atributo do empregado.
     *
     * @return o valor do atributo, já formatado
     */
    public String getAtributoEmpregado(String emp, String atributo)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, EmpregadoNaoEhSindicalizadoException,
            EmpregadoNaoEhComissionadoException, EmpregadoNaoRecebeEmBancoException {

        return empregados.getAtributoEmpregado(emp, atributo);
    }

    /**
     * Localiza um empregado pelo nome e pela posição da ocorrência.
     *
     * @param indice posição da ocorrência, começando em 1
     * @return a identificação do empregado encontrado
     */
    public String getEmpregadoPorNome(String nome, int indice) throws NaoHaEmpregadoComEsseNomeException {
        return empregados.getEmpregadoPorNome(nome, indice);
    }

    /**
     * Remove um empregado do sistema pela sua identificação.
     *
     * @param emp identificação do empregado a remover
     */
    public void removerEmpregado(String emp)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            ComandoDepoisDeEncerrarException {
        commandManager.verificarNaoEncerrado();
        SistemaSnapshot antes = criarSnapshot();
        empregados.removerEmpregado(emp);
        SistemaSnapshot depois = criarSnapshot();
        commandManager.registrarComando(antes, depois);
    }

    /**
     * Registra um cartão de ponto para um empregado horista.
     */
    public void lancaCartao(String emp, String data, String horas)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhHoristaException, DataInvalidaException, HorasDevemSerPositivasException,
            ComandoDepoisDeEncerrarException {
        commandManager.verificarNaoEncerrado();
        SistemaSnapshot antes = criarSnapshot();
        empregados.lancaCartao(emp, data, horas);
        SistemaSnapshot depois = criarSnapshot();
        commandManager.registrarComando(antes, depois);
    }

    /**
     * Calcula as horas normais trabalhadas por um empregado horista em um intervalo.
     */
    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhHoristaException, DataInicialInvalidaException, DataFinalInvalidaException,
            DataInicialPosteriorDataFinalException {
        return empregados.getHorasNormaisTrabalhadas(emp, dataInicial, dataFinal);
    }

    /**
     * Alias para getHorasNormaisTrabalhadas
     */
    public String getHorasTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhHoristaException, DataInicialInvalidaException, DataFinalInvalidaException,
            DataInicialPosteriorDataFinalException {
        return getHorasNormaisTrabalhadas(emp, dataInicial, dataFinal);
    }

    /**
     * Calcula as horas extras trabalhadas por um empregado horista em um intervalo.
     */
    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhHoristaException, DataInicialInvalidaException, DataFinalInvalidaException,
            DataInicialPosteriorDataFinalException {
        return empregados.getHorasExtrasTrabalhadas(emp, dataInicial, dataFinal);
    }

    /**
     * Registra o resultado de uma venda para um empregado comissionado.
     */
    public void lancaVenda(String emp, String data, String valor)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhComissionadoException, DataInvalidaException, ValorDeveSerPositivoException,
            ComandoDepoisDeEncerrarException {
        commandManager.verificarNaoEncerrado();
        SistemaSnapshot antes = criarSnapshot();
        empregados.lancaVenda(emp, data, valor);
        SistemaSnapshot depois = criarSnapshot();
        commandManager.registrarComando(antes, depois);
    }

    /**
     * Calcula o total de vendas realizadas por um empregado comissionado em um intervalo.
     */
    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhComissionadoException, DataInicialInvalidaException, DataFinalInvalidaException,
            DataInicialPosteriorDataFinalException {
        return empregados.getVendasRealizadas(emp, dataInicial, dataFinal);
    }

    /**
     * Altera atributos simples do empregado (3 parâmetros).
     */
    public void alteraEmpregado(String emp, String atributo, String valor)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, NomeNuloException, EnderecoNuloException,
            TipoInvalidoException, SalarioNuloException, SalarioNaoNumericoException,
            SalarioNegativoException, ComissaoNulaException, ComissaoNaoNumericaException,
            ComissaoNegativaException, EmpregadoNaoEhComissionadoException,
            MetodoPagamentoInvalidoException, ValorTrueFalseException,
            IdentificacaoSindicatoNulaException, ComandoDepoisDeEncerrarException {
        commandManager.verificarNaoEncerrado();
        SistemaSnapshot antes = criarSnapshot();
        empregados.alteraEmpregado(emp, atributo, valor);
        SistemaSnapshot depois = criarSnapshot();
        commandManager.registrarComando(antes, depois);
    }

    /**
     * Altera tipo do empregado com comissão ou salário adicional (4 parâmetros).
     */
    public void alteraEmpregado(String emp, String atributo, String valor, String salOuComissao)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, TipoInvalidoException, ComissaoNulaException,
            ComissaoNaoNumericaException, ComissaoNegativaException, SalarioNuloException,
            SalarioNaoNumericoException, SalarioNegativoException, ComandoDepoisDeEncerrarException {
        commandManager.verificarNaoEncerrado();
        SistemaSnapshot antes = criarSnapshot();
        empregados.alteraEmpregado(emp, atributo, valor, salOuComissao);
        SistemaSnapshot depois = criarSnapshot();
        commandManager.registrarComando(antes, depois);
    }

    /**
     * Altera filiação sindical para sindicalizado com identificação e taxa sindical (5 parâmetros).
     */
    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, ValorTrueFalseException, IdentificacaoSindicatoNulaException,
            IdentificacaoSindicatoDuplicadaException, TaxaSindicalNulaException,
            TaxaSindicalNaoNumericaException, TaxaSindicalNegativaException, ComandoDepoisDeEncerrarException {
        commandManager.verificarNaoEncerrado();
        SistemaSnapshot antes = criarSnapshot();
        empregados.alteraEmpregado(emp, atributo, valor, idSindicato, taxaSindical);
        SistemaSnapshot depois = criarSnapshot();
        commandManager.registrarComando(antes, depois);
    }

    /**
     * Altera método de pagamento para depósito bancário (6 parâmetros).
     */
    public void alteraEmpregado(String emp, String atributo, String valor, String banco, String agencia, String contaCorrente)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, MetodoPagamentoInvalidoException,
            BancoNuloException, AgenciaNulaException, ContaCorrenteNulaException,
            ComandoDepoisDeEncerrarException {
        commandManager.verificarNaoEncerrado();
        SistemaSnapshot antes = criarSnapshot();
        empregados.alteraEmpregado(emp, atributo, valor, banco, agencia, contaCorrente);
        SistemaSnapshot depois = criarSnapshot();
        commandManager.registrarComando(antes, depois);
    }

    /**
     * Registra taxa de serviço cobrada a um membro do sindicato.
     */
    public void lancaTaxaServico(String membro, String data, String valor)
            throws IdentificacaoMembroNulaException, MembroNaoExisteException,
            DataInvalidaException, ValorDeveSerPositivoException, ComandoDepoisDeEncerrarException {
        commandManager.verificarNaoEncerrado();
        SistemaSnapshot antes = criarSnapshot();
        empregados.lancaTaxaServico(membro, data, valor);
        SistemaSnapshot depois = criarSnapshot();
        commandManager.registrarComando(antes, depois);
    }

    /**
     * Calcula o total de taxas de serviço cobradas a um empregado sindicalizado em um intervalo.
     */
    public String getTaxasServico(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhSindicalizadoException, DataInicialInvalidaException, DataFinalInvalidaException,
            DataInicialPosteriorDataFinalException {
        return empregados.getTaxasServico(emp, dataInicial, dataFinal);
    }

    /**
     * Calcula o total bruto da folha de pagamento na data especificada.
     */
    public String totalFolha(String data) throws DataInvalidaException {
        return folhaService.totalFolha(data);
    }

    /**
     * Emite a folha de pagamento na data especificada e gera o relatório no arquivo indicado.
     */
    public void rodaFolha(String data, String saida) throws DataInvalidaException, IOException, ComandoDepoisDeEncerrarException {
        commandManager.verificarNaoEncerrado();
        SistemaSnapshot antes = criarSnapshot();
        folhaService.rodaFolha(data, saida);
        SistemaSnapshot depois = criarSnapshot();
        commandManager.registrarComando(antes, depois);
    }

    /**
     * Descarta o cadastro atual e apaga o arquivo de persistência.
     */
    public void zerarSistema() throws IOException, ComandoDepoisDeEncerrarException {
        commandManager.verificarNaoEncerrado();
        SistemaSnapshot antes = criarSnapshot();
        empregados.zerarSistema();
        folhaService.limpar();
        repositorio.limpar();
        SistemaSnapshot depois = criarSnapshot();
        commandManager.registrarComando(antes, depois);
    }

    /**
     * Grava o cadastro atual em arquivo para a próxima execução.
     */
    public void encerrarSistema() throws IOException {
        commandManager.encerrar();
        repositorio.getEmpregados().clear();
        repositorio.getEmpregados().putAll(empregados.getEmpregados());
        repositorio.gravar();
    }

    /**
     * Retorna a quantidade total de empregados cadastrados no sistema.
     */
    public int getNumeroDeEmpregados() {
        return empregados.getEmpregados().size();
    }

    /**
     * Desfaz a última operação que modificou o estado do sistema.
     */
    public void undo() throws NaoHaComandoADesfazerException, ComandoDepoisDeEncerrarException {
        SistemaSnapshot snapshot = commandManager.undo();
        restaurarSnapshot(snapshot);
    }

    /**
     * Refaz a última operação desfeita.
     */
    public void redo() throws NaoHaComandoARefazerException, ComandoDepoisDeEncerrarException {
        SistemaSnapshot snapshot = commandManager.redo();
        restaurarSnapshot(snapshot);
    }

    private SistemaSnapshot criarSnapshot() {
        return new SistemaSnapshot(
                empregados.getEmpregados(),
                empregados.getUltimoId(),
                folhaService.getUltimaDataFolhaProcessada()
        );
    }

    private void restaurarSnapshot(SistemaSnapshot snapshot) {
        empregados.getEmpregados().clear();
        for (Map.Entry<String, Empregado> entry : snapshot.getEmpregados().entrySet()) {
            empregados.getEmpregados().put(entry.getKey(), entry.getValue().clonar());
        }
        empregados.restaurarUltimoId(snapshot.getUltimoId());
        folhaService.setUltimaDataFolhaProcessada(snapshot.getUltimaDataFolhaProcessada());
    }

    private int maiorIdentificacao() {
        int maior = 0;
        for (String identificacao : repositorio.getEmpregados().keySet()) {
            try {
                maior = Math.max(maior, Integer.parseInt(identificacao));
            } catch (NumberFormatException ignorada) {
                // Identificação fora do padrão numérico não altera a numeração.
            }
        }
        return maior;
    }
}
