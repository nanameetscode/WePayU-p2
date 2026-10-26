package br.ufal.ic.p2.wepayu.models.folha;

/**
 * Quem trata contracheques de forma diferente conforme o tipo de empregado, como o
 * relatório da folha, que tem uma seção para cada tipo (padrão Visitor).
 */
public interface VisitanteDeContracheque {

    void visitarHorista(Contracheque contracheque, ProventosHorista proventos);

    void visitarAssalariado(Contracheque contracheque, ProventosAssalariado proventos);

    void visitarComissionado(Contracheque contracheque, ProventosComissionado proventos);
}
