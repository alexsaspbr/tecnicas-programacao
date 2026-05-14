package biblioteca.servico;

import biblioteca.modelo.Emprestimo;
import biblioteca.modelo.Livro;
import biblioteca.repositorio.AutorRepositorio;
import biblioteca.repositorio.EmprestimoRepositorio;
import biblioteca.repositorio.LivroRepositorio;
import simplodb.ArquivoMotor;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RelatorioServicoTest {
    public static void executar() throws Exception {
        deveListarTop5LivrosMaisEmprestados();
        deveIgnorarEmprestimosDeLivrosNaoEncontradosNoTop5();
        deveCalcularMultasPendentesPorUsuario();
        deveRetornarMapaVazioQuandoNaoHaMultasPendentes();
        deveGerarRelatorioCompleto();
    }

    private static void deveListarTop5LivrosMaisEmprestados() throws Exception {
        Contexto contexto = criarContexto();
        Livro livro1 = contexto.salvarLivro("isbn-1", "Livro 1", "Tecnologia");
        Livro livro2 = contexto.salvarLivro("isbn-2", "Livro 2", "Romance");
        Livro livro3 = contexto.salvarLivro("isbn-3", "Livro 3", "Fantasia");
        Livro livro4 = contexto.salvarLivro("isbn-4", "Livro 4", "Tecnologia");
        Livro livro5 = contexto.salvarLivro("isbn-5", "Livro 5", "Romance");
        Livro livro6 = contexto.salvarLivro("isbn-6", "Livro 6", "Fantasia");

        contexto.salvarEmprestimos(livro1.getId(), 6);
        contexto.salvarEmprestimos(livro2.getId(), 5);
        contexto.salvarEmprestimos(livro3.getId(), 4);
        contexto.salvarEmprestimos(livro4.getId(), 3);
        contexto.salvarEmprestimos(livro5.getId(), 2);
        contexto.salvarEmprestimos(livro6.getId(), 1);

        List<Livro> top5 = contexto.servico.top5LivrosMaisEmprestados();

        verificarIgual(5, top5.size(), "Top 5 deveria retornar apenas cinco livros");
        verificarIgual("Livro 1", top5.get(0).getTitulo(), "Livro mais emprestado deveria vir primeiro");
        verificarIgual("Livro 2", top5.get(1).getTitulo(), "Segundo livro mais emprestado deveria vir em segundo");
        verificarIgual("Livro 3", top5.get(2).getTitulo(), "Terceiro livro mais emprestado deveria vir em terceiro");
        verificarIgual("Livro 4", top5.get(3).getTitulo(), "Quarto livro mais emprestado deveria vir em quarto");
        verificarIgual("Livro 5", top5.get(4).getTitulo(), "Quinto livro mais emprestado deveria vir em quinto");
    }

    private static void deveIgnorarEmprestimosDeLivrosNaoEncontradosNoTop5() throws Exception {
        Contexto contexto = criarContexto();
        Livro livro = contexto.salvarLivro("isbn-1", "Livro Existente", "Tecnologia");
        contexto.salvarEmprestimos(livro.getId(), 1);
        contexto.salvarEmprestimos(999L, 5);

        List<Livro> top5 = contexto.servico.top5LivrosMaisEmprestados();

        verificarIgual(1, top5.size(), "Top 5 deveria ignorar livros sem cadastro");
        verificarIgual("Livro Existente", top5.get(0).getTitulo(), "Livro existente deveria permanecer no resultado");
    }

    private static void deveCalcularMultasPendentesPorUsuario() throws Exception {
        Contexto contexto = criarContexto();
        Long usuario1 = 10L;
        Long usuario2 = 20L;

        contexto.salvarEmprestimoAberto(usuario1, 1L, LocalDateTime.now().minusDays(2).minusHours(1));
        contexto.salvarEmprestimoAberto(usuario1, 2L, LocalDateTime.now().minusDays(3).minusHours(1));
        contexto.salvarEmprestimoAberto(usuario2, 3L, LocalDateTime.now().minusDays(4).minusHours(1));
        contexto.salvarEmprestimoAberto(usuario1, 4L, LocalDateTime.now().plusDays(1));
        contexto.salvarEmprestimoDevolvido(usuario1, 5L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(5));

        Map<Long, BigDecimal> multas = contexto.servico.multasPendentesPorUsuario();

        verificarIgual(2, multas.size(), "Deveria haver multas pendentes para dois usuarios");
        verificarIgual(BigDecimal.valueOf(5), multas.get(usuario1), "Usuario 1 deveria acumular duas multas pendentes");
        verificarIgual(BigDecimal.valueOf(4), multas.get(usuario2), "Usuario 2 deveria ter uma multa pendente");
    }

    private static void deveRetornarMapaVazioQuandoNaoHaMultasPendentes() throws Exception {
        Contexto contexto = criarContexto();
        contexto.salvarEmprestimoAberto(10L, 1L, LocalDateTime.now().plusDays(1));
        contexto.salvarEmprestimoDevolvido(10L, 2L, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(1));

        Map<Long, BigDecimal> multas = contexto.servico.multasPendentesPorUsuario();

        verificar(multas.isEmpty(), "Nao deveria haver multas pendentes");
    }

    private static void deveGerarRelatorioCompleto() throws Exception {
        Contexto contexto = criarContexto();
        Livro tecnologia = contexto.salvarLivro("isbn-1", "Livro Tecnologia", "Tecnologia");
        Livro romance = contexto.salvarLivro("isbn-2", "Livro Romance", "Romance");
        Livro fantasia = contexto.salvarLivro("isbn-3", "Livro Fantasia", "Fantasia");

        contexto.salvarEmprestimos(tecnologia.getId(), 3);
        contexto.salvarEmprestimos(romance.getId(), 2);
        contexto.salvarEmprestimos(fantasia.getId(), 1);
        contexto.salvarEmprestimoAberto(99L, tecnologia.getId(), LocalDateTime.now().minusDays(2).minusHours(1));

        RelatorioServico.RelatorioCompleto relatorio = contexto.servico.gerarRelatorioCompleto();

        verificarIgual(3, relatorio.top5MaisEmprestados().size(), "Relatorio deveria conter ranking de livros emprestados");
        verificarIgual("Livro Tecnologia", relatorio.top5MaisEmprestados().get(0).getTitulo(), "Livro mais emprestado deveria liderar o ranking");
        verificarIgual(BigDecimal.valueOf(2), relatorio.multasPendentes().get(99L), "Relatorio deveria conter multas pendentes");
        verificarIgual(3, relatorio.livrosPorGenero().size(), "Relatorio deveria agrupar livros por genero");
        verificarIgual(1, relatorio.livrosPorGenero().get("Tecnologia").size(), "Genero Tecnologia deveria ter um livro");
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

    private static Contexto criarContexto() throws Exception {
        Path diretorioTemporario = Files.createTempDirectory("relatorio-servico-test-");
        ArquivoMotor motor = new ArquivoMotor(diretorioTemporario);
        LivroRepositorio livroRepo = new LivroRepositorio(motor);
        AutorRepositorio autorRepo = new AutorRepositorio(motor);
        EmprestimoRepositorio emprestimoRepo = new EmprestimoRepositorio(motor);
        RelatorioServico servico = new RelatorioServico(livroRepo, autorRepo, emprestimoRepo);
        return new Contexto(livroRepo, emprestimoRepo, servico);
    }

    private record Contexto(
            LivroRepositorio livroRepo,
            EmprestimoRepositorio emprestimoRepo,
            RelatorioServico servico
    ) {
        private Livro salvarLivro(String isbn, String titulo, String genero) {
            return livroRepo.salvar(new Livro(isbn, titulo, genero, 2026, 1L));
        }

        private void salvarEmprestimos(Long livroId, int quantidade) {
            for (int i = 0; i < quantidade; i++) {
                salvarEmprestimoAberto(100L + i, livroId, LocalDateTime.now().plusDays(1));
            }
        }

        private Emprestimo salvarEmprestimoAberto(Long usuarioId, Long livroId, LocalDateTime vencimento) {
            Emprestimo emprestimo = new Emprestimo(usuarioId, livroId);
            emprestimo.setDataEmprestimo(vencimento.minusDays(Emprestimo.PRAZO_DIAS));
            emprestimo.setDataDevolucaoPrevista(vencimento);
            emprestimo.setDataDevolvido(null);
            return emprestimoRepo.salvar(emprestimo);
        }

        private Emprestimo salvarEmprestimoDevolvido(
                Long usuarioId,
                Long livroId,
                LocalDateTime vencimento,
                LocalDateTime devolucao
        ) {
            Emprestimo emprestimo = salvarEmprestimoAberto(usuarioId, livroId, vencimento);
            emprestimo.setDataDevolvido(devolucao);
            return emprestimoRepo.salvar(emprestimo);
        }
    }
}
