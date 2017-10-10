import java.util.Arrays;
import java.util.Comparator;

public class NameSort {
    
    private static final Name[] NAMES = new Name[] {
        new Name("Sally", "Smith"),
        new Name("Bob", "Marley"),
        new Name("Bob", "Dylan"),
        new Name("John", "Lennon"),
    };
    
    private static void printNames(String caption, Name[] names) {
    	System.out.println(caption);
    	for (Name n:names) {
    		System.out.println(n);
    	}
    }

    public static void main(String[] args) {

        // сортировка массива с помощью анонимного внутреннего класса
        Name[] copy = Arrays.copyOf(NAMES, NAMES.length);
        Arrays.sort(copy, new Comparator<Name>() {
            @Override
            public int compare(Name a, Name b) {
                return a.compareTo(b);
            }
        });
        printNames("Names sorted with anonymous inner class:", copy);

        // сортировка массива с помощью лямбда-выражения
        copy = Arrays.copyOf(NAMES, NAMES.length);
        Arrays.sort(copy, (a, b) -> a.compareTo(b));
        printNames("Names sorted with lambda expression:", copy);
    }
}