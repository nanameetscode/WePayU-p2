package br.ufal.ic.p2.wepayu.models.remuneracao;

import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNegativoException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNuloException;
import br.ufal.ic.p2.wepayu.models.agenda.AgendaDePagamento;
import br.ufal.ic.p2.wepayu.models.empregado.Empregado;
import br.ufal.ic.p2.wepayu.models.folha.PeriodoDePagamento;
import br.ufal.ic.p2.wepayu.models.folha.Proventos;
import br.ufal.ic.p2.wepayu.models.folha.ProventosHorista;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Recebe por hora trabalhada, pelos cartões de ponto do período. As horas extras
 * valem 1,5 vez a hora normal.
 */
public class RemuneracaoHorista extends Remuneracao {

    private static final BigDecimal ADICIONAL_HORA_EXTRA = new BigDecimal("1.5");

    /**
     * @param salarioPorHora valor de uma hora normal
     */
    @ConstructorProperties({"salario"})
    public RemuneracaoHorista(BigDecimal salarioPorHora) throws ValorNuloException, ValorNegativoException {
        super(salarioPorHora);
    }

    @Override
    public TipoEmpregado getTipo() {
        return TipoEmpregado.HORISTA;
    }

    @Override
    public Remuneracao comSalario(BigDecimal novoSalario) throws ValorNuloException, ValorNegativoException {
        return new RemuneracaoHorista(novoSalario);
    }

    /**
     * As horas do período valem o mesmo em qualquer agenda.
     */
    @Override
    protected Proventos calcularProventos(Empregado empregado, AgendaDePagamento agenda,
            PeriodoDePagamento periodo) {

        BigDecimal horasNormais = empregado.getLancamentos().horasNormais(periodo.aPagar());
        BigDecimal horasExtras = empregado.getLancamentos().horasExtras(periodo.aPagar());
        BigDecimal bruto = getSalario().multiply(horasNormais)
                .add(getSalario().multiply(ADICIONAL_HORA_EXTRA).multiply(horasExtras));
        return new ProventosHorista(horasNormais, horasExtras, bruto);
    }

    /**
     * Pelas regras do enunciado (us7), o horista só é contratado no dia do primeiro
     * cartão de ponto.
     */
    @Override
    public Optional<LocalDate> contratacaoNaAdmissao() {
        return Optional.empty();
    }

    @Override
    public RemuneracaoHorista comoHorista() {
        return this;
    }
}
