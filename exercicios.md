# Exercícios — Streams em Java

---

### Exercício 1 — Filtrar e contar

Dada a classe `Produto` e a lista abaixo, use a API de Streams para:

a) Filtrar apenas os produtos com preço acima de R$ 100,00 e **imprimir** cada um.
b) Contar quantos produtos custam mais de R$ 100,00 e imprimir o resultado.

```java
import java.util.List;
import java.util.stream.Collectors;

public class Produto {
    private String nome;
    private double preco;

    public Produto(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public String getNome()  { return nome; }
    public double getPreco() { return preco; }
    public String toString() { return nome + " (R$ " + preco + ")"; }
}

public class Ex1FiltrarContar {

    public static void main(String[] args) {
        var produtos = List.of(
            new Produto("Caneta",   3.50),
            new Produto("Mochila",  120.00),
            new Produto("Caderno",  45.00),
            new Produto("Notebook", 3200.00),
            new Produto("Régua",    8.00),
            new Produto("Tablet",   850.00)
        );

        // a) Filtrar apenas os produtos com preço acima de R$ 100,00 e imprimir cada um.
        produtos.stream()
            .filter(p -> p.getPreco() > 100)
            .forEach(System.out::println);

        // b) Contar quantos produtos custam mais de R$ 100,00 e imprimir o resultado.
        long count = produtos.stream()
            .filter(p -> p.getPreco() > 100)
            .count();
        System.out.println("Total acima de R$ 100: " + count);
    }
}
```

**Saída esperada:**
```
Mochila (R$ 120.0)
Notebook (R$ 3200.0)
Tablet (R$ 850.0)
Total acima de R$ 100: 3
```

---

### Exercício 2 — Transformar e coletar

Usando a mesma lista de `Produto`, extraia apenas os **nomes** dos produtos, ordene-os em ordem alfabética e colete o resultado em uma `List<String>`. Imprima a lista final.

```java
public class Ex2MapSorted {

    public static void main(String[] args) {
        var produtos = List.of(
            new Produto("Caneta",   3.50),
            new Produto("Mochila",  120.00),
            new Produto("Caderno",  45.00),
            new Produto("Notebook", 3200.00),
            new Produto("Régua",    8.00),
            new Produto("Tablet",   850.00)
        );

        // Extrair apenas os nomes dos produtos, ordene-os em ordem alfabética e colete o resultado
        List<String> nomes = produtos.stream()
            .map(Produto::getNome)
            .sorted()
            .collect(Collectors.toList());

        System.out.println(nomes);
    }
}
```

**Saída esperada:**
```
[Caderno, Caneta, Mochila, Notebook, Régua, Tablet]
```

---

### Exercício 3 — Estatísticas com streams de primitivos

Usando a mesma lista de `Produto`, calcule:

a) A **média de preços** de todos os produtos.
b) O **produto mais barato** (use `min()` com `Comparator`).

```java
import java.util.Comparator;
import java.util.OptionalDouble;

public class Ex3Estatisticas {

    public static void main(String[] args) {
        var produtos = List.of(
            new Produto("Caneta",   3.50),
            new Produto("Mochila",  120.00),
            new Produto("Caderno",  45.00),
            new Produto("Notebook", 3200.00),
            new Produto("Régua",    8.00),
            new Produto("Tablet",   850.00)
        );

        // a) Calcular a média de preços de todos os produtos.
        produtos.stream()
            .mapToDouble(Produto::getPreco)
            .average()
            .ifPresent(avg -> System.out.printf("Média de preços: R$ %.2f%n", avg));

        // b) Encontrar o produto mais barato.
        produtos.stream()
            .min(Comparator.comparingDouble(Produto::getPreco))
            .ifPresent(p -> System.out.println("Mais barato: " + p));
    }
}
```

**Saída esperada:**
```
Média de preços: R$ 704,42
Mais barato: Caneta (R$ 3.5)
```

> Dica: `mapToDouble(Produto::getPreco)` converte a stream em uma `DoubleStream`.

---

### Exercício 4 — Agrupando com Collectors

Usando a mesma lista de `Produto`, agrupe os produtos em duas categorias usando `partitioningBy()`:
- `true` → produtos com preço **≤ R$ 50,00** (baratos)
- `false` → produtos com preço **> R$ 50,00** (caros)

Imprima os dois grupos.

```java
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

public class Ex4Agrupamento {

    public static void main(String[] args) {
        var produtos = List.of(
            new Produto("Caneta",   3.50),
            new Produto("Mochila",  120.00),
            new Produto("Caderno",  45.00),
            new Produto("Notebook", 3200.00),
            new Produto("Régua",    8.00),
            new Produto("Tablet",   850.00)
        );

        // Agrupar os produtos em duas categorias: baratos (≤ R$ 50,00) e caros (> R$ 50,00)
        Map<Boolean, List<Produto>> grupos = produtos.stream()
            .collect(Collectors.partitioningBy(p -> p.getPreco() <= 50));

        System.out.println("Baratos (≤ R$ 50): " + grupos.get(true));
        System.out.println("Caros   (> R$ 50): " + grupos.get(false));
    }
}
```

**Saída esperada:**
```
Baratos (≤ R$ 50): [Caneta (R$ 3.5), Caderno (R$ 45.0), Régua (R$ 8.0)]
Caros   (> R$ 50): [Mochila (R$ 120.0), Notebook (R$ 3200.0), Tablet (R$ 850.0)]
```
