package klt.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import klt.Main;
import klt.data.ImageData;
import klt.data.JsonParser;
import klt.data.KeywordsCache;
import klt.web.ShutterProvider;

public class MainWindowController implements Initializable {

	public Main app;

	@FXML
	private Label leftStatusLabel;

	@FXML
	private Label rightStatusLabel;

	@FXML
	private FlowPane imagesContainer;
	
	@FXML
	private FlowPane tagsContainer;
	
	@FXML
	private FlowPane relatedKeywordsContainer;
	
	@FXML
	private Label allMatchesCountLabel;

	@FXML
	private ScrollPane sp;

	@FXML
	private TextArea salesKeysArea;
	
	@FXML
	private TextArea otherKeysArea;

	@FXML
	private TextField queryInput;

	@FXML
	private Button searchBtn;

	@FXML
	private Button selectAllBtn;

	@FXML
	private Button unSelectAllBtn;

	@FXML
	private ProgressBar loadBar;

	@FXML
	private VBox progressBarBox;
	
	@FXML
	private VBox rightBox;
	
	@FXML
	private RadioButton radioAll;
	
	@FXML
	private RadioButton radioPhotos;
	
	@FXML
	private RadioButton radioVectors;
	
	@FXML
	private RadioButton radioIllustration;
	
	@FXML
	private Label salesCountLabel;
	
	@FXML
	private Label otherCountLabel;
	
	ToggleGroup radioTypeGroup = new ToggleGroup();

	private List<SelectableBorderPane> images = new ArrayList<SelectableBorderPane>();
	private List<SelectableBorderPane> previousImages = new ArrayList<SelectableBorderPane>();
	private List<SelectableBorderPane> badImages = new ArrayList<SelectableBorderPane>();

	
	
	private ObservableList<SelectableBorderPane> selectedItems = FXCollections.observableArrayList();

	public ObservableList<SelectableBorderPane> getSelectedItems() {
		return selectedItems;
	}

	Task<Void> backgroundLoadTask;

	Task<Void> processingTask;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		SelectionHandler selectionHandler = new SelectionHandler(imagesContainer);
		imagesContainer.addEventHandler(MouseEvent.MOUSE_PRESSED, selectionHandler.getMousePressedEventHandler());
		imagesContainer.prefWidthProperty().bind(sp.widthProperty());
		imagesContainer.prefHeightProperty().bind(sp.heightProperty());

		queryInput.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				search();
			}
		});

	}

	public void setup() {
		loadBar.prefWidthProperty().bind(progressBarBox.widthProperty().subtract(20));
		
		radioAll.setToggleGroup(radioTypeGroup);
		radioPhotos.setToggleGroup(radioTypeGroup);
		radioVectors.setToggleGroup(radioTypeGroup);
		radioIllustration.setToggleGroup(radioTypeGroup);
		radioAll.setOnAction(value-> search());
		radioPhotos.setOnAction(value-> search());
		radioVectors.setOnAction(value-> search());
		radioIllustration.setOnAction(value-> search());
		//testTags();
	}

	private void createProcessingTask() {
		processingTask = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {
				System.out.println("Run Processing Task");
				rightStatusUpdate("Waiting for keywords data to be downloaded...");
				while (!selectedItems.stream().filter(SelectableBorderPane.class::isInstance)
						.map(result -> (SelectableBorderPane) result).allMatch(n -> !n.imageData.getAllKeywords().isEmpty())) {
					if (isCancelled()) {
						break;
					}
					Thread.sleep(200);
				}
				rightStatusUpdate("Keywords data downloaded. Processing...");
				
				///////PROCESSING
				List<String> salekwds = new ArrayList<String>();
				List<String> otherkwds = new ArrayList<String>();
				for (SelectableBorderPane p : selectedItems) {
					salekwds.addAll(p.imageData.getSaleKeywords());
					otherkwds.addAll(p.imageData.getOtherKeywords());
				}
				Map<String, Long> salesresult = salekwds.stream()
		                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
				
				Map<String, Long> salesresultSorted =  sortByValue(salesresult);
				
				Map<String, Long> otherresult = otherkwds.stream()
		                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
				
				Map<String, Long> otherresultSorted =  sortByValue(otherresult);
				
				List<String> resultSalesKeys = new ArrayList<String>();
				List<String> resultOtherKeys = new ArrayList<String>();
				
				for (Map.Entry<String, Long> entry : salesresultSorted.entrySet()) {
					resultSalesKeys.add(0,entry.getKey());
				}
				
				for (Map.Entry<String, Long> entry : otherresultSorted.entrySet()) {
					resultOtherKeys.add(0,entry.getKey());
				}
				
				Platform.runLater(new Runnable() {
					 public void run() {
					salesKeysArea.setText(String.join(", ", resultSalesKeys));
					otherKeysArea.setText(String.join(", ", resultOtherKeys));
					salesCountLabel.setText("Good sale keys: " + resultSalesKeys.size());
					otherCountLabel.setText("Other keys: " + resultOtherKeys.size());
				  }
				});
				/////////////////
				
				
				return null;
			}
		};
		
		processingTask.setOnCancelled((value->{
			System.out.println("Cancel Processing Task");
			rightStatusUpdate("Keywords result updated.");
		}));
		
		processingTask.setOnFailed((value->{
			System.out.println("Failed Processing Task");
			rightStatusUpdate("Processing task failure");
		}));
		
		processingTask.setOnSucceeded((value->{
			System.out.println("Completed Processing Task");
			rightStatusUpdate("Keywords result updated.");
		}));
	}

	  public <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
	        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
	        list.sort(Entry.comparingByValue());
	        Map<K, V> result = new LinkedHashMap<>();
	        for (Entry<K, V> entry : list) {
	            result.put(entry.getKey(), entry.getValue());
	        }

	        return result;
	    }
	
	private void createBackgroundLoadTask() {
		backgroundLoadTask = new Task<Void>() {
			@Override
			public Void call() {
				try {
				System.out.println("Run Background Task");
				loadBar.setVisible(true);
				
				for (int i = 0; i < images.size(); i++) {
					leftStatusUpdate("Task in progress... Loading data for image " + i + " from " + images.size());
					ImageData imageData = images.get(i).imageData;
					updateKeywords(imageData);
					if (imageData.getSaleKeywords().isEmpty()) {
					   badImages.add(images.get(i));
					}
					if (isCancelled()) {
						break;
					}
					updateProgress(i, images.size());
				}
				loadBar.setVisible(false);
			
				return null;
				}
				catch(Throwable e) {
					System.out.println(e.getMessage());
					return null;
				}
			}
		};
		loadBar.progressProperty().bind(backgroundLoadTask.progressProperty());
		
		backgroundLoadTask.setOnCancelled((value->{
			System.out.println("Cancelled Background Task");
			leftStatusUpdate("Background task cancelled");
		}));
		
		backgroundLoadTask.setOnFailed((value->{
			System.out.println("Failed Background Task");
			leftStatusUpdate("Background task failure");
		}));
		
		backgroundLoadTask.setOnSucceeded((value->{
			System.out.println("Completed Background Task");
			for (SelectableBorderPane pane:badImages) {
			    imagesContainer.getChildren().remove(pane);
			    images.remove(pane);
			}
			keywordsProcessing();
			leftStatusUpdate("Loading keywords data for all images done. Images: " + images.size() + ", zero sale excluded: " + badImages.size());
			
		}));
	}
	
	private void callTask(Task t) {
		if (t.isRunning())
			t.cancel(true);
		Thread thr = new Thread(t);
		thr.setDaemon(true);
		thr.start();
	}

	public void addImageThumbnail(ImageData imageData) {
		Platform.runLater(new Runnable() {
			public void run() {
		SelectableBorderPane pane = SelectableBorderPane.create(imageData);
		imagesContainer.getChildren().add(pane);
		images.add(pane);
			}});
	}

	
	private void updateKeywords(ImageData image) {
		if (image==null) {
			System.out.println("updateKeywords methods error: Image object is null");
			return;
		}
		else if (!image.getAllKeywords().isEmpty()) {
			System.out.println("updateKeywords methods message: imageData keywords is already present");
			return;
		}
		else if (KeywordsCache.get(image)) {
			System.out.println("updateKeywords methods message: found in cache");
		}
		else {
			Set<String> kwds = null;
			try {
				kwds = ShutterProvider.getKeywords(image.link);
			} catch (IOException e) {
				System.out.println("updateKeywords methods error: Can't get Keywords data. IOException when requesting data");
			}
			if (kwds==null) {
				System.out.println("updateKeywords methods error: Can't get Keywords data. NULL");
			}
			else {
				image.addKeywords(kwds);
				KeywordsCache.add(image);
			}
		}
	}
	
	

	@FXML
	private void search() {

		if (backgroundLoadTask != null && backgroundLoadTask.isRunning())
			backgroundLoadTask.cancel(true);

		cleanResults();

		String query = queryInput.getText();
		if (query != null && !query.trim().isEmpty()) {
			String result = null;
			List<ImageData> data = null;
			if (radioAll.isSelected())
				result = ShutterProvider.findImagesAll(query);
			else if (radioPhotos.isSelected())
				result = ShutterProvider.findImagesPhotos(query);
			else if (radioVectors.isSelected())
				result = ShutterProvider.findImagesVector(query);
			else if (radioIllustration.isSelected())
				result  = ShutterProvider.findImagesIllustration(query, 1);
			
			if (result==null) {
				app.showAlert("Request error");
				leftStatusUpdate("Request error");
				return;
			}
			
			else {
				if (radioIllustration.isSelected()) {
					data = JsonParser.parseImagesData(result);
					List<ImageData> illustrationsList = new ArrayList<ImageData>();
					illustrationsList.addAll(data.stream().filter(im->im.image_type.equals("illustration")).collect(Collectors.toList()));
					int page = 2;
					while (illustrationsList.size()<100 && page<6) {
						result  = ShutterProvider.findImagesIllustration(query, page);
						if (result==null)
							break;
						data = JsonParser.parseImagesData(result);
						illustrationsList.addAll(data.stream().filter(im->im.image_type.equals("illustration")).collect(Collectors.toList()));
						page++;
					}
					data = illustrationsList;
			    }
				else
					data = JsonParser.parseImagesData(result);
			}
			
			if (data==null) {
				app.showAlert("Parsing error");
				leftStatusUpdate("Parsing error");
				return;
			}
			
			updateAllMatchesCountLabel(JsonParser.getAllMatchesCount(result));
			addRelatedKeywords(JsonParser.getRelatedKeywords(result));
			
			for (ImageData imdata : data) {
				addImageThumbnail(imdata);
			}
			
			boolean needProcessing = false;
			for (SelectableBorderPane newimage : this.images) {
				SelectableBorderPane same = this.previousImages.stream()
						.filter(im -> im.imageData.id.equals(newimage.imageData.id)).findFirst().orElse(null);
				if (same != null) {
					needProcessing = true;
					newimage.imageData.addKeywords(same.imageData.getAllKeywords());
					selectedItems.add(newimage);
					newimage.notifySelection(true);
				}
			}
			if (needProcessing)
				keywordsProcessing();
			
			Platform.runLater(new Runnable() {
				public void run() {
			createBackgroundLoadTask();
			callTask(backgroundLoadTask);
				}});
		}
	}

	private void updateAllMatchesCountLabel(int count) {
		Platform.runLater(new Runnable() {
			public void run() {
				allMatchesCountLabel.setText("Total records: " + count);
			}
		});
	}
	
	private void addRelatedKeywords(List<String> keywords) {
		Platform.runLater(new Runnable() {
			public void run() {
				for (String key:keywords) {
					Button button = new Button(key);
					button.setStyle(
			                "-fx-background-radius: 20px; " +
			                        "-fx-min-width: 20px; " +
			                        "-fx-min-height: 5px; "+
			                        "-fx-background-color: #E3E6FF;" +
			                        " -fx-text-fill: black;" //+
			                );
					
					button.setOnAction(click -> {
						queryInput.setText(key);
						search();
						});
					
					relatedKeywordsContainer.getChildren().add(button);
				}
				
				//relatedKeywordsContainer
			}
		});
		
	}
	
	
	private void cleanResults() {
		Platform.runLater(new Runnable() {
			public void run() {
				allMatchesCountLabel.setText("");
				relatedKeywordsContainer.getChildren().clear();
				salesKeysArea.clear();
				otherKeysArea.clear();
				salesCountLabel.setText("");
				otherCountLabel.setText("");
				badImages.clear();
				imagesContainer.getChildren().clear();
				previousImages.clear();
				previousImages.addAll((Collection<? extends SelectableBorderPane>) selectedItems);
				images.clear();
				selectedItems.clear();
			}
		});
		
	}

	public boolean select(SelectableBorderPane n, boolean selected) {
		if (n.requestSelection(selected)) {
			if (selected) {
				selectedItems.add(n);
				ImageData imData = ((SelectableBorderPane) n).imageData;
				if (imData != null)
					updateKeywords(imData);
			} else {
				selectedItems.remove(n);
			}
			n.notifySelection(selected);
			keywordsProcessing();
			return true;
		} else {
			return false;
		}
	}

	@FXML
	private void unselectAll() {
		if (selectedItems.isEmpty())
			return;
		List<SelectableNode> unselectList = new ArrayList<>();
		unselectList.addAll(selectedItems);
		for (SelectableNode sN : unselectList) {
			selectedItems.remove(sN);
			sN.notifySelection(false);
		}
		keywordsProcessing();
	}

	@FXML
	private void selectAll() {
		if (selectedItems.containsAll(images))
			return;
		for (SelectableBorderPane sN : images) {
			selectedItems.add(sN);
			sN.notifySelection(true);
		}
		keywordsProcessing();
	}

	public void keywordsProcessing() {
		this.salesKeysArea.setText("...");
		this.otherKeysArea.setText("...");
		salesCountLabel.setText("");
		otherCountLabel.setText("");
		if (processingTask!=null && processingTask.isRunning())
			processingTask.cancel(true);
		createProcessingTask();
		callTask(processingTask);
	}

	private void leftStatusUpdate(String text) {

		Platform.runLater(new Runnable() {
			public void run() {
				leftStatusLabel.setText(text);
			}
		});
	}

	private void rightStatusUpdate(String text) {

		Platform.runLater(new Runnable() {
			public void run() {
				rightStatusLabel.setText(text);
			}
		});

	}

	
	private void testTags() {
		List<TagButton> buttons = new ArrayList<TagButton>();
		buttons.add(new TagButton("Bob"));
		buttons.add(new TagButton("Jim"));
		buttons.add(new TagButton("John"));
		buttons.add(new TagButton("VeryLongTagOnAButton"));
		//for (int i=0;i<300;i++) {
		//	buttons.add(new TagButton("VeryLongTagOnAButton"));
		//}
		for (TagButton b:buttons) {
			tagsContainer.getChildren().add(b);
		}
	}
}
