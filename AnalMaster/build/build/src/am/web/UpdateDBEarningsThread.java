package am.web;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONException;

import am.Main;
import am.ShutterImage;
import am.db.SQLManager;
import am.Earning;
import am.JsonParser;

public class UpdateDBEarningsThread extends Thread {

	public static boolean isStarted = false;
	public boolean isStop  = false;
	private Main app;
    private ShutterProvider provider;
    private SQLManager sqlManager;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public UpdateDBEarningsThread(Main app, ShutterProvider provider, SQLManager sqlManager) {
		super();
		this.setDaemon(false);
		this.app = app;
		this.provider = provider;
		this.sqlManager = sqlManager;
	}

	@Override
	public void run() {
		
		String startDate = this.sqlManager.getLastEarningsUpdateDate();
		if (startDate==null)
			startDate = this.sqlManager.getFirstUploadDate();
		else
			this.sqlManager.deleteEarningsForDate(startDate);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.parse(startDate, formatter);
		
		try {
			
			while (date.isBefore(LocalDate.now())) {
			   if (isInterrupted() || isStop)
				   break;
			    
			    String textDate = date.format(formatter);
			    LOGGER.fine("Start getting earnings for day " + textDate);
			    app.mainController.logGreen("Searching earnings for " + textDate);
				List<Earning> dayEarnings = provider.getDayEarnings(textDate);
				
				for (Earning earn:dayEarnings){
					System.out.println(earn);
				}
				
				if (!dayEarnings.isEmpty()) {
					LOGGER.fine("Start adding to DB");
					this.sqlManager.insertNewEarnings(dayEarnings);
					LOGGER.fine("Added earnings to DB " + dayEarnings.size());
					app.mainController.log(" Added earnings to DB " + dayEarnings.size());
				}
				
				date = date.plusDays(1);
				
			}
		
		}
		catch (IOException e) {
			app.mainController.log("Network request error");
			LOGGER.severe("Network request error:  " +  e.getMessage());
			if (e.getMessage().contains("Incorrect nextc.sid")) {
				app.mainController.enableControlsEarnings();
				app.showAlert(e.getMessage());
				return;
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
			}
		}
		
		app.mainController.enableControlsEarnings();
		
		}
}
