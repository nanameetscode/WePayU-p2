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

    private final double salarioMensal;
    private final double taxaDeComissao;

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
    public Map<String, String> getAtributos() {
        Map<String, String> atributos = super.getAtributos();
        atributos.put("comissao", Formatador.formatarMoeda(taxaDeComissao));
        return atributos;
    }

    private final java.util.List<ResultadoVenda> vendas = new java.util.ArrayList<>();

    public java.util.List<ResultadoVenda> getVendas() {
        return vendas;
    }

    public void setVendas(java.util.List<ResultadoVenda> novasVendas) {
        this.vendas.clear();
        if (novasVendas != null) {
            this.vendas.addAll(novasVendas);
        }
    }

    @Override
    public void lancarVenda(String data, double valor) {
        vendas.add(new ResultadoVenda(data, valor));
    }

    @Override
    public double getVendasRealizadas(java.time.LocalDate inicio, java.time.LocalDate fim) {
        double total = 0.0;
        for (ResultadoVenda v : vendas) {
            java.time.LocalDate dataVenda = Formatador.converterData(v.getData());
            if (dataVenda != null && !dataVenda.isBefore(inicio) && dataVenda.isBefore(fim)) {
                total += v.getValor();
            }
        }
        return total;
    }

    public double getSalarioMensal() {
        return salarioMensal;
    }

    public double getTaxaDeComissao() {
        return taxaDeComissao;
    }
}
