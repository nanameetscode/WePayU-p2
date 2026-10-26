package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;
import br.ufal.ic.p2.wepayu.exceptions.validacao.CampoData;
import br.ufal.ic.p2.wepayu.models.Cadastro;
import br.ufal.ic.p2.wepayu.models.folha.Folha;
import br.ufal.ic.p2.wepayu.relatorio.RelatorioFolha;
import br.ufal.ic.p2.wepayu.utils.Formatador;
import br.ufal.ic.p2.wepayu.utils.LeitorDeEntrada;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Cálculo e emissão da folha de pagamento.
 *
 * <p>Quem recebe, quanto e com que descontos é decidido por cada empregado; este
 * serviço só reúne os contracheques do dia, grava o relatório e, por último,
 * efetiva os pagamentos.
 */
public class FolhaService {

    private final Cadastro cadastro;

    public FolhaService(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    /**
     * @return o total bruto que a folha do dia pagaria, sem rodá-la
     */
    public String totalFolha(String data) throws WePayUException {
        return Formatador.moeda(calcular(LeitorDeEntrada.lerData(data, CampoData.DATA)).getTotalBruto());
    }

    /**
     * Roda a folha do dia: grava o relatório e só então efetiva os pagamentos, para que
     * uma falha ao gravar não deixe pagamentos efetivados sem relatório. Rodar de novo a
     * folha de um dia já efetivado grava o mesmo relatório, sem pagar ninguém outra vez.
     *
     * @param saida arquivo do relatório
     */
    public void rodaFolha(String data, String saida) throws WePayUException {
        LocalDate dia = LeitorDeEntrada.lerData(data, CampoData.DATA);
        Optional<String> jaEmitido = cadastro.getRelatorioEmitido(dia);
        if (jaEmitido.isPresent()) {
            RelatorioFolha.gravar(jaEmitido.get(), saida);
            return;
        }
        Folha folha = calcular(dia);
        String relatorio = RelatorioFolha.gerar(folha);
        RelatorioFolha.gravar(relatorio, saida);
        folha.efetivar();
        cadastro.registrarRelatorioEmitido(dia, relatorio);
    }

    private Folha calcular(LocalDate dia) {
        return new Folha(dia, cadastro.listarPorNome().stream()
                .filter(empregado -> empregado.recebeEm(dia))
                .map(empregado -> empregado.gerarContracheque(dia))
                .toList());
    }
}
