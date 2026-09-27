package br.ufal.ic.p2.wepayu.persistence;

import br.ufal.ic.p2.wepayu.models.Empregado;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Persistência do cadastro de empregados em arquivo XML, conforme especificado.
 *
 * <p>A gravação e a leitura usam {@link XMLEncoder} e {@link XMLDecoder}, que
 * serializam o grafo de objetos sem exigir banco de dados relacional.
 *
 * <p>As classes do domínio são imutáveis e, por isso, não possuem construtor sem
 * argumentos. Para que ainda assim possam ser reconstruídas na leitura, cada uma
 * declara {@code @ConstructorProperties}, indicando ao mecanismo de persistência
 * quais propriedades alimentam o seu construtor.
 */
public class RepositorioXML {

    private static final String NOME_ARQUIVO = "wepayu.xml";

    private final Map<String, Empregado> empregados = new LinkedHashMap<>();

    /**
     * Recupera do arquivo o cadastro gravado na execução anterior.
     *
     * <p>Se o arquivo não existir, o sistema começa vazio, que é o esperado na
     * primeira execução de um script de teste.
     */
    @SuppressWarnings("unchecked")
    public void carregar() {
        empregados.clear();
        File arquivo = new File(NOME_ARQUIVO);
        if (!arquivo.exists()) {
            return;
        }
        try (XMLDecoder decoder = new XMLDecoder(new FileInputStream(arquivo))) {
            Map<String, Empregado> mapa = (Map<String, Empregado>) decoder.readObject();
            if (mapa != null) {
                empregados.putAll(mapa);
            }
        } catch (Exception erro) {
            // Um arquivo ilegível não deve impedir o sistema de iniciar vazio.
            empregados.clear();
        }
    }

    /**
     * Grava o cadastro atual em arquivo, substituindo o conteúdo anterior.
     *
     * @throws IOException se o arquivo não puder ser escrito
     */
    public void gravar() throws IOException {
        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(NOME_ARQUIVO)))) {
            encoder.writeObject(empregados);
        }
    }

    /**
     * Remove o arquivo de persistência, para que a próxima execução comece do zero.
     *
     * @throws IOException se o arquivo não puder ser removido
     */
    public void limpar() throws IOException {
        empregados.clear();
        File arquivo = new File(NOME_ARQUIVO);
        if (arquivo.exists() && !arquivo.delete()) {
            throw new IOException("Nao foi possivel remover " + NOME_ARQUIVO);
        }
    }

    /**
     * @return o mapa de empregados mantido pelo repositório
     */
    public Map<String, Empregado> getEmpregados() {
        return empregados;
    }
}
