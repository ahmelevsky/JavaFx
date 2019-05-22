package te;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.logging.Logger;

public class MemoryLogger {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static long startUsed = 0;
	private static long lastUsed = 0;
	
	public static void print() {
		System.gc();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
		MemoryUsage hmusage = mbean.getHeapMemoryUsage();
		if (startUsed==0)
			startUsed = hmusage.getUsed();
		LOGGER.info("Heap Memory Usage: " + hmusage.getUsed() + ", More than initial: " + (hmusage.getUsed() - startUsed) + ", dif: " + (hmusage.getUsed()-lastUsed));
		lastUsed = hmusage.getUsed();
	}
	
}
