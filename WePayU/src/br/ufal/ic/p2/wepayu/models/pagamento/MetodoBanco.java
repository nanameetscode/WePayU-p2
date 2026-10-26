package br.ufal.ic.p2.wepayu.models.pagamento;

import br.ufal.ic.p2.wepayu.exceptions.pagamento.AgenciaNulaException;
import br.ufal.ic.p2.wepayu.exceptions.pagamento.BancoNuloException;
import br.ufal.ic.p2.wepayu.exceptions.pagamento.ContaCorrenteNulaException;
import br.ufal.ic.p2.wepayu.utils.Validacao;

import java.beans.ConstructorProperties;

/**
 * Depósito em conta corrente. Só existe com banco, agência e conta preenchidos.
 */
public class MetodoBanco extends MetodoPagamento {

    private final String banco;
    private final String agencia;
    private final String contaCorrente;

    /**
     * @throws BancoNuloException         se o banco não for informado
     * @throws AgenciaNulaException       se a agência não for informada
     * @throws ContaCorrenteNulaException se a conta corrente não for informada
     */
    @ConstructorProperties({"banco", "agencia", "contaCorrente"})
    public MetodoBanco(String banco, String agencia, String contaCorrente)
            throws BancoNuloException, AgenciaNulaException, ContaCorrenteNulaException {

        this.banco = Validacao.exigirPreenchido(banco, BancoNuloException::new);
        this.agencia = Validacao.exigirPreenchido(agencia, AgenciaNulaException::new);
        this.contaCorrente = Validacao.exigirPreenchido(contaCorrente, ContaCorrenteNulaException::new);
    }

    @Override
    public String getRotulo() {
        return "banco";
    }

    @Override
    public String getDescricao(String endereco) {
        return banco + ", Ag. " + agencia + " CC " + contaCorrente;
    }

    @Override
    public MetodoBanco comoBanco() {
        return this;
    }

    public String getBanco() {
        return banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getContaCorrente() {
        return contaCorrente;
    }
}
