public class TestRunner {
    private static int executados = 0;
    private static int ignorados = 0;
    private static int falhas = 0;

    public static void main(String[] args) throws Exception {
        executar("EmprestimoTest", biblioteca.modelo.EmprestimoTest::executar);
        executar("RepositorioTest", simplodb.RepositorioTest::executar);
        executar("ArquivoMotorTest", simplodb.ArquivoMotorTest::executar);
        executar("LivroRepositorioTest", biblioteca.repositorio.LivroRepositorioTest::executar);
        executar("BibliotecaServicoTest", biblioteca.servico.BibliotecaServicoTest::executar);
        executar("RelatorioServicoTest", biblioteca.servico.RelatorioServicoTest::executar);

        System.out.println();
        System.out.println("Resumo dos testes:");
        System.out.println("- Executados com sucesso: " + executados);
        System.out.println("- Ignorados por exercicio pendente: " + ignorados);
        System.out.println("- Falhas: " + falhas);

        if (falhas > 0) {
            System.exit(1);
        }
    }

    private static void executar(String nome, Teste teste) {
        try {
            teste.executar();
            executados++;
            System.out.println("[OK] " + nome);
        } catch (Throwable erro) {
            if (isExercicioPendente(erro)) {
                ignorados++;
                System.out.println("[IGNORADO] " + nome + " - exercicio ainda nao implementado");
                return;
            }

            falhas++;
            System.out.println("[FALHOU] " + nome);
            erro.printStackTrace(System.out);
        }
    }

    private static boolean isExercicioPendente(Throwable erro) {
        Throwable atual = erro;
        while (atual != null) {
            if (atual instanceof UnsupportedOperationException) {
                return true;
            }
            atual = atual.getCause();
        }
        return false;
    }

    @FunctionalInterface
    private interface Teste {
        void executar() throws Exception;
    }
}
