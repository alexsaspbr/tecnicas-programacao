# Gabarito — Programação Funcional em Java

---

### Exercício 1 — Filtrando com Predicate

```java
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Ex1Predicate {

    public static void main(String[] args) {
        List<Produto> produtos = new ArrayList<>();
        produtos.add(new Produto("Caneta",   3.50));
        produtos.add(new Produto("Mochila",  120.00));
        produtos.add(new Produto("Caderno",  45.00));
        produtos.add(new Produto("Notebook", 3200.00));
        produtos.add(new Produto("Régua",    8.00));

        filtrar(produtos, p -> p.getPreco() > 50.00);
    }

    private static void filtrar(List<Produto> lista, Predicate<Produto> criterio) {
        for (Produto p : lista) {
            if (criterio.test(p)) {
                System.out.println(p);
            }
        }
    }
}
```

**Saída:**
```
Mochila (R$ 120.0)
Notebook (R$ 3200.0)
```

O `Predicate<Produto>` é implementado com `p -> p.getPreco() > 50.00`. O método `test()` é chamado dentro de `filtrar()` para avaliar cada produto — nenhuma classe adicional foi necessária.

---

### Exercício 2 — Consumindo com Consumer

```java
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Ex2Consumer {

    public static void main(String[] args) {
        List<Produto> produtos = new ArrayList<>();
        produtos.add(new Produto("Caneta",   3.50));
        produtos.add(new Produto("Mochila",  120.00));
        produtos.add(new Produto("Caderno",  45.00));
        produtos.add(new Produto("Notebook", 3200.00));
        produtos.add(new Produto("Régua",    8.00));

        Consumer<Produto> imprimir =
            p -> System.out.println(
                String.format("Produto: %s | Preço: R$ %.2f", p.getNome(), p.getPreco())
            );

        produtos.forEach(imprimir);
    }
}
```

**Saída:**
```
Produto: Caneta | Preço: R$ 3,50
Produto: Mochila | Preço: R$ 120,00
Produto: Caderno | Preço: R$ 45,00
Produto: Notebook | Preço: R$ 3200,00
Produto: Régua | Preço: R$ 8,00
```

`Consumer<Produto>` recebe um produto e não retorna nada (`void`). O `forEach` chama `accept()` internamente para cada elemento. O lambda pode ser guardado em variável ou passado diretamente.

---

### Exercício 3 — Ordenando com Comparator

```java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Ex3Comparator {

    public static void main(String[] args) {
        List<Produto> produtos = new ArrayList<>();
        produtos.add(new Produto("Caneta",   3.50));
        produtos.add(new Produto("Mochila",  120.00));
        produtos.add(new Produto("Caderno",  45.00));
        produtos.add(new Produto("Notebook", 3200.00));
        produtos.add(new Produto("Régua",    8.00));

        // Por preço crescente
        Comparator<Produto> porPreco = (p1, p2) -> Double.compare(p1.getPreco(), p2.getPreco());
        produtos.sort(porPreco);
        System.out.println("Por preço crescente:");
        produtos.forEach(p -> System.out.println("  " + p));

        // Por nome alfabético
        Comparator<Produto> porNome = (p1, p2) -> p1.getNome().compareTo(p2.getNome());
        produtos.sort(porNome);
        System.out.println("\nPor nome (A→Z):");
        produtos.forEach(p -> System.out.println("  " + p));
    }
}
```

**Saída:**
```
Por preço crescente:
  Caneta (R$ 3.5)
  Régua (R$ 8.0)
  Caderno (R$ 45.0)
  Mochila (R$ 120.0)
  Notebook (R$ 3200.0)

Por nome (A→Z):
  Caderno (R$ 45.0)
  Caneta (R$ 3.5)
  Mochila (R$ 120.0)
  Notebook (R$ 3200.0)
  Régua (R$ 8.0)
```

Use `Double.compare()` para comparar `double` — evita erros de subtração com ponto flutuante. `String.compareTo()` já implementa ordem lexicográfica.

---

### Exercício 4 — removeIf e Supplier

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class Ex4RemoveIfSupplier {

    public static void main(String[] args) {
        List<Integer> numeros = new ArrayList<>(
            Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        );

        // a) removeIf remove os elementos onde o Predicate retorna true
        numeros.removeIf(n -> n % 2 != 0);
        System.out.println("removeIf: " + numeros);

        // b) Supplier retorna uma nova lista — não modifica nada
        Supplier<List<Integer>> pares = () -> {
            List<Integer> lista = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                if (i % 2 == 0) lista.add(i);
            }
            return lista;
        };

        // c) Comparação
        List<Integer> listaDoSupplier = pares.get();
        System.out.println("Supplier: " + listaDoSupplier);
        System.out.println("Iguais: " + numeros.equals(listaDoSupplier));
    }
}
```

**Saída:**
```
removeIf: [2, 4, 6, 8, 10]
Supplier: [2, 4, 6, 8, 10]
Iguais: true
```

`removeIf` **muta** a lista original; o `Supplier` **cria** uma lista nova. Lambdas com bloco `{ }` admitem múltiplas linhas e exigem `return` explícito. As duas abordagens produzem o mesmo resultado com efeitos colaterais diferentes.
