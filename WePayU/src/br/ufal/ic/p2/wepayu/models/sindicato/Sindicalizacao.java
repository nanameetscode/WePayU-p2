package br.ufal.ic.p2.wepayu.models.sindicato;

import br.ufal.ic.p2.wepayu.exceptions.sindicato.EmpregadoNaoEhSindicalizadoException;
import br.ufal.ic.p2.wepayu.exceptions.sindicato.IdentificacaoSindicatoNulaException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNegativoException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNuloException;
import br.ufal.ic.p2.wepayu.models.Periodo;

import java.math.BigDecimal;

/**
 * Situação do empregado perante o sindicato: filiado ({@link MembroSindicato}) ou
 * não ({@link NaoSindicalizado}).
 *
 * <p>Quem não é filiado é representado por um objeto próprio, e não por {@code null}:
 * a folha pede o desconto a qualquer empregado e cada situação responde por si.
 * As implementações são imutáveis.
 */
public abstract class Sindicalizacao {

    /**
     * @return se o empregado é filiado ao sindicato
     */
    public abstract boolean isSindicalizado();

    /**
     * Filia o empregado, ou atualiza os dados de quem já é filiado.
     *
     * @param idSindicato  identificação do empregado no sindicato
     * @param taxaSindical taxa diária cobrada pelo sindicato
     * @return a nova situação de filiado
     */
    public abstract MembroSindicato filiar(String idSindicato, BigDecimal taxaSindical)
            throws IdentificacaoSindicatoNulaException, ValorNuloException, ValorNegativoException;

    /**
     * Calcula o desconto do sindicato num pagamento, sem alterar esta situação.
     *
     * @param periodo período coberto pelo pagamento
     * @param bruto   salário bruto do período; o desconto nunca passa dele
     * @return o desconto e a situação resultante do pagamento
     */
    public abstract CobrancaSindical cobrar(Periodo periodo, BigDecimal bruto);

    /**
     * @return se esta situação é a do filiado com a identificação informada
     */
    public boolean possuiIdentificacao(String idSindicato) {
        return false;
    }

    /**
     * Dá acesso aos dados de filiado.
     *
     * @return esta situação vista como filiação
     * @throws EmpregadoNaoEhSindicalizadoException se o empregado não é filiado
     */
    public MembroSindicato comoMembro() throws EmpregadoNaoEhSindicalizadoException {
        throw new EmpregadoNaoEhSindicalizadoException();
    }
}
