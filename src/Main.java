import com.animal.Animal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        List<Animal> animais = new ArrayList<>();
        animais.add(new Animal("Coelho", true,  false, 1.0));
        animais.add(new Animal("Sapo", true, false, 0.2));
        animais.add(new Animal("Sapo", true, true, 0.1));
        animais.add(new Animal("Cobra", false, true, 5.0));

        Map<Boolean, List<Animal>> grouped = animais.stream()                      //fonte
                .filter(Animal::podeSalta) //intermediaria
                //.peek(System.out::println) //intermediaria
                .sorted(Comparator.comparing(Animal::getNome).thenComparing(Animal::getPeso))
                //.map(Animal::getNome) //intermediaria
                //.peek(System.out::println) //intermediaria
                //.map(s -> s.toUpperCase()) //intermediaria
                //.peek(System.out::println) //intermediaria
                /* .map(s -> {
                     System.out.println("TESTE");Comparator.comparing(Animal::getNome)
                     return s.length();
                 })*/ //intermediaria*/
                //.forEach                .sorted(Comparator.comparing(Animal::getNome). thenComparing(Animal::getPeso))(System.out::println);//terminal
                //.forEach(System.out::println);
                .collect(Collectors.groupingBy(Animal::podeSalta));
        //.count();
        //System.out.printf("Quantidade de animais %d", count);
        System.out.println(animais.stream()                      //fonte
                .filter(Animal::podeSalta) //intermediaria
                .map(Animal::getNome)
                //.map(nome -> nome.concat(";"))//intermediaria
                //.forEach(System.out::print);
                .collect(Collectors.joining(";", "ANTES ", " DEPOIS ")));
        //System.out.println(nomesAnimais);

        //TODO Trazer exemplo de paralel stream

    }

}