# Gabarito — Interfaces Funcionais e Optional

---

### Exercício 1 — Supplier, Consumer e BiConsumer

```java
import java.util.function.*;

public class Ex1SupplierConsumer {

    public static void main(String[] args) {

        // a) Supplier — fábrica de Produto padrão
        Supplier<Produto> fabrica = () -> new Produto("Indefinido", 0.0);
        System.out.println(fabrica.get().getNome()); // Indefinido

        // b) Consumer — exibe produto formatado
        Consumer<Produto> exibir = p ->
            System.out.printf("Produto: %s | Preço: R$ %.2f%n",
                              p.getNome(), p.getPreco());

        exibir.accept(new Produto("Notebook", 3200.00));
        exibir.accept(new Produto("Mouse",      89.90));

        // c) BiConsumer — cria e delega ao Consumer
        BiConsumer<String, Double> cadastrar =
            (nome, preco) -> exibir.accept(new Produto(nome, preco));

        cadastrar.accept("Teclado", 149.00);
    }
}
```

**Saída:**
```
Indefinido
Produto: Notebook | Preço: R$ 3200,00
Produto: Mouse | Preço: R$ 89,90
Produto: Teclado | Preço: R$ 149,00
```

`Supplier` não recebe parâmetros — ideal para diferir a criação de objetos. `Consumer` encapsula um efeito colateral (impressão) reutilizável. O `BiConsumer` reutiliza o `Consumer` já criado, evitando duplicação de lógica de formatação.

---

### Exercício 2 — Predicate, BiPredicate e Composição

```java
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;

public class Ex2Predicate {

    public static void main(String[] args) {
        List<String> palavras = List.of("Ada", "Java", "Kotlin", "Python", "Go", "Clojure");

        // a) mais de 5 caracteres
        Predicate<String> maisDe5 = s -> s.length() > 5;

        // b) começa com maiúscula
        Predicate<String> comecaMaiusculo = s -> Character.isUpperCase(s.charAt(0));

        // c) and() — ambos os critérios
        List<String> ambos = palavras.stream()
            .filter(maisDe5.and(comecaMaiusculo))
            .collect(Collectors.toList());
        System.out.println("AND: " + ambos);

        // d) or() — ao menos um
        List<String> algum = palavras.stream()
            .filter(maisDe5.or(comecaMaiusculo))
            .collect(Collectors.toList());
        System.out.println("OR:  " + algum);

        // e) BiPredicate — tamanho exato
        BiPredicate<String, Integer> tamanhoExato = (s, n) -> s.length() == n;
        System.out.println(tamanhoExato.test("Java",   4)); // true
        System.out.println(tamanhoExato.test("Python", 3)); // false
    }
}
```

**Saída:**
```
AND: [Kotlin, Python, Clojure]
OR:  [Ada, Java, Kotlin, Python, Clojure]
true
false
```

`and()`, `or()` e `negate()` retornam novos `Predicate` — os originais não são modificados. `"Go"` não aparece em nenhum resultado: tem 2 caracteres (falha `maisDe5`) e começa com maiúscula — mas para o OR, o segundo predicado é verdadeiro... espere, "Go" começa com 'G' maiúsculo, então `comecaMaiusculo.test("Go")` é `true`. Portanto "Go" aparece no OR. A saída OR contém `[Ada, Java, Kotlin, Python, Go, Clojure]` menos os que falham em ambos — como todos começam com maiúscula, o OR inclui todos.

Revisando: `maisDe5.or(comecaMaiusculo)` — "Ada" (3 chars, maiúsculo) → OR true; "Java" (4, maiúsculo) → true; "Go" (2, maiúsculo) → true; "Kotlin","Python","Clojure" → true. Resultado: todos exceto os que falham em ambos. Como todos começam com maiúscula, o OR retorna todos os 6.

Ajuste da saída esperada:
```
AND: [Kotlin, Python, Clojure]
OR:  [Ada, Java, Kotlin, Python, Go, Clojure]
true
false
```

`Character.isUpperCase(s.charAt(0))` verifica o primeiro caractere sem criar objetos extras. Compor predicados com `and`/`or` é preferível a ifs aninhados — cada predicado pode ser testado e reutilizado de forma independente.

---

### Exercício 3 — Function, BiFunction e Operadores

```java
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;

public class Ex3Function {

    public static void main(String[] args) {

        // a) tamanho da string
        Function<String, Integer> tamanho = String::length;
        System.out.println(tamanho.apply("Kotlin")); // 6

        // b) normalizar: trim + toLowerCase
        Function<String, String> normalizar = s -> s.trim().toLowerCase();
        System.out.println(normalizar.apply("  JAVA  ")); // java

        // c) andThen: normalizar primeiro, depois contar
        Function<String, Integer> normalEContar = normalizar.andThen(tamanho);
        System.out.println(normalEContar.apply("  Java  ")); // 4

        // d) BiFunction: repetição com separador
        BiFunction<String, Integer, String> repetir = (s, n) ->
            String.join(" | ", java.util.Collections.nCopies(n, s));
        System.out.println(repetir.apply("Ada", 3)); // Ada | Ada | Ada

        // e) UnaryOperator: lista com quadrados
        UnaryOperator<List<Integer>> quadrados = lista ->
            lista.stream()
                 .map(x -> x * x)
                 .collect(Collectors.toList());
        System.out.println(quadrados.apply(List.of(1, 2, 3, 4))); // [1, 4, 9, 16]

        // f) BinaryOperator: maior valor
        BinaryOperator<Integer> maior = (a, b) -> a > b ? a : b;
        System.out.println(maior.apply(7, 42)); // 42
    }
}
```

**Saída:**
```
6
java
4
Ada | Ada | Ada
[1, 4, 9, 16]
42
```

`String::length` é um method reference equivalente a `s -> s.length()`. `andThen` encadeia funções esquerda para direita: `normalizar.andThen(tamanho)` primeiro normaliza, depois conta. `Collections.nCopies(n, s)` gera uma lista com `n` cópias de `s` — `String.join` concatena com o separador. `UnaryOperator<T>` é apenas um `Function<T,T>` com a restrição de que entrada e saída têm o mesmo tipo.

---

### Exercício 4 — Optional

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

        // a) ifPresent — só executa se houver valor
        buscarPreco("Notebook").ifPresent(System.out::println); // 3200.0

        // b) orElse — valor literal de fallback
        double precoB = buscarPreco("Tablet").orElse(0.0);
        System.out.println(precoB); // 0.0

        // c) orElseGet — Supplier calculado só quando vazio
        double precoC = buscarPreco("Tablet").orElseGet(() -> Double.NaN);
        System.out.println(precoC); // NaN

        // d) orElseThrow — lança exceção customizada
        try {
            buscarPreco("Tablet")
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage()); // Produto não encontrado
        }

        // e) descrever — sem get()
        System.out.println(descrever(buscarPreco("Notebook"))); // Preço: R$ 3200.0
        System.out.println(descrever(buscarPreco("Tablet")));   // Preço indisponível
    }

    static String descrever(Optional<Double> opt) {
        return opt.map(v -> "Preço: R$ " + v)
                  .orElse("Preço indisponível");
    }
}
```

**Saída:**
```
3200.0
0.0
NaN
Produto não encontrado
Preço: R$ 3200.0
Preço indisponível
```

`ifPresent` é o equivalente funcional de `if (opt.isPresent()) { ... }` — mais conciso e sem risco de chamar `get()` acidentalmente. `orElse` avalia o argumento **sempre**, mesmo quando o Optional tem valor; `orElseGet` é _lazy_ — o `Supplier` só é chamado quando necessário (prefira-o para fallbacks caros). O método `descrever` usa `map` para transformar o valor _dentro_ do Optional e `orElse` para tratar a ausência — nunca chama `get()` diretamente, o padrão mais seguro.
