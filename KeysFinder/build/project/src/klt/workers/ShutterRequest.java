package klt.workers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONException;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import klt.data.ImageData;
import klt.data.JsonParser;
import klt.ui.MainWindowController;
import klt.web.ShutterProvider;

public class ShutterRequest {
	private static ShutterRequest request;
	
	public  WorkerStatus status;
	private RequestData requestData;
	private ResponseData responseData;
	private MainWindowController ui; 
	private String error;
	Task<ResponseData> searchTask = new Task<ResponseData>() {
		@Override
		public ResponseData call()  {
			status = WorkerStatus.PROGRESS;
			System.out.println("SEARCH TASK STARTED");
			responseData = new ResponseData();
			String result = null;
			try {
			switch(requestData.type) {
			case ALL:
				result = ShutterProvider.findImagesAll(requestData.query);
				responseData.images = JsonParser.parseImagesData(result);
				break;
			case PHOTOS:
				result = ShutterProvider.findImagesPhotos(requestData.query);
				responseData.images = JsonParser.parseImagesData(result);
				break;
			case VECTORS:
				result = ShutterProvider.findImagesVector(requestData.query);
				responseData.images = JsonParser.parseImagesData(result);
				break;
			case ILLUSTRATIONS:
				int page = 1;
				responseData.images = new ArrayList<ImageData>();
				while (responseData.images.size() < 100 && page < 6) {
					result = ShutterProvider.findImagesIllustration(requestData.query, page);
					List<ImageData> notFilteredImagesList = JsonParser.parseImagesData(result);
					responseData.images.addAll(notFilteredImagesList.stream().filter(im -> im.image_type.equals("illustration"))
							.collect(Collectors.toList()));
					page++;
				}
				break;
			}
			responseData.matchesCount = JsonParser.getAllMatchesCount(result);
			responseData.relatedKeywords = JsonParser.getRelatedKeywords(result);
			return responseData;
			}
			catch (IOException e1) {
				responseData = null;
				error = e1.getMessage();
				return responseData;
			}
			catch (JSONException e2) {
				responseData = null;
				error = e2.getMessage();
				return responseData;
			}
		}
	};
	
	
	
	public static void execute(RequestData requestData, MainWindowController ui) {
		
		if (request!=null )
			request.cancel();
		
		request = new ShutterRequest(requestData);
		request.ui = ui;
		
		request.searchTask.setOnCancelled(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	request.status = WorkerStatus.CANCELLED;
		    	System.out.println("Search task cancelled");
			}});
		
		request.searchTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	request.status = WorkerStatus.FAILED;
		    	System.out.println("Search task faled");
			}});
		
		request.searchTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	request.status = WorkerStatus.DONE;
		    	System.out.println("Search task succeed");
		    	request.responseData = request.searchTask.getValue();
		    	if (request.responseData!=null) {
		    		request.publish();
		    	}
		    	else {
		    		request.status = WorkerStatus.FAILED;
		    		request.showError();
		    	}
		    }
		});
		
		request.exec();
	}
		
	private ShutterRequest(RequestData requestData) {
		this.requestData = requestData;
		this.status = WorkerStatus.READY;
	}
	
	private void exec() {
		Thread thr = new Thread(this.searchTask);
		thr.setDaemon(true);
		thr.start();
	}
	
	private void cancel() {
		if (this.searchTask.isRunning())
			this.searchTask.cancel(true);
	}
	
	private void publish() {
		this.ui.updateAllMatchesCountLabel(this.responseData.matchesCount);
		this.ui.addRelatedKeywords(this.responseData.relatedKeywords);
		this.ui.addImageThumbnails(this.responseData.images);
		this.ui.selectPreviouslySelected();
	}
	
	private void showError() {
		this.ui.app.showAlert(this.error);
		this.ui.leftStatusUpdate(this.error);
	}
	
}
