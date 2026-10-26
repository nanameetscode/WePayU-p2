package br.ufal.ic.p2.wepayu.models.empregado;

import br.ufal.ic.p2.wepayu.exceptions.empregado.EmpregadoNaoEhComissionadoException;
import br.ufal.ic.p2.wepayu.exceptions.empregado.EmpregadoNaoEhHoristaException;
import br.ufal.ic.p2.wepayu.exceptions.empregado.EnderecoNuloException;
import br.ufal.ic.p2.wepayu.exceptions.empregado.NomeNuloException;
import br.ufal.ic.p2.wepayu.models.Periodo;
import br.ufal.ic.p2.wepayu.models.agenda.AgendaDePagamento;
import br.ufal.ic.p2.wepayu.models.folha.Contracheque;
import br.ufal.ic.p2.wepayu.models.pagamento.MetodoEmMaos;
import br.ufal.ic.p2.wepayu.models.pagamento.MetodoPagamento;
import br.ufal.ic.p2.wepayu.models.remuneracao.Remuneracao;
import br.ufal.ic.p2.wepayu.models.sindicato.CobrancaSindical;
import br.ufal.ic.p2.wepayu.models.sindicato.NaoSindicalizado;
import br.ufal.ic.p2.wepayu.models.sindicato.Sindicalizacao;
import br.ufal.ic.p2.wepayu.utils.Validacao;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Alguém que recebe salário da empresa.
 *
 * <p>O que varia de um empregado para outro é delegado a objetos próprios, trocados
 * quando o empregado muda: a {@link Remuneracao} (horista, assalariado ou
 * comissionado), a {@link AgendaDePagamento}, o {@link MetodoPagamento} e a
 * {@link Sindicalizacao}. Por isso trocar o tipo do empregado não cria um empregado
 * novo e nada se perde: cartões de ponto, vendas e a situação no sindicato continuam
 * com ele.
 *
 * <p>Cartões e vendas ficam no {@link LancamentosDoEmpregado}. Só o horista lança
 * cartões e só o comissionado lança vendas, mas o histórico fica guardado se o tipo
 * mudar e volta a ser usado se o tipo antigo voltar. Todas as partes são imutáveis;
 * o empregado só troca uma parte por outra.
 */
public class Empregado {

    private String nome;
    private String endereco;
    private Remuneracao remuneracao;
    private AgendaDePagamento agenda;
    private MetodoPagamento metodoPagamento;
    private Sindicalizacao sindicalizacao;
    private LancamentosDoEmpregado lancamentos;
    private LocalDate admissao;
    private LocalDate ultimoPagamento;

    /**
     * Empregado recém-contratado: recebe em mãos, na agenda padrão do seu tipo, não é
     * sindicalizado e não tem histórico.
     */
    public Empregado(String nome, String endereco, Remuneracao remuneracao)
            throws NomeNuloException, EnderecoNuloException {

        this(nome, endereco, remuneracao, remuneracao.getTipo().getAgendaPadrao(), new MetodoEmMaos(),
                new NaoSindicalizado(), new LancamentosDoEmpregado(),
                remuneracao.contratacaoNaAdmissao().orElse(null), null);
    }

    /**
     * @param ultimoPagamento dia do último pagamento efetivado, ou {@code null} se nunca recebeu
     */
    @ConstructorProperties({"nome", "endereco", "remuneracao", "agenda", "metodoPagamento", "sindicalizacao",
            "lancamentos", "admissao", "ultimoPagamento"})
    public Empregado(String nome, String endereco, Remuneracao remuneracao, AgendaDePagamento agenda,
            MetodoPagamento metodoPagamento, Sindicalizacao sindicalizacao, LancamentosDoEmpregado lancamentos,
            LocalDate admissao, LocalDate ultimoPagamento) throws NomeNuloException, EnderecoNuloException {

        this.nome = validarNome(nome);
        this.endereco = validarEndereco(endereco);
        this.remuneracao = Objects.requireNonNull(remuneracao);
        this.agenda = Objects.requireNonNull(agenda);
        this.metodoPagamento = Objects.requireNonNull(metodoPagamento);
        this.sindicalizacao = Objects.requireNonNull(sindicalizacao);
        this.lancamentos = Objects.requireNonNull(lancamentos);
        this.admissao = admissao;
        this.ultimoPagamento = ultimoPagamento;
    }

    private Empregado(Empregado original) {
        this.nome = original.nome;
        this.endereco = original.endereco;
        this.remuneracao = original.remuneracao;
        this.agenda = original.agenda;
        this.metodoPagamento = original.metodoPagamento;
        this.sindicalizacao = original.sindicalizacao;
        this.lancamentos = original.lancamentos;
        this.admissao = original.admissao;
        this.ultimoPagamento = original.ultimoPagamento;
    }

    /**
     * @throws NomeNuloException se o nome faltar
     */
    public static String validarNome(String nome) throws NomeNuloException {
        return Validacao.exigirPreenchido(nome, NomeNuloException::new);
    }

    /**
     * @throws EnderecoNuloException se o endereço faltar
     */
    public static String validarEndereco(String endereco) throws EnderecoNuloException {
        return Validacao.exigirPreenchido(endereco, EnderecoNuloException::new);
    }

    /**
     * @return cópia independente; as partes, imutáveis, são compartilhadas
     */
    public Empregado copiar() {
        return new Empregado(this);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) throws NomeNuloException {
        this.nome = validarNome(nome);
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) throws EnderecoNuloException {
        this.endereco = validarEndereco(endereco);
    }

    public Remuneracao getRemuneracao() {
        return remuneracao;
    }

    /**
     * Troca o salário ou a comissão, mantendo todo o resto.
     */
    public void alterarRemuneracao(Remuneracao novaRemuneracao) {
        this.remuneracao = Objects.requireNonNull(novaRemuneracao);
    }

    /**
     * Troca o tipo do empregado, que passa para a agenda padrão do novo tipo. A data de
     * contratação não muda; só o horista que ainda não tinha sido contratado (sem
     * cartão) recebe a contratação do novo tipo.
     */
    public void trocarTipo(Remuneracao novaRemuneracao) {
        alterarRemuneracao(novaRemuneracao);
        this.agenda = novaRemuneracao.getTipo().getAgendaPadrao();
        if (getDataDeContratacao().isEmpty()) {
            this.admissao = novaRemuneracao.contratacaoNaAdmissao().orElse(null);
        }
    }

    public AgendaDePagamento getAgenda() {
        return agenda;
    }

    public void alterarAgenda(AgendaDePagamento novaAgenda) {
        this.agenda = Objects.requireNonNull(novaAgenda);
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    public void alterarMetodoPagamento(MetodoPagamento novoMetodo) {
        this.metodoPagamento = Objects.requireNonNull(novoMetodo);
    }

    /**
     * @return descrição da forma de pagamento no relatório da folha
     */
    public String getDescricaoMetodoPagamento() {
        return metodoPagamento.getDescricao(endereco);
    }

    public Sindicalizacao getSindicalizacao() {
        return sindicalizacao;
    }

    /**
     * Filia, desfilia ou atualiza a filiação ao sindicato.
     */
    public void alterarSindicalizacao(Sindicalizacao novaSituacao) {
        this.sindicalizacao = Objects.requireNonNull(novaSituacao);
    }

    public LancamentosDoEmpregado getLancamentos() {
        return lancamentos;
    }

    /**
     * @throws EmpregadoNaoEhHoristaException se o empregado não for horista
     */
    public void exigirHorista() throws EmpregadoNaoEhHoristaException {
        remuneracao.comoHorista();
    }

    /**
     * @throws EmpregadoNaoEhComissionadoException se o empregado não for comissionado
     */
    public void exigirComissionado() throws EmpregadoNaoEhComissionadoException {
        remuneracao.comoComissionado();
    }

    /**
     * @throws EmpregadoNaoEhHoristaException se o empregado não for horista
     */
    public void lancarCartao(CartaoDePonto cartao) throws EmpregadoNaoEhHoristaException {
        exigirHorista();
        lancamentos = lancamentos.comCartao(cartao);
    }

    /**
     * @throws EmpregadoNaoEhComissionadoException se o empregado não for comissionado
     */
    public void lancarVenda(ResultadoVenda venda) throws EmpregadoNaoEhComissionadoException {
        exigirComissionado();
        lancamentos = lancamentos.comVenda(venda);
    }

    /**
     * @throws EmpregadoNaoEhHoristaException se o empregado não for horista
     */
    public BigDecimal getHorasNormais(Periodo periodo) throws EmpregadoNaoEhHoristaException {
        exigirHorista();
        return lancamentos.horasNormais(periodo);
    }

    /**
     * @throws EmpregadoNaoEhHoristaException se o empregado não for horista
     */
    public BigDecimal getHorasExtras(Periodo periodo) throws EmpregadoNaoEhHoristaException {
        exigirHorista();
        return lancamentos.horasExtras(periodo);
    }

    /**
     * @throws EmpregadoNaoEhComissionadoException se o empregado não for comissionado
     */
    public BigDecimal getVendasRealizadas(Periodo periodo) throws EmpregadoNaoEhComissionadoException {
        exigirComissionado();
        return lancamentos.totalVendido(periodo);
    }

    /**
     * A contratação é fixada na admissão e não muda com o tipo: 1/1/2005 para quem entra
     * como assalariado ou comissionado e, para quem entra como horista, o primeiro
     * cartão de ponto (us7). Um cartão anterior à data fixada a antecipa.
     *
     * @return dia da contratação, ou vazio para o horista que ainda não lançou cartão
     */
    public Optional<LocalDate> getDataDeContratacao() {
        return Stream.of(Optional.ofNullable(admissao), lancamentos.dataDoPrimeiroCartao())
                .flatMap(Optional::stream)
                .min(Comparator.naturalOrder());
    }

    /**
     * @return dia da contratação fixado na admissão, ou {@code null} para o horista
     *         (gravado no XML)
     */
    public LocalDate getAdmissao() {
        return admissao;
    }

    /**
     * @return se o empregado já tinha sido contratado no dia
     */
    public boolean contratadoAte(LocalDate dia) {
        return getDataDeContratacao().filter(contratacao -> !contratacao.isAfter(dia)).isPresent();
    }

    /**
     * Quem ainda não foi contratado aparece zerado na folha, como nos relatórios de
     * referência; as semanas da agenda contam então da data de contratação padrão.
     */
    public boolean recebeEm(LocalDate dia) {
        LocalDate contratacao = getDataDeContratacao().orElse(Remuneracao.DATA_DE_CONTRATACAO_PADRAO);
        return agenda.ehDiaDePagamento(dia, contratacao);
    }

    /**
     * Calcula o pagamento do dia sem alterar o empregado.
     */
    public Contracheque gerarContracheque(LocalDate dia) {
        return remuneracao.gerarContracheque(this, dia);
    }

    /**
     * Efetiva o pagamento do dia: aplica a cobrança do sindicato e marca o dia como o
     * último pagamento. Um dia já pago, ou anterior a ele, não é pago de novo.
     */
    public void registrarPagamento(LocalDate dia, CobrancaSindical cobranca) {
        if (ultimoPagamento != null && !dia.isAfter(ultimoPagamento)) {
            return;
        }
        this.sindicalizacao = cobranca.situacaoAposPagamento();
        this.ultimoPagamento = dia;
    }

    /**
     * Tira do período os dias que um pagamento anterior já cobriu, para que nada seja
     * pago ou cobrado duas vezes quando o tipo ou a agenda muda. Na reimpressão de um
     * dia já pago, devolve o próprio período.
     */
    public Periodo semDiasJaPagos(Periodo periodo) {
        boolean pagoNoMeioDoPeriodo = ultimoPagamento != null
                && !ultimoPagamento.isBefore(periodo.inicio())
                && ultimoPagamento.isBefore(periodo.fim());
        return pagoNoMeioDoPeriodo ? new Periodo(ultimoPagamento.plusDays(1), periodo.fim()) : periodo;
    }

    /**
     * @return dia do último pagamento efetivado, ou {@code null} se nunca recebeu (gravado no XML)
     */
    public LocalDate getUltimoPagamento() {
        return ultimoPagamento;
    }
}
