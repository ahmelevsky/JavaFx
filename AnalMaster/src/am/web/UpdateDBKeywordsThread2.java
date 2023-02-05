package am.web;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import am.JsonParser;
import am.Main;
import am.ShutterImage;
import am.db.SQLManager;

public class UpdateDBKeywordsThread2 extends Thread {

	public static boolean isStarted = false;
	public boolean isStop  = false;
	private Main app;
    private ShutterProvider provider;
    private SQLManager sqlManager;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public UpdateDBKeywordsThread2(Main app, ShutterProvider provider, SQLManager sqlManager) {
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
			
				for (long id:ids) {
					if (isStop)
						break;
					String json =null;
					try {
						json = provider.getKeywordsTop(id);
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
						ShutterImage image = new ShutterImage(id);
						image = JsonParser.parseKeywordsTopAndPaste(json, image);
						sqlManager.insertKeywords(image.getMedia_id(), image.keywordsRate);
						LOGGER.fine("Obtained and inserted keywords for image " + id);
					}
				}
			
		app.mainController.getAllImages();
		app.mainController.enableControlsKeywords();
		}

}
