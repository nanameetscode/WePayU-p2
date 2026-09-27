package br.ufal.ic.p2.wepayu.utils;

import java.util.Locale;

/**
 * utilitário para formatação de dados monetários e numéricos
 * de acordo com o padrão esperado pelo EasyAccept
 */
public class Formatador {

    /**
     * formata um valor numérico com duas casas decimais e separador vírgula,
     * sem separador de milhar (ex: 23,00 ou 2300,45)
     */
    public static String formatarMoeda(double valor) {
        return String.format(Locale.US, "%.2f", valor).replace('.', ',');
    }
}
