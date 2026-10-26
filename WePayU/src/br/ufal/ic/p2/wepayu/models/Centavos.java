package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Cortes em centavos dos cálculos da folha. Os resultados esperados pelos scripts
 * truncam (não arredondam) a parte fixa dos pagamentos e a comissão.
 */
public final class Centavos {

    private static final int CASAS = 2;

    private Centavos() {
    }

    /**
     * @return o valor truncado em centavos
     */
    public static BigDecimal truncar(BigDecimal valor) {
        return valor.setScale(CASAS, RoundingMode.DOWN);
    }

    /**
     * @return o quociente truncado em centavos
     */
    public static BigDecimal dividir(BigDecimal dividendo, BigDecimal divisor) {
        return dividendo.divide(divisor, CASAS, RoundingMode.DOWN);
    }
}
