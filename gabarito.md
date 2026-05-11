# Gabarito — I/O Streams em Java

---

### Exercício 1 — Copiando Arquivos com Bytes

```java
import java.io.*;

public class Ex1CopiaBytes {

    public static void main(String[] args) throws IOException {
        File origem = File.createTempFile("origem", ".bin");
        origem.deleteOnExit();
        try (var out = new FileOutputStream(origem)) {
            out.write(new byte[]{72, 101, 108, 108, 111}); // "Hello"
        }

        File destino1 = File.createTempFile("destino1", ".bin");
        File destino2 = File.createTempFile("destino2", ".bin");
        destino1.deleteOnExit();
        destino2.deleteOnExit();

        copiar(origem, destino1);
        copiarComBuffer(origem, destino2);

        System.out.println("Byte a byte igual: " + conteudoIgual(origem, destino1));
        System.out.println("Com buffer igual: "  + conteudoIgual(origem, destino2));
    }

    static void copiar(File origem, File destino) throws IOException {
        try (var in  = new FileInputStream(origem);
             var out = new FileOutputStream(destino)) {
            int b;
            while ((b = in.read()) != -1)
                out.write(b);
        }
    }

    static void copiarComBuffer(File origem, File destino) throws IOException {
        try (var in  = new FileInputStream(origem);
             var out = new FileOutputStream(destino)) {
            var buffer = new byte[1024];
            int lidos;
            while ((lidos = in.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, lidos);
                out.flush();
            }
        }
    }

    static boolean conteudoIgual(File a, File b) throws IOException {
        return java.util.Arrays.equals(
            new FileInputStream(a).readAllBytes(),
            new FileInputStream(b).readAllBytes()
        );
    }
}
```

**Saída:**
```
Byte a byte igual: true
Com buffer igual: true
```

`read()` sem argumentos retorna um `int` entre 0 e 255 (o byte lido), ou −1 ao atingir o fim. A variante com buffer lê até `buffer.length` bytes por chamada — muito menos chamadas ao sistema operacional, portanto muito mais eficiente para arquivos grandes. `try-with-resources` fecha os streams e chama `flush()` automaticamente.

---

### Exercício 2 — Lendo e Escrevendo Texto

```java
import java.io.*;
import java.util.List;

public class Ex2Texto {

    public static void main(String[] args) throws IOException {
        File arquivo = File.createTempFile("notas", ".txt");
        arquivo.deleteOnExit();

        List<String> alunos = List.of(
            "Ana — 9.5",
            "Bruno — 7.0",
            "Carla — 8.5",
            "Diego — 6.0"
        );

        escrever(arquivo, alunos);
        lerFormatado(arquivo);
    }

    static void escrever(File arquivo, List<String> linhas) throws IOException {
        try (var writer = new BufferedWriter(new FileWriter(arquivo))) {
            for (int i = 0; i < linhas.size(); i++) {
                writer.write((i + 1) + ". " + linhas.get(i));
                writer.newLine();
            }
        }
    }

    static void lerFormatado(File arquivo) throws IOException {
        try (var reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null)
                System.out.println(linha);
        }
    }
}
```

**Saída:**
```
1. Ana — 9.5
2. Bruno — 7.0
3. Carla — 8.5
4. Diego — 6.0
```

`readLine()` devolve `null` quando não há mais linhas — é a condição de saída do `while`. `newLine()` usa o separador correto para o sistema operacional (evite hardcode de `"\n"`). Se quiser substituir `BufferedWriter` por `PrintWriter`: basta trocar `writer.write(s); writer.newLine()` por `writer.println(s)`.

---

### Exercício 3 — `Files.lines()` com Pipeline de Streams

```java
import java.io.*;
import java.nio.file.*;
import java.util.Comparator;

public class Ex3FilesLines {

    public static void main(String[] args) throws IOException {
        Path arquivo = Files.createTempFile("alunos", ".txt");
        arquivo.toFile().deleteOnExit();

        Files.writeString(arquivo,
            "Ana,9.5\nBruno,5.0\nCarla,8.5\nDiego,6.0\nElisa,7.0\n");

        try (var stream = Files.lines(arquivo)) {
            stream
                .map(linha -> linha.split(","))
                .filter(partes -> Double.parseDouble(partes[1]) >= 7.0)
                .sorted(Comparator.comparingDouble(
                    (String[] p) -> Double.parseDouble(p[1])).reversed())
                .forEach(partes ->
                    System.out.println(partes[0] + ": " + partes[1]));
        }
    }
}
```

**Saída:**
```
Ana: 9.5
Carla: 8.5
Elisa: 7.0
```

`Files.lines()` retorna um `Stream<String>` lido _lazily_ — o arquivo não é carregado inteiro na memória. O `try-with-resources` fecha tanto o stream quanto o arquivo. `Comparator.comparingDouble(...).reversed()` é mais seguro que subtrair doubles (sem risco de overflow). A pipeline é declarativa: cada etapa (`map`, `filter`, `sorted`, `forEach`) descreve _o que_ fazer, não _como_.

---

### Exercício 4 — Serialização Completa

```java
import java.io.*;
import java.util.*;

public class Aluno implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private double nota;
    private transient String senha;

    public Aluno(String nome, double nota, String senha) {
        this.nome  = nome;
        this.nota  = nota;
        this.senha = senha;
    }

    public String toString() {
        return nome + " (nota=" + nota + ", senha=" + senha + ")";
    }
}

public class Ex4Serializacao {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        List<Aluno> originais = List.of(
            new Aluno("Ana",   9.5, "s3cr3t"),
            new Aluno("Bruno", 7.0, "abc123"),
            new Aluno("Carla", 8.5, "xyz789")
        );

        File arquivo = File.createTempFile("alunos", ".ser");
        arquivo.deleteOnExit();

        salvar(originais, arquivo);

        List<Aluno> carregados = carregar(arquivo);
        carregados.forEach(System.out::println);
    }

    static void salvar(List<Aluno> alunos, File arquivo) throws IOException {
        try (var out = new ObjectOutputStream(
                           new BufferedOutputStream(
                               new FileOutputStream(arquivo)))) {
            for (Aluno a : alunos)
                out.writeObject(a);
        }
    }

    static List<Aluno> carregar(File arquivo) throws IOException, ClassNotFoundException {
        var lista = new ArrayList<Aluno>();
        try (var in = new ObjectInputStream(
                          new BufferedInputStream(
                              new FileInputStream(arquivo)))) {
            while (true) {
                var obj = in.readObject();
                if (obj instanceof Aluno a) lista.add(a);
            }
        } catch (EOFException e) {
            // fim do stream — encerramento normal
        }
        return lista;
    }
}
```

**Saída:**
```
Ana (nota=9.5, senha=null)
Bruno (nota=7.0, senha=null)
Carla (nota=8.5, senha=null)
```

`transient` faz a JVM ignorar `senha` na serialização — após desserializar, o campo recebe `null` (valor padrão para `String`). O encadeamento `ObjectOutputStream → BufferedOutputStream → FileOutputStream` é a forma idiomática: `Buffered` acumula bytes antes de gravar, reduzindo chamadas ao SO. `EOFException` é lançada quando `readObject()` tenta ler além do fim do arquivo — é o mecanismo convencional para encerrar o loop de desserialização.
