package com.paginacao;

import java.util.Arrays;
import java.util.List;

// Testes isolados dos exercícios
public class TestePaginador {
  public static void main (String[] args) {
    List<String> nomes = Arrays.asList("A", "B", "C", "D", "E");
    Paginador<String> paginador = new Paginador<>();

    // Testando Exercício
    System.out.println("Testando o Exercício 2 - PrimeirosN");
    List<String> resultado2 = paginador.primeirosN(nomes, 3);

    System.out.println("Esperado: [A, B, C]");
    System.out.println("Obtido: " + resultado2);

    // Teste de borda - pedir mais do que existe
    List<String> resultadoMaior2 = paginador.primeirosN(nomes, 10);
    System.out.println("Teste: n = 10 - Maior que a lista: " + resultadoMaior2);

    System.out.println("Testando o Exercício 3 - IgnorarN");
    List<String> resultado3 = paginador.ignorarN(nomes, 2);

    System.out.println("Esperado: [C, D, E]");
    System.out.println("Obtido: " + resultado3);

    List<String> resultadoMaior3 = paginador.ignorarN(nomes, 10);
    System.out.println("Teste: n = 10 - Maior que a lista: " + resultadoMaior3);

    System.out.println("Esperado: []");
    System.out.println("Obtido:   " + resultadoMaior3);
  }
}
