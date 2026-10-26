package br.ufal.ic.p2.wepayu.models.empregado;

import br.ufal.ic.p2.wepayu.models.LancamentoDeValor;
import br.ufal.ic.p2.wepayu.models.Periodo;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Cartões de ponto e vendas lançados para um empregado, e as somas por período.
 *
 * <p>É imutável: lançar algo devolve um novo histórico. Quem decide se o empregado
 * pode lançar cartões ou vendas é o {@link Empregado}, pelo seu tipo.
 */
public class LancamentosDoEmpregado {

    private final List<CartaoDePonto> cartoes;
    private final List<ResultadoVenda> vendas;

    /**
     * Histórico vazio, de quem acabou de ser contratado.
     */
    public LancamentosDoEmpregado() {
        this(List.of(), List.of());
    }

    @ConstructorProperties({"cartoes", "vendas"})
    public LancamentosDoEmpregado(List<CartaoDePonto> cartoes, List<ResultadoVenda> vendas) {
        this.cartoes = Collections.unmodifiableList(new ArrayList<>(cartoes));
        this.vendas = Collections.unmodifiableList(new ArrayList<>(vendas));
    }

    /**
     * @return este histórico com mais um cartão de ponto
     */
    public LancamentosDoEmpregado comCartao(CartaoDePonto cartao) {
        List<CartaoDePonto> novos = new ArrayList<>(cartoes);
        novos.add(cartao);
        return new LancamentosDoEmpregado(novos, vendas);
    }

    /**
     * @return este histórico com mais uma venda
     */
    public LancamentosDoEmpregado comVenda(ResultadoVenda venda) {
        List<ResultadoVenda> novas = new ArrayList<>(vendas);
        novas.add(venda);
        return new LancamentosDoEmpregado(cartoes, novas);
    }

    /**
     * @return dia do cartão de ponto mais antigo, se houver algum
     */
    public Optional<LocalDate> dataDoPrimeiroCartao() {
        return cartoes.stream().map(CartaoDePonto::getData).min(Comparator.naturalOrder());
    }

    public BigDecimal horasNormais(Periodo periodo) {
        return CartaoDePonto.somarHorasNormais(cartoes, periodo);
    }

    public BigDecimal horasExtras(Periodo periodo) {
        return CartaoDePonto.somarHorasExtras(cartoes, periodo);
    }

    public BigDecimal totalVendido(Periodo periodo) {
        return LancamentoDeValor.somar(vendas, periodo);
    }

    /**
     * @return cartões de ponto, somente para leitura (gravados no XML)
     */
    public List<CartaoDePonto> getCartoes() {
        return cartoes;
    }

    /**
     * @return vendas, somente para leitura (gravadas no XML)
     */
    public List<ResultadoVenda> getVendas() {
        return vendas;
    }
}
