package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.utils.Formatador;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Entidade básica do sistema: alguém que recebe salário em uma empresa.
 *
 * <p>A classe é abstrata porque o comportamento e os dados de remuneração variam
 * conforme o tipo do empregado. A variação é resolvida por <strong>polimorfismo</strong>:
 * cada subclasse responde por seu próprio tipo ({@link #getTipo()}) e pelo seu
 * próprio salário ({@link #getSalario()}), em vez de ser resolvida por
 * {@code instanceof} ou por estruturas de decisão sobre o tipo.
 *
 * <p>Os dados comuns a todos os empregados ficam nesta classe; os dados específicos
 * de cada tipo ficam na sua subclasse, que os publica chamando
 * {@link #getAtributos()}.
 */
public abstract class Empregado {

    private final String nome;
    private final String endereco;
    private boolean sindicalizado;

    /**
     * @param nome     nome do empregado, já validado como não nulo
     * @param endereco endereço do empregado, já validado como não nulo
     */
    protected Empregado(String nome, String endereco) {
        this.nome = nome;
        this.endereco = endereco;
        this.sindicalizado = false;
    }

    /**
     * @return o tipo do empregado, resolvido pela própria subclasse
     */
    public abstract TipoEmpregado getTipo();

    /**
     * Salário do empregado. O nome do atributo é semântico e varia conforme o tipo
     * (o horista informa um valor por hora, o assalariado e o comissionado informam
     * valores mensais), por isso o acesso é por comportamento e não por campo comum.
     *
     * @return o salário do empregado
     */
    public abstract double getSalario();

    /**
     * Publica os atributos deste empregado, já formatados como o EasyAccept espera.
     *
     * <p>A implementação base reúne o que é comum a todos os tipos. Cada subclasse
     * que possuir dados adicionais sobrescreve este método, chama
     * {@code super.getAtributos()} e acrescenta as suas próprias entradas. Assim o
     * atributo inexistente ({@code atributo=abc}) é detectado por uma busca ausente,
     * sem nenhuma cadeia de condições sobre o tipo do empregado.
     *
     * @return mapa atributo para valor formatado
     */
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

    /**
     * @param sindicalizado filiação ao sindicato
     */
    public void setSindicalizado(boolean sindicalizado) {
        this.sindicalizado = sindicalizado;
    }

    @Override
    public String toString() {
        return getTipo().getRotulo() + ": " + nome;
    }
}
