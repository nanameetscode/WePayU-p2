package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.utils.Formatador;

import java.beans.ConstructorProperties;
import java.util.Map;

/**
 * Empregado que recebe salário mensal acrescido de comissão sobre as vendas.
 *
 * <p>É o único tipo que possui dado próprio além do salário — a taxa de comissão.
 * O atributo é publicado sobrescrevendo {@link #getAtributos()}: a subclasse
 * acrescenta a sua entrada ao mapa montado pela classe base, de modo que o
 * atributo {@code comissao} simplesmente não existe para os outros tipos. É essa
 * ausência que faz o script de teste receber {@code Tipo nao aplicavel.} ao
 * tentar informar comissão para um horista, sem nenhuma verificação de tipo.
 */
public class EmpregadoComissionado extends Empregado {

    private double salarioMensal;
    private double taxaDeComissao;

    /**
     * @param nome            nome do empregado
     * @param endereco        endereço do empregado
     * @param salarioMensal   salário base mensal
     * @param taxaDeComissao  percentual de comissão sobre as vendas
     */
    @ConstructorProperties({"nome", "endereco", "salarioMensal", "taxaDeComissao"})
    public EmpregadoComissionado(String nome, String endereco, double salarioMensal, double taxaDeComissao) {
        super(nome, endereco);
        this.salarioMensal = salarioMensal;
        this.taxaDeComissao = taxaDeComissao;
    }

    @Override
    public TipoEmpregado getTipo() {
        return TipoEmpregado.COMISSIONADO;
    }

    @Override
    public double getSalario() {
        return salarioMensal;
    }

    @Override
    public void setSalario(double salario) {
        this.salarioMensal = salario;
    }

    @Override
    public void setComissao(double comissao) {
        this.taxaDeComissao = comissao;
    }

    @Override
    public String getComissaoFormatada() {
        return Formatador.formatarMoeda(taxaDeComissao);
    }

    @Override
    public void verificarComissionado() {
        // Sucesso: este empregado é comissionado.
    }

    @Override
    public Map<String, String> getAtributos() {
        Map<String, String> atributos = super.getAtributos();
        atributos.put("comissao", Formatador.formatarMoeda(taxaDeComissao));
        return atributos;
    }

    public double getSalarioMensal() {
        return salarioMensal;
    }

    public void setSalarioMensal(double salarioMensal) {
        this.salarioMensal = salarioMensal;
    }

    public double getTaxaDeComissao() {
        return taxaDeComissao;
    }

    public void setTaxaDeComissao(double taxaDeComissao) {
        this.taxaDeComissao = taxaDeComissao;
    }

    private java.util.List<ResultadoVenda> vendas = new java.util.ArrayList<>();

    public java.util.List<ResultadoVenda> getVendas() {
        return vendas;
    }

    public void setVendas(java.util.List<ResultadoVenda> vendas) {
        this.vendas = vendas != null ? vendas : new java.util.ArrayList<>();
    }

    @Override
    public void lancarVenda(String data, double valor) {
        vendas.add(new ResultadoVenda(data, valor));
    }

    @Override
    public double getVendasRealizadas(java.time.LocalDate inicio, java.time.LocalDate fim) {
        double total = 0;
        for (ResultadoVenda venda : vendas) {
            java.time.LocalDate dataVenda = Formatador.converterData(venda.getData());
            if (dataVenda != null && !dataVenda.isBefore(inicio) && dataVenda.isBefore(fim)) {
                total += venda.getValor();
            }
        }
        return total;
    }

    @Override
    public Empregado clonar() {
        EmpregadoComissionado clone = new EmpregadoComissionado(getNome(), getEndereco(), salarioMensal, taxaDeComissao);
        copiarDadosBase(clone);
        clone.vendas = new java.util.ArrayList<>();
        for (ResultadoVenda rv : this.vendas) {
            clone.vendas.add(new ResultadoVenda(rv.getData(), rv.getValor()));
        }
        return clone;
    }
}
