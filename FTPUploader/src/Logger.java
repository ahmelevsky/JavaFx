import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Logger {
	private OutputStream f;
	private PrintStream p;
	File baseDir = new File(".");
	
	void createLogFile() {
		File file = new File(baseDir +  File.separator + "_FTPUploader.log");
		try {
			if (file.exists())
				file.createNewFile();
			this.f = new FileOutputStream(file);
			this.p = new PrintStream(this.f);
		} catch (IOException e) {
			
		}
	}
	
	public synchronized void log(String s){
		if (p==null) createLogFile();
		p.println((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.UK)).format(new Date().getTime()) + "\t" + s);
	}
}
