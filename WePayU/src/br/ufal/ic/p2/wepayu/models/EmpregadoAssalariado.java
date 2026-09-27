package br.ufal.ic.p2.wepayu.models;

import java.beans.ConstructorProperties;

/**
 * Empregado que recebe salário fixo mensal, sem qualquer parcela variável.
 *
 * <p>Não acrescenta atributos próprios: o salário fixo é tudo o que a subclasse
 * acrescenta ao modelo, e é por isso que não sobrescreve
 * {@link #getAtributos()}.
 */
public class EmpregadoAssalariado extends Empregado {

    private double salario;

    /**
     * @param nome     nome do empregado
     * @param endereco endereço do empregado
     * @param salario  salário fixo mensal
     */
    @ConstructorProperties({"nome", "endereco", "salario"})
    public EmpregadoAssalariado(String nome, String endereco, double salario) {
        super(nome, endereco);
        this.salario = salario;
    }

    @Override
    public TipoEmpregado getTipo() {
        return TipoEmpregado.ASSALARIADO;
    }

    @Override
    public double getSalario() {
        return salario;
    }

    @Override
    public void setSalario(double salario) {
        this.salario = salario;
    }

    @Override
    public Empregado clonar() {
        EmpregadoAssalariado clone = new EmpregadoAssalariado(getNome(), getEndereco(), salario);
        copiarDadosBase(clone);
        return clone;
    }
}
