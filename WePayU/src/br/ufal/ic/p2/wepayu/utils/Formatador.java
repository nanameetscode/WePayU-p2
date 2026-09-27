package br.ufal.ic.p2.wepayu.utils;

import java.util.Locale;

/**
 * Utilitário de conversão entre números e o texto usado pelo EasyAccept.
 *
 * <p>Os scripts de teste escrevem os valores monetários no padrão brasileiro, com
 * vírgula como separador decimal, e o framework compara o texto devolvido
 * literalmente. Esta classe é o único ponto do sistema que conhece esse padrão,
 * de modo que a conversão aconteça uma única vez em cada sentido.
 */
public class Formatador {

    /**
     * Formata um valor numérico com duas casas decimais e separador vírgula,
     * sem separador de milhar (ex: 23,00 ou 2300,45).
     *
     * @param valor valor a formatar
     * @return o valor no padrão esperado pelo EasyAccept
     */
    public static String formatarMoeda(double valor) {
        return String.format(Locale.US, "%.2f", valor).replace('.', ',');
    }

    /**
     * Converte o texto de um parâmetro numérico em número.
     *
     * <p>A vírgula é o separador decimal dos scripts, enquanto {@code Double} só
     * reconhece o ponto. A troca é feita aqui para que a validação dos parâmetros
     * receba sempre um texto no formato esperado pela biblioteca.
     *
     * @param texto valor recebido do script, possivelmente com vírgula decimal
     * @return o número convertido, ou {@code null} se o texto não for numérico
     */
    public static Double converterNumero(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException naoEhNumero) {
            return null;
        }
    }
}
