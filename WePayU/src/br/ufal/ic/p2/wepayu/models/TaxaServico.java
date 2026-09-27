package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;

/**
 * Registro de taxa de serviço cobrada pelo sindicato a um empregado sindicalizado.
 *
 * <p>Compatível com os padrões JavaBeans e serialização via {@link java.beans.XMLEncoder}.
 */
public class TaxaServico implements Serializable {
    private static final long serialVersionUID = 1L;

    private String data;
    private double valor;

    /**
     * Construtor padrão sem argumentos para serialização JavaBeans/XML.
     */
    public TaxaServico() {
    }

    /**
     * @param data  data da taxa de serviço no formato d/M/yyyy
     * @param valor valor monetário da taxa
     */
    public TaxaServico(String data, double valor) {
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

    private boolean cobrada;

    public boolean isCobrada() {
        return cobrada;
    }

    public void setCobrada(boolean cobrada) {
        this.cobrada = cobrada;
    }
}
