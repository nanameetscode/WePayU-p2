package br.ufal.ic.p2.wepayu.models.remuneracao;

import br.ufal.ic.p2.wepayu.exceptions.validacao.CampoNumerico;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNegativoException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNuloException;
import br.ufal.ic.p2.wepayu.models.Centavos;
import br.ufal.ic.p2.wepayu.models.agenda.AgendaDePagamento;
import br.ufal.ic.p2.wepayu.models.empregado.Empregado;
import br.ufal.ic.p2.wepayu.models.folha.PeriodoDePagamento;
import br.ufal.ic.p2.wepayu.models.folha.Proventos;
import br.ufal.ic.p2.wepayu.models.folha.ProventosComissionado;
import br.ufal.ic.p2.wepayu.utils.Validacao;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;

/**
 * Assalariado que também recebe comissão sobre as vendas, como diz o enunciado
 * ("alguns empregados assalariados são comissionados"). Cada pagamento leva a mesma
 * parte fixa de um assalariado na mesma agenda, mais a comissão sobre as vendas do
 * período, truncada em centavos como no resultado esperado.
 */
public class RemuneracaoComissionada extends RemuneracaoAssalariada {

    private final BigDecimal taxaDeComissao;

    /**
     * @param salarioMensal  salário fixo do mês
     * @param taxaDeComissao fração das vendas paga como comissão (ex.: 0,05)
     */
    @ConstructorProperties({"salario", "taxaDeComissao"})
    public RemuneracaoComissionada(BigDecimal salarioMensal, BigDecimal taxaDeComissao)
            throws ValorNuloException, ValorNegativoException {

        super(salarioMensal);
        this.taxaDeComissao = Validacao.exigirNaoNegativo(taxaDeComissao, CampoNumerico.COMISSAO);
    }

    @Override
    public TipoEmpregado getTipo() {
        return TipoEmpregado.COMISSIONADO;
    }

    @Override
    public Remuneracao comSalario(BigDecimal novoSalario) throws ValorNuloException, ValorNegativoException {
        return new RemuneracaoComissionada(novoSalario, taxaDeComissao);
    }

    /**
     * @return a mesma remuneração com outra taxa de comissão
     */
    public RemuneracaoComissionada comComissao(BigDecimal novaTaxa) throws ValorNuloException, ValorNegativoException {
        return new RemuneracaoComissionada(getSalario(), novaTaxa);
    }

    @Override
    protected Proventos calcularProventos(Empregado empregado, AgendaDePagamento agenda,
            PeriodoDePagamento periodo) {

        BigDecimal vendas = empregado.getLancamentos().totalVendido(periodo.aPagar());
        BigDecimal comissao = Centavos.truncar(vendas.multiply(taxaDeComissao));
        return new ProventosComissionado(parteFixa(agenda, periodo), vendas, comissao);
    }

    @Override
    public RemuneracaoComissionada comoComissionado() {
        return this;
    }

    public BigDecimal getTaxaDeComissao() {
        return taxaDeComissao;
    }
}
