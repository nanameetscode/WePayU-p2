package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.Exception.TipoNaoAplicavelException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoAssalariado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.EmpregadoHorista;
import br.ufal.ic.p2.wepayu.models.TipoEmpregado;

/**
 * Cria a instância de {@link Empregado} correspondente ao tipo informado.
 *
 * <p>Este é o <strong>único</strong> ponto do sistema onde o tipo do empregado
 * despacha a criação de si mesmo. A decisão fica isolada aqui, e não espalhada
 * pela lógica de negócio: qualquer outro ponto do sistema recebe um
 * {@link Empregado} já construído e trabalha por polimorfismo, sem precisar saber
 * qual subclasse está em mãos.
 *
 * <p>A comissão chega aqui como {@code null} quando o script não informou o
 * parâmetro, e como um valor quando informou. A diferença importa: comissao
 * ausente é "parâmetro não aplicável a este tipo", enquanto comissão vazia ou
 * inválida é um erro de preenchimento do parâmetro em si, tratado antes pela
 * validação de campos.
 */
public class FabricaEmpregado {

    /**
     * Cria o empregado do tipo indicado, já validado quanto à aplicabilidade da comissão.
     *
     * @param tipo           tipo do empregado, já reconhecido
     * @param nome           nome do empregado
     * @param endereco       endereço do empregado
     * @param salario        salário, já validado como numérico e não negativo
     * @param comissao       comissão, ou {@code null} se o script não informou o parâmetro
     * @return o empregado correspondente ao tipo
     * @throws TipoNaoAplicavelException se a comissão foi informada para um tipo que não a
     *                                   aceita, ou não foi informada para um tipo que exige
     */
    public Empregado criar(TipoEmpregado tipo, String nome, String endereco, double salario, Double comissao)
            throws TipoNaoAplicavelException {

        return switch (tipo) {
            case HORISTA, ASSALARIADO -> {
                exigirAusenciaDeComissao(comissao);
                yield criarSemComissao(tipo, nome, endereco, salario);
            }
            case COMISSIONADO -> {
                exigirComissao(comissao);
                yield new EmpregadoComissionado(nome, endereco, salario, comissao);
            }
        };
    }

    /**
     * Cria o empregado dos tipos que não possuem comissão, delegando ao construtor
     * que cada subclasse oferece.
     */
    private Empregado criarSemComissao(TipoEmpregado tipo, String nome, String endereco, double salario) {
        if (tipo == TipoEmpregado.HORISTA) {
            return new EmpregadoHorista(nome, endereco, salario);
        }
        return new EmpregadoAssalariado(nome, endereco, salario);
    }

    private void exigirAusenciaDeComissao(Double comissao) throws TipoNaoAplicavelException {
        if (comissao != null) {
            throw new TipoNaoAplicavelException();
        }
    }

    private void exigirComissao(Double comissao) throws TipoNaoAplicavelException {
        if (comissao == null) {
            throw new TipoNaoAplicavelException();
        }
    }
}
