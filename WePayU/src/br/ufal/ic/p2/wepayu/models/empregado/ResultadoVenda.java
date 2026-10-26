package br.ufal.ic.p2.wepayu.models.empregado;

import br.ufal.ic.p2.wepayu.exceptions.validacao.DataInvalidaException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorDeveSerPositivoException;
import br.ufal.ic.p2.wepayu.models.LancamentoDeValor;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Venda realizada por um comissionado, base do cálculo da sua comissão.
 */
public class ResultadoVenda extends LancamentoDeValor {

    @ConstructorProperties({"data", "valor"})
    public ResultadoVenda(LocalDate data, BigDecimal valor)
            throws DataInvalidaException, ValorDeveSerPositivoException {

        super(data, valor);
    }
}
