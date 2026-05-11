# Gabarito — Trabalhando com Datas em Java

---

### Exercício 1 — Calculadora de Idade

```java
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public class CalculadoraIdade {

    public static void main(String[] args) {
        calcularIdade(LocalDate.of(1995, 6, 20));
        calcularIdade(LocalDate.of(2010, 12, 1));
    }

    public static void calcularIdade(LocalDate nascimento) {
        LocalDate hoje = LocalDate.now();
        Period periodo = Period.between(nascimento, hoje);

        System.out.printf("Idade: %d anos e %d meses%n",
            periodo.getYears(), periodo.getMonths());
        System.out.println("Maior de idade: " + (periodo.getYears() >= 18));

        LocalDate proximoAniversario = nascimento.withYear(hoje.getYear());
        if (!proximoAniversario.isAfter(hoje)) {
            proximoAniversario = proximoAniversario.plusYears(1);
        }
        long diasParaAniversario = ChronoUnit.DAYS.between(hoje, proximoAniversario);
        System.out.println("Dias para o próximo aniversário: " + diasParaAniversario);
        System.out.println();
    }
}
```

`ChronoUnit.DAYS.between()` calcula a diferença exata em dias entre dois `LocalDate`. O `withYear()` ajusta apenas o ano, mantendo dia e mês — e a verificação com `isAfter()` garante que, se o aniversário já passou este ano, buscamos o do próximo.

---

### Exercício 2 — Formatador de Datas

```java
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FormatadorDatas {

    private static final List<DateTimeFormatter> FORMATOS = List.of(
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy")
    );

    public static void main(String[] args) {
        System.out.println(parsearData("24/04/2024"));
        System.out.println(parsearData("2024-04-24"));
        System.out.println(parsearData("24-04-2024"));
    }

    public static LocalDate parsearData(String texto) {
        for (DateTimeFormatter fmt : FORMATOS) {
            try {
                return LocalDate.parse(texto, fmt);
            } catch (DateTimeParseException e) {
                // tenta o próximo formato
            }
        }
        throw new IllegalArgumentException("Formato de data não reconhecido: " + texto);
    }
}
```

Capturar `DateTimeParseException` dentro do loop permite tentar cada formato sem interromper o fluxo. A lista de formatadores pode ser estendida facilmente sem alterar a lógica de parsing. O `toString()` padrão do `LocalDate` já usa ISO 8601 (`yyyy-MM-dd`), por isso as três entradas produzem a mesma saída.

---

### Exercício 3 — Sistema de Reservas

```java
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Reserva {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private LocalDate dataEntrada;
    private LocalDate dataSaida;

    public Reserva(LocalDate dataEntrada, LocalDate dataSaida) {
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
    }

    public long quantidadeDeNoites() {
        return ChronoUnit.DAYS.between(dataEntrada, dataSaida);
    }

    public boolean estaAtiva() {
        LocalDate hoje = LocalDate.now();
        return !hoje.isBefore(dataEntrada) && hoje.isBefore(dataSaida);
    }

    @Override
    public String toString() {
        return String.format("Reserva de %s a %s (%d noites)",
            dataEntrada.format(FMT),
            dataSaida.format(FMT),
            quantidadeDeNoites()
        );
    }

    public static void main(String[] args) {
        Reserva r1 = new Reserva(LocalDate.of(2026, 4, 24), LocalDate.of(2026, 4, 30));
        Reserva r2 = new Reserva(LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 15));

        System.out.println(r1);
        System.out.println("Ativa: " + r1.estaAtiva());
        System.out.println();
        System.out.println(r2);
        System.out.println("Ativa: " + r2.estaAtiva());
    }
}
```

`estaAtiva()` usa o intervalo semi-aberto `[dataEntrada, dataSaida)`: `!hoje.isBefore(dataEntrada)` equivale a `hoje >= dataEntrada`, e `hoje.isBefore(dataSaida)` garante que o dia de saída já não conta como noite de estadia. O formatter é declarado como `static final` para ser criado uma única vez e reutilizado.

---

### Exercício 4 — Conversor de Fuso Horário

```java
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ConversorFuso {

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm z");
    private static final ZoneId ZONA_SP = ZoneId.of("America/Sao_Paulo");

    public static void main(String[] args) {
        LocalDateTime reuniaoSP = LocalDateTime.of(2024, 6, 15, 10, 0);

        System.out.println(converterFuso(reuniaoSP, "America/New_York"));
        System.out.println(converterFuso(reuniaoSP, "Europe/London"));
        System.out.println(converterFuso(reuniaoSP, "Asia/Tokyo"));
    }

    public static String converterFuso(LocalDateTime dataHoraSP, String fusoDestino) {
        ZonedDateTime horarioSP = dataHoraSP.atZone(ZONA_SP);
        ZonedDateTime horarioDestino = horarioSP.withZoneSameInstant(ZoneId.of(fusoDestino));
        return horarioDestino.format(FMT);
    }
}
```

`atZone()` associa um fuso a um `LocalDateTime` sem alterar os valores numéricos; `withZoneSameInstant()` converte para o mesmo ponto no tempo em outro fuso — o instante absoluto é preservado, os números mudam. `ZoneId.of()` aceita qualquer identificador IANA (ex: `"America/New_York"`, `"Asia/Tokyo"`).
