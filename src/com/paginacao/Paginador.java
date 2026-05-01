package com.paginacao;

import java.util.List;
import java.util.stream.Collectors;

public class Paginador<T> {

    /**
     * Exercício 1 — Retorna os elementos da página solicitada. ✔
     *
     * A paginação é baseada em índice zero: pagina=0 retorna a primeira página.
     *
     * Dica: para chegar à página correta, quantos elementos você precisa pular?
     *
     * @param lista         lista completa de elementos
     * @param pagina        número da página desejada (começa em 0)
     * @param tamanhoPagina quantidade máxima de elementos por página
     * @return lista com os elementos da página solicitada
     */
    public List<T> paginar(List<T> lista, int pagina, int tamanhoPagina) {
        // TODO: implemente usando skip e limit
        return lista.stream()
                .skip((long) pagina * tamanhoPagina)
                .limit(tamanhoPagina)
                .toList();

    //    throw new UnsupportedOperationException("Não implementado"); removido após implementação
    }

    /**
     * Exercício 2 — Retorna apenas os primeiros N elementos da lista. ✔
     *
     * @param lista lista completa de elementos
     * @param n     quantidade de elementos a retornar
     * @return lista com no máximo n elementos
     */
    public List<T> primeirosN(List<T> lista, int n) {
        // TODO: implemente usando limit
        return lista.stream()
                .limit(n)
                .toList();
        // throw new UnsupportedOperationException("Não implementado"); removido após implementação
    }

    /**
     * Exercício 3 — Ignora os primeiros N elementos e retorna o restante. ✔
     *
     * @param lista lista completa de elementos
     * @param n     quantidade de elementos a ignorar
     * @return lista sem os primeiros n elementos
     */
    public List<T> ignorarN(List<T> lista, int n) {
        // TODO: implemente usando skip
        return lista.stream()
                .skip(n)
                .toList();
      //  throw new UnsupportedOperationException("Não implementado"); removido após implementação
    }

    /**
     * Exercício 4 — Calcula o total de páginas necessárias para exibir toda a lista. ✔
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
        if (tamanhoPagina <= 0) {
            throw new IllegalArgumentException("tamanhoPagina deve ser maior que zero");
        }

        return (lista.size() + tamanhoPagina - 1) / tamanhoPagina;

        // throw new UnsupportedOperationException("Não implementado"); removido após implementação
    }
}
