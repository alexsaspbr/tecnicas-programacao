package com.paginacao;

import java.util.Arrays;
import java.util.List;

public class TestePaginador {
  public static void main (String[] args) {
    List<String> nomes = Arrays.asList("A", "B", "C", "D", "E");
    Paginador<String> paginador = new Paginador<>();

    System.out.println("Testando o Exercício 2 - PrimeirosN");
    List<String> resultado = paginador.primeirosN(nomes, 3);

    System.out.println("Esperado: [A, B, C]");
    System.out.println("Obtido: " + resultado);

    // Teste de borda - pedir mais do que existe
    List<String> resultadoMaior = paginador.primeirosN(nomes, 10);
    System.out.println("Teste: n = 10 - Maior que a lista: " + resultadoMaior);

  }
}
