package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;
import br.ufal.ic.p2.wepayu.exceptions.empregado.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.models.empregado.Empregado;
import br.ufal.ic.p2.wepayu.utils.Formatador;

import java.util.Map;

/**
 * Leitura dos atributos de um empregado pelo nome usado nos scripts.
 *
 * <p>Cada atributo tem o seu leitor numa tabela, em vez de uma cadeia de {@code if}.
 * Atributos que só existem para alguns empregados (comissão, dados do sindicato,
 * dados bancários) são pedidos ao objeto responsável, que lança o erro certo
 * quando o empregado não os tem.
 */
public class AtributosEmpregado {

    @FunctionalInterface
    private interface Leitor {
        String ler(Empregado empregado) throws WePayUException;
    }

    private static final Map<String, Leitor> LEITORES = Map.ofEntries(
            Map.entry("nome", Empregado::getNome),
            Map.entry("endereco", Empregado::getEndereco),
            Map.entry("tipo", empregado -> empregado.getRemuneracao().getTipo().getRotulo()),
            Map.entry("salario", empregado -> Formatador.moeda(empregado.getRemuneracao().getSalario())),
            Map.entry("comissao", empregado ->
                    Formatador.moeda(empregado.getRemuneracao().comoComissionado().getTaxaDeComissao())),
            Map.entry("sindicalizado", empregado -> String.valueOf(empregado.getSindicalizacao().isSindicalizado())),
            Map.entry("idSindicato", empregado -> empregado.getSindicalizacao().comoMembro().getIdSindicato()),
            Map.entry("taxaSindical", empregado ->
                    Formatador.moeda(empregado.getSindicalizacao().comoMembro().getTaxaSindical())),
            Map.entry("metodoPagamento", empregado -> empregado.getMetodoPagamento().getRotulo()),
            Map.entry("agendaPagamento", empregado -> empregado.getAgenda().getDescricao()),
            Map.entry("banco", empregado -> empregado.getMetodoPagamento().comoBanco().getBanco()),
            Map.entry("agencia", empregado -> empregado.getMetodoPagamento().comoBanco().getAgencia()),
            Map.entry("contaCorrente", empregado -> empregado.getMetodoPagamento().comoBanco().getContaCorrente()));

    /**
     * @return o valor do atributo, no formato dos scripts
     * @throws AtributoNaoExisteException se o atributo não existir
     */
    public String ler(Empregado empregado, String atributo) throws WePayUException {
        Leitor leitor = atributo == null ? null : LEITORES.get(atributo);
        if (leitor == null) {
            throw new AtributoNaoExisteException();
        }
        return leitor.ler(empregado);
    }
}
