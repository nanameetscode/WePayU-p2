package br.ufal.ic.p2.wepayu.services;

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
import br.ufal.ic.p2.wepayu.models.TipoEmpregado;
import br.ufal.ic.p2.wepayu.utils.Formatador;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Regras de negócio do cadastro de empregados: valida os dados recebidos do script
 * de teste, delega a criação à {@link FabricaEmpregado} e guarda o resultado.
 *
 * <p>Este serviço não conhece subclasses concretas de {@link Empregado}: para
 * responder às consultas basta o mapa de atributos que o próprio empregado monta.
 */
public class EmpregadoService {

    /** Situações possíveis de um parâmetro numérico, na ordem em que são verificadas. */
    private enum StatusNumero {
        VALIDO, NULO, NAO_NUMERICO, NEGATIVO
    }

    /**
     * Resultado da análise de um parâmetro numérico recebido como texto.
     *
     * @param status se o texto é nulo, não numérico, negativo ou um valor válido
     * @param valor  o número convertido, significativo apenas quando o status é válido
     */
    private record Numero(StatusNumero status, double valor) { }

    private final Map<String, Empregado> empregados = new LinkedHashMap<>();
    private final FabricaEmpregado fabrica = new FabricaEmpregado();
    private int ultimoId;

    /**
     * Cadastra um empregado que não recebe comissão.
     *
     * <p>Variante utilizada pelos scripts que informam apenas nome, endereço, tipo e
     * salário. A comissão chega à validação como ausente, e é por isso que o tipo que
     * exige comissão é rejeitado com {@code Tipo nao aplicavel.}.
     *
     * @return a identificação do empregado, escolhida automaticamente pelo sistema
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario)
            throws NomeNuloException, EnderecoNuloException, TipoInvalidoException,
            SalarioNuloException, SalarioNaoNumericoException, SalarioNegativoException,
            ComissaoNulaException, ComissaoNaoNumericaException, ComissaoNegativaException,
            TipoNaoAplicavelException {

        return criarEmpregado(nome, endereco, tipo, salario, null);
    }

    /**
     * Cadastra um empregado, com comissão ou sem ela.
     *
     * <p>Os parâmetros chegam como texto porque é assim que o EasyAccept invoca a
     * fachada. A verificação segue a ordem em que os scripts esperam as mensagens:
     * nome, endereço, tipo, salário, comissão e, por último, a compatibilidade entre
     * o tipo e a comissão informada.
     *
     * @param comissao texto da comissão, ou {@code null} se o script não informou o parâmetro
     * @return a identificação do empregado, escolhida automaticamente pelo sistema
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
            throws NomeNuloException, EnderecoNuloException, TipoInvalidoException,
            SalarioNuloException, SalarioNaoNumericoException, SalarioNegativoException,
            ComissaoNulaException, ComissaoNaoNumericaException, ComissaoNegativaException,
            TipoNaoAplicavelException {

        if (nome == null || nome.isBlank()) {
            throw new NomeNuloException();
        }
        if (endereco == null || endereco.isBlank()) {
            throw new EnderecoNuloException();
        }

        TipoEmpregado tipoDoEmpregado = TipoEmpregado.fromRotulo(tipo);

        double salarioNumerico = validarSalario(salario);
        Double comissaoNumerica = comissao == null ? null : validarComissao(comissao);

        Empregado empregado = fabrica.criar(tipoDoEmpregado, nome, endereco, salarioNumerico, comissaoNumerica);

        String identificacao = String.valueOf(++ultimoId);
        empregados.put(identificacao, empregado);
        return identificacao;
    }

    /**
     * Recupera um atributo do empregado, já formatado como o script espera.
     *
     * @param emp      identificação do empregado
     * @param atributo nome do atributo consultado
     * @return o valor do atributo
     */
    public String getAtributoEmpregado(String emp, String atributo)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException {

        Empregado empregado = buscar(emp);
        String valor = empregado.getAtributos().get(atributo);
        if (valor == null) {
            throw new AtributoNaoExisteException();
        }
        return valor;
    }

    /**
     * Localiza um empregado pelo nome, contando as ocorrências a partir de um índice.
     *
     * A busca é por substring, e não por igualdade: pode haver mais de um empregado
     * cujo nome contenha o texto informado, e o índice é o que permite escolher qual
     * deles.
     *
     * @param nome   texto procurado no nome do empregado
     * @param indice posição da ocorrência, começando em 1
     * @return a identificação do empregado encontrado
     */
    public String getEmpregadoPorNome(String nome, int indice) throws NaoHaEmpregadoComEsseNomeException {
        List<String> identificacoes = new ArrayList<>();
        for (Map.Entry<String, Empregado> entry : empregados.entrySet()) {
            if (entry.getValue().getNome().contains(nome)) {
                identificacoes.add(entry.getKey());
            }
        }
        if (indice < 1 || indice > identificacoes.size()) {
            throw new NaoHaEmpregadoComEsseNomeException();
        }
        return identificacoes.get(indice - 1);
    }

    /**
     * Remove um empregado do sistema pela sua identificação.
     *
     * @param emp identificação do empregado a remover
     * @throws IdentificacaoEmpregadoNulaException se a identificação for nula ou vazia
     * @throws EmpregadoNaoExisteException         se o empregado não for encontrado
     */
    public void removerEmpregado(String emp)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException {
        buscar(emp);
        empregados.remove(emp);
    }

    /**
     * Remove todos os empregados cadastrados, reiniciando a numeração.
     */
    public void zerarSistema() {
        empregados.clear();
        ultimoId = 0;
    }

    /**
     * @return os empregados cadastrados, na ordem em que foram criados
     */
    public Map<String, Empregado> getEmpregados() {
        return empregados;
    }

    /**
     * Restaura a numeração de identificações após recarregar o sistema do disco.
     *
     * @param maiorId maior identificação presente no estado recuperado
     */
    public void restaurarUltimoId(int maiorId) {
        this.ultimoId = maiorId;
    }

    private Empregado buscar(String emp)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException {

        if (emp == null || emp.isBlank()) {
            throw new IdentificacaoEmpregadoNulaException();
        }
        Empregado empregado = empregados.get(emp);
        if (empregado == null) {
            throw new EmpregadoNaoExisteException();
        }
        return empregado;
    }

    private double validarSalario(String valor)
            throws SalarioNuloException, SalarioNaoNumericoException, SalarioNegativoException {

        Numero numero = classificar(valor);
        return switch (numero.status()) {
            case NULO -> throw new SalarioNuloException();
            case NAO_NUMERICO -> throw new SalarioNaoNumericoException();
            case NEGATIVO -> throw new SalarioNegativoException();
            case VALIDO -> numero.valor();
        };
    }

    private double validarComissao(String valor)
            throws ComissaoNulaException, ComissaoNaoNumericaException, ComissaoNegativaException {

        Numero numero = classificar(valor);
        return switch (numero.status()) {
            case NULO -> throw new ComissaoNulaException();
            case NAO_NUMERICO -> throw new ComissaoNaoNumericaException();
            case NEGATIVO -> throw new ComissaoNegativaException();
            case VALIDO -> numero.valor();
        };
    }

    /**
     * Analisa um parâmetro numérico recebido do script de teste.
     *
     * <p>Salário e comissão obedecem exatamente às mesmas três regras, mudando apenas
     * a exceção informada ao chamador. Classificar o texto uma única vez e deixar cada
     * campo responsável por traduzir o resultado em sua própria exceção evita duplicar
     * a análise sem precisar lançar exceção genérica alguma.
     */
    private Numero classificar(String valor) {
        if (valor == null || valor.isBlank()) {
            return new Numero(StatusNumero.NULO, 0);
        }
        Double convertido = Formatador.converterNumero(valor);
        if (convertido == null) {
            return new Numero(StatusNumero.NAO_NUMERICO, 0);
        }
        if (convertido < 0) {
            return new Numero(StatusNumero.NEGATIVO, 0);
        }
        return new Numero(StatusNumero.VALIDO, convertido);
    }
}
