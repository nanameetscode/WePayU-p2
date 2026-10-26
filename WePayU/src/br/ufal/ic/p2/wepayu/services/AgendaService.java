package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;
import br.ufal.ic.p2.wepayu.models.Cadastro;

/**
 * Criação de novas agendas de pagamento pela direção da empresa (US10).
 */
public class AgendaService {

    private final Cadastro cadastro;

    public AgendaService(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    /**
     * @param descricao descrição da agenda, como "semanal 3 3" ou "mensal 7"
     */
    public void criarAgendaDePagamentos(String descricao) throws WePayUException {
        cadastro.criarAgenda(descricao);
    }
}
