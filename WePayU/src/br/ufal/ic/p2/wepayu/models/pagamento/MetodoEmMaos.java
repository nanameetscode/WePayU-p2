package br.ufal.ic.p2.wepayu.models.pagamento;

/**
 * Cheque entregue em mãos ao empregado. É a forma usada quando nenhuma outra é escolhida.
 */
public class MetodoEmMaos extends MetodoPagamento {

    @Override
    public String getRotulo() {
        return "emMaos";
    }

    @Override
    public String getDescricao(String endereco) {
        return "Em maos";
    }
}
