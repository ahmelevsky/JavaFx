package mc;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendAnimation;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

import web.GiphyWorker;

public class Mashenka {
     
	 private final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	 String sticker_mashenka_1 = "CAACAgIAAxkBAANeXsvoqjaxY4oEUwrSBDvl9IWkAAH2AAJnAAObYkcLbxRoU2YFzPcZBA";
	 TelegramBot bot; 
	 String[] ignoredWords = { "мне", "пожалуйста", "будь добр", "плиз", "наконец", "снова", "опять", "еще раз",
				"все-таки", "все же", "\\,", "\\."};
	 String[] symbols = {"\\.", "\\,", "\\!", "\\?", "-", "\\:"};
	 String[] sendWords = {"пришли", "покажи", "найди"};
	 String[] showWords = {"покажись", "появись"};
	 String[] startWords = {"маашенька", "мааашенька", "машенька", "кот", "мяучело", "котик", "котенька", "котя"};
	 
	 
	
	 public Mashenka(String token) {
		 this.bot = new TelegramBot(token);
		 LOGGER.fine("Running Mcat bot.");
		 System.out.println("Running Mcat bot.");
	 };
	 
	 public GetUpdatesResponse getUpdates(GetUpdates getUpdates) {
		 return this.bot.execute(getUpdates);
	 }
	 
	 public void get(Update update) {
		 if (update.message().sticker() != null) {
				System.out.println(update.message().sticker().fileId());
				LOGGER.fine("GET STICKER: " + (update.message().sticker().fileId()));
		 }
		 
			if (update.message() != null && update.message().text() != null) {
				String text = update.message().text().toLowerCase();
				text = removeWords(text, symbols);
				
				if (isContain(text,"как тебя зовут") && isContain(text,startWords) )  {
					MLogger.logMessage(update);
					bot.execute(new SendMessage(update.message().chat().id(), "Маааашенька..."));
					MLogger.logAnswer(update, "Маааашенька...");
				}
				
				else if (isStarts(text, startWords)) {
					MLogger.logMessage(update);
					doSpecialCommand(update, removeFirst(text, startWords));
				}
			   
			    else if (isContain(text,this.startWords)){
			    	MLogger.logMessage(update);
					bot.execute(new SendMessage(update.message().chat().id(), "Мау!"));
					MLogger.logAnswer(update,  "Мау!");
				}
			}
	 }
	 
	 private void doSpecialCommand(Update update, String text) {
		 if (isContain(text, this.sendWords))
			 sendGif(update, removeWords(text, this.sendWords));
		 else if (text.trim().isEmpty()) {
			 bot.execute(new SendMessage(update.message().chat().id(), "Чего?"));
			 MLogger.logAnswer(update,  "Мау!");
		 }
		 else if (isContain(text, this.showWords)) {
			 showYourself(update);
		 }
		 else {
			 bot.execute(new SendMessage(update.message().chat().id(), "Мау!"));
			 MLogger.logAnswer(update,  "Мау!");
		 }
			 
	 }
	 
	 private void showYourself(Update update) {
		 SendSticker sendSticker = new SendSticker(update.message().chat().id(), sticker_mashenka_1);
		 bot.execute(sendSticker);
		 LOGGER.fine("SHOW MASHENKA");
	 }
	 
	 private void sendGif(Update update, String text) {
		text = removeWords(text, this.ignoredWords);
		if (text.trim().isEmpty()) {
			LOGGER.fine("Request GIF string empty after clean");
			bot.execute(new SendMessage(update.message().chat().id(), "Я просто кот, чего надо то"));
			MLogger.logAnswer(update,  "Я просто кот, чего надо то");
		}
		else 
		try {
			System.out.println(text.trim());
			LOGGER.fine("Request GIF result string: " + text.trim());
		    String json = GiphyWorker.searchSticker(text.trim());
		    if (json!=null) {
			 List<String> list= GiphyWorker.getImagesList(json);
			 String link = null;
			 if (list.isEmpty()) {
				 System.out.println("Send random gif");
				 LOGGER.fine("Send random gif");
				 json = GiphyWorker.searchRandomSticker();
				 link = GiphyWorker.getRandomImage(json);
			 }
			 else {
				 link = list.get(new Random().nextInt(list.size()));
				 LOGGER.fine("Found Gif");
			 }
			 SendAnimation sendSticker =  new SendAnimation(update.message().chat().id(), link);
			 bot.execute(sendSticker);
		   }
		}
		catch (Exception e) {
			LOGGER.severe(e.getMessage());
			bot.execute(new SendMessage(update.message().chat().id(), "Ой!"));
		}
	 }

	 private boolean isContain(String source, String subItem){
         String pattern = "\\b"+subItem+"\\b";
         Pattern p=Pattern.compile(pattern);
         Matcher m=p.matcher(source);
         return m.find();
    }

	 private boolean isContain(String source, String[] subItems){
         boolean result = false;
         for (String item:subItems) {
        	 String pattern = "\\b"+item+"\\b";
             Pattern p=Pattern.compile(pattern);
             Matcher m=p.matcher(source);
             result = result ||  m.find();
         }
         return result;
    }
	 
	 private boolean isStarts(String source, String[] subItems){
         Pattern p = Pattern.compile("^(" + String.join("|", this.startWords)+ ")\\b");
         Matcher m = p.matcher(source.trim());
         return m.find();
	 }
	 
	private String removeWords(String source, String[] words) {
		for (String ex:words) {
			 String pattern = "\\b"+ex+"\\b";
			source = source.replaceAll(pattern, "");
		}
		return source;
	}
	
	private String removeFirst(String source, String[] words) {
		for (String ex:words) {
			 String pattern = "\\b"+ex+"\\b";
		    	source = source.replaceFirst(pattern, "");
		}
		return source;
	}
}
