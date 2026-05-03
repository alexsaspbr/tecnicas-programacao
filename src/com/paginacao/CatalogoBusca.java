package com.paginacao;

import java.util.List;
import java.util.stream.Collectors;

public class CatalogoBusca {

    /**
     * Exercício 5 — Busca livros pelo título e retorna a página solicitada do
     * resultado.
     */
    public List<Livro> buscarComPaginacao(List<Livro> livros, String termo,
            int pagina, int tamanhoPagina) {

        String termoNormalizado = termo.toLowerCase();

        return livros.stream()
                .filter(livro
                        -> livro.getTitulo().toLowerCase().contains(termoNormalizado)
                )
                .skip((long) pagina * tamanhoPagina)
                .limit(tamanhoPagina)
                .collect(Collectors.toList());
    }
}
