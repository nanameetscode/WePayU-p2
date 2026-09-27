package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ComissaoNaoNumericaException;
import br.ufal.ic.p2.wepayu.Exception.ComissaoNegativaException;
import br.ufal.ic.p2.wepayu.Exception.ComissaoNulaException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.EnderecoNuloException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoEmpregadoNulaException;
import br.ufal.ic.p2.wepayu.Exception.NaoHaEmpregadoComEsseNomeException;
import br.ufal.ic.p2.wepayu.Exception.NomeNuloException;
import br.ufal.ic.p2.wepayu.Exception.SalarioNaoNumericoException;
import br.ufal.ic.p2.wepayu.Exception.SalarioNegativoException;
import br.ufal.ic.p2.wepayu.Exception.SalarioNuloException;
import br.ufal.ic.p2.wepayu.Exception.TipoInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.TipoNaoAplicavelException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.persistence.RepositorioXML;
import br.ufal.ic.p2.wepayu.services.EmpregadoService;

import java.io.IOException;

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

    /**
     * Cria a fachada e recupera o cadastro gravado na execução anterior.
     */
    public Facade() {
        this.repositorio = new RepositorioXML();
        this.repositorio.carregar();
        this.empregados = new EmpregadoService();
        this.empregados.getEmpregados().putAll(repositorio.getEmpregados());
        this.empregados.restaurarUltimoId(maiorIdentificacao());
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
            TipoNaoAplicavelException {

        return empregados.criarEmpregado(nome, endereco, tipo, salario);
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
            TipoNaoAplicavelException {

        return empregados.criarEmpregado(nome, endereco, tipo, salario, comissao);
    }

    /**
     * Recupera um atributo do empregado.
     *
     * @return o valor do atributo, já formatado
     */
    public String getAtributoEmpregado(String emp, String atributo)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException {

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
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException {
        empregados.removerEmpregado(emp);
    }

    /**
     * Descarta o cadastro atual e apaga o arquivo de persistência.
     */
    public void zerarSistema() throws IOException {
        empregados.zerarSistema();
        repositorio.limpar();
    }

    /**
     * Grava o cadastro atual em arquivo para a próxima execução.
     */
    public void encerrarSistema() throws IOException {
        repositorio.getEmpregados().clear();
        repositorio.getEmpregados().putAll(empregados.getEmpregados());
        repositorio.gravar();
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
