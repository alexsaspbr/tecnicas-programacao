# Pontos-chave: Paginação com Java Streams

> Gerado a partir do exercício `exercicio-paginacao-streams.md`  
> Todos os 23 testes passaram ✅

---

## 1. Java Streams — o modelo mental

Um `Stream<T>` é uma **sequência de elementos processada de forma declarativa** (o quê, não o como). Ele **não armazena dados** — é um pipeline sobre uma fonte (lista, array, arquivo etc.).

```
fonte → [operações intermediárias] → operação terminal → resultado
```

| Tipo | Característica | Exemplos |
|---|---|---|
| Intermediária | Lazy, retorna outro `Stream` | `filter`, `map`, `skip`, `limit`, `sorted` |
| Terminal | Dispara a execução, consome o stream | `collect`, `count`, `forEach`, `findFirst` |

> **Lazy evaluation**: nenhuma operação intermediária executa antes de a terminal ser chamada. Isso é crucial para entender a eficiência do pipeline.

---

## 2. `skip(n)` — descarte os primeiros N

```java
lista.stream().skip(7).collect(Collectors.toList());
// Descarta índices 0..6, retorna 7 em diante
```

- Pula exatamente `n` elementos do início.
- Se `n >= tamanho`, retorna stream vazio — **nunca lança exceção**.
- Útil para "avançar" dentro de uma coleção.

---

## 3. `limit(n)` — pegue no máximo N

```java
lista.stream().limit(3).collect(Collectors.toList());
// Retorna os 3 primeiros elementos
```

- É uma **operação de curto-circuito** (short-circuit): para de processar o stream assim que `n` elementos são coletados.
- Se a lista tem menos de `n` elementos, retorna tudo — sem erro.

---

## 4. `skip` + `limit` = janela deslizante (paginação)

A combinação das duas operações seleciona uma **fatia arbitrária** da sequência:

```java
// Fórmula geral de paginação
lista.stream()
     .skip((long) pagina * tamanhoPagina)   // avança até a página certa
     .limit(tamanhoPagina)                  // pega só o tamanho da página
     .collect(Collectors.toList());
```

**Por que o cast `(long)`?**  
`pagina * tamanhoPagina` pode causar overflow de `int` em listas muito grandes.  
`skip` recebe `long`, então o cast garante aritmética correta.

### Visualização com `pagina=1`, `tamanhoPagina=3`

```
índice:  [0]  [1]  [2]  [3]  [4]  [5]  [6]  [7]  [8]  [9]
          ← skip(3) →   ← limit(3) →
                         ↑            ↑
                      pega aqui    para aqui
```

---

## 5. `filter` — filtragem declarativa

```java
livros.stream()
      .filter(l -> l.getTitulo().toLowerCase().contains(termoLower))
      ...
```

- Recebe um `Predicate<T>` (função `T → boolean`).
- Mantém apenas elementos onde o predicado é `true`.
- **Case-insensitive**: converta ambos os lados para lowercase antes de comparar.

### Ordem importa: filtre antes de paginar

```java
// ✅ Correto: filtra primeiro, depois pagina sobre os resultados filtrados
stream().filter(...).skip(...).limit(...)

// ❌ Errado: paginaria a lista bruta e depois filtraria (resultado incorreto)
stream().skip(...).limit(...).filter(...)
```

---

## 6. Divisão com teto (Ceiling Division)

Para calcular o total de páginas sem usar `double` ou `Math.ceil`:

```java
// Fórmula inteira equivalente a Math.ceil(size / tamanhoPagina)
int total = (lista.size() + tamanhoPagina - 1) / tamanhoPagina;
```

### Por que funciona?

| `size` | `tamanhoPagina` | `size + t - 1` | `/ t` (inteiro) | `Math.ceil` |
|--------|----------------|----------------|-----------------|-------------|
| 10     | 3              | 12             | 4               | 4.0 ✅      |
| 9      | 3              | 11             | 3               | 3.0 ✅      |
| 1      | 5              | 5              | 1               | 1.0 ✅      |

**Caso de borda importante**: lista vazia → retorne `0` explicitamente antes de calcular, para evitar divisão enganosa.

---

## 7. `Collectors.toList()` vs `Stream.toList()` (Java 16+)

```java
// Java 8+: lista mutável
.collect(Collectors.toList())

// Java 16+: lista imutável (mais simples e mais segura)
.toList()
```

Prefira `.toList()` em projetos Java 16+ para deixar claro que o resultado não deve ser modificado.

---

## 8. Generics em classes utilitárias

A classe `Paginador<T>` é **genérica**: funciona com `Livro`, `Produto`, `Usuario` — qualquer tipo.

```java
public class Paginador<T> {
    public List<T> paginar(List<T> lista, int pagina, int tamanhoPagina) { ... }
}

// Uso
Paginador<Livro> p = new Paginador<>();
Paginador<String> ps = new Paginador<>();
```

Isso é **polimorfismo paramétrico**: o algoritmo é o mesmo, o tipo varia. É o padrão usado em coleções do próprio Java (`List<T>`, `Optional<T>`, etc.).

---

## 9. Resumo — o que estudar a seguir

| Tema | Por quê estudar |
|---|---|
| **Java Streams API** (`java.util.stream`) | Base de todo processamento funcional em Java moderno |
| **Operações intermediárias vs terminais** | Entender lazy evaluation e short-circuit |
| **`Collectors`** (`toList`, `groupingBy`, `joining`...) | Transformar streams em coleções e agregações |
| **Lambda e `Predicate<T>`** | Sintaxe funcional usada em `filter`, `map` etc. |
| **Generics (`<T>`)** | Escrever código reutilizável e type-safe |
| **Overflow de inteiros** | Por que usar `(long)` em multiplicações de índices |
| **Ceiling division** | Aritmética inteira sem ponto flutuante |
| **Imutabilidade e `Stream.toList()`** | Boas práticas com coleções retornadas de métodos |

---

> **Dica de leitura**: [Java SE — Package java.util.stream](https://docs.oracle.com/en/java/docs/api/java.base/java/util/stream/package-summary.html)
