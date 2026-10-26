package br.ufal.ic.p2.wepayu;

import easyaccept.EasyAccept;

/**
 * Roda, em ordem, os scripts de aceitação de US1 a US10.
 *
 * <p>A ordem importa: cada script {@code usN_1} confere o estado que o {@code usN}
 * gravou em arquivo ao encerrar o sistema.
 */
public class Main {

    private static final String[] SCRIPTS = {
        "us1", "us1_1", "us2", "us2_1", "us3", "us3_1", "us4", "us4_1",
        "us5", "us5_1", "us6", "us6_1", "us7", "us8", "us9", "us9_1", "us10", "us10_1"
    };

    public static void main(String[] args) {
        for (String script : SCRIPTS) {
            EasyAccept.main(new String[]{Facade.class.getName(), "tests/" + script + ".txt"});
        }
    }
}
