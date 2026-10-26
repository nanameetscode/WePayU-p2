package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.exceptions.validacao.CampoData;
import br.ufal.ic.p2.wepayu.exceptions.validacao.DataInvalidaException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorDeveSerPositivoException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Registro datado de um valor em dinheiro, como uma venda ou uma taxa de serviço.
 *
 * <p>Concentra o que os dois têm em comum: só existem com data e valor positivo,
 * e são somados por período nas consultas e na folha.
 */
public abstract class LancamentoDeValor {

    private final LocalDate data;
    private final BigDecimal valor;

    /**
     * @throws DataInvalidaException         se a data não for informada
     * @throws ValorDeveSerPositivoException se o valor não for positivo
     */
    protected LancamentoDeValor(LocalDate data, BigDecimal valor)
            throws DataInvalidaException, ValorDeveSerPositivoException {

        if (data == null) {
            throw new DataInvalidaException(CampoData.DATA);
        }
        if (valor == null || valor.signum() <= 0) {
            throw new ValorDeveSerPositivoException();
        }
        this.data = data;
        this.valor = valor;
    }

    /**
     * Cópia sem nova validação, para subclasses que derivam um registro já válido.
     */
    protected LancamentoDeValor(LancamentoDeValor original) {
        this.data = original.data;
        this.valor = original.valor;
    }

    /**
     * @return soma dos valores dos lançamentos cuja data está no período
     */
    public static BigDecimal somar(List<? extends LancamentoDeValor> lancamentos, Periodo periodo) {
        return somar(lancamentos.stream().filter(lancamento -> periodo.contem(lancamento.getData())).toList());
    }

    /**
     * @return soma dos valores de todos os lançamentos
     */
    public static BigDecimal somar(List<? extends LancamentoDeValor> lancamentos) {
        return lancamentos.stream().map(LancamentoDeValor::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public LocalDate getData() {
        return data;
    }

    public BigDecimal getValor() {
        return valor;
    }
}
