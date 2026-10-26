package br.ufal.ic.p2.wepayu.models.pagamento;

import br.ufal.ic.p2.wepayu.exceptions.pagamento.EmpregadoNaoRecebeEmBancoException;

/**
 * Forma pela qual o empregado recebe o salário (padrão Strategy).
 *
 * <p>Cada forma sabe o próprio rótulo e como se descrever no relatório da folha.
 * As implementações são imutáveis: trocar de forma é trocar de objeto.
 */
public abstract class MetodoPagamento {

    /**
     * @return rótulo usado pelos scripts (emMaos, correios ou banco)
     */
    public abstract String getRotulo();

    /**
     * @param endereco endereço do empregado, usado pelo envio pelos correios
     * @return descrição da forma de pagamento no relatório da folha
     */
    public abstract String getDescricao(String endereco);

    /**
     * Dá acesso aos dados bancários, que só existem no depósito em conta.
     *
     * @return esta forma de pagamento vista como depósito em banco
     * @throws EmpregadoNaoRecebeEmBancoException se o empregado não recebe por depósito
     */
    public MetodoBanco comoBanco() throws EmpregadoNaoRecebeEmBancoException {
        throw new EmpregadoNaoRecebeEmBancoException();
    }
}
