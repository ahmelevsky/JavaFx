package am.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONException;

import am.Main;
import am.ShutterImage;
import am.db.SQLManager;
import am.JsonParser;

public class UpdateDBThread2 extends Thread {

	public static boolean isStarted = false;
	public boolean isStop  = false;
	private Main app;
    private ShutterProvider provider;
    private SQLManager sqlManager;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public UpdateDBThread2(Main app, ShutterProvider provider, SQLManager sqlManager) {
		super();
		this.setDaemon(false);
		this.app = app;
		this.provider = provider;
		this.sqlManager = sqlManager;
	}

	@Override
	public void run() {
		if (provider==null || !provider.isConnection()) {
			app.showAlert("Shutter provider is not ready");
			return;
		}
		
		long total = 0;
		int per_page = 10;
		int page_number = 1;
		String imagesString = null;
		try {
		while(!isInterrupted() && !isStop){
			try {
			imagesString = provider.getImages(per_page, page_number);
			if (imagesString == null) {
				app.showAlert("Ошибка соединения с сервером");
				return;
				}
			LOGGER.fine("Start getting images from SS page number " + page_number);
			if (total==0) {
				total = JsonParser.getTotalImagesCount(imagesString);
				System.out.println("Total images: " + total);
				app.mainController.log("Total images: " + total);
			}
			
			List<ShutterImage> listFromShutter = JsonParser.parseImagesData(imagesString);
			System.out.println("Obtaining new images from ShutterStock: " + listFromShutter.size());
			
			LOGGER.fine("Parsed images");
			
			if (listFromShutter.isEmpty()) { 
				LOGGER.fine("No more images on ShutterStock");
				break;
			}
			
			List<ShutterImage> toRemove = new ArrayList<ShutterImage>();
			for (ShutterImage im:listFromShutter) {
				if(sqlManager.isInDB(im.getMedia_id())) 
					toRemove.add(im);
				else {
				    String jsonString = provider.getImageDetails(im.getMedia_id());
				    im = JsonParser.parseImageDataAndPaste(jsonString, im);
				    LOGGER.fine("Start downloading preview");
				    //im.setPreviewBytes(this.provider.downloadByteArray(im.getPreviewPath()));
				    im.setPreviewBytes(this.provider.downloadImage(im.getPreviewPath()));
				    LOGGER.fine("End downloading preview");
				}				
			}
			listFromShutter.removeAll(toRemove);
			
			LOGGER.fine("Existed images: " + toRemove.size());
			
			
			if (!listFromShutter.isEmpty()) {
				LOGGER.fine("Start adding to DB");
				sqlManager.insertImages(listFromShutter);
				LOGGER.fine("Added images to DB " + listFromShutter.size());
			}
			else {
			 // на случай если мы хотим дойти до старых только и все
				//	break;
				
			}
			
			
			page_number++;
			
			}
			catch (IOException e) {
				app.mainController.log("Network request error");
				LOGGER.severe("Network request error:  " +  e.getMessage());
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
				}
			}
			
        }
		
		app.mainController.getAllImages();
		app.mainController.enableControls();
		
		}
		catch (JSONException e) {
				int endIndex = imagesString.length()>300 ? 300 : imagesString.length();
				app.showAlert("Некорректные данные:\n" + imagesString.substring(0, endIndex)); 
		}
	}

}
