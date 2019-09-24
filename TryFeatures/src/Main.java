import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

	public static void main(String[] args) {
		 CompletableFuture.supplyAsync(() -> longProcess()).thenAccept(System.out::println);
		 
	     try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
	     System.out.println("Done!");
	}
	
	public static void example1() {
		IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList())
		 
	      .stream()
	 
	      .filter(integer -> integer % 2 == 0)
	 
	      .forEach(System.out::println);
	}

	public static void example2() {
		IntStream.rangeClosed(1, 10) .filter(integer -> integer % 2 == 0)
		 
	      .forEach(System.out::println);
	}
	
	 private static int longProcess() {
		 
	     try {
	 
	     Thread.sleep(2000);
	 
	     } catch (InterruptedException e) {
	 
	     e.printStackTrace();
	 
	     }
	 
	     return 1;
	 
	 }
}
