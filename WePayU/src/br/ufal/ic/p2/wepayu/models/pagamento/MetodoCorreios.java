package br.ufal.ic.p2.wepayu.models.pagamento;

/**
 * Cheque enviado pelos correios para o endereço do empregado.
 */
public class MetodoCorreios extends MetodoPagamento {

    @Override
    public String getRotulo() {
        return "correios";
    }

    @Override
    public String getDescricao(String endereco) {
        return "Correios, " + endereco;
    }
}
