package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.utils.Formatador;

import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Empregado que recebe por hora trabalhada.
 *
 * <p>O que caracteriza o horista não é um "salário" maior ou menor, mas o fato de
 * o valor informado ser a <strong>remuneração de uma hora</strong>. Por isso o
 * atributo chama-se {@code salarioPorHora} e não apenas "salário": o nome evita
 * que a herança esconda o conceito que distingue este tipo dos demais.
 */
public class EmpregadoHorista extends Empregado {

    private double salarioPorHora;
    private List<CartaoDePonto> cartoes = new ArrayList<>();

    /**
     * @param nome           nome do empregado
     * @param endereco       endereço do empregado
     * @param salarioPorHora remuneração de uma hora trabalhada
     */
    @ConstructorProperties({"nome", "endereco", "salarioPorHora"})
    public EmpregadoHorista(String nome, String endereco, double salarioPorHora) {
        super(nome, endereco);
        this.salarioPorHora = salarioPorHora;
    }

    @Override
    public TipoEmpregado getTipo() {
        return TipoEmpregado.HORISTA;
    }

    @Override
    public double getSalario() {
        return salarioPorHora;
    }

    @Override
    public void setSalario(double salario) {
        this.salarioPorHora = salario;
    }

    public double getSalarioPorHora() {
        return salarioPorHora;
    }

    public void setSalarioPorHora(double salarioPorHora) {
        this.salarioPorHora = salarioPorHora;
    }

    public List<CartaoDePonto> getCartoes() {
        return cartoes;
    }

    public void setCartoes(List<CartaoDePonto> cartoes) {
        this.cartoes = cartoes != null ? cartoes : new ArrayList<>();
    }

    @Override
    public void lancarCartao(String data, double horas) {
        cartoes.add(new CartaoDePonto(data, horas));
    }

    @Override
    public double getHorasNormais(LocalDate inicio, LocalDate fim) {
        double total = 0;
        for (CartaoDePonto cartao : cartoes) {
            LocalDate dataPonto = Formatador.converterData(cartao.getData());
            if (dataPonto != null && !dataPonto.isBefore(inicio) && dataPonto.isBefore(fim)) {
                total += cartao.getHorasNormais();
            }
        }
        return total;
    }

    @Override
    public double getHorasExtras(LocalDate inicio, LocalDate fim) {
        double total = 0;
        for (CartaoDePonto cartao : cartoes) {
            LocalDate dataPonto = Formatador.converterData(cartao.getData());
            if (dataPonto != null && !dataPonto.isBefore(inicio) && dataPonto.isBefore(fim)) {
                total += cartao.getHorasExtras();
            }
        }
        return total;
    }

    @Override
    public Empregado clonar() {
        EmpregadoHorista clone = new EmpregadoHorista(getNome(), getEndereco(), salarioPorHora);
        copiarDadosBase(clone);
        clone.cartoes = new ArrayList<>();
        for (CartaoDePonto cp : this.cartoes) {
            clone.cartoes.add(new CartaoDePonto(cp.getData(), cp.getHoras()));
        }
        return clone;
    }
}
