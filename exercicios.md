# Exercícios — Interfaces Funcionais e Optional

---

### Exercício 1 — Supplier, Consumer e BiConsumer

Usando a classe `Produto` abaixo, implemente os três itens com lambdas:

a) Um `Supplier<Produto>` que retorne sempre um novo `Produto("Indefinido", 0.0)`.

b) Um `Consumer<Produto>` que imprima o produto no formato `"Produto: Notebook | Preço: R$ 3200,00"`.

c) Um `BiConsumer<String, Double>` que receba nome e preço, crie um `Produto` e imprima usando o mesmo `Consumer` do item (b).

```java
import java.util.List;
import java.util.function.*;

public class Produto {
    private String nome;
    private double preco;

    public Produto(String nome, double preco) {
        this.nome  = nome;
        this.preco = preco;
    }
    public String getNome()  { return nome; }
    public double getPreco() { return preco; }
}

public class Ex1SupplierConsumer {

    public static void main(String[] args) {

        // a) TODO: Supplier<Produto> que retorna new Produto("Indefinido", 0.0)
        Supplier<Produto> fabrica = null;
        System.out.println(fabrica.get().getNome()); // Indefinido

        // b) TODO: Consumer<Produto> que imprime no formato acima
        Consumer<Produto> exibir = null;
        exibir.accept(new Produto("Notebook", 3200.00));
        exibir.accept(new Produto("Mouse",      89.90));

        // c) TODO: BiConsumer<String, Double> que cria Produto e usa o Consumer acima
        BiConsumer<String, Double> cadastrar = null;
        cadastrar.accept("Teclado", 149.00);
    }
}
```

**Saída esperada:**
```
Indefinido
Produto: Notebook | Preço: R$ 3200,00
Produto: Mouse | Preço: R$ 89,90
Produto: Teclado | Preço: R$ 149,00
```

> Dica: `String.format("Produto: %s | Preço: R$ %.2f", ...)` formata o valor com vírgula no locale brasileiro.

---

### Exercício 2 — Predicate, BiPredicate e Composição

Usando a lista de palavras abaixo, implemente com lambdas:

a) Um `Predicate<String>` que retorne `true` se a string tiver mais de 5 caracteres.

b) Um `Predicate<String>` que retorne `true` se a string começar com letra maiúscula.

c) Componha os dois predicados com `and()` e filtre a lista, imprimindo apenas as palavras que atendem **ambos** os critérios.

d) Componha com `or()` e imprima as palavras que atendem **ao menos um** critério.

e) Um `BiPredicate<String, Integer>` que retorne `true` se a string tiver exatamente `n` caracteres. Teste com `("Java", 4)` e `("Python", 3)`.

```java
import java.util.List;
import java.util.function.*;

public class Ex2Predicate {

    public static void main(String[] args) {
        List<String> palavras = List.of("Ada", "Java", "Kotlin", "Python", "Go", "Clojure");

        // a) TODO: Predicate — mais de 5 caracteres
        Predicate<String> maisDe5 = null;

        // b) TODO: Predicate — começa com maiúscula
        Predicate<String> comecaMaiusculo = null;

        // c) TODO: and() — ambos; imprimir com rótulo "AND:"
        // d) TODO: or()  — ao menos um; imprimir com rótulo "OR:"

        // e) TODO: BiPredicate — string com exatamente n caracteres
        BiPredicate<String, Integer> tamanhoExato = null;
        System.out.println(tamanhoExato.test("Java",   4)); // true
        System.out.println(tamanhoExato.test("Python", 3)); // false
    }
}
```

**Saída esperada:**
```
AND: [Kotlin, Python, Clojure]
OR:  [Ada, Java, Kotlin, Python, Clojure]
true
false
```

---

### Exercício 3 — Function, BiFunction e Operadores

Implemente as transformações abaixo com lambdas e method references:

a) `Function<String, Integer>` que retorna o número de caracteres da string.

b) `Function<String, String>` que remove espaços nas extremidades e converte para minúsculas.

c) Componha (a) e (b) com `andThen` para: dado `"  Java  "`, primeiro normalizar e depois contar.

d) `BiFunction<String, Integer, String>` que repete a string `n` vezes separada por `" | "`.
   Ex.: `("Ada", 3)` → `"Ada | Ada | Ada"`.

e) `UnaryOperator<List<Integer>>` que recebe uma lista e retorna uma nova lista com cada elemento ao quadrado.

f) `BinaryOperator<Integer>` que retorna o maior entre dois inteiros.

```java
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;

public class Ex3Function {

    public static void main(String[] args) {

        // a) TODO: Function String → Integer (length)
        Function<String, Integer> tamanho = null;
        System.out.println(tamanho.apply("Kotlin")); // 6

        // b) TODO: Function String → String (trim + toLowerCase)
        Function<String, String> normalizar = null;
        System.out.println(normalizar.apply("  JAVA  ")); // java

        // c) TODO: andThen — normalizar e depois contar
        Function<String, Integer> normalEContar = null;
        System.out.println(normalEContar.apply("  Java  ")); // 4

        // d) TODO: BiFunction String, Integer → String (repetição com " | ")
        BiFunction<String, Integer, String> repetir = null;
        System.out.println(repetir.apply("Ada", 3)); // Ada | Ada | Ada

        // e) TODO: UnaryOperator List<Integer> → List<Integer> (quadrado)
        UnaryOperator<List<Integer>> quadrados = null;
        System.out.println(quadrados.apply(List.of(1, 2, 3, 4))); // [1, 4, 9, 16]

        // f) TODO: BinaryOperator Integer — retorna o maior
        BinaryOperator<Integer> maior = null;
        System.out.println(maior.apply(7, 42)); // 42
    }
}
```

---

### Exercício 4 — Optional

Dado o método `buscarPreco` abaixo, que retorna `Optional.empty()` quando o produto não é encontrado:

a) Chame `buscarPreco("Notebook")` e imprima o preço usando `ifPresent`.

b) Chame `buscarPreco("Tablet")` (inexistente) e use `orElse` para retornar `0.0`.

c) Chame `buscarPreco("Tablet")` e use `orElseGet` com um `Supplier` que retorna `Double.NaN`.

d) Chame `buscarPreco("Tablet")` e use `orElseThrow` com um `Supplier` que lança `IllegalArgumentException("Produto não encontrado")`. Capture a exceção e imprima a mensagem.

e) Escreva o método `descrever(Optional<Double> opt)` que:
   - Se presente, retorna `"Preço: R$ <valor>"`.
   - Se ausente, retorna `"Preço indisponível"`.
   Implemente **sem** usar `get()` diretamente.

```java
import java.util.Map;
import java.util.Optional;

public class Ex4Optional {

    private static final Map<String, Double> CATALOGO = Map.of(
        "Notebook", 3200.00,
        "Mouse",      89.90,
        "Teclado",   149.00
    );

    public static Optional<Double> buscarPreco(String nome) {
        return Optional.ofNullable(CATALOGO.get(nome));
    }

    public static void main(String[] args) {

        // a) TODO: ifPresent — imprimir preço do Notebook
        // Saída: 3200.0

        // b) TODO: orElse — preço do Tablet com fallback 0.0
        double precoB = 0;
        System.out.println(precoB); // 0.0

        // c) TODO: orElseGet — Supplier retorna Double.NaN
        double precoC = 0;
        System.out.println(precoC); // NaN

        // d) TODO: orElseThrow — capturar IllegalArgumentException e imprimir mensagem
        // Saída: Produto não encontrado

        // e) TODO: implementar descrever() e testar com Notebook e Tablet
        System.out.println(descrever(buscarPreco("Notebook"))); // Preço: R$ 3200.0
        System.out.println(descrever(buscarPreco("Tablet")));   // Preço indisponível
    }

    static String descrever(Optional<Double> opt) {
        // TODO: sem usar get() — use orElse ou map+orElse
        return null;
    }
}
```
