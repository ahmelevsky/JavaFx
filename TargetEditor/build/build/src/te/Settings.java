package te;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class Settings {
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static String writeOption = "jpg";
	public static Locale locale = Locale.ENGLISH;
	public static ResourceBundle bundle;
	public static boolean autosaveEnabled;

	public static String getLanguage() {
		return locale.getLanguage();
	}
	
	
	public static void setLanguage(String language) {
		if (language.equals("ru")) {
			locale = new Locale("ru");
		}
		else if (language.equals("en")) {
			locale = new Locale("en");
		}
		else {
			LOGGER.warning("Unknown language is loaded from settings file. English will be used");
			locale = new Locale("en");
		}
	}
		
		public static String getWriteOption() {
			return writeOption;
		}
		
		public static void setWriteOption(String wo) {
			if (wo.equals("jpg")) {
				writeOption = "jpg";
			}
			else if (wo.equals("eps")) {
				writeOption = "eps";
			}
			else if (wo.equals("all")) {
				writeOption = "all";
			}
			else {
				LOGGER.warning("No Write Option is loaded from settings file. Only JPG will be used");
				writeOption = "jpg";
			}
		}
	
		
}
