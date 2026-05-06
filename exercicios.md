# Exercícios — I/O e NIO.2 em Java

---

### Exercício 1 — Navegando um Path

Dado o caminho `/projetos/ada/backend/src/Main.java`, use a interface `Path` para responder:

a) Qual o número de segmentos do caminho?
b) Qual o segmento de índice 2?
c) Qual o nome do arquivo (segmento final)?
d) Qual o diretório pai?
e) Qual o resultado de `subpath(1, 4)`?

```java
import java.nio.file.Path;
import java.nio.file.Paths;

public class Ex1Path {

    public static void main(String[] args) {
        Path path = Paths.get("/projetos/ada/backend/src/Main.java");

        // a) TODO: imprimir o número de segmentos
        // b) TODO: imprimir o segmento de índice 2
        // c) TODO: imprimir o nome do arquivo (getFileName)
        // d) TODO: imprimir o diretório pai
        // e) TODO: imprimir subpath(1, 4)
    }
}
```

**Saída esperada:**
```
Segmentos: 5
Índice 2: backend
Arquivo: Main.java
Pai: /projetos/ada/backend/src
subpath(1,4): ada/backend/src
```

---

### Exercício 2 — Normalize e Resolve

a) Use `normalize()` para simplificar cada path abaixo e imprima o resultado:

```java
Path p1 = Path.of("/projetos/ada/../backend/./src");
Path p2 = Path.of("./config/../resources/app.properties");
Path p3 = Path.of("../../logs/app.log");
```

b) Use `resolve()` para montar o caminho completo de um arquivo a partir de uma base e um subpath:

```java
Path base = Path.of("/home/usuario");
Path sub  = Path.of("documentos/relatorio.txt");
// TODO: imprimir base.resolve(sub)
```

c) Use `relativize()` para calcular o caminho relativo entre os dois paths abaixo:

```java
Path origem  = Path.of("/projetos/ada");
Path destino = Path.of("/projetos/ada/backend/src/Main.java");
// TODO: imprimir origem.relativize(destino)
```

**Saída esperada (parte a):**
```
/projetos/backend/src
config/resources/app.properties
../../logs/app.log
```

---

### Exercício 3 — Leitura e Escrita de Arquivo

Implemente os dois métodos abaixo:

- `escreverLinhas(Path destino, List<String> linhas)`: escreve cada linha da lista em um arquivo, uma por linha, usando `BufferedWriter`.
- `lerLinhas(Path origem)`: lê todas as linhas do arquivo e retorna uma `List<String>`, usando `Files.readAllLines()`.

Em seguida, no `main`, escreva o arquivo, leia-o de volta e imprima cada linha com seu número.

```java
import java.io.*;
import java.nio.file.*;
import java.util.List;

public class Ex3Arquivo {

    public static void main(String[] args) throws IOException {
        Path arquivo = Path.of("turma.txt");

        List<String> alunos = List.of("Ana", "Bruno", "Carla", "Diego", "Elisa");

        // TODO: chamar escreverLinhas passando arquivo e alunos
        // TODO: chamar lerLinhas e imprimir cada linha numerada (1. Ana, 2. Bruno, ...)
    }

    static void escreverLinhas(Path destino, List<String> linhas) throws IOException {
        // TODO: usar BufferedWriter + FileWriter para escrever cada linha
    }

    static List<String> lerLinhas(Path origem) throws IOException {
        // TODO: usar Files.readAllLines para retornar as linhas
        return null;
    }
}
```

**Saída esperada:**
```
1. Ana
2. Bruno
3. Carla
4. Diego
5. Elisa
```

---

### Exercício 4 — Serialização de Objetos

a) Faça a classe `Produto` ser serializável. O campo `desconto` deve ser marcado como `transient` (não serializado).

b) Implemente `salvar(List<Produto> produtos, File arquivo)` usando `ObjectOutputStream`.

c) Implemente `carregar(File arquivo)` usando `ObjectInputStream`, retornando `List<Produto>`.

d) No `main`, salve a lista em um arquivo temporário, carregue-a de volta e verifique que `desconto` é `0.0` (valor padrão após desserialização).

```java
import java.io.*;
import java.util.*;

// a) TODO: implementar Serializable e marcar desconto como transient
public class Produto {
    private static final long serialVersionUID = 1L;
    private String nome;
    private double preco;
    private double desconto; // não deve ser serializado

    public Produto(String nome, double preco, double desconto) {
        this.nome     = nome;
        this.preco    = preco;
        this.desconto = desconto;
    }
    public String toString() {
        return nome + " R$" + preco + " desconto=" + desconto;
    }
}

public class Ex4Serializacao {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        List<Produto> originais = List.of(
            new Produto("Notebook", 3200.00, 0.10),
            new Produto("Mouse",      89.90, 0.05),
            new Produto("Teclado",   149.00, 0.15)
        );

        File arquivo = File.createTempFile("produtos", ".ser");
        arquivo.deleteOnExit();

        // TODO: chamar salvar
        // TODO: chamar carregar e imprimir cada produto (desconto deve ser 0.0)
    }

    // b) TODO: implementar salvar com ObjectOutputStream
    static void salvar(List<Produto> produtos, File arquivo) throws IOException { }

    // c) TODO: implementar carregar com ObjectInputStream
    static List<Produto> carregar(File arquivo) throws IOException, ClassNotFoundException {
        return null;
    }
}
```

**Saída esperada:**
```
Notebook R$3200.0 desconto=0.0
Mouse R$89.9 desconto=0.0
Teclado R$149.0 desconto=0.0
```
