package com.paginacao;

import java.util.List;
import java.util.stream.Collectors;

public class CatalogoBusca {

    /**
     * Exercício 5 — Busca livros pelo título e retorna a página solicitada do resultado.
     *
     * A busca deve ser case-insensitive.
     * A paginação deve ocorrer APÓS o filtro (sobre os resultados filtrados).
     *
     * @param livros        lista completa de livros
     * @param termo         texto a buscar no título (case-insensitive)
     * @param pagina        número da página (começa em 0)
     * @param tamanhoPagina quantidade máxima de resultados por página
     * @return lista paginada dos livros cujo título contém o termo
     */
    public List<Livro> buscarComPaginacao(List<Livro> livros, String termo,
                                          int pagina, int tamanhoPagina) {



        String termoLowe = termo.toLowerCase();

        return livros.stream()
                .filter(livro -> livro.getTitulo() != null &&
                        livro.getTitulo().toLowerCase().contains(termoLowe))
                .skip((long) pagina * tamanhoPagina)
                .limit(tamanhoPagina)
                .collect(Collectors.toList());
    }
}
