package br.ufal.ic.p2.wepayu.models;

/**
 * Pagamento realizado via depósito bancário em conta corrente.
 */
public class MetodoBanco extends MetodoPagamento {
    private static final long serialVersionUID = 1L;

    private String banco;
    private String agencia;
    private String contaCorrente;

    public MetodoBanco() {
    }

    public MetodoBanco(String banco, String agencia, String contaCorrente) {
        this.banco = banco;
        this.agencia = agencia;
        this.contaCorrente = contaCorrente;
    }

    @Override
    public String getRotulo() {
        return "banco";
    }

    @Override
    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    @Override
    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    @Override
    public String getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    @Override
    public String getDescricaoMetodo(String endereco) {
        return banco + ", Ag. " + agencia + " CC " + contaCorrente;
    }

    @Override
    public MetodoPagamento clonar() {
        return new MetodoBanco(banco, agencia, contaCorrente);
    }
}
