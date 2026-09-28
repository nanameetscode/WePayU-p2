package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Memento que encapsula uma fotografia completa do estado interno do sistema.
 */
public class SistemaSnapshot {

    private final Map<String, Empregado> empregados;
    private final int ultimoId;
    private final LocalDate ultimaDataFolhaProcessada;

    /**
     * @param empregados                mapa de empregados a ser clonado
     * @param ultimoId                  última chave sequencial gerada
     * @param ultimaDataFolhaProcessada última data em que a folha foi rodada
     */
    public SistemaSnapshot(Map<String, Empregado> empregados, int ultimoId, LocalDate ultimaDataFolhaProcessada) {
        this.empregados = new LinkedHashMap<>();
        for (Map.Entry<String, Empregado> entry : empregados.entrySet()) {
            this.empregados.put(entry.getKey(), entry.getValue().clonar());
        }
        this.ultimoId = ultimoId;
        this.ultimaDataFolhaProcessada = ultimaDataFolhaProcessada;
    }

    public Map<String, Empregado> getEmpregados() {
        return empregados;
    }

    public int getUltimoId() {
        return ultimoId;
    }

    public LocalDate getUltimaDataFolhaProcessada() {
        return ultimaDataFolhaProcessada;
    }
}
