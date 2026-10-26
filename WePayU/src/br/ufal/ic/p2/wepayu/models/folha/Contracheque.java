package br.ufal.ic.p2.wepayu.models.folha;

import br.ufal.ic.p2.wepayu.models.empregado.Empregado;
import br.ufal.ic.p2.wepayu.models.sindicato.CobrancaSindical;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Pagamento de um empregado num dia: proventos, descontos do sindicato e líquido.
 *
 * <p>Criar o contracheque não altera o empregado. A dívida e as taxas de serviço do
 * sindicato só mudam quando o contracheque é {@linkplain #efetivar() efetivado}.
 */
public class Contracheque {

    private final Empregado empregado;
    private final LocalDate dia;
    private final Proventos proventos;
    private final CobrancaSindical cobranca;

    public Contracheque(Empregado empregado, LocalDate dia, Proventos proventos, CobrancaSindical cobranca) {
        this.empregado = empregado;
        this.dia = dia;
        this.proventos = proventos;
        this.cobranca = cobranca;
    }

    public String getNomeEmpregado() {
        return empregado.getNome();
    }

    public String getDescricaoMetodoPagamento() {
        return empregado.getDescricaoMetodoPagamento();
    }

    public BigDecimal getBruto() {
        return proventos.getBruto();
    }

    public BigDecimal getDescontos() {
        return cobranca.valor();
    }

    public BigDecimal getLiquido() {
        return getBruto().subtract(getDescontos());
    }

    /**
     * Entrega este contracheque ao visitante conforme o tipo de empregado.
     */
    public void registrarEm(VisitanteDeContracheque visitante) {
        proventos.aceitar(visitante, this);
    }

    /**
     * Aplica ao empregado as consequências do pagamento. Só a {@link Folha} efetiva.
     */
    void efetivar() {
        empregado.registrarPagamento(dia, cobranca);
    }
}
