import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {


    public static void main(String[] args) throws IOException {

        byte[] bytes       = Files.readAllBytes(Path.of(""));
        var bytes2         = Files.readAllBytes(Path.of(""));

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