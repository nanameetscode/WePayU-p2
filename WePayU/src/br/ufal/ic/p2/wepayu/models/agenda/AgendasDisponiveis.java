package br.ufal.ic.p2.wepayu.models.agenda;

import br.ufal.ic.p2.wepayu.exceptions.agenda.AgendaJaExisteException;
import br.ufal.ic.p2.wepayu.exceptions.agenda.AgendaNaoDisponivelException;
import br.ufal.ic.p2.wepayu.exceptions.agenda.DescricaoDeAgendaInvalidaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Agendas de pagamento que a empresa oferece aos empregados.
 *
 * <p>Começa com as três agendas padrão, uma para cada tipo de empregado (US9), e a
 * direção pode criar outras (US10). Duas descrições da mesma agenda, como "semanal 5"
 * e "semanal 1 5", são a mesma agenda. A lista é imutável: criar uma agenda devolve
 * uma nova lista.
 */
public final class AgendasDisponiveis {

    /** Toda sexta-feira: padrão dos horistas. */
    public static final AgendaDePagamento SEMANAL = AgendaSemanal.TODA_SEXTA;

    /** Último dia útil do mês: padrão dos assalariados. */
    public static final AgendaDePagamento MENSAL = new AgendaUltimoDiaUtil();

    /** A cada duas sextas-feiras: padrão dos comissionados. */
    public static final AgendaDePagamento BISSEMANAL = AgendaSemanal.A_CADA_DUAS_SEXTAS;

    private final List<AgendaDePagamento> agendas;

    /**
     * Só as agendas padrão.
     */
    public AgendasDisponiveis() {
        this(List.of(SEMANAL, MENSAL, BISSEMANAL));
    }

    /**
     * @param agendas agendas oferecidas, como gravadas no arquivo de dados
     */
    public AgendasDisponiveis(List<AgendaDePagamento> agendas) {
        this.agendas = List.copyOf(agendas);
    }

    /**
     * @param descricao descrição da agenda escolhida pelo empregado
     * @return a agenda oferecida com essa descrição
     * @throws AgendaNaoDisponivelException se a empresa não oferece essa agenda
     */
    public AgendaDePagamento buscar(String descricao) throws AgendaNaoDisponivelException {
        try {
            return procurar(DescricaoDeAgenda.interpretar(descricao)).orElseThrow(AgendaNaoDisponivelException::new);
        } catch (DescricaoDeAgendaInvalidaException descricaoInvalida) {
            throw new AgendaNaoDisponivelException();
        }
    }

    /**
     * @param descricao descrição da nova agenda
     * @return as agendas oferecidas mais a nova
     * @throws DescricaoDeAgendaInvalidaException se a descrição não seguir o formato
     * @throws AgendaJaExisteException            se a empresa já oferece essa agenda
     */
    public AgendasDisponiveis comNovaAgenda(String descricao)
            throws DescricaoDeAgendaInvalidaException, AgendaJaExisteException {

        AgendaDePagamento nova = DescricaoDeAgenda.interpretar(descricao);
        if (procurar(nova).isPresent()) {
            throw new AgendaJaExisteException();
        }
        List<AgendaDePagamento> todas = new ArrayList<>(agendas);
        todas.add(nova);
        return new AgendasDisponiveis(todas);
    }

    /**
     * @return as agendas oferecidas, somente para leitura
     */
    public List<AgendaDePagamento> listar() {
        return agendas;
    }

    private Optional<AgendaDePagamento> procurar(AgendaDePagamento agenda) {
        return agendas.stream()
                .filter(oferecida -> oferecida.getDescricao().equals(agenda.getDescricao()))
                .findFirst();
    }
}
