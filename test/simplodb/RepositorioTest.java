package simplodb;

import biblioteca.modelo.Livro;
import biblioteca.repositorio.LivroRepositorio;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class RepositorioTest {
    public static void executar() throws Exception {
        deveAtribuirIdAutomaticoAoSalvar();
        deveBuscarPorId();
        deveRetornarOptionalVazioQuandoIdNaoExiste();
        deveBuscarTodos();
        deveAtualizarRegistroComMesmoId();
        deveBuscarComFiltro();
        deveRetornarListaVaziaQuandoFiltroNaoEncontra();
        deveDeletarRegistroExistente();
        deveRetornarFalsoAoDeletarRegistroInexistente();
        deveContinuarSequenciaDeIdAoRecriarRepositorio();
    }

    private static void deveAtribuirIdAutomaticoAoSalvar() throws Exception {
        LivroRepositorio repositorio = criarRepositorio();

        Livro primeiro = repositorio.salvar(novoLivro("isbn-1", "Livro 1", "Romance", 2001));
        Livro segundo = repositorio.salvar(novoLivro("isbn-2", "Livro 2", "Tecnologia", 2002));

        verificarIgual(1L, primeiro.getId(), "Primeiro registro salvo deveria receber ID 1");
        verificarIgual(2L, segundo.getId(), "Segundo registro salvo deveria receber ID 2");
    }

    private static void deveBuscarPorId() throws Exception {
        LivroRepositorio repositorio = criarRepositorio();
        Livro livro = repositorio.salvar(novoLivro("isbn-1", "Livro 1", "Romance", 2001));

        Optional<Livro> encontrado = repositorio.buscarPorId(livro.getId());

        verificar(encontrado.isPresent(), "Busca por ID existente deveria retornar registro");
        verificarIgual("Livro 1", encontrado.get().getTitulo(), "Busca por ID deveria retornar livro correto");
    }

    private static void deveRetornarOptionalVazioQuandoIdNaoExiste() throws Exception {
        LivroRepositorio repositorio = criarRepositorio();

        Optional<Livro> encontrado = repositorio.buscarPorId(999L);

        verificar(encontrado.isEmpty(), "Busca por ID inexistente deveria retornar Optional vazio");
    }

    private static void deveBuscarTodos() throws Exception {
        LivroRepositorio repositorio = criarRepositorio();
        repositorio.salvar(novoLivro("isbn-1", "Livro 1", "Romance", 2001));
        repositorio.salvar(novoLivro("isbn-2", "Livro 2", "Tecnologia", 2002));

        List<Livro> todos = repositorio.buscarTodos();

        verificarIgual(2, todos.size(), "Buscar todos deveria retornar todos os registros salvos");
        verificar(contemTitulo(todos, "Livro 1"), "Buscar todos deveria conter Livro 1");
        verificar(contemTitulo(todos, "Livro 2"), "Buscar todos deveria conter Livro 2");
    }

    private static void deveAtualizarRegistroComMesmoId() throws Exception {
        LivroRepositorio repositorio = criarRepositorio();
        Livro livro = repositorio.salvar(novoLivro("isbn-1", "Titulo original", "Romance", 2001));
        livro.setTitulo("Titulo atualizado");

        repositorio.salvar(livro);
        Livro atualizado = repositorio.buscarPorId(livro.getId()).orElseThrow();

        verificarIgual("Titulo atualizado", atualizado.getTitulo(), "Salvar registro com ID existente deveria atualizar dados");
    }

    private static void deveBuscarComFiltro() throws Exception {
        LivroRepositorio repositorio = criarRepositorio();

        repositorio.salvar(novoLivro("9780132350884", "Clean Code", "Tecnologia", 2008));
        repositorio.salvar(novoLivro("9788525406958", "Dom Casmurro", "Romance", 1899));
        repositorio.salvar(novoLivro("9788535902778", "Memorias Postumas", "Romance", 1881));

        List<Livro> encontrados = repositorio.buscarComFiltro(livro -> "Romance".equals(livro.getGenero()));

        verificarIgual(2, encontrados.size(), "Deveria encontrar dois livros de Romance");
        verificar(contemTitulo(encontrados, "Dom Casmurro"), "Filtro deveria encontrar Dom Casmurro");
        verificar(contemTitulo(encontrados, "Memorias Postumas"), "Filtro deveria encontrar Memorias Postumas");
    }

    private static void deveRetornarListaVaziaQuandoFiltroNaoEncontra() throws Exception {
        LivroRepositorio repositorio = criarRepositorio();
        repositorio.salvar(novoLivro("isbn-1", "Livro 1", "Romance", 2001));

        List<Livro> encontrados = repositorio.buscarComFiltro(livro -> "Terror".equals(livro.getGenero()));

        verificar(encontrados.isEmpty(), "Filtro sem correspondencias deveria retornar lista vazia");
    }

    private static void deveDeletarRegistroExistente() throws Exception {
        LivroRepositorio repositorio = criarRepositorio();
        Livro livro = repositorio.salvar(novoLivro("isbn-1", "Livro 1", "Romance", 2001));

        boolean deletado = repositorio.deletar(livro.getId());

        verificar(deletado, "Deletar registro existente deveria retornar true");
        verificar(repositorio.buscarPorId(livro.getId()).isEmpty(), "Registro deletado nao deveria ser encontrado");
    }

    private static void deveRetornarFalsoAoDeletarRegistroInexistente() throws Exception {
        LivroRepositorio repositorio = criarRepositorio();

        boolean deletado = repositorio.deletar(999L);

        verificar(!deletado, "Deletar registro inexistente deveria retornar false");
    }

    private static void deveContinuarSequenciaDeIdAoRecriarRepositorio() throws Exception {
        Path diretorioTemporario = Files.createTempDirectory("repositorio-test-");
        LivroRepositorio primeiroRepositorio = new LivroRepositorio(new ArquivoMotor(diretorioTemporario));
        primeiroRepositorio.salvar(novoLivro("isbn-1", "Livro 1", "Romance", 2001));
        primeiroRepositorio.salvar(novoLivro("isbn-2", "Livro 2", "Tecnologia", 2002));

        LivroRepositorio segundoRepositorio = new LivroRepositorio(new ArquivoMotor(diretorioTemporario));
        Livro novoLivro = segundoRepositorio.salvar(novoLivro("isbn-3", "Livro 3", "Fantasia", 2003));

        verificarIgual(3L, novoLivro.getId(), "Repositorio recriado deveria continuar a sequencia do maior ID persistido");
    }

    private static LivroRepositorio criarRepositorio() throws Exception {
        Path diretorioTemporario = Files.createTempDirectory("repositorio-test-");
        return new LivroRepositorio(new ArquivoMotor(diretorioTemporario));
    }

    private static Livro novoLivro(String isbn, String titulo, String genero, int anoPublicacao) {
        return new Livro(isbn, titulo, genero, anoPublicacao, 1L);
    }

    private static boolean contemTitulo(List<Livro> livros, String titulo) {
        return livros.stream().anyMatch(livro -> titulo.equals(livro.getTitulo()));
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
