package br.ufal.ic.p2.wepayu.utils;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Converte valores do sistema no texto que o EasyAccept compara: vírgula como
 * separador decimal e nenhum separador de milhar.
 *
 * <p>É o único ponto que conhece o formato de saída; a leitura dos parâmetros
 * recebidos fica em {@link LeitorDeEntrada}.
 */
public final class Formatador {

    private Formatador() {
    }

    /**
     * @param valor valor monetário
     * @return o valor com duas casas decimais arredondadas (ex.: 23,00 ou 2300,45)
     */
    public static String moeda(BigDecimal valor) {
        return String.format(Locale.US, "%.2f", valor).replace('.', ',');
    }

    /**
     * @param horas quantidade de horas
     * @return as horas sem zeros à direita (ex.: 8, 16 ou 1,5)
     */
    public static String horas(BigDecimal horas) {
        return horas.stripTrailingZeros().toPlainString().replace('.', ',');
    }
}
