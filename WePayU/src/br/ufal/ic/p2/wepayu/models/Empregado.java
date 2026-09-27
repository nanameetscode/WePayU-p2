package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhHoristaException;
import br.ufal.ic.p2.wepayu.utils.Formatador;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Entidade basica do sistema: alguem que recebe salario em uma empresa.
 */
public abstract class Empregado {

    private final String nome;
    private final String endereco;
    private boolean sindicalizado;
    private String idSindicato;
    private double taxaSindical;
    private java.util.List<TaxaServico> taxasServico = new java.util.ArrayList<>();

    protected Empregado(String nome, String endereco) {
        this.nome = nome;
        this.endereco = endereco;
        this.sindicalizado = false;
    }

    public abstract TipoEmpregado getTipo();

    public abstract double getSalario();

    public Map<String, String> getAtributos() {
        Map<String, String> atributos = new LinkedHashMap<>();
        atributos.put("nome", nome);
        atributos.put("endereco", endereco);
        atributos.put("tipo", getTipo().getRotulo());
        atributos.put("salario", Formatador.formatarMoeda(getSalario()));
        atributos.put("sindicalizado", String.valueOf(sindicalizado));
        return atributos;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public boolean isSindicalizado() {
        return sindicalizado;
    }

    public void setSindicalizado(boolean sindicalizado) {
        this.sindicalizado = sindicalizado;
    }

    public void lancarCartao(String data, double horas) throws EmpregadoNaoEhHoristaException {
        throw new EmpregadoNaoEhHoristaException();
    }

    public double getHorasNormais(LocalDate inicio, LocalDate fim) throws EmpregadoNaoEhHoristaException {
        throw new EmpregadoNaoEhHoristaException();
    }

    public double getHorasExtras(LocalDate inicio, LocalDate fim) throws EmpregadoNaoEhHoristaException {
        throw new EmpregadoNaoEhHoristaException();
    }

    public void lancarVenda(String data, double valor) throws br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhComissionadoException {
        throw new br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhComissionadoException();
    }

    public double getVendasRealizadas(LocalDate inicio, LocalDate fim) throws br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhComissionadoException {
        throw new br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhComissionadoException();
    }

    public String getIdSindicato() {
        return idSindicato;
    }

    public void setIdSindicato(String idSindicato) {
        this.idSindicato = idSindicato;
    }

    public double getTaxaSindical() {
        return taxaSindical;
    }

    public void setTaxaSindical(double taxaSindical) {
        this.taxaSindical = taxaSindical;
    }

    public java.util.List<TaxaServico> getTaxasServico() {
        return taxasServico;
    }

    public void setTaxasServico(java.util.List<TaxaServico> taxasServico) {
        this.taxasServico = taxasServico != null ? taxasServico : new java.util.ArrayList<>();
    }

    public void setDadosSindicato(boolean sindicalizado, String idSindicato, double taxaSindical) {
        this.sindicalizado = sindicalizado;
        this.idSindicato = sindicalizado ? idSindicato : null;
        this.taxaSindical = sindicalizado ? taxaSindical : 0.0;
        if (!sindicalizado) {
            this.taxasServico.clear();
        }
    }

    public void lancarTaxaServico(String data, double valor) {
        taxasServico.add(new TaxaServico(data, valor));
    }

    public double getTaxasServico(LocalDate inicio, LocalDate fim) throws br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhSindicalizadoException {
        if (!sindicalizado) {
            throw new br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhSindicalizadoException();
        }
        double total = 0;
        for (TaxaServico taxa : taxasServico) {
            LocalDate dataTaxa = Formatador.converterData(taxa.getData());
            if (dataTaxa != null && !dataTaxa.isBefore(inicio) && dataTaxa.isBefore(fim)) {
                total += taxa.getValor();
            }
        }
        return total;
    }

    public String getValorAtributo(String atributo) throws AtributoNaoExisteException, br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhSindicalizadoException {
        if ("idSindicato".equals(atributo) || "taxaSindical".equals(atributo)) {
            if (!isSindicalizado()) {
                throw new br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhSindicalizadoException();
            }
            if ("idSindicato".equals(atributo)) {
                return idSindicato;
            }
            return Formatador.formatarMoeda(taxaSindical);
        }
        String valor = getAtributos().get(atributo);
        if (valor == null) {
            throw new AtributoNaoExisteException();
        }
        return valor;
    }

    @Override
    public String toString() {
        return getTipo().getRotulo() + ": " + nome;
    }
}
