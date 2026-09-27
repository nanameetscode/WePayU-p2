package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoRecebeEmBancoException;

import java.io.Serializable;

/**
 * Estratégia de pagamento de salários (Padrão Strategy).
 *
 * <p>Encapsula o meio pelo qual o empregado recebe sua remuneração e fornece
 * acesso polimórfico aos atributos bancários sem uso de {@code instanceof}.
 */
public abstract class MetodoPagamento implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * @return rótulo canônico do método de pagamento (ex: "emMaos", "correios", "banco")
     */
    public abstract String getRotulo();

    public String getBanco() throws EmpregadoNaoRecebeEmBancoException {
        throw new EmpregadoNaoRecebeEmBancoException();
    }

    public String getAgencia() throws EmpregadoNaoRecebeEmBancoException {
        throw new EmpregadoNaoRecebeEmBancoException();
    }

    public String getContaCorrente() throws EmpregadoNaoRecebeEmBancoException {
        throw new EmpregadoNaoRecebeEmBancoException();
    }

    /**
     * @param endereco endereço do empregado para métodos postais
     * @return descrição textual para o relatório da folha de pagamento
     */
    public abstract String getDescricaoMetodo(String endereco);

    /**
     * @return cópia do método de pagamento
     */
    public abstract MetodoPagamento clonar();
}
