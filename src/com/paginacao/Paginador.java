package com.paginacao;

import java.util.List;
import java.util.stream.Collectors;

public class Paginador<T> {

    public List<T> paginar(List<T> lista, int pagina, int tamanhoPagina) {
        return lista.stream()
                .skip((long) pagina * tamanhoPagina)
                .limit(tamanhoPagina)
                .collect(Collectors.toList());
    }

    /**
     * Exercício 2 — Retorna apenas os primeiros N elementos da lista.
     *
     * @param lista lista completa de elementos
     * @param n     quantidade de elementos a retornar
     * @return lista com no máximo n elementos
     */
    public List<T> primeirosN(List<T> lista, int n) {
        // TODO: implemente usando limit
        throw new UnsupportedOperationException("Não implementado");
    }

    /**
     * Exercício 3 — Ignora os primeiros N elementos e retorna o restante.
     *
     * @param lista lista completa de elementos
     * @param n     quantidade de elementos a ignorar
     * @return lista sem os primeiros n elementos
     */
    public List<T> ignorarN(List<T> lista, int n) {
        // TODO: implemente usando skip
        throw new UnsupportedOperationException("Não implementado");
    }

    /**
     * Exercício 4 — Calcula o total de páginas necessárias para exibir toda a lista.
     *
     * Exemplo: 10 elementos com tamanhoPagina=3 exige 4 páginas (3+3+3+1).
     *
     * Dica: use divisão inteira e pense nos casos de borda.
     *
     * @param lista         lista completa de elementos
     * @param tamanhoPagina quantidade máxima de elementos por página
     * @return total de páginas
     */
    public int totalPaginas(List<T> lista, int tamanhoPagina) {
        // TODO: implemente
        throw new UnsupportedOperationException("Não implementado");
    }
}
