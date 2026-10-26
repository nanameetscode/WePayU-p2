package br.ufal.ic.p2.wepayu.models.sindicato;

import java.math.BigDecimal;

/**
 * Resultado de cobrar o sindicato num pagamento: quanto é descontado agora e como
 * fica a filiação depois do pagamento (dívida restante e taxas já cobradas).
 *
 * <p>Calcular a cobrança não altera nada; a nova situação só é aplicada quando a
 * folha é efetivada.
 *
 * @param valor                  desconto aplicado no contracheque
 * @param situacaoAposPagamento  filiação como fica se o pagamento for efetivado
 */
public record CobrancaSindical(BigDecimal valor, Sindicalizacao situacaoAposPagamento) {
}
