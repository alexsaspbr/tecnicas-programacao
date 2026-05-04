package com.paginacao;

import java.util.List;
import java.util.stream.Collectors;

public class CatalogoBusca {

    public List<Livro> buscarComPaginacao(List<Livro> livros, String termo,
                                          int pagina, int tamanhoPagina) {

        String termoLower = termo.toLowerCase();

        return livros.stream()
                .filter(l -> l.getTitulo().toLowerCase().contains(termoLower))
                .skip((long) pagina * tamanhoPagina)
                .limit(tamanhoPagina)
                .collect(Collectors.toList());
    }
}
