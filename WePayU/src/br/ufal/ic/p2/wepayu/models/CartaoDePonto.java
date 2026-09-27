package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;

/**
 * Registro de horas trabalhadas por um empregado horista em um dia.
 *
 * <p>Compatível com os padrões de serialização JavaBeans e {@link java.beans.XMLEncoder}.
 */
public class CartaoDePonto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String data;
    private double horas;

    /**
     * Construtor padrão sem argumentos para serialização JavaBeans/XML.
     */
    public CartaoDePonto() {
    }

    /**
     * @param data  data do ponto no formato d/M/yyyy
     * @param horas total de horas trabalhadas no dia
     */
    public CartaoDePonto(String data, double horas) {
        this.data = data;
        this.horas = horas;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getHoras() {
        return horas;
    }

    public void setHoras(double horas) {
        this.horas = horas;
    }

    /**
     * @return até 8 horas normais trabalhadas
     */
    public double getHorasNormais() {
        return Math.min(horas, 8.0);
    }

    /**
     * @return horas que excederem o limite diário de 8 horas
     */
    public double getHorasExtras() {
        return Math.max(0.0, horas - 8.0);
    }
}
