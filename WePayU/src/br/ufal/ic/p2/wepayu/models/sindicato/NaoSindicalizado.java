package br.ufal.ic.p2.wepayu.models.sindicato;

import br.ufal.ic.p2.wepayu.exceptions.sindicato.IdentificacaoSindicatoNulaException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNegativoException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.ValorNuloException;
import br.ufal.ic.p2.wepayu.models.Periodo;

import java.math.BigDecimal;

/**
 * Empregado que não pertence ao sindicato: não tem descontos sindicais.
 */
public class NaoSindicalizado extends Sindicalizacao {

    @Override
    public boolean isSindicalizado() {
        return false;
    }

    @Override
    public MembroSindicato filiar(String idSindicato, BigDecimal taxaSindical)
            throws IdentificacaoSindicatoNulaException, ValorNuloException, ValorNegativoException {

        return new MembroSindicato(idSindicato, taxaSindical);
    }

    @Override
    public CobrancaSindical cobrar(Periodo periodo, BigDecimal bruto) {
        return new CobrancaSindical(BigDecimal.ZERO, this);
    }
}
