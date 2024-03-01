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
import klt.data.JsonParserNewAPI;
import klt.ui.MainWindowController;
import klt.web.ShutterProvider;
import klt.web.ShutterProviderNewApi;

public class ShutterRequest {
	//private static ShutterRequest request;
	
	public  WorkerStatus status;
	private RequestData requestData;
	private ResponseData responseData;
	private MainWindowController ui; 
	private String error;
	//private int badCount;
	
	private boolean isNewApi;
	private String newApiString = "";
	Task<ResponseData> searchTask;
	
	
	private void createSearchTask() {
	ShutterProvider providerTemp = null;
	JsonParser parserTemp = null;
	if (this.isNewApi) {
		providerTemp = new ShutterProviderNewApi(this.newApiString);
		parserTemp = new JsonParserNewAPI();
	}
	else { 
		providerTemp = new ShutterProvider(this.newApiString);
		parserTemp = new JsonParser();
	}
	final ShutterProvider provider = providerTemp;
	final JsonParser parser = parserTemp;
	
	searchTask = new Task<ResponseData>() {
		@Override
		public ResponseData call()  {
			status = WorkerStatus.PROGRESS;
			System.out.println("SEARCH TASK STARTED");
			responseData = new ResponseData();
			String result = null;
			String user = null;
			/*
			if (requestData.user!=null) {
				try {
					user = provider.getUser(requestData.user);
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
			*/
			user = requestData.user;
			
			
			List<ImageData> templist = new ArrayList<ImageData>();
			try {
				int page = 1;
				while (responseData.images.size() < requestData.requestCount) {
					templist.clear();
					if (((requestData.requestCount - responseData.images.size()) <100) && !requestData.type.equals(ImagesType.ILLUSTRATIONS))
						result = provider.findImages(requestData.query, user, requestData.type, page, requestData.requestCount - responseData.images.size());
					else
						result = provider.findImages(requestData.query, user, requestData.type, page, 100);
					templist = parser.parseImagesData(result);
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
				String resVectors = provider.findImages(requestData.query, user, ImagesType.VECTORS, 1, 100);
				responseData.matchesCount = parser.getAllMatchesCount(result) -  parser.getAllMatchesCount(resVectors);
			}
			else
				responseData.matchesCount = parser.getAllMatchesCount(result);
			
			responseData.relatedKeywords = parser.getRelatedKeywords(result);
			
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
	}
	
	public void execute() {
		
		createSearchTask();
		
		this.searchTask.setOnRunning(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	ui.setSearchIndicator(true);
			}});
		
		
		this.searchTask.setOnCancelled(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	status = WorkerStatus.CANCELLED;
		    	System.out.println("Search task cancelled");
		    	ui.setSearchIndicator(false);
			}});
		
		this.searchTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	ui.setSearchIndicator(false);
		    	status = WorkerStatus.FAILED;
		    	System.out.println("Search task faled " + error);
		    	ui.app.showAlert("Error: search request failed: " + error);
			}});
		
		this.searchTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	ui.setSearchIndicator(false);
		    	status = WorkerStatus.DONE;
		    	System.out.println("Search task succeed");
		    	responseData = searchTask.getValue();
		    	if (responseData!=null) {
		    		publish();
		    	}
		    	else {
		    		status = WorkerStatus.FAILED;
		    		showError();
		    	}
		    }
		});
		
		this.exec();
	}
		
	public ShutterRequest(RequestData requestData, MainWindowController ui, boolean isNewApi, String newApiString) {
		this.requestData = requestData;
		this.ui = ui;
		this.status = WorkerStatus.READY;
		this.isNewApi = isNewApi;
		if (!newApiString.isEmpty())
			this.newApiString = newApiString;
	}
	
	private void exec() {
		Thread thr = new Thread(this.searchTask);
		thr.setDaemon(true);
		thr.start();
	}
	
	public void cancel() {
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
