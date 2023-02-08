package am.web;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import am.JsonParser;
import am.Main;
import am.ShutterImage;
import am.db.SQLManager;

public class UpdateDBKeywordsThread extends Thread {

	public static boolean isStarted = false;
	public boolean isStop  = false;
	private Main app;
    private ShutterProvider provider;
    private SQLManager sqlManager;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public UpdateDBKeywordsThread(Main app, ShutterProvider provider, SQLManager sqlManager) {
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
		
		List<Long> ids = sqlManager.getTopImagesIds();
		app.mainController.log("Top Images in DB count: " + ids.size());
		LOGGER.info("Top Images in DB count: " + ids.size());
		int iter = 100;
		for (int i = 0; i < ids.size(); i += iter) {
			   List<Long> sub = ids.subList(i, Math.min(ids.size(),i+iter));
			   if (isStop)
					break;
			   String json =null;
				try {
					json = provider.getKeywordsTop(sub);
				}
					catch (IOException e) {
						app.mainController.log("Network request error");
						LOGGER.severe("Network request error:  " +  e.getMessage());
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e1) {
						}
					}
					
				if (json!=null) {
					List<ShutterImage> images = JsonParser.parseKeywordsTopAndPasteToMultiple(json);
					sqlManager.insertKeywordsBatch(images);
					LOGGER.fine("Obtained and inserted keywords for images count " + images.size());
				}
			}
			
		app.mainController.getAllImages();
		app.mainController.enableControlsKeywords();
		}

}
