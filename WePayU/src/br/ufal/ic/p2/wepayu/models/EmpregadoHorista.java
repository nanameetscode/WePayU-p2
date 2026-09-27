package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.utils.Formatador;

import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Empregado que recebe por hora trabalhada.
 */
public class EmpregadoHorista extends Empregado {

    private final double salarioPorHora;
    private List<CartaoDePonto> cartoes = new ArrayList<>();

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

    public double getSalarioPorHora() {
        return salarioPorHora;
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
}
