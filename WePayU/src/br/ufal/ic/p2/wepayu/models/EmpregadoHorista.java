package br.ufal.ic.p2.wepayu.models;

import java.beans.ConstructorProperties;

/**
 * Empregado que recebe por hora trabalhada.
 *
 * <p>O que caracteriza o horista não é um "salário" maior ou menor, mas o fato de
 * o valor informado ser a <strong>remuneração de uma hora</strong>. Por isso o
 * atributo chama-se {@code salarioPorHora} e não apenas "salário": o nome evita
 * que a herança esconda o conceito que distingue este tipo dos demais.
 */
public class EmpregadoHorista extends Empregado {

    private final double salarioPorHora;

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

    public double getSalarioPorHora() {
        return salarioPorHora;
    }
}
