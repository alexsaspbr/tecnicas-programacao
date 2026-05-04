package com.paginacao;

import java.util.Collections;
import java.util.List;


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
        // TODO: implemente usando filter, skip e limit
        if(livros == null  || livros.isEmpty()){
            return Collections.emptyList();
        } else if(pagina <  0  ||  tamanhoPagina <= 0) {
            throw new UnsupportedOperationException("Não implementado.");
        }  else {
            return livros.stream()
                    .filter(livro -> livro.getTitulo().toLowerCase().contains(termo.toLowerCase()))
                    .skip((long) pagina  * tamanhoPagina) /*casting (long)*/
                    .limit(tamanhoPagina)
                    .toList();
        }

    }
}
