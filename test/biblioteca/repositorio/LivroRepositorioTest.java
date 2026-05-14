package biblioteca.repositorio;

import biblioteca.modelo.Livro;
import simplodb.ArquivoMotor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LivroRepositorioTest {
    public static void executar() throws Exception {
        deveBuscarPorIsbn();
        deveRetornarVazioQuandoIsbnNaoExiste();
        deveBuscarPorGeneroOrdenadoPorAnoDescendente();
        deveRetornarListaVaziaQuandoGeneroNaoExiste();
        deveAgruparPorGenero();
    }

    private static void deveBuscarPorIsbn() throws Exception {
        LivroRepositorio repositorio = criarRepositorio();
        repositorio.salvar(new Livro("isbn-1", "Livro A", "Romance", 2001, 1L));
        repositorio.salvar(new Livro("isbn-2", "Livro B", "Tecnologia", 2002, 2L));

        Optional<Livro> encontrado = repositorio.buscarPorIsbn("isbn-2");

        verificar(encontrado.isPresent(), "Deveria encontrar livro pelo ISBN");
        verificarIgual("Livro B", encontrado.get().getTitulo(), "Deveria retornar o livro com ISBN correspondente");
    }

    private static void deveRetornarVazioQuandoIsbnNaoExiste() throws Exception {
        LivroRepositorio repositorio = criarRepositorio();
        repositorio.salvar(new Livro("isbn-1", "Livro A", "Romance", 2001, 1L));

        Optional<Livro> encontrado = repositorio.buscarPorIsbn("isbn-inexistente");

        verificar(encontrado.isEmpty(), "Busca por ISBN inexistente deveria retornar Optional vazio");
    }

    private static void deveBuscarPorGeneroOrdenadoPorAnoDescendente() throws Exception {
        LivroRepositorio repositorio = criarRepositorio();
        repositorio.salvar(new Livro("isbn-1", "Romance Antigo", "Romance", 1980, 1L));
        repositorio.salvar(new Livro("isbn-2", "Romance Novo", "Romance", 2020, 2L));
        repositorio.salvar(new Livro("isbn-4", "Romance Intermediario", "Romance", 2005, 4L));
        repositorio.salvar(new Livro("isbn-3", "Fantasia", "Fantasia", 2000, 3L));

        List<Livro> romances = repositorio.buscarPorGeneroCoordenado("rOmAnCe");

        verificarIgual(3, romances.size(), "Deveria encontrar tres romances");
        verificarIgual("Romance Novo", romances.get(0).getTitulo(), "Romance mais recente deveria vir primeiro");
        verificarIgual("Romance Intermediario", romances.get(1).getTitulo(), "Romance intermediario deveria vir em segundo");
        verificarIgual("Romance Antigo", romances.get(2).getTitulo(), "Romance mais antigo deveria vir por ultimo");
    }

    private static void deveRetornarListaVaziaQuandoGeneroNaoExiste() throws Exception {
        LivroRepositorio repositorio = criarRepositorio();
        repositorio.salvar(new Livro("isbn-1", "Livro A", "Romance", 2001, 1L));

        List<Livro> livros = repositorio.buscarPorGeneroCoordenado("Terror");

        verificar(livros.isEmpty(), "Genero inexistente deveria retornar lista vazia");
    }

    private static void deveAgruparPorGenero() throws Exception {
        LivroRepositorio repositorio = criarRepositorio();
        repositorio.salvar(new Livro("isbn-a", "Livro A", "Romance", 2001, 1L));
        repositorio.salvar(new Livro("isbn-b", "Livro B", "Romance", 2002, 2L));
        repositorio.salvar(new Livro("isbn-c", "Livro C", "Tecnologia", 2003, 3L));

        Map<String, List<Livro>> porGenero = repositorio.agruparPorGenero();

        verificarIgual(2, porGenero.size(), "Deveria criar um grupo para cada genero");
        verificarIgual(2, porGenero.get("Romance").size(), "Deveria agrupar dois livros em Romance");
        verificarIgual(1, porGenero.get("Tecnologia").size(), "Deveria agrupar um livro em Tecnologia");
        verificarIgual("Livro C", porGenero.get("Tecnologia").get(0).getTitulo(), "Grupo Tecnologia deveria conter o livro correto");
    }

    private static LivroRepositorio criarRepositorio() throws Exception {
        Path diretorioTemporario = Files.createTempDirectory("livro-repositorio-test-");
        return new LivroRepositorio(new ArquivoMotor(diretorioTemporario));
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
