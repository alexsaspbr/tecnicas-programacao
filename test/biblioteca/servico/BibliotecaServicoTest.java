package biblioteca.servico;

import biblioteca.modelo.Emprestimo;
import biblioteca.modelo.Livro;
import biblioteca.modelo.Usuario;
import biblioteca.repositorio.EmprestimoRepositorio;
import biblioteca.repositorio.LivroRepositorio;
import biblioteca.repositorio.UsuarioRepositorio;
import simplodb.ArquivoMotor;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BibliotecaServicoTest {
    public static void executar() throws Exception {
        deveRegistrarEmprestimoQuandoDadosSaoValidos();
        deveRecusarUsuarioInexistente();
        deveRecusarLivroInexistente();
        deveRecusarLivroJaEmprestado();
        deveRecusarQuandoUsuarioAtingiuLimiteDeEmprestimos();
        deveDevolverLivro();
        deveRecusarDevolucaoDeEmprestimoInexistente();
        deveRecusarDevolucaoDuplicada();
        deveCalcularMultaTotalDoUsuario();
    }

    private static void deveRegistrarEmprestimoQuandoDadosSaoValidos() throws Exception {
        Contexto contexto = criarContexto();
        Usuario usuario = contexto.salvarUsuario("Ana");
        Livro livro = contexto.salvarLivro("isbn-1", "Livro A");

        Emprestimo emprestimo = contexto.servico.registrarEmprestimo(usuario.getId(), livro.getId());

        verificar(emprestimo.getId() != null, "Emprestimo registrado deveria receber ID");
        verificarIgual(usuario.getId(), emprestimo.getUsuarioId(), "Emprestimo deveria pertencer ao usuario informado");
        verificarIgual(livro.getId(), emprestimo.getLivroId(), "Emprestimo deveria pertencer ao livro informado");
        verificar(contexto.emprestimoRepo.buscarPorId(emprestimo.getId()).isPresent(), "Emprestimo deveria ser persistido");
    }

    private static void deveRecusarUsuarioInexistente() throws Exception {
        Contexto contexto = criarContexto();
        Livro livro = contexto.salvarLivro("isbn-1", "Livro A");

        IllegalArgumentException erro = verificarLanca(
                IllegalArgumentException.class,
                () -> contexto.servico.registrarEmprestimo(999L, livro.getId()),
                "Usuario inexistente deveria impedir emprestimo"
        );

        verificar(erro.getMessage().contains("999"), "Mensagem deveria mencionar o ID do usuario inexistente");
    }

    private static void deveRecusarLivroInexistente() throws Exception {
        Contexto contexto = criarContexto();
        Usuario usuario = contexto.salvarUsuario("Ana");

        IllegalArgumentException erro = verificarLanca(
                IllegalArgumentException.class,
                () -> contexto.servico.registrarEmprestimo(usuario.getId(), 999L),
                "Livro inexistente deveria impedir emprestimo"
        );

        verificar(erro.getMessage().contains("999"), "Mensagem deveria mencionar o ID do livro inexistente");
    }

    private static void deveRecusarLivroJaEmprestado() throws Exception {
        Contexto contexto = criarContexto();
        Usuario usuarioA = contexto.salvarUsuario("Ana");
        Usuario usuarioB = contexto.salvarUsuario("Bruno");
        Livro livro = contexto.salvarLivro("isbn-1", "Livro A");
        contexto.servico.registrarEmprestimo(usuarioA.getId(), livro.getId());

        IllegalStateException erro = verificarLanca(
                IllegalStateException.class,
                () -> contexto.servico.registrarEmprestimo(usuarioB.getId(), livro.getId()),
                "Livro ja emprestado deveria impedir novo emprestimo"
        );

        verificar(erro.getMessage().contains(String.valueOf(livro.getId())), "Mensagem deveria mencionar o ID do livro emprestado");
    }

    private static void deveRecusarQuandoUsuarioAtingiuLimiteDeEmprestimos() throws Exception {
        Contexto contexto = criarContexto();
        Usuario usuario = contexto.salvarUsuario("Ana");
        Livro livro1 = contexto.salvarLivro("isbn-1", "Livro A");
        Livro livro2 = contexto.salvarLivro("isbn-2", "Livro B");
        Livro livro3 = contexto.salvarLivro("isbn-3", "Livro C");
        Livro livro4 = contexto.salvarLivro("isbn-4", "Livro D");

        contexto.servico.registrarEmprestimo(usuario.getId(), livro1.getId());
        contexto.servico.registrarEmprestimo(usuario.getId(), livro2.getId());
        contexto.servico.registrarEmprestimo(usuario.getId(), livro3.getId());

        IllegalStateException erro = verificarLanca(
                IllegalStateException.class,
                () -> contexto.servico.registrarEmprestimo(usuario.getId(), livro4.getId()),
                "Usuario com limite atingido deveria impedir novo emprestimo"
        );

        verificar(erro.getMessage().contains(String.valueOf(usuario.getId())), "Mensagem deveria mencionar o ID do usuario");
    }

    private static void deveDevolverLivro() throws Exception {
        Contexto contexto = criarContexto();
        Usuario usuario = contexto.salvarUsuario("Ana");
        Livro livro = contexto.salvarLivro("isbn-1", "Livro A");
        Emprestimo emprestimo = contexto.servico.registrarEmprestimo(usuario.getId(), livro.getId());

        Emprestimo devolvido = contexto.servico.devolverLivro(emprestimo.getId());

        verificar(devolvido.isDevolvido(), "Emprestimo devolvido deveria ter data de devolucao");
        verificar(contexto.emprestimoRepo.buscarAbertos().isEmpty(), "Emprestimo devolvido nao deveria continuar em aberto");
    }

    private static void deveRecusarDevolucaoDeEmprestimoInexistente() throws Exception {
        Contexto contexto = criarContexto();

        IllegalArgumentException erro = verificarLanca(
                IllegalArgumentException.class,
                () -> contexto.servico.devolverLivro(999L),
                "Emprestimo inexistente deveria impedir devolucao"
        );

        verificar(erro.getMessage().contains("999"), "Mensagem deveria mencionar o ID do emprestimo inexistente");
    }

    private static void deveRecusarDevolucaoDuplicada() throws Exception {
        Contexto contexto = criarContexto();
        Usuario usuario = contexto.salvarUsuario("Ana");
        Livro livro = contexto.salvarLivro("isbn-1", "Livro A");
        Emprestimo emprestimo = contexto.servico.registrarEmprestimo(usuario.getId(), livro.getId());
        contexto.servico.devolverLivro(emprestimo.getId());

        IllegalStateException erro = verificarLanca(
                IllegalStateException.class,
                () -> contexto.servico.devolverLivro(emprestimo.getId()),
                "Emprestimo ja devolvido deveria impedir segunda devolucao"
        );

        verificar(erro.getMessage().contains(String.valueOf(emprestimo.getId())), "Mensagem deveria mencionar o ID do emprestimo");
    }

    private static void deveCalcularMultaTotalDoUsuario() throws Exception {
        Contexto contexto = criarContexto();
        Usuario usuario = contexto.salvarUsuario("Ana");
        Usuario outroUsuario = contexto.salvarUsuario("Bruno");

        contexto.salvarEmprestimoAberto(usuario.getId(), 1L, LocalDateTime.now().minusDays(2).minusHours(1));
        contexto.salvarEmprestimoAberto(usuario.getId(), 2L, LocalDateTime.now().minusDays(3).minusHours(1));
        contexto.salvarEmprestimoAberto(outroUsuario.getId(), 3L, LocalDateTime.now().minusDays(10));
        contexto.salvarEmprestimoAberto(usuario.getId(), 4L, LocalDateTime.now().plusDays(1));

        BigDecimal multaTotal = contexto.servico.calcularMultaTotal(usuario.getId());

        verificarIgual(BigDecimal.valueOf(5), multaTotal, "Multa total deveria somar apenas atrasos abertos do usuario informado");
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

    private static <T extends Throwable> T verificarLanca(Class<T> tipo, Acao acao, String mensagem) throws Exception {
        try {
            acao.executar();
        } catch (Throwable erro) {
            if (tipo.isInstance(erro)) {
                return tipo.cast(erro);
            }
            throw new AssertionError(mensagem + " | excecao esperada: " + tipo.getSimpleName()
                    + " | excecao obtida: " + erro.getClass().getSimpleName(), erro);
        }

        throw new AssertionError(mensagem + " | nenhuma excecao foi lancada");
    }

    private static Contexto criarContexto() throws Exception {
        Path diretorioTemporario = Files.createTempDirectory("biblioteca-servico-test-");
        ArquivoMotor motor = new ArquivoMotor(diretorioTemporario);
        LivroRepositorio livroRepo = new LivroRepositorio(motor);
        UsuarioRepositorio usuarioRepo = new UsuarioRepositorio(motor);
        EmprestimoRepositorio emprestimoRepo = new EmprestimoRepositorio(motor);
        BibliotecaServico servico = new BibliotecaServico(livroRepo, usuarioRepo, emprestimoRepo);
        return new Contexto(livroRepo, usuarioRepo, emprestimoRepo, servico);
    }

    @FunctionalInterface
    private interface Acao {
        void executar() throws Exception;
    }

    private record Contexto(
            LivroRepositorio livroRepo,
            UsuarioRepositorio usuarioRepo,
            EmprestimoRepositorio emprestimoRepo,
            BibliotecaServico servico
    ) {
        private Usuario salvarUsuario(String nome) {
            String normalizado = nome.toLowerCase();
            return usuarioRepo.salvar(new Usuario(
                    nome,
                    normalizado + "@example.com",
                    "cpf-" + normalizado,
                    LocalDate.of(2026, 5, 11)
            ));
        }

        private Livro salvarLivro(String isbn, String titulo) {
            return livroRepo.salvar(new Livro(isbn, titulo, "Tecnologia", 2026, 1L));
        }

        private Emprestimo salvarEmprestimoAberto(Long usuarioId, Long livroId, LocalDateTime vencimento) {
            Emprestimo emprestimo = new Emprestimo(usuarioId, livroId);
            emprestimo.setDataEmprestimo(vencimento.minusDays(Emprestimo.PRAZO_DIAS));
            emprestimo.setDataDevolucaoPrevista(vencimento);
            emprestimo.setDataDevolvido(null);
            return emprestimoRepo.salvar(emprestimo);
        }
    }
}
