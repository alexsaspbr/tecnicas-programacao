package biblioteca.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EmprestimoTest {
    public static void executar() {
        deveIdentificarEmprestimoAtrasado();
        naoDeveMarcarComoAtrasadoQuandoAindaEstaNoPrazo();
        //naoDeveMarcarComoAtrasadoQuandoJaFoiDevolvido();
        deveCalcularMultaQuandoAtrasado();
        deveCalcularMultaQuandoFoiDevolvidoComAtraso();
        naoDeveCalcularMultaQuandoNaoEstaAtrasado();
        deveFormatarResumoDoEmprestimo();
        deveFormatarResumoDoEmprestimoAtrasado();
        deveFormatarResumoDoEmprestimoDevolvido();
    }

    private static void deveIdentificarEmprestimoAtrasado() {
        Emprestimo emprestimo = novoEmprestimoAberto(LocalDateTime.now().minusDays(2));

        verificar(emprestimo.estaAtrasado(), "Emprestimo vencido e nao devolvido deveria estar atrasado");
    }

    private static void naoDeveMarcarComoAtrasadoQuandoAindaEstaNoPrazo() {
        Emprestimo emprestimo = novoEmprestimoAberto(LocalDateTime.now().plusDays(2));

        verificar(!emprestimo.estaAtrasado(), "Emprestimo com vencimento futuro nao deveria estar atrasado");
    }

    private static void naoDeveMarcarComoAtrasadoQuandoJaFoiDevolvido() {
        Emprestimo emprestimo = novoEmprestimoAberto(LocalDateTime.now().minusDays(2));
        emprestimo.setDataDevolvido(LocalDateTime.now().minusDays(1));

        verificar(!emprestimo.estaAtrasado(), "Emprestimo devolvido nao deveria estar atrasado");
    }

    private static void deveCalcularMultaQuandoAtrasado() {
        Emprestimo emprestimo = novoEmprestimoAberto(LocalDateTime.now().minusDays(3).minusHours(1));

        verificarIgual(BigDecimal.valueOf(3), emprestimo.calcularMulta(), "Multa deveria ser de 3 dias");
    }

    private static void deveCalcularMultaQuandoFoiDevolvidoComAtraso() {
        LocalDateTime vencimento = LocalDateTime.of(2026, 5, 1, 10, 0);
        Emprestimo emprestimo = novoEmprestimoAberto(vencimento);
        emprestimo.setDataDevolvido(vencimento.plusDays(4).plusHours(2));

        verificarIgual(BigDecimal.valueOf(4), emprestimo.calcularMulta(), "Multa deveria usar a data de devolucao");
    }

    private static void naoDeveCalcularMultaQuandoNaoEstaAtrasado() {
        Emprestimo emprestimo = novoEmprestimoAberto(LocalDateTime.now().plusDays(1));

        verificarIgual(BigDecimal.ZERO, emprestimo.calcularMulta(), "Emprestimo no prazo nao deveria ter multa");
    }

    private static void deveFormatarResumoDoEmprestimo() {
        Emprestimo emprestimo = novoEmprestimoAberto(LocalDateTime.of(2026, 5, 20, 14, 30));

        verificarIgual(
                "Empr\u00e9stimo #3 | Livro: 7 | Usu\u00e1rio: 2 | Vence: 20/05/2026 14:30",
                emprestimo.toString(),
                "Resumo do emprestimo em aberto deveria seguir o formato esperado"
        );
    }

    private static void deveFormatarResumoDoEmprestimoAtrasado() {
        Emprestimo emprestimo = novoEmprestimoAberto(LocalDateTime.now().minusDays(3).minusHours(1));

        String resumo = emprestimo.toString();

        verificar(resumo.startsWith("Empr\u00e9stimo #3 | Livro: 7 | Usu\u00e1rio: 2 | Vence: "), "Resumo deveria conter dados base");
        verificar(resumo.contains(" | ATRASADO | Multa: R$ 3.00"), "Resumo atrasado deveria conter status e multa");
    }

    private static void deveFormatarResumoDoEmprestimoDevolvido() {
        Emprestimo emprestimo = novoEmprestimoAberto(LocalDateTime.of(2026, 5, 20, 14, 30));
        emprestimo.setDataDevolvido(LocalDateTime.of(2026, 5, 18, 9, 15));

        verificarIgual(
                "Empr\u00e9stimo #3 | Livro: 7 | Usu\u00e1rio: 2 | Vence: 20/05/2026 14:30 | Devolvido: 18/05/2026 09:15",
                emprestimo.toString(),
                "Resumo do emprestimo devolvido deveria seguir o formato esperado"
        );
    }

    private static Emprestimo novoEmprestimoAberto(LocalDateTime vencimento) {
        Emprestimo emprestimo = new Emprestimo(2L, 7L);
        emprestimo.setId(3L);
        emprestimo.setDataEmprestimo(vencimento.minusDays(Emprestimo.PRAZO_DIAS));
        emprestimo.setDataDevolucaoPrevista(vencimento);
        emprestimo.setDataDevolvido(null);
        return emprestimo;
    }

    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    private static void verificarIgual(Object esperado, Object obtido, String mensagem) {
        if (!esperado.equals(obtido)) {
            throw new AssertionError(mensagem + " | esperado: " + esperado + " | obtido: " + obtido);
        }
    }
}
