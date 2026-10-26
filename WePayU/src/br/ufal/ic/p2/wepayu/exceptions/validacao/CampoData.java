package br.ufal.ic.p2.wepayu.exceptions.validacao;

/**
 * Campos de data recebidos dos scripts. Cada um tem a sua mensagem de data
 * inválida, todas tratadas por {@link DataInvalidaException}.
 */
public enum CampoData {

    DATA("Data invalida."),
    DATA_INICIAL("Data inicial invalida."),
    DATA_FINAL("Data final invalida.");

    private final String mensagemInvalida;

    CampoData(String mensagemInvalida) {
        this.mensagemInvalida = mensagemInvalida;
    }

    String getMensagemInvalida() {
        return mensagemInvalida;
    }
}
