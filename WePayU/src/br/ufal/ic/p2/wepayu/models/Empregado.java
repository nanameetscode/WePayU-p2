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

    public String getValorAtributo(String atributo) throws AtributoNaoExisteException {
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
