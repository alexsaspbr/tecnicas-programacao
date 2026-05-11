# Gabarito — I/O e NIO.2 em Java

---

### Exercício 1 — Navegando um Path

```java
import java.nio.file.Path;
import java.nio.file.Paths;

public class Ex1Path {

    public static void main(String[] args) {
        Path path = Paths.get("/projetos/ada/backend/src/Main.java");

        System.out.println("Segmentos: " + path.getNameCount());
        System.out.println("Índice 2: "  + path.getName(2));
        System.out.println("Arquivo: "   + path.getFileName());
        System.out.println("Pai: "       + path.getParent());
        System.out.println("subpath(1,4): " + path.subpath(1, 4));
    }
}
```

**Saída:**
```
Segmentos: 5
Índice 2: backend
Arquivo: Main.java
Pai: /projetos/ada/backend/src
subpath(1,4): ada/backend/src
```

`getNameCount()` não conta a raiz `/`. Índices em `subpath()` seguem a convenção Java: início inclusivo, fim exclusivo — `subpath(1, 4)` retorna segmentos 1, 2 e 3.

---

### Exercício 2 — Normalize e Resolve

```java
import java.nio.file.Path;

public class Ex2NormResolve {

    public static void main(String[] args) {
        // a) normalize
        System.out.println(Path.of("/projetos/ada/../backend/./src").normalize());
        System.out.println(Path.of("./config/../resources/app.properties").normalize());
        System.out.println(Path.of("../../logs/app.log").normalize());

        // b) resolve
        Path base = Path.of("/home/usuario");
        Path sub  = Path.of("documentos/relatorio.txt");
        System.out.println(base.resolve(sub));

        // c) relativize
        Path origem  = Path.of("/projetos/ada");
        Path destino = Path.of("/projetos/ada/backend/src/Main.java");
        System.out.println(origem.relativize(destino));
    }
}
```

**Saída:**
```
/projetos/backend/src
config/resources/app.properties
../../logs/app.log
/home/usuario/documentos/relatorio.txt
backend/src/Main.java
```

`normalize()` elimina `.` e `..` resolvíveis, mas não remove `../..` que extrapolam a raiz conhecida. `relativize()` exige que ambos os paths sejam do mesmo tipo (ambos absolutos ou ambos relativos).

---

### Exercício 3 — Leitura e Escrita de Arquivo

```java
import java.io.*;
import java.nio.file.*;
import java.util.List;

public class Ex3Arquivo {

    public static void main(String[] args) throws IOException {
        Path arquivo = Path.of("turma.txt");

        List<String> alunos = List.of("Ana", "Bruno", "Carla", "Diego", "Elisa");

        escreverLinhas(arquivo, alunos);

        List<String> lidos = lerLinhas(arquivo);
        for (int i = 0; i < lidos.size(); i++) {
            System.out.println((i + 1) + ". " + lidos.get(i));
        }
    }

    static void escreverLinhas(Path destino, List<String> linhas) throws IOException {
        try (var writer = new BufferedWriter(new FileWriter(destino.toFile()))) {
            for (String linha : linhas) {
                writer.write(linha);
                writer.newLine();
            }
        }
    }

    static List<String> lerLinhas(Path origem) throws IOException {
        return Files.readAllLines(origem);
    }
}
```

**Saída:**
```
1. Ana
2. Bruno
3. Carla
4. Diego
5. Elisa
```

`BufferedWriter` agrupa as escritas e reduz chamadas ao sistema operacional. `newLine()` usa o separador correto para o sistema operacional (`\r\n` no Windows, `\n` no Linux/macOS). `Files.readAllLines()` retorna uma lista imutável — não tente modificá-la.

---

### Exercício 4 — Serialização de Objetos

```java
import java.io.*;
import java.util.*;

public class Produto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private double preco;
    private transient double desconto;

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

        salvar(originais, arquivo);

        List<Produto> carregados = carregar(arquivo);
        carregados.forEach(System.out::println);
    }

    static void salvar(List<Produto> produtos, File arquivo) throws IOException {
        try (var out = new ObjectOutputStream(
                           new BufferedOutputStream(
                               new FileOutputStream(arquivo)))) {
            for (Produto p : produtos)
                out.writeObject(p);
        }
    }

    static List<Produto> carregar(File arquivo) throws IOException, ClassNotFoundException {
        var lista = new ArrayList<Produto>();
        try (var in = new ObjectInputStream(
                          new BufferedInputStream(
                              new FileInputStream(arquivo)))) {
            while (true) {
                var obj = in.readObject();
                if (obj instanceof Produto p) lista.add(p);
            }
        } catch (EOFException e) {
            // fim do arquivo
        }
        return lista;
    }
}
```

**Saída:**
```
Notebook R$3200.0 desconto=0.0
Mouse R$89.9 desconto=0.0
Teclado R$149.0 desconto=0.0
```

`transient` impede que `desconto` seja gravado — após desserializar, o campo recebe o valor padrão do tipo (`0.0` para `double`). O encadeamento `ObjectOutputStream → BufferedOutputStream → FileOutputStream` é a forma idiomática: o `Buffered` reduz as escritas físicas, e o `File` faz a ligação com o disco. `EOFException` indica o fim do stream de objetos — é a forma convencional de encerrar o loop de leitura.
