package br.ufal.ic.p2.wepayu.exceptions.validacao;

/**
 * Campos numéricos que seguem as mesmas três regras de preenchimento: não podem
 * faltar, precisam ser numéricos e não podem ser negativos.
 *
 * <p>Só a mensagem muda de um campo para outro (inclusive o gênero da palavra),
 * então cada constante guarda as suas três mensagens e as exceções
 * {@link ValorNuloException}, {@link ValorNaoNumericoException} e
 * {@link ValorNegativoException} servem a todos os campos.
 */
public enum CampoNumerico {

    SALARIO("Salario nao pode ser nulo.", "Salario deve ser numerico.", "Salario deve ser nao-negativo."),
    COMISSAO("Comissao nao pode ser nula.", "Comissao deve ser numerica.", "Comissao deve ser nao-negativa."),
    TAXA_SINDICAL("Taxa sindical nao pode ser nula.", "Taxa sindical deve ser numerica.",
            "Taxa sindical deve ser nao-negativa."),

    /** Dívida com o sindicato, que só é lida do arquivo de dados, nunca dos scripts. */
    DIVIDA_SINDICAL("Divida sindical nao pode ser nula.", "Divida sindical deve ser numerica.",
            "Divida sindical deve ser nao-negativa.");

    private final String mensagemNulo;
    private final String mensagemNaoNumerico;
    private final String mensagemNegativo;

    CampoNumerico(String mensagemNulo, String mensagemNaoNumerico, String mensagemNegativo) {
        this.mensagemNulo = mensagemNulo;
        this.mensagemNaoNumerico = mensagemNaoNumerico;
        this.mensagemNegativo = mensagemNegativo;
    }

    String getMensagemNulo() {
        return mensagemNulo;
    }

    String getMensagemNaoNumerico() {
        return mensagemNaoNumerico;
    }

    String getMensagemNegativo() {
        return mensagemNegativo;
    }
}
