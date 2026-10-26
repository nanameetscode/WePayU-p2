package br.ufal.ic.p2.wepayu.persistence;

import br.ufal.ic.p2.wepayu.exceptions.sistema.ArquivoException;
import br.ufal.ic.p2.wepayu.models.DadosGravados;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Grava e recupera os empregados e as agendas de pagamento num arquivo XML com {@link XMLEncoder} e
 * {@link XMLDecoder}, como pede o enunciado.
 *
 * <p>As classes do domínio são reconstruídas pelos construtores marcados com
 * {@code @ConstructorProperties}, sem setters para listas ou objetos internos. Os tipos que o XMLEncoder não sabe
 * gravar sozinho (datas, valores decimais e as listas e mapas somente para leitura
 * que o domínio devolve) recebem aqui a receita de como recriá-los.
 */
public class RepositorioXML {

    private static final Path ARQUIVO = Path.of("wepayu.xml");
    private static final Path ARQUIVO_TEMPORARIO = Path.of("wepayu.xml.tmp");

    // as classes das coleções somente leitura são internas ao JDK; obtê-las de uma instância
    // é o único jeito de registrá-las
    private static final Class<?> LISTA_SOMENTE_LEITURA = Collections.unmodifiableList(new ArrayList<>()).getClass();
    private static final Class<?> MAPA_SOMENTE_LEITURA = Collections.unmodifiableMap(new LinkedHashMap<>()).getClass();

    /**
     * @return os dados gravados; sem empregados nem agendas se ainda não há arquivo
     * @throws ArquivoException se o arquivo existir mas não puder ser lido
     */
    public DadosGravados carregar() throws ArquivoException {
        if (!Files.exists(ARQUIVO)) {
            return new DadosGravados();
        }
        String mensagem = "Arquivo de dados " + ARQUIVO + " invalido ou corrompido";
        List<Exception> erros = new ArrayList<>();
        DadosGravados dados;
        try (InputStream entrada = new BufferedInputStream(Files.newInputStream(ARQUIVO));
             XMLDecoder decoder = new XMLDecoder(entrada, null, erros::add)) {

            dados = (DadosGravados) decoder.readObject();
        } catch (IOException | RuntimeException erro) {
            throw new ArquivoException(mensagem, erro);
        }
        if (!erros.isEmpty() || dados == null) {
            throw new ArquivoException(mensagem, erros.isEmpty() ? null : erros.get(0));
        }
        return dados;
    }

    /**
     * Grava os empregados, substituindo o arquivo anterior só quando a gravação termina
     * bem: uma falha no meio não destrói os dados que já estavam gravados.
     *
     * @throws ArquivoException se o arquivo não puder ser escrito
     */
    public void gravar(DadosGravados dados) throws ArquivoException {
        String mensagem = "Nao foi possivel gravar " + ARQUIVO;
        List<Exception> erros = new ArrayList<>();
        try (OutputStream saida = new BufferedOutputStream(Files.newOutputStream(ARQUIVO_TEMPORARIO));
             XMLEncoder encoder = new XMLEncoder(saida)) {

            encoder.setExceptionListener(erros::add);
            registrarReceitas(encoder);
            encoder.writeObject(dados);
        } catch (IOException erro) {
            throw new ArquivoException(mensagem, erro);
        }
        if (!erros.isEmpty()) {
            throw new ArquivoException(mensagem, erros.get(0));
        }
        try {
            Files.move(ARQUIVO_TEMPORARIO, ARQUIVO, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException erro) {
            throw new ArquivoException(mensagem, erro);
        }
    }

    private static void registrarReceitas(XMLEncoder encoder) {
        encoder.setPersistenceDelegate(LocalDate.class, recriarPor(LocalDate.class, "parse"));
        encoder.setPersistenceDelegate(BigDecimal.class, recriarPor(BigDecimal.class, "new"));
        encoder.setPersistenceDelegate(LISTA_SOMENTE_LEITURA, new ReceitaDeColecaoSomenteLeitura(
                ArrayList.class, lista -> new ArrayList<Object>((List<?>) lista)));
        encoder.setPersistenceDelegate(MAPA_SOMENTE_LEITURA, new ReceitaDeColecaoSomenteLeitura(
                LinkedHashMap.class, mapa -> new LinkedHashMap<Object, Object>((Map<?, ?>) mapa)));
    }

    /**
     * @return receita que recria o valor chamando {@code metodo} com o seu texto
     */
    private static PersistenceDelegate recriarPor(Class<?> classe, String metodo) {
        return new PersistenceDelegate() {
            @Override
            protected Expression instantiate(Object valor, Encoder saida) {
                return new Expression(valor, classe, metodo, new Object[]{valor.toString()});
            }
        };
    }

    /**
     * Grava uma coleção somente leitura como uma cópia comum e modificável, que os
     * construtores do domínio copiam de novo ao recriar os objetos.
     */
    private static final class ReceitaDeColecaoSomenteLeitura extends PersistenceDelegate {

        private final Class<?> tipoDaCopia;
        private final UnaryOperator<Object> copiar;

        ReceitaDeColecaoSomenteLeitura(Class<?> tipoDaCopia, UnaryOperator<Object> copiar) {
            this.tipoDaCopia = tipoDaCopia;
            this.copiar = copiar;
        }

        @Override
        protected Expression instantiate(Object colecao, Encoder saida) {
            return new Expression(colecao, tipoDaCopia, "new", new Object[]{copiar.apply(colecao)});
        }

        @Override
        protected boolean mutatesTo(Object antiga, Object nova) {
            return nova != null;
        }

        @Override
        protected void initialize(Class<?> tipo, Object antiga, Object nova, Encoder saida) {
            // a cópia já sai completa do construtor
        }
    }
}
