package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;
import br.ufal.ic.p2.wepayu.models.Cadastro;

import java.util.Optional;

/**
 * Contratação, demissão e consultas de empregados.
 */
public class EmpregadoService {

    private final Cadastro cadastro;
    private final FabricaEmpregado fabrica = new FabricaEmpregado();
    private final AtributosEmpregado atributos = new AtributosEmpregado();

    public EmpregadoService(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    /**
     * Cadastra um empregado sem comissão; para o comissionado, falta a comissão.
     *
     * @return a identificação do empregado
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws WePayUException {
        return cadastro.adicionar(fabrica.criar(nome, endereco, tipo, salario, Optional.empty()));
    }

    /**
     * Cadastra um empregado informando a comissão, que só o comissionado aceita.
     *
     * @return a identificação do empregado
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
            throws WePayUException {

        return cadastro.adicionar(fabrica.criar(nome, endereco, tipo, salario, Optional.of(comissao)));
    }

    public void removerEmpregado(String emp) throws WePayUException {
        cadastro.remover(emp);
    }

    /**
     * @return o valor do atributo, no formato dos scripts
     */
    public String getAtributoEmpregado(String emp, String atributo) throws WePayUException {
        return atributos.ler(cadastro.buscar(emp), atributo);
    }

    /**
     * @param indice posição entre os empregados cujo nome contém o texto, começando em 1
     * @return a identificação do empregado
     */
    public String getEmpregadoPorNome(String nome, int indice) throws WePayUException {
        return cadastro.buscarPorNome(nome, indice);
    }

    public int getNumeroDeEmpregados() {
        return cadastro.getNumeroDeEmpregados();
    }
}
