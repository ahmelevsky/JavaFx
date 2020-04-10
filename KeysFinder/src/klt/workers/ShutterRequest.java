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
	//private int badCount;
	
	
	Task<ResponseData> searchTask = new Task<ResponseData>() {
		@Override
		public ResponseData call()  {
			status = WorkerStatus.PROGRESS;
			System.out.println("SEARCH TASK STARTED");
			responseData = new ResponseData();
			String result = null;
			String user = null;
			if (requestData.user!=null) {
				try {
					user = ShutterProvider.getUser(requestData.user);
					System.out.println(user);
				} catch (IOException e) {
					error = "No such UserId or UserName";
					showError();
					return responseData;
				}
			    if (user == null || user.isEmpty()) {
			    	error = "No such UserId or UserName";
					showError();
					return responseData;
			    }
			}
			List<ImageData> templist = new ArrayList<ImageData>();
			try {
				int page = 1;
				while (responseData.images.size() < requestData.requestCount) {
					templist.clear();
					if (((requestData.requestCount - responseData.images.size()) <100) && !requestData.type.equals(ImagesType.ILLUSTRATIONS))
						result = ShutterProvider.findImages(requestData.query, user, requestData.type, page, requestData.requestCount - responseData.images.size());
					else
						result = ShutterProvider.findImages(requestData.query, user, requestData.type, page, 100);
					templist = JsonParser.parseImagesData(result);
					if (templist.isEmpty())
						break;
					if (requestData.requestCount<=100)
						templist = cutList(templist, requestData.requestCount);
					else if ((requestData.requestCount - responseData.images.size()) <=100)
						templist = cutList(templist, requestData.requestCount - responseData.images.size());
					if (requestData.type.equals(ImagesType.ILLUSTRATIONS))  {
						responseData.images.addAll(templist.stream().filter(im -> im.image_type.equals("illustration"))
							.collect(Collectors.toList()));
						if (templist.stream().filter(im -> im.image_type.equals("illustration")).count()==0)
								break;
					}
					else 
						responseData.images.addAll(templist);
					page++;
					
				    //badCount =+ (int)responseData.images.stream().filter(im -> im.getSaleKeywords().isEmpty()).count();
					//responseData.images.removeIf(im -> im.getSaleKeywords().isEmpty());
				}
				
			if (requestData.type.equals(ImagesType.ILLUSTRATIONS))  {
				String resVectors = ShutterProvider.findImages(requestData.query, user, ImagesType.VECTORS, 1);
				responseData.matchesCount = JsonParser.getAllMatchesCount(result) -  JsonParser.getAllMatchesCount(resVectors);
			}
			else
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
		
		request.searchTask.setOnRunning(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	request.ui.setSearchIndicator(true);
			}});
		
		
		request.searchTask.setOnCancelled(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	request.status = WorkerStatus.CANCELLED;
		    	System.out.println("Search task cancelled");
		    	request.ui.setSearchIndicator(false);
			}});
		
		request.searchTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	request.ui.setSearchIndicator(false);
		    	request.status = WorkerStatus.FAILED;
		    	System.out.println("Search task faled " + request.error);
		    	ui.app.showAlert("Error: search request failed: " + request.error);
			}});
		
		request.searchTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	request.ui.setSearchIndicator(false);
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
		//this.ui.leftStatusUpdate("Done. Images loaded: " + this.responseData.images.size() + ", zero sale excluded: " + this.badCount);
	}
	
	private void showError() {
		this.ui.app.showAlert(this.error);
		this.ui.leftStatusUpdate(this.error);
	}
	
	private List<ImageData> cutList(List<ImageData> list, int cutTo) {
		if (cutTo<list.size())
			return list.subList(0, cutTo);
		else 
			return list;
	}
	
}
