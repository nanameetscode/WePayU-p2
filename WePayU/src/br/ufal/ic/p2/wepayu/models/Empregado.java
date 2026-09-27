package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhComissionadoException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhHoristaException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhSindicalizadoException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoRecebeEmBancoException;
import br.ufal.ic.p2.wepayu.utils.Formatador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
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

    private String nome;
    private String endereco;
    private boolean sindicalizado;
    private String idSindicato;
    private double taxaSindical;
    private List<TaxaServico> taxasServico = new ArrayList<>();
    private MetodoPagamento metodoPagamento = new MetodoEmMaos();

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
     * Atualiza o salário do empregado.
     *
     * @param salario novo valor de salário
     */
    public abstract void setSalario(double salario);

    /**
     * Atualiza a comissão do empregado. Lança exceção por padrão para não comissionados.
     */
    public void setComissao(double comissao) throws EmpregadoNaoEhComissionadoException {
        throw new EmpregadoNaoEhComissionadoException();
    }

    /**
     * @return a taxa de comissão formatada
     */
    public String getComissaoFormatada() throws EmpregadoNaoEhComissionadoException {
        throw new EmpregadoNaoEhComissionadoException();
    }

    /**
     * Valida polimorficamente se este empregado é comissionado.
     */
    public void verificarComissionado() throws EmpregadoNaoEhComissionadoException {
        throw new EmpregadoNaoEhComissionadoException();
    }

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
        atributos.put("metodoPagamento", metodoPagamento != null ? metodoPagamento.getRotulo() : "emMaos");
        return atributos;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento != null ? metodoPagamento : new MetodoEmMaos();
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

    /**
     * Registra um cartão de ponto. Lança exceção por padrão para tipos que não são horistas.
     */
    public void lancarCartao(String data, double horas) throws EmpregadoNaoEhHoristaException {
        throw new EmpregadoNaoEhHoristaException();
    }

    /**
     * Calcula as horas normais trabalhadas em um intervalo semiaberto [inicio, fim).
     */
    public double getHorasNormais(LocalDate inicio, LocalDate fim) throws EmpregadoNaoEhHoristaException {
        throw new EmpregadoNaoEhHoristaException();
    }

    /**
     * Calcula as horas extras trabalhadas em um intervalo semiaberto [inicio, fim).
     */
    public double getHorasExtras(LocalDate inicio, LocalDate fim) throws EmpregadoNaoEhHoristaException {
        throw new EmpregadoNaoEhHoristaException();
    }

    /**
     * Registra um resultado de venda. Lança exceção por padrão para tipos que não são comissionados.
     */
    public void lancarVenda(String data, double valor) throws EmpregadoNaoEhComissionadoException {
        throw new EmpregadoNaoEhComissionadoException();
    }

    /**
     * Calcula o total de vendas realizadas em um intervalo semiaberto [inicio, fim).
     */
    public double getVendasRealizadas(LocalDate inicio, LocalDate fim) throws EmpregadoNaoEhComissionadoException {
        throw new EmpregadoNaoEhComissionadoException();
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

    public List<TaxaServico> getTaxasServico() {
        return taxasServico;
    }

    public void setTaxasServico(List<TaxaServico> taxasServico) {
        this.taxasServico = taxasServico != null ? taxasServico : new ArrayList<>();
    }

    /**
     * Atualiza os dados sindicais do empregado.
     */
    public void setDadosSindicato(boolean sindicalizado, String idSindicato, double taxaSindical) {
        this.sindicalizado = sindicalizado;
        this.idSindicato = sindicalizado ? idSindicato : null;
        this.taxaSindical = sindicalizado ? taxaSindical : 0.0;
        if (!sindicalizado) {
            this.taxasServico.clear();
        }
    }

    /**
     * Registra uma taxa de serviço cobrada pelo sindicato.
     */
    public void lancarTaxaServico(String data, double valor) {
        taxasServico.add(new TaxaServico(data, valor));
    }

    /**
     * Calcula o total de taxas de serviço em um intervalo semiaberto [inicio, fim).
     */
    public double getTaxasServico(LocalDate inicio, LocalDate fim) throws EmpregadoNaoEhSindicalizadoException {
        if (!sindicalizado) {
            throw new EmpregadoNaoEhSindicalizadoException();
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

    private double debitoSindicalAcumulado;

    public double getDebitoSindicalAcumulado() {
        return debitoSindicalAcumulado;
    }

    public void setDebitoSindicalAcumulado(double debitoSindicalAcumulado) {
        this.debitoSindicalAcumulado = debitoSindicalAcumulado;
    }

    public List<CartaoDePonto> getCartoes() {
        return Collections.emptyList();
    }

    public List<ResultadoVenda> getVendas() {
        return Collections.emptyList();
    }

    public double getTaxaDeComissao() {
        return 0.0;
    }

    public String getDescricaoMetodoPagamento() {
        return metodoPagamento != null ? metodoPagamento.getDescricaoMetodo(endereco) : "Em maos";
    }

    /**
     * Recupera o valor de um atributo do empregado.
     */
    public String getValorAtributo(String atributo)
            throws AtributoNaoExisteException, EmpregadoNaoEhSindicalizadoException,
            EmpregadoNaoEhComissionadoException, EmpregadoNaoRecebeEmBancoException {

        if ("idSindicato".equals(atributo) || "taxaSindical".equals(atributo)) {
            if (!isSindicalizado()) {
                throw new EmpregadoNaoEhSindicalizadoException();
            }
            if ("idSindicato".equals(atributo)) {
                return idSindicato;
            }
            return Formatador.formatarMoeda(taxaSindical);
        }
        if ("metodoPagamento".equals(atributo)) {
            return metodoPagamento != null ? metodoPagamento.getRotulo() : "emMaos";
        }
        if ("banco".equals(atributo)) {
            return metodoPagamento.getBanco();
        }
        if ("agencia".equals(atributo)) {
            return metodoPagamento.getAgencia();
        }
        if ("contaCorrente".equals(atributo)) {
            return metodoPagamento.getContaCorrente();
        }
        if ("comissao".equals(atributo)) {
            return getComissaoFormatada();
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

    /**
     * @return cópia em profundidade do empregado
     */
    public abstract Empregado clonar();

    /**
     * Copia os dados base comuns para a instância destino informada.
     */
    protected void copiarDadosBase(Empregado destino) {
        destino.nome = this.nome;
        destino.endereco = this.endereco;
        destino.sindicalizado = this.sindicalizado;
        destino.idSindicato = this.idSindicato;
        destino.taxaSindical = this.taxaSindical;
        destino.debitoSindicalAcumulado = this.debitoSindicalAcumulado;
        destino.taxasServico = new ArrayList<>();
        for (TaxaServico ts : this.taxasServico) {
            TaxaServico copia = new TaxaServico(ts.getData(), ts.getValor());
            copia.setCobrada(ts.isCobrada());
            destino.taxasServico.add(copia);
        }
        destino.metodoPagamento = (this.metodoPagamento != null) ? this.metodoPagamento.clonar() : new MetodoEmMaos();
    }
}
