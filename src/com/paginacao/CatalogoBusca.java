package com.paginacao;

import java.util.List;
import java.util.stream.Collectors;

public class CatalogoBusca {

    /**
     * Exercício 5 — Busca livros pelo título e retorna a página solicitada do resultado.
     *
     * Pipeline:
     *   1. filter  → mantém apenas livros cujo título contém o termo (case-insensitive)
     *   2. skip    → descarta as páginas anteriores à solicitada
     *   3. limit   → pega no máximo tamanhoPagina resultados
     *
     * A conversão para lowercase em ambos os lados garante busca case-insensitive
     */
    public List<Livro> buscarComPaginacao(List<Livro> livros, String termo,
                                          int pagina, int tamanhoPagina) {
        String termoLower = termo.toLowerCase();

        return livros.stream()
                .filter(livro -> livro.getTitulo().toLowerCase().contains(termoLower))
                .skip((long) pagina * tamanhoPagina)
                .limit(tamanhoPagina)
                .collect(Collectors.toList());
    }
}
