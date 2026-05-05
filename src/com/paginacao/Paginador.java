package com.paginacao;

import java.util.List;
import java.util.stream.Collectors;

public class Paginador<T> {

    /**
     * Exercício 1 — Retorna os elementos da página solicitada.
     *
     * Estratégia: para chegar na página P com tamanho T, pulamos P*T elementos
     * e então limitamos aos próximos T.
     */
    public List<T> paginar(List<T> lista, int pagina, int tamanhoPagina) {
        return lista.stream()
                .skip((long) pagina * tamanhoPagina)
                .limit(tamanhoPagina)
                .collect(Collectors.toList());
    }

    /**
     * Exercício 2 — Retorna apenas os primeiros N elementos da lista.
     *
     * limit(n) é uma operação de curto-circuito: para de processar o stream
     * assim que n elementos forem coletados, sem percorrer o restante.
     */
    public List<T> primeirosN(List<T> lista, int n) {
        return lista.stream()
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * Exercício 3 — Ignora os primeiros N elementos e retorna o restante.
     *
     * skip(n) descarta os n primeiros elementos do stream e repassa
     * todos os subsequentes para o pipeline.
     */
    public List<T> ignorarN(List<T> lista, int n) {
        return lista.stream()
                .skip(n)
                .collect(Collectors.toList());
    }

    /**
     * Exercício 4 — Calcula o total de páginas necessárias.
     *
     * Usa divisão com teto (ceiling division) para garantir que um
     * "resto" de elementos sempre resulte em uma página extra.
     *
     * Fórmula: (tamanho + tamanhoPagina - 1) / tamanhoPagina
     * Equivalente a Math.ceil((double) tamanho / tamanhoPagina),
     * mas sem precisar de cast para double.
     *
     * Caso de borda: lista vazia retorna 0 (não há páginas).
     */
    public int totalPaginas(List<T> lista, int tamanhoPagina) {
        if (lista.isEmpty()) return 0;
        return (lista.size() + tamanhoPagina - 1) / tamanhoPagina;
    }
}
