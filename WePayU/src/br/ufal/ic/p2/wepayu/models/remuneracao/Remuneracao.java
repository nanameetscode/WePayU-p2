package br.ufal.ic.p2.wepayu.models.remuneracao;

import br.ufal.ic.p2.wepayu.exceptions.empregado.EmpregadoNaoEhComissionadoException;
import br.ufal.ic.p2.wepayu.exceptions.empregado.EmpregadoNaoEhHoristaException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.CampoNumerico;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNegativoException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNuloException;
import br.ufal.ic.p2.wepayu.models.Periodo;
import br.ufal.ic.p2.wepayu.models.agenda.AgendaDePagamento;
import br.ufal.ic.p2.wepayu.models.empregado.Empregado;
import br.ufal.ic.p2.wepayu.models.folha.Contracheque;
import br.ufal.ic.p2.wepayu.models.folha.PeriodoDePagamento;
import br.ufal.ic.p2.wepayu.models.folha.Proventos;
import br.ufal.ic.p2.wepayu.utils.Validacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Como um empregado é remunerado: o seu tipo (padrão Strategy).
 *
 * <p>Cada tipo responde quanto o empregado ganhou num período de pagamento; quando
 * ele recebe e que dias o pagamento cobre é a {@link AgendaDePagamento} do empregado
 * que decide. O roteiro do contracheque é sempre o mesmo e fica em
 * {@link #gerarContracheque} (padrão Template Method).
 *
 * <p>As implementações são imutáveis; trocar o salário ou o tipo cria outra remuneração.
 */
public abstract class Remuneracao {

    /**
     * Pelas regras do enunciado (us7), assalariados e comissionados são contratados
     * em 1/1/2005.
     */
    public static final LocalDate DATA_DE_CONTRATACAO_PADRAO = LocalDate.of(2005, 1, 1);

    private final BigDecimal salario;

    /**
     * @param salario valor por hora (horista) ou mensal (assalariado e comissionado)
     */
    protected Remuneracao(BigDecimal salario) throws ValorNuloException, ValorNegativoException {
        this.salario = Validacao.exigirNaoNegativo(salario, CampoNumerico.SALARIO);
    }

    /**
     * @return o tipo de empregado que esta remuneração representa
     */
    public abstract TipoEmpregado getTipo();

    /**
     * @return a mesma remuneração com outro salário
     */
    public abstract Remuneracao comSalario(BigDecimal novoSalario) throws ValorNuloException, ValorNegativoException;

    /**
     * @param agenda  agenda de pagamento do empregado
     * @param periodo dias cobertos pelo pagamento
     * @return o que o empregado ganhou no período, antes dos descontos
     */
    protected abstract Proventos calcularProventos(Empregado empregado, AgendaDePagamento agenda,
            PeriodoDePagamento periodo);

    /**
     * Calcula o pagamento do empregado no dia, sem alterar nada. O período é o da agenda,
     * sem os dias que um pagamento anterior já cobriu: horas, vendas, parte fixa do
     * salário e taxa sindical nunca são pagas ou cobradas duas vezes, mesmo que a
     * agenda ou o tipo mude. Quem ainda não foi contratado não paga taxa sindical.
     */
    public final Contracheque gerarContracheque(Empregado empregado, LocalDate dia) {
        AgendaDePagamento agenda = empregado.getAgenda();
        Periodo daAgenda = agenda.periodoDePagamento(dia);
        PeriodoDePagamento periodo = new PeriodoDePagamento(daAgenda, empregado.semDiasJaPagos(daAgenda));
        Proventos proventos = calcularProventos(empregado, agenda, periodo);
        Periodo diasDeSindicato = empregado.contratadoAte(dia) ? periodo.aPagar() : Periodo.vazioEm(dia);
        return new Contracheque(empregado, dia, proventos,
                empregado.getSindicalizacao().cobrar(diasDeSindicato, proventos.getBruto()));
    }

    /**
     * @return dia da contratação de quem é admitido com este tipo, ou vazio se ela só
     *         acontece no primeiro cartão de ponto (horista)
     */
    public Optional<LocalDate> contratacaoNaAdmissao() {
        return Optional.of(DATA_DE_CONTRATACAO_PADRAO);
    }

    public BigDecimal getSalario() {
        return salario;
    }

    /**
     * @return esta remuneração vista como a de um horista
     * @throws EmpregadoNaoEhHoristaException se o empregado não for horista
     */
    public RemuneracaoHorista comoHorista() throws EmpregadoNaoEhHoristaException {
        throw new EmpregadoNaoEhHoristaException();
    }

    /**
     * @return esta remuneração vista como a de um comissionado
     * @throws EmpregadoNaoEhComissionadoException se o empregado não for comissionado
     */
    public RemuneracaoComissionada comoComissionado() throws EmpregadoNaoEhComissionadoException {
        throw new EmpregadoNaoEhComissionadoException();
    }
}
