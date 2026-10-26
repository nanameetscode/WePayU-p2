package br.ufal.ic.p2.wepayu.relatorio;

import br.ufal.ic.p2.wepayu.exceptions.sistema.ArquivoException;
import br.ufal.ic.p2.wepayu.models.folha.Contracheque;
import br.ufal.ic.p2.wepayu.models.folha.Folha;
import br.ufal.ic.p2.wepayu.models.folha.ProventosAssalariado;
import br.ufal.ic.p2.wepayu.models.folha.ProventosComissionado;
import br.ufal.ic.p2.wepayu.models.folha.ProventosHorista;
import br.ufal.ic.p2.wepayu.models.folha.VisitanteDeContracheque;
import br.ufal.ic.p2.wepayu.utils.Formatador;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Texto da folha de pagamento no layout dos arquivos de referência (pasta {@code ok}):
 * colunas de largura fixa, uma seção por tipo de empregado e quebras de linha CRLF.
 *
 * <p>Cada contracheque chega pelo método do seu tipo (padrão Visitor), que escreve a
 * linha na seção certa e acumula os totais.
 */
public final class RelatorioFolha implements VisitanteDeContracheque {

    private static final String FIM_DE_LINHA = "\r\n";
    private static final int LARGURA = 127;

    private static final String COLUNAS_HORISTA = "%-36s %5d %5d %13s %9s %15s";
    private static final String COLUNAS_ASSALARIADO = "%-48s %13s %9s %15s";
    private static final String COLUNAS_COMISSIONADO = "%-21s %8s %8s %8s %13s %9s %15s";
    private static final String COLUNA_METODO = " %s";

    private final Secao horistas = new Secao("HORISTAS",
            "Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo",
            "==================================== ===== ===== ============= ========= =============== "
                    + "======================================");
    private final Secao assalariados = new Secao("ASSALARIADOS",
            "Nome                                             Salario Bruto Descontos Salario Liquido Metodo",
            "================================================ ============= ========= =============== "
                    + "======================================");
    private final Secao comissionados = new Secao("COMISSIONADOS",
            "Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo",
            "===================== ======== ======== ======== ============= ========= =============== "
                    + "======================================");

    private int totalHorasNormais;
    private int totalHorasExtras;
    private BigDecimal totalFixo = BigDecimal.ZERO;
    private BigDecimal totalVendas = BigDecimal.ZERO;
    private BigDecimal totalComissao = BigDecimal.ZERO;

    private RelatorioFolha() {
    }

    /**
     * @return o texto do relatório da folha
     */
    public static String gerar(Folha folha) {
        RelatorioFolha relatorio = new RelatorioFolha();
        folha.registrarEm(relatorio);
        return relatorio.montar(folha);
    }

    /**
     * Grava o relatório da folha no arquivo indicado, substituindo o conteúdo anterior.
     *
     * @throws ArquivoException se o arquivo não puder ser escrito
     */
    public static void gravar(String relatorio, String caminho) throws ArquivoException {
        try {
            Files.writeString(Path.of(caminho), relatorio, StandardCharsets.ISO_8859_1);
        } catch (IOException | InvalidPathException erro) {
            throw new ArquivoException("Nao foi possivel gravar a folha em " + caminho, erro);
        }
    }

    @Override
    public void visitarHorista(Contracheque contracheque, ProventosHorista proventos) {
        // o relatório mostra só as horas inteiras
        int horasNormais = proventos.getHorasNormais().intValue();
        int horasExtras = proventos.getHorasExtras().intValue();
        totalHorasNormais += horasNormais;
        totalHorasExtras += horasExtras;
        horistas.adicionar(contracheque, formatar(COLUNAS_HORISTA + COLUNA_METODO,
                contracheque.getNomeEmpregado(), horasNormais, horasExtras,
                moeda(contracheque.getBruto()), moeda(contracheque.getDescontos()),
                moeda(contracheque.getLiquido()), contracheque.getDescricaoMetodoPagamento()));
    }

    @Override
    public void visitarAssalariado(Contracheque contracheque, ProventosAssalariado proventos) {
        assalariados.adicionar(contracheque, formatar(COLUNAS_ASSALARIADO + COLUNA_METODO,
                contracheque.getNomeEmpregado(), moeda(contracheque.getBruto()),
                moeda(contracheque.getDescontos()), moeda(contracheque.getLiquido()),
                contracheque.getDescricaoMetodoPagamento()));
    }

    @Override
    public void visitarComissionado(Contracheque contracheque, ProventosComissionado proventos) {
        totalFixo = totalFixo.add(proventos.getFixo());
        totalVendas = totalVendas.add(proventos.getVendas());
        totalComissao = totalComissao.add(proventos.getComissao());
        comissionados.adicionar(contracheque, formatar(COLUNAS_COMISSIONADO + COLUNA_METODO,
                contracheque.getNomeEmpregado(), moeda(proventos.getFixo()), moeda(proventos.getVendas()),
                moeda(proventos.getComissao()), moeda(contracheque.getBruto()),
                moeda(contracheque.getDescontos()), moeda(contracheque.getLiquido()),
                contracheque.getDescricaoMetodoPagamento()));
    }

    private String montar(Folha folha) {
        StringBuilder texto = new StringBuilder()
                .append("FOLHA DE PAGAMENTO DO DIA ").append(folha.getDia()).append(FIM_DE_LINHA)
                .append("=".repeat(36)).append(FIM_DE_LINHA)
                .append(FIM_DE_LINHA);
        horistas.escrever(texto, formatar(COLUNAS_HORISTA, "TOTAL HORISTAS", totalHorasNormais,
                totalHorasExtras, moeda(horistas.bruto), moeda(horistas.descontos), moeda(horistas.liquido)));
        assalariados.escrever(texto, formatar(COLUNAS_ASSALARIADO, "TOTAL ASSALARIADOS",
                moeda(assalariados.bruto), moeda(assalariados.descontos), moeda(assalariados.liquido)));
        comissionados.escrever(texto, formatar(COLUNAS_COMISSIONADO, "TOTAL COMISSIONADOS", moeda(totalFixo),
                moeda(totalVendas), moeda(totalComissao), moeda(comissionados.bruto),
                moeda(comissionados.descontos), moeda(comissionados.liquido)));
        return texto.append("TOTAL FOLHA: ").append(moeda(folha.getTotalBruto())).append(FIM_DE_LINHA).toString();
    }

    private static String formatar(String formato, Object... valores) {
        return String.format(Locale.ROOT, formato, valores);
    }

    private static String moeda(BigDecimal valor) {
        return Formatador.moeda(valor);
    }

    /**
     * Seção de um tipo de empregado: cabeçalho, linhas e os totais comuns a todas as seções.
     */
    private static final class Secao {

        private final String titulo;
        private final String colunas;
        private final String sublinhado;
        private final StringBuilder linhas = new StringBuilder();
        private BigDecimal bruto = BigDecimal.ZERO;
        private BigDecimal descontos = BigDecimal.ZERO;
        private BigDecimal liquido = BigDecimal.ZERO;

        Secao(String titulo, String colunas, String sublinhado) {
            this.titulo = titulo;
            this.colunas = colunas;
            this.sublinhado = sublinhado;
        }

        void adicionar(Contracheque contracheque, String linha) {
            bruto = bruto.add(contracheque.getBruto());
            descontos = descontos.add(contracheque.getDescontos());
            liquido = liquido.add(contracheque.getLiquido());
            linhas.append(linha).append(FIM_DE_LINHA);
        }

        void escrever(StringBuilder texto, String linhaDeTotal) {
            String faixa = "=".repeat(LARGURA);
            String linhaDoTitulo = "===================== " + titulo + " ";
            texto.append(faixa).append(FIM_DE_LINHA)
                    .append(linhaDoTitulo).append("=".repeat(LARGURA - linhaDoTitulo.length())).append(FIM_DE_LINHA)
                    .append(faixa).append(FIM_DE_LINHA)
                    .append(colunas).append(FIM_DE_LINHA)
                    .append(sublinhado).append(FIM_DE_LINHA)
                    .append(linhas)
                    .append(FIM_DE_LINHA)
                    .append(linhaDeTotal).append(FIM_DE_LINHA)
                    .append(FIM_DE_LINHA);
        }
    }
}
