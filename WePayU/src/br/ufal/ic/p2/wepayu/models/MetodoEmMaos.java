package br.ufal.ic.p2.wepayu.models;

/**
 * Pagamento realizado em mãos por cheque.
 */
public class MetodoEmMaos extends MetodoPagamento {
    private static final long serialVersionUID = 1L;

    public MetodoEmMaos() {
    }

    @Override
    public String getRotulo() {
        return "emMaos";
    }

    @Override
    public String getDescricaoMetodo(String endereco) {
        return "Em maos";
    }

    @Override
    public MetodoPagamento clonar() {
        return new MetodoEmMaos();
    }
}
