package br.ufal.ic.p2.wepayu.models;

/**
 * Pagamento enviado pelos correios para o endereço do empregado.
 */
public class MetodoCorreios extends MetodoPagamento {
    private static final long serialVersionUID = 1L;

    public MetodoCorreios() {
    }

    @Override
    public String getRotulo() {
        return "correios";
    }

    @Override
    public String getDescricaoMetodo(String endereco) {
        return "Correios, " + endereco;
    }

    @Override
    public MetodoPagamento clonar() {
        return new MetodoCorreios();
    }
}
