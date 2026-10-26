package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.models.agenda.AgendaDePagamento;
import br.ufal.ic.p2.wepayu.models.empregado.Empregado;

import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * O que vai para o arquivo de dados entre uma execução e outra: empregados, numeração
 * das identificações, agendas oferecidas e relatórios das folhas já efetivadas.
 *
 * <p>É uma cópia do {@link Cadastro} feita para gravar e ler; alterá-la não muda o sistema.
 */
public class DadosGravados {

    private final Map<String, Empregado> empregados;
    private final int ultimaIdentificacao;
    private final List<AgendaDePagamento> agendas;
    private final Map<LocalDate, String> relatoriosEmitidos;

    /**
     * Sem nada gravado: a primeira execução do sistema.
     */
    public DadosGravados() {
        this(Map.of(), 0, List.of(), Map.of());
    }

    @ConstructorProperties({"empregados", "ultimaIdentificacao", "agendas", "relatoriosEmitidos"})
    public DadosGravados(Map<String, Empregado> empregados, int ultimaIdentificacao,
            List<AgendaDePagamento> agendas, Map<LocalDate, String> relatoriosEmitidos) {

        this.empregados = Collections.unmodifiableMap(new LinkedHashMap<>(empregados));
        this.ultimaIdentificacao = ultimaIdentificacao;
        this.agendas = Collections.unmodifiableList(new ArrayList<>(agendas));
        this.relatoriosEmitidos = Collections.unmodifiableMap(new LinkedHashMap<>(relatoriosEmitidos));
    }

    public Map<String, Empregado> getEmpregados() {
        return empregados;
    }

    public int getUltimaIdentificacao() {
        return ultimaIdentificacao;
    }

    public List<AgendaDePagamento> getAgendas() {
        return agendas;
    }

    public Map<LocalDate, String> getRelatoriosEmitidos() {
        return relatoriosEmitidos;
    }
}
