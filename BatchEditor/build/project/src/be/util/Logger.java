package be.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {
	
    private static Logger logger;	
	private OutputStream f;
	private PrintStream p;
	
	public static Logger getLogger(){
		if (logger==null)
			logger = new Logger();
		return logger;
	}
	
	public Logger(){
		createLogFile();
	}
	
	void createLogFile() {
		File file = new File(System.getProperty("user.home") + File.separator + "batcheditor.log");
		try {
		if (file.exists())
			file.delete();
			this.f = new FileOutputStream(file);
			this.p = new PrintStream(this.f);
		} catch (FileNotFoundException e) {
		}
	}

	public synchronized void log(String s){
		if (p==null) createLogFile();
		p.println((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.UK)).format(new Date().getTime()) + "\t" + s);
	}
	
	public synchronized void debug(String s){
		if (p==null) createLogFile();
		p.println((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.UK)).format(new Date().getTime()) + "\t" +
				Thread.currentThread().getStackTrace()[2].getClassName() + "\t" +
				Thread.currentThread().getStackTrace()[2].getMethodName() + "\t"+ s);
	}
	
	public synchronized void error(String s){
		if (p==null) createLogFile();
		p.println((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.UK)).format(new Date().getTime()) + "\tERROR: " +s);
	}

}
