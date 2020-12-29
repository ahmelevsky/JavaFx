package mc;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pengrad.telegrambot.model.Update;


public class MLogger {

	static private FileHandler fileTxt;
    static private LogFormatter formatterTxt;
    static private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    static public void setup() throws IOException {

        // suppress the logging output to the console
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }

        logger.setLevel(Level.ALL);
        fileTxt = new FileHandler("Mcat.log");
        fileTxt.setLevel(Level.ALL);
        formatterTxt = new LogFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);
    }
    
    static public void logMessage(Update update) {
    	logger.fine("MESSAGE in chat:" + 
		           update.message().chat().firstName() + " -" + update.message().chat().id() +  "	" +
		           update.message().from().firstName() + " - " + update.message().from().id() + 
		           ": " + update.message().text());
    }
    
    static public void logAnswer(Update update, String answer) {
    	logger.fine("ANSWER to chat: " + 
		           update.message().chat().firstName() + " -" + update.message().chat().id() + "	" +
		           update.message().from().firstName() + " - " + update.message().from().id() + 
		           ": " + answer);
    }
}
