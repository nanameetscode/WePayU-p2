package br.ufal.ic.p2.wepayu.models.sindicato;

import br.ufal.ic.p2.wepayu.exceptions.sindicato.IdentificacaoSindicatoNulaException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.CampoNumerico;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNegativoException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNuloException;
import br.ufal.ic.p2.wepayu.models.LancamentoDeValor;
import br.ufal.ic.p2.wepayu.models.Periodo;
import br.ufal.ic.p2.wepayu.utils.Validacao;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Filiação de um empregado ao sindicato.
 *
 * <p>Guarda a identificação sindical, a taxa diária, as taxas de serviço lançadas e
 * a dívida que sobra quando um salário não cobre os descontos. Cada pagamento desconta
 * a taxa diária de todos os dias do período, as taxas de serviço pendentes e a dívida
 * anterior; o contracheque nunca fica negativo, e o que não couber vira nova dívida.
 */
public class MembroSindicato extends Sindicalizacao {

    private final String idSindicato;
    private final BigDecimal taxaSindical;
    private final List<TaxaServico> taxasServico;
    private final BigDecimal debito;

    /**
     * Filiação nova, sem taxas de serviço nem dívida.
     */
    public MembroSindicato(String idSindicato, BigDecimal taxaSindical)
            throws IdentificacaoSindicatoNulaException, ValorNuloException, ValorNegativoException {

        this(idSindicato, taxaSindical, List.of(), BigDecimal.ZERO);
    }

    @ConstructorProperties({"idSindicato", "taxaSindical", "taxasServico", "debito"})
    public MembroSindicato(String idSindicato, BigDecimal taxaSindical, List<TaxaServico> taxasServico,
            BigDecimal debito) throws IdentificacaoSindicatoNulaException, ValorNuloException, ValorNegativoException {

        this.idSindicato = Validacao.exigirPreenchido(idSindicato, IdentificacaoSindicatoNulaException::new);
        this.taxaSindical = Validacao.exigirNaoNegativo(taxaSindical, CampoNumerico.TAXA_SINDICAL);
        this.taxasServico = Collections.unmodifiableList(new ArrayList<>(taxasServico));
        this.debito = Validacao.exigirNaoNegativo(debito, CampoNumerico.DIVIDA_SINDICAL);
    }

    private MembroSindicato(MembroSindicato atual, List<TaxaServico> taxasServico, BigDecimal debito) {
        this.idSindicato = atual.idSindicato;
        this.taxaSindical = atual.taxaSindical;
        this.taxasServico = Collections.unmodifiableList(new ArrayList<>(taxasServico));
        this.debito = debito;
    }

    @Override
    public boolean isSindicalizado() {
        return true;
    }

    /**
     * Atualiza identificação e taxa, mantendo as taxas de serviço e a dívida.
     */
    @Override
    public MembroSindicato filiar(String idSindicato, BigDecimal taxaSindical)
            throws IdentificacaoSindicatoNulaException, ValorNuloException, ValorNegativoException {

        return new MembroSindicato(idSindicato, taxaSindical, taxasServico, debito);
    }

    @Override
    public CobrancaSindical cobrar(Periodo periodo, BigDecimal bruto) {
        List<TaxaServico> pendentes = taxasServico.stream()
                .filter(taxa -> taxa.estaPendenteEm(periodo.fim()))
                .toList();
        BigDecimal devido = debito
                .add(taxaSindical.multiply(BigDecimal.valueOf(periodo.dias())))
                .add(LancamentoDeValor.somar(pendentes));
        BigDecimal desconto = devido.min(bruto);

        List<TaxaServico> aposPagamento = taxasServico.stream()
                .map(taxa -> taxa.cobrarEm(periodo.fim()))
                .toList();
        return new CobrancaSindical(desconto, new MembroSindicato(this, aposPagamento, devido.subtract(desconto)));
    }

    @Override
    public boolean possuiIdentificacao(String idSindicato) {
        return this.idSindicato.equals(idSindicato);
    }

    @Override
    public MembroSindicato comoMembro() {
        return this;
    }

    /**
     * @return a filiação com mais uma taxa de serviço lançada
     */
    public MembroSindicato comTaxaServico(TaxaServico taxa) {
        List<TaxaServico> taxas = new ArrayList<>(taxasServico);
        taxas.add(taxa);
        return new MembroSindicato(this, taxas, debito);
    }

    /**
     * @return soma das taxas de serviço lançadas no período, cobradas ou não
     */
    public BigDecimal getTaxasServico(Periodo periodo) {
        return LancamentoDeValor.somar(taxasServico, periodo);
    }

    public String getIdSindicato() {
        return idSindicato;
    }

    public BigDecimal getTaxaSindical() {
        return taxaSindical;
    }

    public List<TaxaServico> getTaxasServico() {
        return taxasServico;
    }

    /**
     * @return dívida com o sindicato que ficou de pagamentos anteriores (gravada no XML)
     */
    public BigDecimal getDebito() {
        return debito;
    }
}
