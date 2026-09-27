package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.TipoInvalidoException;

/**
 * Rótulos canônicos dos tipos de empregado do sistema.
 *
 * <p>Cada constante associa o rótulo usado nos scripts de teste do EasyAccept
 * (e portanto exposto por {@code getAtributoEmpregado atributo=tipo}) ao tipo
 *corresponding do domínio. A tradução fica centralizada aqui para que nenhum
 * outro ponto do sistema precise repetir a lista de tipos.
 */
public enum TipoEmpregado {

    /** Recebe por hora trabalhada. */
    HORISTA("horista"),

    /** Recebe salário fixo mensal. */
    ASSALARIADO("assalariado"),

    /** Recebe salário mensal acrescido de comissão sobre as vendas. */
    COMISSIONADO("comissionado");

    private final String rotulo;

    TipoEmpregado(String rotulo) {
        this.rotulo = rotulo;
    }

    /**
     * @return o rótulo canônico do tipo, tal como aparece nos scripts de teste
     */
    public String getRotulo() {
        return rotulo;
    }

    /**
     * Converte o rótulo recebido do script de teste no tipo de domínio correspondente.
     *
     * @param rotulo rótulo informado no comando {@code criarEmpregado}
     * @return o tipo correspondente ao rótulo
     * @throws TipoInvalidoException se o rótulo não corresponder a nenhum tipo conhecido
     */
    public static TipoEmpregado fromRotulo(String rotulo) throws TipoInvalidoException {
        for (TipoEmpregado tipo : values()) {
            if (tipo.rotulo.equals(rotulo)) {
                return tipo;
            }
        }
        throw new TipoInvalidoException();
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
