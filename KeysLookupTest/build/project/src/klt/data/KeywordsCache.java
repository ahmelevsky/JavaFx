package klt.data;

import java.util.Collection;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.collections4.queue.CircularFifoQueue;

public class KeywordsCache {

	 private static Queue<KeywordsData> fifo = new CircularFifoQueue<KeywordsData>(2000);
	
	 
	 public static void add(ImageData image) {
		 KeywordsData found = fifo.stream().filter(item -> item.id.equals(image.id)).findFirst().orElse(null);
		 if (found==null) 
			 fifo.add(new KeywordsData(image.id, image.getAllKeywords()));
		 else if (found.keywords.isEmpty())
			 found.keywords.addAll(image.getAllKeywords());
	 }
	 
	 /**
	  * 
	  * @param image
	  * @return true if found in cache, else false
	  */
	 public static boolean get(ImageData image) {
		 KeywordsData found = fifo.stream().filter(item -> item.id.equals(image.id)).findFirst().orElse(null);
		 if (found==null || found.keywords.isEmpty())
			 return false;
		 else {
			 image.addKeywords(found.keywords);
			 return true;
		 }
	 }
	 
}
