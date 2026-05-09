import java.io.Serializable;

public class PessoaFisica implements Serializable {

    private static final long serialVersionUID = 4042322851451794298L;

    private String cpf;
    private String nome;
    private transient String apelido;


}
