package com.paginacao;

import java.util.List;
import java.util.stream.Collectors;

public class Paginador<T> {

    /**
     * Exercício 1 — Retorna os elementos da página solicitada.
     */
    public List<T> paginar(List<T> lista, int pagina, int tamanhoPagina) {
        return lista.stream()
                .skip((long) pagina * tamanhoPagina)
                .limit(tamanhoPagina)
                .collect(Collectors.toList());
    }

    /**
     * Exercício 2 — Retorna apenas os primeiros N elementos da lista.
     */
    public List<T> primeirosN(List<T> lista, int n) {
        return lista.stream()
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * Exercício 3 — Ignora os primeiros N elementos e retorna o restante.
     */
    public List<T> ignorarN(List<T> lista, int n) {
        return lista.stream()
                .skip(n)
                .collect(Collectors.toList());
    }

    /**
     * Exercício 4 — Calcula o total de páginas necessárias.
     */
    public int totalPaginas(List<T> lista, int tamanhoPagina) {
        if (lista.isEmpty()) return 0;

        return (lista.size() + tamanhoPagina - 1) / tamanhoPagina;
    }
}