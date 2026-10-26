package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.exceptions.empregado.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.exceptions.empregado.IdentificacaoEmpregadoNulaException;
import br.ufal.ic.p2.wepayu.exceptions.empregado.NaoHaEmpregadoComEsseNomeException;
import br.ufal.ic.p2.wepayu.exceptions.sindicato.IdentificacaoMembroNulaException;
import br.ufal.ic.p2.wepayu.exceptions.sindicato.IdentificacaoSindicatoDuplicadaException;
import br.ufal.ic.p2.wepayu.exceptions.agenda.AgendaJaExisteException;
import br.ufal.ic.p2.wepayu.exceptions.agenda.DescricaoDeAgendaInvalidaException;
import br.ufal.ic.p2.wepayu.exceptions.sindicato.MembroNaoExisteException;
import br.ufal.ic.p2.wepayu.models.agenda.AgendasDisponiveis;
import br.ufal.ic.p2.wepayu.models.empregado.Empregado;
import br.ufal.ic.p2.wepayu.utils.Validacao;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Todo o estado do sistema: os empregados, a numeração das identificações, as
 * agendas de pagamento que a empresa oferece e os relatórios das folhas já efetivadas.
 *
 * <p>O mapa de empregados nunca sai daqui para ser alterado: quem está de fora só
 * recebe vistas somente para leitura e usa os métodos desta classe.
 */
public class Cadastro {

    private final Map<String, Empregado> empregados = new LinkedHashMap<>();
    private int ultimaIdentificacao;
    private AgendasDisponiveis agendas = new AgendasDisponiveis();
    private final Map<LocalDate, String> relatoriosEmitidos = new LinkedHashMap<>();

    /**
     * @return a identificação dada ao empregado, escolhida automaticamente
     */
    public String adicionar(Empregado empregado) {
        String identificacao = String.valueOf(++ultimaIdentificacao);
        empregados.put(identificacao, empregado);
        return identificacao;
    }

    /**
     * @throws IdentificacaoEmpregadoNulaException se a identificação faltar
     * @throws EmpregadoNaoExisteException         se não houver empregado com ela
     */
    public Empregado buscar(String identificacao)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException {

        Validacao.exigirPreenchido(identificacao, IdentificacaoEmpregadoNulaException::new);
        Empregado empregado = empregados.get(identificacao);
        if (empregado == null) {
            throw new EmpregadoNaoExisteException();
        }
        return empregado;
    }

    public void remover(String identificacao) throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException {
        buscar(identificacao);
        empregados.remove(identificacao);
    }

    /**
     * Procura por parte do nome; o índice escolhe entre os empregados encontrados.
     *
     * @param indice posição do empregado entre os encontrados, começando em 1
     * @return a identificação do empregado
     */
    public String buscarPorNome(String nome, int indice) throws NaoHaEmpregadoComEsseNomeException {
        List<String> encontrados = nome == null ? List.of() : empregados.entrySet().stream()
                .filter(entrada -> entrada.getValue().getNome().contains(nome))
                .map(Map.Entry::getKey)
                .toList();
        if (indice < 1 || indice > encontrados.size()) {
            throw new NaoHaEmpregadoComEsseNomeException();
        }
        return encontrados.get(indice - 1);
    }

    /**
     * @param idSindicato identificação do empregado no sindicato
     * @throws IdentificacaoMembroNulaException se a identificação faltar
     * @throws MembroNaoExisteException         se nenhum filiado tiver essa identificação
     */
    public Empregado buscarMembro(String idSindicato)
            throws IdentificacaoMembroNulaException, MembroNaoExisteException {

        Validacao.exigirPreenchido(idSindicato, IdentificacaoMembroNulaException::new);
        return empregados.values().stream()
                .filter(empregado -> empregado.getSindicalizacao().possuiIdentificacao(idSindicato))
                .findFirst()
                .orElseThrow(MembroNaoExisteException::new);
    }

    /**
     * @param interessado empregado que quer usar a identificação sindical
     * @throws IdentificacaoSindicatoDuplicadaException se outro empregado já a usar
     */
    public void verificarIdentificacaoSindicalLivre(String idSindicato, Empregado interessado)
            throws IdentificacaoSindicatoDuplicadaException {

        boolean emUso = empregados.values().stream()
                .anyMatch(empregado -> empregado != interessado
                        && empregado.getSindicalizacao().possuiIdentificacao(idSindicato));
        if (emUso) {
            throw new IdentificacaoSindicatoDuplicadaException();
        }
    }

    /**
     * @return os empregados em ordem alfabética, como saem na folha
     */
    public List<Empregado> listarPorNome() {
        return empregados.values().stream().sorted(Comparator.comparing(Empregado::getNome)).toList();
    }

    public int getNumeroDeEmpregados() {
        return empregados.size();
    }

    /**
     * @return o relatório da folha do dia, se ela já foi efetivada
     */
    public Optional<String> getRelatorioEmitido(LocalDate dia) {
        return Optional.ofNullable(relatoriosEmitidos.get(dia));
    }

    /**
     * Guarda o relatório da folha efetivada no dia: rodar de novo essa folha deve
     * reproduzir o mesmo relatório (us7).
     */
    public void registrarRelatorioEmitido(LocalDate dia, String relatorio) {
        relatoriosEmitidos.put(dia, relatorio);
    }

    /**
     * @return as agendas de pagamento que a empresa oferece
     */
    public AgendasDisponiveis getAgendas() {
        return agendas;
    }

    /**
     * Passa a oferecer mais uma agenda de pagamento (US10).
     */
    public void criarAgenda(String descricao) throws DescricaoDeAgendaInvalidaException, AgendaJaExisteException {
        agendas = agendas.comNovaAgenda(descricao);
    }

    /**
     * Substitui o estado pelo que foi gravado. Sem agendas gravadas, valem as padrão.
     */
    public void carregar(DadosGravados dados) {
        limpar();
        empregados.putAll(copiar(dados.getEmpregados()));
        ultimaIdentificacao = dados.getUltimaIdentificacao();
        if (!dados.getAgendas().isEmpty()) {
            agendas = new AgendasDisponiveis(dados.getAgendas());
        }
        relatoriosEmitidos.putAll(dados.getRelatoriosEmitidos());
    }

    /**
     * @return cópia de todo o estado, para gravar no arquivo de dados
     */
    public DadosGravados exportar() {
        return new DadosGravados(copiar(empregados), ultimaIdentificacao, agendas.listar(), relatoriosEmitidos);
    }

    /**
     * Remove todos os empregados, as agendas criadas e os relatórios, e reinicia a numeração.
     */
    public void limpar() {
        empregados.clear();
        ultimaIdentificacao = 0;
        agendas = new AgendasDisponiveis();
        relatoriosEmitidos.clear();
    }

    /**
     * @return cópia independente de todo o estado
     */
    public Cadastro copiar() {
        Cadastro copia = new Cadastro();
        copia.restaurar(this);
        return copia;
    }

    /**
     * Torna este estado uma cópia independente do estado informado.
     */
    public void restaurar(Cadastro estado) {
        empregados.clear();
        empregados.putAll(copiar(estado.empregados));
        ultimaIdentificacao = estado.ultimaIdentificacao;
        agendas = estado.agendas;
        relatoriosEmitidos.clear();
        relatoriosEmitidos.putAll(estado.relatoriosEmitidos);
    }

    /**
     * @return cópias independentes dos empregados, na mesma ordem
     */
    private static Map<String, Empregado> copiar(Map<String, Empregado> originais) {
        Map<String, Empregado> copias = new LinkedHashMap<>();
        originais.forEach((identificacao, empregado) -> copias.put(identificacao, empregado.copiar()));
        return copias;
    }
}
