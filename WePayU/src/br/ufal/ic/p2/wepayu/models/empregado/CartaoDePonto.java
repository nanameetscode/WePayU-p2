package br.ufal.ic.p2.wepayu.models.empregado;

import br.ufal.ic.p2.wepayu.exceptions.validacao.CampoData;
import br.ufal.ic.p2.wepayu.exceptions.validacao.DataInvalidaException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.HorasDevemSerPositivasException;
import br.ufal.ic.p2.wepayu.models.Periodo;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Horas trabalhadas por um horista num dia. As que passam da jornada diária de
 * 8 horas (somando todos os cartões da mesma data) são horas extras.
 */
public class CartaoDePonto {

    private static final BigDecimal JORNADA_DIARIA = BigDecimal.valueOf(8);

    private final LocalDate data;
    private final BigDecimal horas;

    /**
     * @throws DataInvalidaException           se a data não for informada
     * @throws HorasDevemSerPositivasException se as horas não forem positivas
     */
    @ConstructorProperties({"data", "horas"})
    public CartaoDePonto(LocalDate data, BigDecimal horas)
            throws DataInvalidaException, HorasDevemSerPositivasException {

        if (data == null) {
            throw new DataInvalidaException(CampoData.DATA);
        }
        if (horas == null || horas.signum() <= 0) {
            throw new HorasDevemSerPositivasException();
        }
        this.data = data;
        this.horas = horas;
    }

    /**
     * @return soma das horas normais do período, até 8 por dia
     */
    public static BigDecimal somarHorasNormais(List<CartaoDePonto> cartoes, Periodo periodo) {
        return somarPorDia(cartoes, periodo, horasDoDia -> horasDoDia.min(JORNADA_DIARIA));
    }

    /**
     * @return soma das horas extras do período, o que passa de 8 em cada dia
     */
    public static BigDecimal somarHorasExtras(List<CartaoDePonto> cartoes, Periodo periodo) {
        return somarPorDia(cartoes, periodo, horasDoDia -> horasDoDia.subtract(JORNADA_DIARIA).max(BigDecimal.ZERO));
    }

    /**
     * A jornada é por dia: dois cartões na mesma data somam as horas antes de separar as extras.
     */
    private static BigDecimal somarPorDia(List<CartaoDePonto> cartoes, Periodo periodo,
            UnaryOperator<BigDecimal> parcelaDoDia) {

        Map<LocalDate, BigDecimal> horasPorDia = cartoes.stream()
                .filter(cartao -> periodo.contem(cartao.data))
                .collect(Collectors.toMap(CartaoDePonto::getData, CartaoDePonto::getHoras, BigDecimal::add));
        return horasPorDia.values().stream().map(parcelaDoDia).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public LocalDate getData() {
        return data;
    }

    /**
     * @return horas trabalhadas no dia (gravadas no XML)
     */
    public BigDecimal getHoras() {
        return horas;
    }
}
