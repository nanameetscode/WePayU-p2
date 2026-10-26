package br.ufal.ic.p2.wepayu.models.remuneracao;

import br.ufal.ic.p2.wepayu.exceptions.empregado.TipoInvalidoException;
import br.ufal.ic.p2.wepayu.exceptions.empregado.TipoNaoAplicavelException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.CampoNumerico;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNegativoException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNuloException;
import br.ufal.ic.p2.wepayu.models.agenda.AgendaDePagamento;
import br.ufal.ic.p2.wepayu.models.agenda.AgendasDisponiveis;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Tipos de empregado, com o rótulo usado pelos scripts.
 *
 * <p>Cada constante sabe criar a sua {@link Remuneracao}, tanto na contratação quanto
 * na troca de tipo. Assim nenhum outro ponto do sistema decide por {@code switch} ou
 * {@code if} qual classe instanciar.
 */
public enum TipoEmpregado {

    HORISTA("horista", AgendasDisponiveis.SEMANAL, CampoNumerico.SALARIO,
            soComSalario(RemuneracaoHorista::new), (atual, salario) -> new RemuneracaoHorista(salario)),

    ASSALARIADO("assalariado", AgendasDisponiveis.MENSAL, CampoNumerico.SALARIO,
            soComSalario(RemuneracaoAssalariada::new), (atual, salario) -> new RemuneracaoAssalariada(salario)),

    /**
     * Único tipo com comissão: é obrigatória na contratação e é o valor que acompanha a
     * troca para este tipo, que mantém o salário atual.
     */
    COMISSIONADO("comissionado", AgendasDisponiveis.BISSEMANAL, CampoNumerico.COMISSAO,
            (salario, comissao) -> new RemuneracaoComissionada(salario,
                    comissao.orElseThrow(TipoNaoAplicavelException::new)),
            (atual, comissao) -> new RemuneracaoComissionada(atual.getSalario(), comissao));

    /**
     * Cria a remuneração de quem é contratado com o tipo.
     */
    @FunctionalInterface
    private interface Contratacao {
        Remuneracao criar(BigDecimal salario, Optional<BigDecimal> comissao)
                throws TipoNaoAplicavelException, ValorNuloException, ValorNegativoException;
    }

    /**
     * Cria a remuneração de quem passa a ter o tipo.
     */
    @FunctionalInterface
    private interface Troca {
        Remuneracao trocar(Remuneracao atual, BigDecimal valorInformado)
                throws ValorNuloException, ValorNegativoException;
    }

    /**
     * Construtor da remuneração dos tipos que só dependem do salário.
     */
    @FunctionalInterface
    private interface RemuneracaoPorSalario {
        Remuneracao criar(BigDecimal salario) throws ValorNuloException, ValorNegativoException;
    }

    private final String rotulo;
    private final AgendaDePagamento agendaPadrao;
    private final CampoNumerico campoDaTroca;
    private final Contratacao contratacao;
    private final Troca troca;

    TipoEmpregado(String rotulo, AgendaDePagamento agendaPadrao, CampoNumerico campoDaTroca,
            Contratacao contratacao, Troca troca) {

        this.rotulo = rotulo;
        this.agendaPadrao = agendaPadrao;
        this.campoDaTroca = campoDaTroca;
        this.contratacao = contratacao;
        this.troca = troca;
    }

    /**
     * Cria a remuneração de um empregado deste tipo.
     *
     * @param comissao comissão, presente apenas quando o script a informou
     * @throws TipoNaoAplicavelException se a comissão for informada para quem não a
     *                                   recebe, ou omitida para o comissionado
     */
    public Remuneracao criar(BigDecimal salario, Optional<BigDecimal> comissao)
            throws TipoNaoAplicavelException, ValorNuloException, ValorNegativoException {

        return contratacao.criar(salario, comissao);
    }

    /**
     * Cria a remuneração de quem passa a ser deste tipo.
     *
     * @param atual          remuneração atual do empregado
     * @param valorInformado valor que acompanha a troca, lido do campo {@link #getCampoDaTroca()}
     */
    public Remuneracao trocar(Remuneracao atual, BigDecimal valorInformado)
            throws ValorNuloException, ValorNegativoException {

        return troca.trocar(atual, valorInformado);
    }

    /**
     * @return o campo do valor que acompanha a troca para este tipo: o salário do
     *         horista e do assalariado, ou a comissão do comissionado
     */
    public CampoNumerico getCampoDaTroca() {
        return campoDaTroca;
    }

    public String getRotulo() {
        return rotulo;
    }

    /**
     * @return a agenda de pagamento de quem é contratado com este tipo, ou passa a ele
     */
    public AgendaDePagamento getAgendaPadrao() {
        return agendaPadrao;
    }

    /**
     * @param rotulo rótulo usado pelos scripts
     * @throws TipoInvalidoException se o rótulo não for de nenhum tipo
     */
    public static TipoEmpregado doRotulo(String rotulo) throws TipoInvalidoException {
        for (TipoEmpregado tipo : values()) {
            if (tipo.rotulo.equals(rotulo)) {
                return tipo;
            }
        }
        throw new TipoInvalidoException();
    }

    /**
     * @return contratação dos tipos sem comissão: informar a comissão é um erro
     */
    private static Contratacao soComSalario(RemuneracaoPorSalario construtor) {
        return (salario, comissao) -> {
            if (comissao.isPresent()) {
                throw new TipoNaoAplicavelException();
            }
            return construtor.criar(salario);
        };
    }
}
