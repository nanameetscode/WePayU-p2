package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.CampoNumerico;
import br.ufal.ic.p2.wepayu.models.empregado.Empregado;
import br.ufal.ic.p2.wepayu.models.remuneracao.TipoEmpregado;
import br.ufal.ic.p2.wepayu.utils.LeitorDeEntrada;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Cria empregados a partir dos parâmetros de texto do script (padrão Factory).
 *
 * <p>Confere os parâmetros na ordem em que os scripts esperam os erros: nome,
 * endereço, tipo, salário, comissão e, por último, se o tipo aceita a comissão.
 * A escolha da classe de remuneração fica com o próprio {@link TipoEmpregado}.
 */
public class FabricaEmpregado {

    /**
     * @param comissao texto da comissão, presente só quando o script a informou
     */
    public Empregado criar(String nome, String endereco, String tipo, String salario, Optional<String> comissao)
            throws WePayUException {

        Empregado.validarNome(nome);
        Empregado.validarEndereco(endereco);
        TipoEmpregado tipoDoEmpregado = TipoEmpregado.doRotulo(tipo);
        BigDecimal salarioLido = LeitorDeEntrada.lerValorNaoNegativo(salario, CampoNumerico.SALARIO);
        Optional<BigDecimal> comissaoLida = comissao.isPresent()
                ? Optional.of(LeitorDeEntrada.lerValorNaoNegativo(comissao.get(), CampoNumerico.COMISSAO))
                : Optional.empty();
        return new Empregado(nome, endereco, tipoDoEmpregado.criar(salarioLido, comissaoLida));
    }
}
