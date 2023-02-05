package am.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.json.JSONException;

import am.JsonParser;
import am.Main;
import am.ShutterImage;
import am.db.SQLManager;

public class UpdateDBTopThread extends Thread {

	public static boolean isStarted = false;
	public boolean isStop  = false;
	private Main app;
    private ShutterProvider provider;
    private SQLManager sqlManager;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public UpdateDBTopThread(Main app, ShutterProvider provider, SQLManager sqlManager) {
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
			app.mainController.enableControls();
			return;
		}
		LOGGER.fine("Start update TOP Performers ");
		int per_page = 200;
		int page_number = 1;
		int correctionsteps = 3;
		
		try {
			while(!isInterrupted() && !isStop){
				try {

					LOGGER.info("Obtaining  " + page_number + " with per page: " + per_page);
					List<ShutterImage> images = provider.getTopPerformers(per_page, page_number);
					
					if (images == null) {
						LOGGER.info("No n ew images found. Page Number " + page_number + ", and per page " + per_page);
						app.mainController.getAllImages();
						app.mainController.enableControls();
						break;
					}
					
					
					LOGGER.info("Obtained images " + images.size());
					
					
					/*
					LOGGER.info("Start downloading and insert to DB keywords");
					
					for (ShutterImage image:images) {
						String json = provider.getKeywordsTop(image.getMedia_id());
						image = JsonParser.parseKeywordsTopAndPaste(json, image);
						sqlManager.insertKeywords(image.getMedia_id(), image.keywordsRate);
					}
					LOGGER.info("Complete inserting keywords");
					
					*/
					
					LOGGER.info("Inserting images TOP data");
					int errors = 0;
					for (ShutterImage image:images) {
						errors = errors - sqlManager.insertImageTop(image);
					}
					if (errors>0 && correctionsteps==3) {
						page_number=page_number-2;
						if (page_number<1)
							page_number=1;
						correctionsteps--;
						LOGGER.info("Done with images set with sql errors " + errors);
						LOGGER.info("correctionsteps=" + correctionsteps);
					}
					else {
						if (correctionsteps<3 && correctionsteps>0) 
							correctionsteps--;
						else if (correctionsteps==0)
							correctionsteps=3;
						LOGGER.info("Done with images set successfully " + (images.size() - errors));
						LOGGER.info("correctionsteps=" + correctionsteps);
						page_number++;
					}
				}
				catch (IOException e) {
					app.showAlert("Network request error");
					LOGGER.severe("Network request error:  " +  e.getMessage());
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e1) {
					}
				}
			}
			
			//app.mainController.getAllImages();
			app.mainController.enableControlsTop();
		}
		catch (JSONException e) {
			app.showAlert("JSON parse exception");
			LOGGER.severe("JSON parse exception:  " +  e.getMessage());
			app.mainController.getAllImages();
			app.mainController.enableControlsTop();
		}
	}

}
