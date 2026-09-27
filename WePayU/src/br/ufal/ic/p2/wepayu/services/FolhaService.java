package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;
import br.ufal.ic.p2.wepayu.models.CartaoDePonto;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.ResultadoVenda;
import br.ufal.ic.p2.wepayu.models.TaxaServico;
import br.ufal.ic.p2.wepayu.models.TipoEmpregado;
import br.ufal.ic.p2.wepayu.utils.Formatador;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Serviço responsável pelo cálculo e emissão de folhas de pagamento da empresa.
 */
public class FolhaService {

    private final EmpregadoService empregadoService;
    private LocalDate ultimaDataFolhaProcessada;

    public FolhaService(EmpregadoService empregadoService) {
        this.empregadoService = empregadoService;
    }

    public void limpar() {
        this.ultimaDataFolhaProcessada = null;
    }

    public LocalDate getUltimaDataFolhaProcessada() {
        return ultimaDataFolhaProcessada;
    }

    public void setUltimaDataFolhaProcessada(LocalDate ultimaDataFolhaProcessada) {
        this.ultimaDataFolhaProcessada = ultimaDataFolhaProcessada;
    }

    private record ItemHorista(String nome, int horas, int extra, double bruto, double descontos, double liquido, String metodo) {}
    private record ItemAssalariado(String nome, double bruto, double descontos, double liquido, String metodo) {}
    private record ItemComissionado(String nome, double fixo, double vendas, double comissao, double bruto, double descontos, double liquido, String metodo) {}

    private record ResultadoFolha(String texto, double totalFolha) {}

    /**
     * Calcula o total bruto da folha para a data informada sem alterar o estado.
     *
     * @param dataStr data no formato d/M/yyyy
     * @return total bruto da folha formatado
     */
    public String totalFolha(String dataStr) throws DataInvalidaException {
        LocalDate data = Formatador.converterData(dataStr);
        if (data == null) {
            throw new DataInvalidaException();
        }
        ResultadoFolha resultado = processarFolha(data, false);
        return Formatador.formatarMoeda(resultado.totalFolha());
    }

    /**
     * Emite a folha de pagamento para a data indicada e grava o relatório em arquivo.
     *
     * @param dataStr data no formato d/M/yyyy
     * @param saida   caminho do arquivo de saída
     */
    public void rodaFolha(String dataStr, String saida) throws DataInvalidaException, IOException {
        LocalDate data = Formatador.converterData(dataStr);
        if (data == null) {
            throw new DataInvalidaException();
        }
        boolean persistir = (ultimaDataFolhaProcessada == null || data.isAfter(ultimaDataFolhaProcessada));
        ResultadoFolha resultado = processarFolha(data, persistir);
        if (persistir) {
            ultimaDataFolhaProcessada = data;
        }

        File arquivoSaida = new File(saida);
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(arquivoSaida), StandardCharsets.ISO_8859_1)) {
            writer.write(resultado.texto());
        }
    }

    private ResultadoFolha processarFolha(LocalDate data, boolean persistir) {
        boolean isSexta = (data.getDayOfWeek() == DayOfWeek.FRIDAY);

        LocalDate inicioCom = LocalDate.of(2005, 1, 14);
        boolean isComDay = isSexta && !data.isBefore(inicioCom) && (ChronoUnit.DAYS.between(inicioCom, data) % 14 == 0);

        boolean isAssalariadoDay = (data.getDayOfMonth() == data.lengthOfMonth());

        List<Empregado> todos = new ArrayList<>(empregadoService.getEmpregados().values());
        todos.sort(Comparator.comparing(Empregado::getNome));

        List<ItemHorista> horistas = new ArrayList<>();
        List<ItemAssalariado> assalariados = new ArrayList<>();
        List<ItemComissionado> comissionados = new ArrayList<>();

        // Processar Horistas
        if (isSexta) {
            LocalDate inicioSemana = data.minusDays(6);
            LocalDate fimSemana = data;

            for (Empregado e : todos) {
                if (e.getTipo() == TipoEmpregado.HORISTA) {
                    double hNormais = 0.0;
                    double hExtras = 0.0;
                    for (CartaoDePonto c : e.getCartoes()) {
                        LocalDate d = Formatador.converterData(c.getData());
                        if (d != null && !d.isBefore(inicioSemana) && !d.isAfter(fimSemana)) {
                            if (c.getHoras() <= 8.0) {
                                hNormais += c.getHoras();
                            } else {
                                hNormais += 8.0;
                                hExtras += (c.getHoras() - 8.0);
                            }
                        }
                    }
                    double bruto = hNormais * e.getSalario() + hExtras * (e.getSalario() * 1.5);
                    double desconto = 0.0;

                    if (e.isSindicalizado()) {
                        double debitoSemana = 7.0 * e.getTaxaSindical();
                        double taxasPendentes = 0.0;
                        for (TaxaServico t : e.getTaxasServico()) {
                            LocalDate dt = Formatador.converterData(t.getData());
                            if (!t.isCobrada() && dt != null && !dt.isAfter(fimSemana)) {
                                taxasPendentes += t.getValor();
                            }
                        }
                        double totalDevido = e.getDebitoSindicalAcumulado() + debitoSemana + taxasPendentes;
                        if (bruto > 0 && bruto >= totalDevido) {
                            desconto = totalDevido;
                            if (persistir) {
                                e.setDebitoSindicalAcumulado(0.0);
                                for (TaxaServico t : e.getTaxasServico()) {
                                    LocalDate dt = Formatador.converterData(t.getData());
                                    if (!t.isCobrada() && dt != null && !dt.isAfter(fimSemana)) {
                                        t.setCobrada(true);
                                    }
                                }
                            }
                        } else if (bruto > 0) {
                            desconto = bruto;
                            if (persistir) {
                                e.setDebitoSindicalAcumulado(totalDevido - bruto);
                            }
                        } else {
                            desconto = 0.0;
                            if (persistir) {
                                e.setDebitoSindicalAcumulado(e.getDebitoSindicalAcumulado() + debitoSemana);
                            }
                        }
                    }

                    double liquido = bruto - desconto;
                    horistas.add(new ItemHorista(e.getNome(), (int) hNormais, (int) hExtras, bruto, desconto, liquido, e.getDescricaoMetodoPagamento()));
                }
            }
        }

        // Processar Assalariados
        if (isAssalariadoDay) {
            int diasNoMes = data.getDayOfMonth();

            for (Empregado e : todos) {
                if (e.getTipo() == TipoEmpregado.ASSALARIADO) {
                    double bruto = e.getSalario();
                    double desconto = 0.0;

                    if (e.isSindicalizado()) {
                        double taxasPendentes = 0.0;
                        for (TaxaServico t : e.getTaxasServico()) {
                            LocalDate dt = Formatador.converterData(t.getData());
                            if (!t.isCobrada() && dt != null && !dt.isAfter(data)) {
                                taxasPendentes += t.getValor();
                            }
                        }
                        desconto = diasNoMes * e.getTaxaSindical() + taxasPendentes;
                        if (persistir) {
                            for (TaxaServico t : e.getTaxasServico()) {
                                LocalDate dt = Formatador.converterData(t.getData());
                                if (!t.isCobrada() && dt != null && !dt.isAfter(data)) {
                                    t.setCobrada(true);
                                }
                            }
                        }
                    }

                    double liquido = bruto - desconto;
                    assalariados.add(new ItemAssalariado(e.getNome(), bruto, desconto, liquido, e.getDescricaoMetodoPagamento()));
                }
            }
        }

        // Processar Comissionados
        if (isComDay) {
            LocalDate inicioQuinzena = data.minusDays(13);
            LocalDate fimQuinzena = data;

            for (Empregado e : todos) {
                if (e.getTipo() == TipoEmpregado.COMISSIONADO) {
                    double fixo = ((int) (e.getSalario() * 24.0 / 52.0 * 100.0)) / 100.0;
                    double vendasPeriodo = 0.0;
                    for (ResultadoVenda v : e.getVendas()) {
                        LocalDate dv = Formatador.converterData(v.getData());
                        if (dv != null && !dv.isBefore(inicioQuinzena) && !dv.isAfter(fimQuinzena)) {
                            vendasPeriodo += v.getValor();
                        }
                    }
                    double comissao = ((int) (vendasPeriodo * e.getTaxaDeComissao() * 100.0)) / 100.0;
                    double bruto = fixo + comissao;
                    double desconto = 0.0;

                    if (e.isSindicalizado()) {
                        double taxasPendentes = 0.0;
                        for (TaxaServico t : e.getTaxasServico()) {
                            LocalDate dt = Formatador.converterData(t.getData());
                            if (!t.isCobrada() && dt != null && !dt.isAfter(fimQuinzena)) {
                                taxasPendentes += t.getValor();
                            }
                        }
                        desconto = 14.0 * e.getTaxaSindical() + taxasPendentes;
                        if (persistir) {
                            for (TaxaServico t : e.getTaxasServico()) {
                                LocalDate dt = Formatador.converterData(t.getData());
                                if (!t.isCobrada() && dt != null && !dt.isAfter(fimQuinzena)) {
                                    t.setCobrada(true);
                                }
                            }
                        }
                    }

                    double liquido = bruto - desconto;
                    comissionados.add(new ItemComissionado(e.getNome(), fixo, vendasPeriodo, comissao, bruto, desconto, liquido, e.getDescricaoMetodoPagamento()));
                }
            }
        }

        // Montar texto do relatório
        StringBuilder sb = new StringBuilder();
        sb.append("FOLHA DE PAGAMENTO DO DIA ").append(data).append("\r\n");
        sb.append("====================================\r\n");
        sb.append("\r\n");

        // HORISTAS
        sb.append("===============================================================================================================================\r\n");
        sb.append("===================== HORISTAS ================================================================================================\r\n");
        sb.append("===============================================================================================================================\r\n");
        sb.append("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo\r\n");
        sb.append("==================================== ===== ===== ============= ========= =============== ======================================\r\n");

        int totHHoras = 0;
        int totHExtra = 0;
        double totHBruto = 0.0;
        double totHDesc = 0.0;
        double totHLiq = 0.0;

        for (ItemHorista h : horistas) {
            totHHoras += h.horas();
            totHExtra += h.extra();
            totHBruto += h.bruto();
            totHDesc += h.descontos();
            totHLiq += h.liquido();
            sb.append(String.format(Locale.US, "%-36s %5d %5d %13s %9s %15s %s\r\n",
                    h.nome(), h.horas(), h.extra(),
                    Formatador.formatarMoeda(h.bruto()),
                    Formatador.formatarMoeda(h.descontos()),
                    Formatador.formatarMoeda(h.liquido()),
                    h.metodo()));
        }
        sb.append("\r\n");
        sb.append(String.format(Locale.US, "%-36s %5d %5d %13s %9s %15s\r\n",
                "TOTAL HORISTAS", totHHoras, totHExtra,
                Formatador.formatarMoeda(totHBruto),
                Formatador.formatarMoeda(totHDesc),
                Formatador.formatarMoeda(totHLiq)));
        sb.append("\r\n");

        // ASSALARIADOS
        sb.append("===============================================================================================================================\r\n");
        sb.append("===================== ASSALARIADOS ============================================================================================\r\n");
        sb.append("===============================================================================================================================\r\n");
        sb.append("Nome                                             Salario Bruto Descontos Salario Liquido Metodo\r\n");
        sb.append("================================================ ============= ========= =============== ======================================\r\n");

        double totABruto = 0.0;
        double totADesc = 0.0;
        double totALiq = 0.0;

        for (ItemAssalariado a : assalariados) {
            totABruto += a.bruto();
            totADesc += a.descontos();
            totALiq += a.liquido();
            sb.append(String.format(Locale.US, "%-48s %13s %9s %15s %s\r\n",
                    a.nome(),
                    Formatador.formatarMoeda(a.bruto()),
                    Formatador.formatarMoeda(a.descontos()),
                    Formatador.formatarMoeda(a.liquido()),
                    a.metodo()));
        }
        sb.append("\r\n");
        sb.append(String.format(Locale.US, "%-48s %13s %9s %15s\r\n",
                "TOTAL ASSALARIADOS",
                Formatador.formatarMoeda(totABruto),
                Formatador.formatarMoeda(totADesc),
                Formatador.formatarMoeda(totALiq)));
        sb.append("\r\n");

        // COMISSIONADOS
        sb.append("===============================================================================================================================\r\n");
        sb.append("===================== COMISSIONADOS ===========================================================================================\r\n");
        sb.append("===============================================================================================================================\r\n");
        sb.append("Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo\r\n");
        sb.append("===================== ======== ======== ======== ============= ========= =============== ======================================\r\n");

        double totCFixo = 0.0;
        double totCVendas = 0.0;
        double totCCom = 0.0;
        double totCBruto = 0.0;
        double totCDesc = 0.0;
        double totCLiq = 0.0;

        for (ItemComissionado c : comissionados) {
            totCFixo += c.fixo();
            totCVendas += c.vendas();
            totCCom += c.comissao();
            totCBruto += c.bruto();
            totCDesc += c.descontos();
            totCLiq += c.liquido();
            sb.append(String.format(Locale.US, "%-21s %8s %8s %8s %13s %9s %15s %s\r\n",
                    c.nome(),
                    Formatador.formatarMoeda(c.fixo()),
                    Formatador.formatarMoeda(c.vendas()),
                    Formatador.formatarMoeda(c.comissao()),
                    Formatador.formatarMoeda(c.bruto()),
                    Formatador.formatarMoeda(c.descontos()),
                    Formatador.formatarMoeda(c.liquido()),
                    c.metodo()));
        }
        sb.append("\r\n");
        sb.append(String.format(Locale.US, "%-21s %8s %8s %8s %13s %9s %15s\r\n",
                "TOTAL COMISSIONADOS",
                Formatador.formatarMoeda(totCFixo),
                Formatador.formatarMoeda(totCVendas),
                Formatador.formatarMoeda(totCCom),
                Formatador.formatarMoeda(totCBruto),
                Formatador.formatarMoeda(totCDesc),
                Formatador.formatarMoeda(totCLiq)));
        sb.append("\r\n");

        double totalFolha = totHBruto + totABruto + totCBruto;
        sb.append("TOTAL FOLHA: ").append(Formatador.formatarMoeda(totalFolha)).append("\r\n");

        return new ResultadoFolha(sb.toString(), totalFolha);
    }
}
