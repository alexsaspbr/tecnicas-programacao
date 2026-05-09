import java.io.*;

public class Main {


    public static void main(String[] args) throws IOException {

       // byte[] bytes       = Files.readAllBytes(Path.of(""));
       // var bytes2         = Files.readAllBytes(Path.of(""));

        String alunoNota = "alex,2.0";
        String[] arrayAlunoNota = alunoNota.split(",");

        System.out.println(arrayAlunoNota[0]);
        System.out.println(Double.parseDouble(arrayAlunoNota[1]));

        try {
            copy();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

   public static void copy() throws IOException {

        try (var in  = new BufferedInputStream(new FileInputStream("/home/alexaraujo/Pictures/lion.jpg"));
             var out = new BufferedOutputStream(new FileOutputStream("destino.jpg"))) {

            //var buffer = new byte[1024];
            int lidos;
            while ((lidos = in.read()) != -1) {
                out.write(lidos);
            }
        }

    }

}