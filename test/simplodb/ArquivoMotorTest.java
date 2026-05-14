package simplodb;

import biblioteca.modelo.Livro;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class ArquivoMotorTest {
    public static void executar() throws Exception {
        deveSalvarECarregarObjeto();
        deveCriarDiretorioDaEntidadeAoSalvar();
        deveSobrescreverObjetoComMesmoId();
        deveRetornarOptionalVazioQuandoArquivoNaoExiste();
        deveCarregarTodosOsObjetos();
        deveRetornarListaVaziaQuandoEntidadeNaoExiste();
        deveIgnorarArquivosQueNaoSaoDatAoCarregarTodos();
        deveDeletarArquivoExistente();
        deveRetornarFalsoAoDeletarArquivoInexistente();
    }

    private static void deveSalvarECarregarObjeto() throws Exception {
        Path diretorioTemporario = Files.createTempDirectory("arquivo-motor-test-");
        ArquivoMotor motor = new ArquivoMotor(diretorioTemporario);
        Livro livro = new Livro("9788595084742", "O Hobbit", "Fantasia", 1937, 1L);

        motor.salvar("livros", 1L, livro);
        Optional<Livro> carregado = motor.carregar("livros", 1L);

        verificar(carregado.isPresent(), "Livro salvo deveria ser carregado");
        verificarIgual("O Hobbit", carregado.get().getTitulo(), "Livro carregado deveria manter o titulo");
        verificarIgual("9788595084742", carregado.get().getIsbn(), "Livro carregado deveria manter o ISBN");
    }

    private static void deveCriarDiretorioDaEntidadeAoSalvar() throws Exception {
        Path diretorioTemporario = Files.createTempDirectory("arquivo-motor-test-");
        ArquivoMotor motor = new ArquivoMotor(diretorioTemporario);

        motor.salvar("livros", 1L, new Livro("isbn-a", "Livro A", "Genero A", 2001, 1L));

        verificar(Files.isDirectory(diretorioTemporario.resolve("livros")), "Salvar deveria criar o diretorio da entidade");
        verificar(Files.exists(diretorioTemporario.resolve("livros").resolve("1.dat")), "Salvar deveria criar arquivo com ID informado");
    }

    private static void deveSobrescreverObjetoComMesmoId() throws Exception {
        Path diretorioTemporario = Files.createTempDirectory("arquivo-motor-test-");
        ArquivoMotor motor = new ArquivoMotor(diretorioTemporario);

        motor.salvar("livros", 1L, new Livro("isbn-a", "Primeira versao", "Genero A", 2001, 1L));
        motor.salvar("livros", 1L, new Livro("isbn-a", "Segunda versao", "Genero A", 2001, 1L));

        Optional<Livro> carregado = motor.carregar("livros", 1L);

        verificar(carregado.isPresent(), "Livro sobrescrito deveria ser carregado");
        verificarIgual("Segunda versao", carregado.get().getTitulo(), "Salvar mesmo ID deveria sobrescrever conteudo");
    }

    private static void deveRetornarOptionalVazioQuandoArquivoNaoExiste() throws Exception {
        Path diretorioTemporario = Files.createTempDirectory("arquivo-motor-test-");
        ArquivoMotor motor = new ArquivoMotor(diretorioTemporario);

        Optional<Livro> carregado = motor.carregar("livros", 999L);

        verificar(carregado.isEmpty(), "Carregar arquivo inexistente deveria retornar Optional vazio");
    }

    private static void deveCarregarTodosOsObjetos() throws Exception {
        Path diretorioTemporario = Files.createTempDirectory("arquivo-motor-test-");
        ArquivoMotor motor = new ArquivoMotor(diretorioTemporario);

        motor.salvar("livros", 1L, new Livro("isbn-a", "Livro A", "Genero A", 2001, 1L));
        motor.salvar("livros", 2L, new Livro("isbn-b", "Livro B", "Genero B", 2002, 2L));

        List<Livro> livros = motor.carregarTodos("livros");

        verificarIgual(2, livros.size(), "Deveria carregar todos os livros salvos");
        verificar(contemTitulo(livros, "Livro A"), "Lista deveria conter Livro A");
        verificar(contemTitulo(livros, "Livro B"), "Lista deveria conter Livro B");
    }

    private static void deveRetornarListaVaziaQuandoEntidadeNaoExiste() throws Exception {
        Path diretorioTemporario = Files.createTempDirectory("arquivo-motor-test-");
        ArquivoMotor motor = new ArquivoMotor(diretorioTemporario);

        List<Livro> livros = motor.carregarTodos("livros");

        verificar(livros.isEmpty(), "Entidade sem diretorio deveria retornar lista vazia");
    }

    private static void deveIgnorarArquivosQueNaoSaoDatAoCarregarTodos() throws Exception {
        Path diretorioTemporario = Files.createTempDirectory("arquivo-motor-test-");
        ArquivoMotor motor = new ArquivoMotor(diretorioTemporario);

        motor.salvar("livros", 1L, new Livro("isbn-a", "Livro A", "Genero A", 2001, 1L));
        Files.writeString(diretorioTemporario.resolve("livros").resolve("rascunho.txt"), "nao deve ser lido");

        List<Livro> livros = motor.carregarTodos("livros");

        verificarIgual(1, livros.size(), "Carregar todos deveria ignorar arquivos sem extensao .dat");
        verificarIgual("Livro A", livros.get(0).getTitulo(), "Arquivo .dat valido deveria ser carregado");
    }

    private static void deveDeletarArquivoExistente() throws Exception {
        Path diretorioTemporario = Files.createTempDirectory("arquivo-motor-test-");
        ArquivoMotor motor = new ArquivoMotor(diretorioTemporario);
        motor.salvar("livros", 1L, new Livro("isbn-a", "Livro A", "Genero A", 2001, 1L));

        boolean deletado = motor.deletar("livros", 1L);

        verificar(deletado, "Deletar arquivo existente deveria retornar true");
        verificar(motor.<Livro>carregar("livros", 1L).isEmpty(), "Arquivo deletado nao deveria ser carregado");
    }

    private static void deveRetornarFalsoAoDeletarArquivoInexistente() throws Exception {
        Path diretorioTemporario = Files.createTempDirectory("arquivo-motor-test-");
        ArquivoMotor motor = new ArquivoMotor(diretorioTemporario);

        boolean deletado = motor.deletar("livros", 999L);

        verificar(!deletado, "Deletar arquivo inexistente deveria retornar false");
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
