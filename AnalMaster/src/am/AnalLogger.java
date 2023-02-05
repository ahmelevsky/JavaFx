package am;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import am.LogFormatter;
import am.MyHtmlFormatter;

public class AnalLogger {
	static private FileHandler fileTxt;
    static private LogFormatter formatterTxt;

    static private FileHandler fileHTML;
    static private Formatter formatterHTML;

    static public void setup() throws IOException {

        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        
        // suppress the logging output to the console
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }

        logger.setLevel(Level.ALL);
        fileTxt = new FileHandler(System.getProperty("user.home") + File.separator + "AnalLogger_1.log" , true);
        fileHTML = new FileHandler(System.getProperty("user.home") + File.separator + "AnalLogger_1.html", true);
        fileTxt.setLevel(Level.ALL);
        fileHTML.setLevel(Level.INFO);
        // create a TXT formatter
        formatterTxt = new LogFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);
        // create an HTML formatter
        formatterHTML = new MyHtmlFormatter();
        fileHTML.setFormatter(formatterHTML);
       // logger.addHandler(fileHTML);
    }
}
