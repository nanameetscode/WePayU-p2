package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;

/**
 * Registro de resultado de venda realizada por um empregado comissionado.
 *
 * <p>Compatível com os padrões JavaBeans e serialização via {@link java.beans.XMLEncoder}.
 */
public class ResultadoVenda implements Serializable {
    private static final long serialVersionUID = 1L;

    private String data;
    private double valor;

    /**
     * Construtor padrão sem argumentos para serialização JavaBeans/XML.
     */
    public ResultadoVenda() {
    }

    /**
     * @param data  data da venda no formato d/M/yyyy
     * @param valor valor monetário da venda
     */
    public ResultadoVenda(String data, double valor) {
        this.data = data;
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
