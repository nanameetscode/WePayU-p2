package br.ufal.ic.p2.wepayu.models.sindicato;

import br.ufal.ic.p2.wepayu.exceptions.validacao.DataInvalidaException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorDeveSerPositivoException;
import br.ufal.ic.p2.wepayu.models.LancamentoDeValor;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Taxa extra cobrada pelo sindicato por um serviço prestado ao filiado.
 *
 * <p>É cobrada uma única vez, no primeiro pagamento a partir da sua data. Depois de
 * cobrada, continua registrada (aparece nas consultas), mas não volta a ser descontada.
 */
public class TaxaServico extends LancamentoDeValor {

    private final boolean cobrada;

    /**
     * Registra uma taxa ainda não cobrada.
     */
    public TaxaServico(LocalDate data, BigDecimal valor) throws DataInvalidaException, ValorDeveSerPositivoException {
        this(data, valor, false);
    }

    @ConstructorProperties({"data", "valor", "cobrada"})
    public TaxaServico(LocalDate data, BigDecimal valor, boolean cobrada)
            throws DataInvalidaException, ValorDeveSerPositivoException {

        super(data, valor);
        this.cobrada = cobrada;
    }

    private TaxaServico(TaxaServico original) {
        super(original);
        this.cobrada = true;
    }

    /**
     * @return se a taxa já foi descontada (gravado no XML)
     */
    public boolean isCobrada() {
        return cobrada;
    }

    /**
     * @return se a taxa ainda deve ser descontada num pagamento feito no dia informado
     */
    public boolean estaPendenteEm(LocalDate dia) {
        return !cobrada && !getData().isAfter(dia);
    }

    /**
     * @return esta taxa marcada como cobrada, se estiver pendente no dia; senão, ela mesma
     */
    public TaxaServico cobrarEm(LocalDate dia) {
        return estaPendenteEm(dia) ? new TaxaServico(this) : this;
    }
}
