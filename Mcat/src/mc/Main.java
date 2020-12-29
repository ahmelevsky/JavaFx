package mc;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendAnimation;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

import web.Faceapp;
import web.GiphyWorker;
import web.WebWorker;

public class Main {

	static String sticker_mashenka_1 = "CAACAgIAAxkBAANeXsvoqjaxY4oEUwrSBDvl9IWkAAH2AAJnAAObYkcLbxRoU2YFzPcZBA";
	static String mashenka_token = "1275207313:AAEMzI57hyI4oTypozHRnmpvwqAv1ohhGm4";
	static String mashenka_test_token = "824326373:AAEBXjIIx9RfJMcxhSZgpDSn0bjdRDrZx2k";
	static Mashenka mashenka;	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public static void main(String[] args) throws IOException {
		Faceapp.test();
	}
	
	
	
	public static void main1(String[] args) throws InterruptedException, IOException {
		
		MLogger.setup();
		mashenka = new Mashenka(mashenka_token);
		
	//	TelegramBot bot = new TelegramBot(mashenka_token);

		// Register for updates

		/*
		 * bot.setUpdatesListener(updates -> { updates.stream().forEach(update -> { if
		 * (update.message().sticker()!=null)
		 * System.out.println(update.message().sticker().fileId()); if
		 * (update.message()!=null && update.message().text() != null ) {
		 * 
		 * if (update.message().text().toLowerCase().contains("машенька")) {
		 * 
		 * bot.execute(new SendMessage(update.message().chat().id(), "Чего?")); } if
		 * (update.message().text().toLowerCase().contains("как тебя зовут")) {
		 * bot.execute(new SendMessage(update.message().chat().id(), "Машенька")); } if
		 * (update.message().text().toLowerCase().contains("кот")) { bot.execute(new
		 * SendMessage(update.message().chat().id(), "Мау!")); } if
		 * (update.message().text().toLowerCase().contains("покажись")) { SendSticker
		 * sendSticker = new SendSticker(update.message().chat().id(),
		 * sticker_mashenka_1); bot.execute(sendSticker);
		 * 
		 * }
		 * 
		 * } });
		 * 
		 * // return id of last processed update or confirm them all return
		 * UpdatesListener.CONFIRMED_UPDATES_ALL; });
		 */
		
		
		int offset = 0;
		while (true) {
			try {
			GetUpdates getUpdates = new GetUpdates().limit(100).offset(offset).timeout(1000);
			GetUpdatesResponse updatesResponse = mashenka.getUpdates(getUpdates);
			List<Update> updates = updatesResponse.updates();
			if (updates==null || updates.size()==0) continue;
			Update update = updates.get(updates.size() - 1);
			offset = update.updateId()+1;
			mashenka.get(update);
			}
			catch (Throwable e) {
				LOGGER.severe(e.getMessage());
			}
			Thread.sleep(1000);
		}
	}
	
	
}
