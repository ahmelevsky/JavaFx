package mc;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

public class Main {

	static String sticker_mashenka_1 = "CAACAgIAAxkBAANeXsvoqjaxY4oEUwrSBDvl9IWkAAH2AAJnAAObYkcLbxRoU2YFzPcZBA";
	static String mashenka_token = "1275207313:AAEMzI57hyI4oTypozHRnmpvwqAv1ohhGm4";
	static String mashenka_test_token = "824326373:AAEBXjIIx9RfJMcxhSZgpDSn0bjdRDrZx2k";
			
	public static void main(String[] args) throws InterruptedException {
		TelegramBot bot = new TelegramBot(mashenka_token);

		// Register for updates

		/*
		 * bot.setUpdatesListener(updates -> { updates.stream().forEach(update -> { if
		 * (update.message().sticker()!=null)
		 * System.out.println(update.message().sticker().fileId()); if
		 * (update.message()!=null && update.message().text() != null ) {
		 * 
		 * if (update.message().text().toLowerCase().contains("машенька")) {
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
			GetUpdates getUpdates = new GetUpdates().limit(100).offset(offset).timeout(1000);
			GetUpdatesResponse updatesResponse = bot.execute(getUpdates);
			List<Update> updates = updatesResponse.updates();
			if (updates.size()==0) continue;
			Update update = updates.get(updates.size() - 1);
			offset = update.updateId()+1;
			if (update.message().sticker() != null)
				System.out.println(update.message().sticker().fileId());
			if (update.message() != null && update.message().text() != null) {

				if (isContain(update.message().text().toLowerCase(),"машенька")) {
					bot.execute(new SendMessage(update.message().chat().id(), "Чего?"));
				}
				if (isContain(update.message().text().toLowerCase(),"как тебя зовут"))  {
					bot.execute(new SendMessage(update.message().chat().id(), "Машенька"));
				}
				if (isContain(update.message().text().toLowerCase(),"кот")){
					bot.execute(new SendMessage(update.message().chat().id(), "Мау!"));
				}
				if (isContain(update.message().text().toLowerCase(),"покажись")){
					SendSticker sendSticker = new SendSticker(update.message().chat().id(), sticker_mashenka_1);
					bot.execute(sendSticker);

				}
				
				if (isContain(update.message().text().toLowerCase(),"пришли котика")){
					SendSticker sendSticker = new SendSticker(update.message().chat().id(), sticker_mashenka_1);
					bot.execute(sendSticker);

				}
				
				if (update.message().text().toLowerCase().contains("кнопки")) {
					Keyboard keyboard = new ReplyKeyboardMarkup(
					        new KeyboardButton[]{
					                new KeyboardButton("text"),
					                new KeyboardButton("contact").requestContact(true),
					                new KeyboardButton("location").requestLocation(true)
					        }
					);
					
				}
			}

			Thread.sleep(1000);
		}
	}

	 private static boolean isContain(String source, String subItem){
         String pattern = "\\b"+subItem+"\\b";
         Pattern p=Pattern.compile(pattern);
         Matcher m=p.matcher(source);
         return m.find();
    }

	
}
