# Exercícios — I/O Streams em Java

---

### Exercício 1 — Copiando Arquivos com Bytes

Implemente o método `copiar(File origem, File destino)` que copia um arquivo binário byte a byte usando `FileInputStream` e `FileOutputStream`.

Em seguida, refatore-o em `copiarComBuffer(File origem, File destino)`, usando um buffer de 1024 bytes para melhorar a performance. Compare as duas versões.

No `main`, crie um arquivo temporário com conteúdo, chame os dois métodos e verifique que os arquivos destino são iguais ao original.

```java
import java.io.*;

public class Ex1CopiaBytes {

    public static void main(String[] args) throws IOException {
        File origem = File.createTempFile("origem", ".bin");
        origem.deleteOnExit();

        // escreve alguns bytes no arquivo de origem
        try (var out = new FileOutputStream(origem)) {
            out.write(new byte[]{72, 101, 108, 108, 111}); // "Hello"
        }

        File destino1 = File.createTempFile("destino1", ".bin");
        File destino2 = File.createTempFile("destino2", ".bin");
        destino1.deleteOnExit();
        destino2.deleteOnExit();

        // TODO: chamar copiar(origem, destino1)
        // TODO: chamar copiarComBuffer(origem, destino2)

        // verificar que os conteúdos são iguais
        System.out.println("Byte a byte igual: "    + conteudoIgual(origem, destino1));
        System.out.println("Com buffer igual: "     + conteudoIgual(origem, destino2));
    }

    static void copiar(File origem, File destino) throws IOException {
        // TODO: implementar com FileInputStream e FileOutputStream, lendo byte a byte
    }

    static void copiarComBuffer(File origem, File destino) throws IOException {
        // TODO: implementar com buffer de byte[1024] e flush()
    }

    static boolean conteudoIgual(File a, File b) throws IOException {
        return java.util.Arrays.equals(
            new FileInputStream(a).readAllBytes(),
            new FileInputStream(b).readAllBytes()
        );
    }
}
```

**Saída esperada:**
```
Byte a byte igual: true
Com buffer igual: true
```

---

### Exercício 2 — Lendo e Escrevendo Texto

Dado o arquivo `notas.txt` com as linhas abaixo, implemente dois métodos:

a) `escrever(File arquivo, List<String> linhas)` — grava cada linha com `BufferedWriter`, acrescentando o número de linha no início (`"1. Ana — 9.5"`).

b) `lerFormatado(File arquivo)` — lê o arquivo com `BufferedReader` e imprime cada linha. Em seguida, refaça a leitura com `PrintWriter` na escrita, substituindo `BufferedWriter`.

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

        // a) TODO: chamar escrever passando arquivo e alunos
        // b) TODO: chamar lerFormatado passando arquivo
    }

    static void escrever(File arquivo, List<String> linhas) throws IOException {
        // TODO: usar BufferedWriter; numerar cada linha ("1. Ana — 9.5", etc.)
    }

    static void lerFormatado(File arquivo) throws IOException {
        // TODO: usar BufferedReader e imprimir cada linha lida
    }
}
```

**Saída esperada:**
```
1. Ana — 9.5
2. Bruno — 7.0
3. Carla — 8.5
4. Diego — 6.0
```

---

### Exercício 3 — `Files.lines()` com Pipeline de Streams

Dado um arquivo `alunos.txt` com uma linha por aluno no formato `"Nome,nota"` (ex.: `"Ana,9.5"`), use `Files.lines()` para:

a) Filtrar apenas os alunos com nota **maior ou igual a 7.0**.  
b) Ordenar os aprovados por nota **decrescente**.  
c) Imprimir cada aprovado no formato `"Ana: 9.5"`.

```java
import java.io.*;
import java.nio.file.*;
import java.util.List;

public class Ex3FilesLines {

    public static void main(String[] args) throws IOException {
        Path arquivo = Files.createTempFile("alunos", ".txt");
        arquivo.toFile().deleteOnExit();

        // Grava dados de exemplo
        Files.writeString(arquivo,
            "Ana,9.5\nBruno,5.0\nCarla,8.5\nDiego,6.0\nElisa,7.0\n");

        // TODO: usar Files.lines() para abrir o arquivo
        // TODO: filtrar nota >= 7.0 (parsear cada linha por vírgula)
        // TODO: ordenar por nota decrescente
        // TODO: imprimir no formato "Nome: nota"
    }
}
```

**Saída esperada:**
```
Ana: 9.5
Carla: 8.5
Elisa: 7.0
```

> Dica: `Double.parseDouble(partes[1])` converte a nota; `Comparator.comparingDouble(...).reversed()` ordena de forma decrescente.

---

### Exercício 4 — Serialização Completa

a) Faça a classe `Aluno` implementar `Serializable`. O campo `senha` deve ser `transient`.

b) Implemente `salvar(List<Aluno> alunos, File arquivo)` com `ObjectOutputStream` encadeado em `BufferedOutputStream`.

c) Implemente `carregar(File arquivo)` com `ObjectInputStream` encadeado em `BufferedInputStream`, tratando `EOFException` para encerrar o loop.

d) No `main`, salve a lista, carregue de volta e confirme que:
- Os campos `nome` e `nota` foram preservados.
- O campo `senha` é `null` após desserializar.

```java
import java.io.*;
import java.util.*;

// a) TODO: implementar Serializable; marcar senha como transient
public class Aluno {
    private static final long serialVersionUID = 1L;
    private String nome;
    private double nota;
    private String senha;

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

        // TODO: salvar a lista
        // TODO: carregar e imprimir cada aluno (senha deve ser null)
    }

    // b) TODO: implementar com ObjectOutputStream + BufferedOutputStream
    static void salvar(List<Aluno> alunos, File arquivo) throws IOException { }

    // c) TODO: implementar com ObjectInputStream + BufferedInputStream + EOFException
    static List<Aluno> carregar(File arquivo) throws IOException, ClassNotFoundException {
        return null;
    }
}
```

**Saída esperada:**
```
Ana (nota=9.5, senha=null)
Bruno (nota=7.0, senha=null)
Carla (nota=8.5, senha=null)
```
