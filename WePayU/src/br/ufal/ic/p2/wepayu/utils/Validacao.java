package br.ufal.ic.p2.wepayu.utils;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.CampoNumerico;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNegativoException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNuloException;

import java.math.BigDecimal;
import java.util.function.Supplier;

/**
 * Regras de preenchimento usadas tanto pelos serviços, ao ler os parâmetros, quanto
 * pelas classes do domínio, para que nenhum objeto exista com dados inválidos.
 */
public final class Validacao {

    private Validacao() {
    }

    /**
     * @param texto texto a verificar
     * @param erro  fábrica do erro específico do campo, criado apenas se o texto faltar
     * @return o próprio texto, quando preenchido
     * @throws E se o texto for nulo ou só tiver espaços
     */
    public static <E extends WePayUException> String exigirPreenchido(String texto, Supplier<E> erro) throws E {
        if (texto == null || texto.isBlank()) {
            throw erro.get();
        }
        return texto;
    }

    /**
     * @param valor valor a verificar
     * @param campo campo verificado, que define as mensagens de erro
     * @return o próprio valor, quando presente e não negativo
     */
    public static BigDecimal exigirNaoNegativo(BigDecimal valor, CampoNumerico campo)
            throws ValorNuloException, ValorNegativoException {

        if (valor == null) {
            throw new ValorNuloException(campo);
        }
        if (valor.signum() < 0) {
            throw new ValorNegativoException(campo);
        }
        return valor;
    }
}
