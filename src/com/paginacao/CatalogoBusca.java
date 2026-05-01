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
        
        try {
            List<Livro> livrosFiltrados = buscar(livros, termo);
            
            Paginador<Livro> paginador = new Paginador<>(); 
            
            List<Livro> listaPaginada = paginador.paginar(livrosFiltrados, pagina, tamanhoPagina);
            return listaPaginada;
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    public List<Livro> buscar(List<Livro> livros, String termo) {
        return buscar(livros, termo, false);
    }

    public List<Livro> buscar(List<Livro> livros, String termo, boolean caseSensitive) {
        
        if (livros == null) {
            throw new IllegalArgumentException("Lista de livros não pode ser nula");
        }
        if (termo == null) {
            throw new IllegalArgumentException("Termo de busca não pode ser nulo");
        }

        if (caseSensitive) {
            final String termoNormalizado = termo.trim();
            return termoNormalizado.isEmpty()
                    ? livros
                    : livros.stream()
                    .filter(livro -> livro.getTitulo().contains(termoNormalizado))
                    .collect(Collectors.toList());
        } else {
            final String termoNormalizado = termo.trim().toLowerCase();
            return termoNormalizado.isEmpty()
                    ? livros
                    : livros.stream()
                    .filter(livro -> livro.getTitulo().toLowerCase().contains(termoNormalizado))
                    .collect(Collectors.toList());
        }
    }

}
