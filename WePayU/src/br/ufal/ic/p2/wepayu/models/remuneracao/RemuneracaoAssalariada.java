package br.ufal.ic.p2.wepayu.models.remuneracao;

import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNegativoException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNuloException;
import br.ufal.ic.p2.wepayu.models.agenda.AgendaDePagamento;
import br.ufal.ic.p2.wepayu.models.empregado.Empregado;
import br.ufal.ic.p2.wepayu.models.folha.PeriodoDePagamento;
import br.ufal.ic.p2.wepayu.models.folha.Proventos;
import br.ufal.ic.p2.wepayu.models.folha.ProventosAssalariado;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;

/**
 * Recebe um salário fixo mensal. Cada pagamento leva a parte do salário que cabe à
 * agenda: o mês inteiro na agenda mensal, ou a fração das semanas pagas na semanal.
 * Se o empregado já recebeu no meio do período (porque mudou de agenda ou de tipo),
 * leva só a parte proporcional aos dias que faltam pagar.
 */
public class RemuneracaoAssalariada extends Remuneracao {

    /**
     * @param salarioMensal salário fixo do mês
     */
    @ConstructorProperties({"salario"})
    public RemuneracaoAssalariada(BigDecimal salarioMensal) throws ValorNuloException, ValorNegativoException {
        super(salarioMensal);
    }

    @Override
    public TipoEmpregado getTipo() {
        return TipoEmpregado.ASSALARIADO;
    }

    @Override
    public Remuneracao comSalario(BigDecimal novoSalario) throws ValorNuloException, ValorNegativoException {
        return new RemuneracaoAssalariada(novoSalario);
    }

    @Override
    protected Proventos calcularProventos(Empregado empregado, AgendaDePagamento agenda,
            PeriodoDePagamento periodo) {

        return new ProventosAssalariado(parteFixa(agenda, periodo));
    }

    /**
     * @return a parte do salário mensal que cabe a um pagamento da agenda, proporcional
     *         aos dias a pagar quando parte do período já foi paga
     */
    protected BigDecimal parteFixa(AgendaDePagamento agenda, PeriodoDePagamento periodo) {
        return periodo.proporcional(agenda.parteDoSalarioMensal(getSalario()));
    }
}
