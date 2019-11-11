package klt.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import klt.Main;
import klt.data.ImageData;
import klt.data.KeywordsCache;
import klt.web.ShutterProvider;
import klt.workers.ImagesType;
import klt.workers.RequestData;
import klt.workers.ShutterRequest;

public class MainWindowController implements Initializable {

	public Main app;

	@FXML
	private Label leftStatusLabel;

	@FXML
	private Label rightStatusLabel;

	@FXML
	private FlowPane imagesContainer;

	@FXML
	private FlowPane relatedKeywordsContainer;

	@FXML
	private Label allMatchesCountLabel;
	
	@FXML
	private Label selectedImagesCountLabel;

	@FXML
	private ScrollPane sp;

	/*
	 * @FXML private TextArea salesKeysArea;
	 */

	@FXML
	private TextArea otherKeysArea;

	@FXML
	private FlowPane keysPane;

	@FXML
	private Button selectAllKeysBtn;

	@FXML
	private Button unselectAllKeysBtn;

	@FXML
	private Button reverseKeysSelectionBtn;

	@FXML
	private Button copyKeysBtn;

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
	
	@FXML
	private Spinner<Integer> requestCountSpinner;
	
	
	@FXML
	private ProgressIndicator searchIndicator;

	ToggleGroup radioTypeGroup = new ToggleGroup();
	
    private int shiftTagPosition;
	

	private List<SelectableBorderPane> images = new ArrayList<SelectableBorderPane>();
	private List<SelectableBorderPane> previousImages = new ArrayList<SelectableBorderPane>();
	private List<SelectableBorderPane> badImages = new ArrayList<SelectableBorderPane>();

	private ObservableList<SelectableBorderPane> selectedItems = FXCollections.observableArrayList();

	public ObservableList<SelectableBorderPane> getSelectedItems() {
		return selectedItems;
	}
	
	public List<TagButton> keysButtons = new ArrayList<TagButton>();

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
		radioAll.setOnAction(value -> search());
		radioPhotos.setOnAction(value -> search());
		radioVectors.setOnAction(value -> search());
		radioIllustration.setOnAction(value -> search());
		// testTags();
		selectedItems.addListener((ListChangeListener<SelectableBorderPane>) c -> {
			selectedImagesCountLabel.setText("Selected images: " + selectedItems.size());
		});
		 SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = //
	                new SpinnerValueFactory.IntegerSpinnerValueFactory(50, 1000, 100);
		 valueFactory.amountToStepByProperty().set(50);
	     requestCountSpinner.setValueFactory(valueFactory);
	     searchIndicator.setVisible(false);
	}

	private void createProcessingTask() {
		processingTask = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {
				System.out.println("Run Processing Task");
				rightStatusUpdate("Waiting for keywords data to be downloaded...");
				
				Platform.runLater(new Runnable() {
					public void run() {
						keysPane.getChildren().clear();
						keysButtons.clear();
					}});
				
				while (!selectedItems.stream().filter(SelectableBorderPane.class::isInstance)
						.map(result -> (SelectableBorderPane) result)
						.allMatch(n -> !n.imageData.getAllKeywords().isEmpty())) {
					if (isCancelled()) {
						break;
					}
					Thread.sleep(200);
				}
				rightStatusUpdate("Keywords data downloaded. Processing...");

				/////// PROCESSING
				List<String> salekwds = new ArrayList<String>();
				List<String> otherkwds = new ArrayList<String>();
				for (SelectableBorderPane p : selectedItems) {
					salekwds.addAll(p.imageData.getSaleKeywords());
					otherkwds.addAll(p.imageData.getOtherKeywords());
				}
				collectKeywords(salekwds, otherkwds);
				/////////////////
				return null;
			}
		};

		processingTask.setOnCancelled((value -> {
			System.out.println("Cancel Processing Task");
			rightStatusUpdate("Keywords result updated.");
		}));

		processingTask.setOnFailed((value -> {
			System.out.println("Failed Processing Task");
			rightStatusUpdate("Processing task failure");
		}));

		processingTask.setOnSucceeded((value -> {
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

	private void collectKeywords(List<String> salekwds, List<String> otherkwds) {
		Platform.runLater(new Runnable() {
			public void run() {

				List<TagButton> tempList = new ArrayList<TagButton>();
				tempList.addAll(keysButtons);
				
				Map<String, Long> salesresult = salekwds.stream()
						.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

				Map<String, Long> salesresultSorted = sortByValue(salesresult);

				Map<String, Long> otherresult = otherkwds.stream()
						.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

				Map<String, Long> otherresultSorted = sortByValue(otherresult);

				List<String> resultSalesKeys = new ArrayList<String>();
				List<String> resultOtherKeys = new ArrayList<String>();

				for (Map.Entry<String, Long> entry : salesresultSorted.entrySet()) {
					resultSalesKeys.add(0, entry.getKey());
				}

				for (Map.Entry<String, Long> entry : otherresultSorted.entrySet()) {
					resultOtherKeys.add(0, entry.getKey());
				}
				
				boolean isSeparator = false;
				boolean isOnlyOne = true;
				int position = 1;
				for (String key:resultSalesKeys) {
					 long allCount = 0;
					 if (otherresultSorted.get(key)!=null)
						 allCount = salesresultSorted.get(key) + otherresultSorted.get(key);
					 else 
						 allCount = salesresultSorted.get(key);
					if (salesresultSorted.get(key)>1) isOnlyOne=false;
					if(salesresultSorted.get(key)==1 && !isSeparator && !isOnlyOne) {
						isSeparator = true;
						  Rectangle rect = new ResizableRectangle(keysPane.getWidth(),5);
					        rect.setFill(Color.TRANSPARENT);
					        rect.widthProperty().bind(keysPane.widthProperty());
						keysPane.getChildren().add(rect);
					}
					final TagButton  button = new TagButton(key, salesresultSorted.get(key),allCount);
					keysButtons.add(button);
					keysPane.getChildren().add(button);
					button.position = position;
					 TagButton old= tempList.stream().filter(t->t.key.equals(key)).findFirst().orElse(null);
						if (old!=null)
							button.setActive(old.isActive);
					    
						button.setOnMouseClicked(new EventHandler<MouseEvent>(){
							@Override
							public void handle(MouseEvent event){ 
							    if(event.getButton().equals(MouseButton.PRIMARY)){
							    	
							    	 if (event.isShiftDown() && shiftTagPosition>0) {
										 TagButton previousButton = keysButtons.stream().filter(b->b.position == shiftTagPosition).findFirst().orElse(null);
										 if (previousButton !=null)
										 keysButtons.forEach(tag -> {
											 if ((shiftTagPosition<button.position && tag.position>shiftTagPosition && tag.position<=button.position)
													  || (shiftTagPosition>button.position && tag.position<shiftTagPosition && tag.position>=button.position)) {
													 tag.setActive(previousButton.isActive);
											 }
									 });
									 }
							    	 else {
							    		 if(!button.isActive)
							    			 button.setActive(true);
							    		 else
							    			 button.setActive(false);
							    	 }
									 recoundSalesCount();
									 shiftTagPosition = button.position;
									}
							 }
						   });
					    
					position++;
				}
				recoundSalesCount();
				resultOtherKeys.removeAll(resultSalesKeys);
				Collections.sort(resultOtherKeys);
				otherKeysArea.setText(String.join(", ", resultOtherKeys));
				otherCountLabel.setText("Other keys: " + resultOtherKeys.size());
			}
		});
	}
	
	
	public void recoundSalesCount() {
		Platform.runLater(new Runnable() {
			public void run() {
				salesCountLabel.setText("Keys: " + keysButtons.stream().filter(k->k.isActive).count());
			}});
	}
	
	@FXML
	private void selectAllKeywords() {
		for (TagButton button:keysButtons)
			button.setActive(true);
		recoundSalesCount();
	}

	@FXML
	private void unselectAllKeywords() {
		for (TagButton button:keysButtons)
			button.setActive(false);
		recoundSalesCount();
	}
	
	@FXML
	private void reversKeywords() {
		for (TagButton button:keysButtons)
			if (button.isActive)
				button.setActive(false);
			else
				button.setActive(true);
		recoundSalesCount();
	}
	
	@FXML
	private void copyKeywords() {
		List<String> keys = new ArrayList<String>();
		for (TagButton button:keysButtons)
			if (button.isActive)
				keys.add(button.key);
		 final Clipboard clipboard = Clipboard.getSystemClipboard();
	     final ClipboardContent content = new ClipboardContent();
	     content.putString(String.join(", ", keys));
         clipboard.setContent(content);
	}
	
	public Task<Void> createBackgroundLoadTask() {
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
				} catch (Throwable e) {
					System.out.println(e.getMessage());
					return null;
				}
			}
		};
		loadBar.progressProperty().bind(backgroundLoadTask.progressProperty());

		backgroundLoadTask.setOnCancelled((value -> {
			System.out.println("Cancelled Background Task");
			leftStatusUpdate("Background task cancelled");
		}));

		backgroundLoadTask.setOnFailed((value -> {
			System.out.println("Failed Background Task");
			leftStatusUpdate("Background task failure");
		}));

		backgroundLoadTask.setOnSucceeded((value -> {
			System.out.println("Completed Background Task");
			for (SelectableBorderPane pane : badImages) {
				imagesContainer.getChildren().remove(pane);
				images.remove(pane);
			}
			keywordsProcessing();
			leftStatusUpdate("Loading keywords data for all images done. Images: " + images.size()
					+ ", zero sale excluded: " + badImages.size());

		}));
		return backgroundLoadTask;
	}

	public void callTask(Task t) {
		if (t.isRunning())
			t.cancel(true);
		Thread thr = new Thread(t);
		thr.setDaemon(true);
		thr.start();
	}

	public void addImageThumbnails(List<ImageData> data) {
		Platform.runLater(new Runnable() {
			public void run() {
				
				for (ImageData imdata : data) {
					SelectableBorderPane pane = SelectableBorderPane.create(imdata);
					imagesContainer.getChildren().add(pane);
					images.add(pane);
				}
				createBackgroundLoadTask();
				callTask(backgroundLoadTask);
			}
		});
	}
	
	public void selectPreviouslySelected() {
		Platform.runLater(new Runnable() {
			public void run() {
				
				boolean needProcessing = false;
				for (SelectableBorderPane newimage : images) {
					SelectableBorderPane same = previousImages.stream()
							.filter(im -> im.imageData.id.equals(newimage.imageData.id)).findFirst().orElse(null);
					if (same != null) {
						needProcessing = true;
						newimage.imageData.addKeywords(same.imageData.getAllKeywords());
						if (!selectedItems.contains(newimage))
							selectedItems.add(newimage);
						newimage.notifySelection(true);
					}
				}
				if (needProcessing)
					keywordsProcessing();				
			
			}
		});
	}
	
	private void updateKeywords(ImageData image) {
		if (image == null) {
			System.out.println("updateKeywords methods error: Image object is null");
			return;
		} else if (!image.getAllKeywords().isEmpty()) {
			System.out.println("updateKeywords methods message: imageData keywords is already present");
			return;
		} else if (KeywordsCache.get(image)) {
			System.out.println("updateKeywords methods message: found in cache");
		} else {
			Set<String> kwds = null;
			try {
				kwds = ShutterProvider.getKeywords(image.link);
			} catch (IOException e) {
				System.out.println(
						"updateKeywords methods error: Can't get Keywords data. IOException when requesting data");
			}
			if (kwds == null) {
				System.out.println("updateKeywords methods error: Can't get Keywords data. NULL");
			} else {
				image.addKeywords(kwds);
				KeywordsCache.add(image);
			}
		}
	}

	@FXML
	private void search() {
		RequestData requestData = new RequestData();
		requestData.query = queryInput.getText();
	    requestData.requestCount = requestCountSpinner.getValue();
		if (requestData.query.trim().isEmpty())
			return;
		if (radioAll.isSelected())
			requestData.type = ImagesType.ALL;
		else if (radioPhotos.isSelected())
			requestData.type = ImagesType.PHOTOS;
		else if (radioVectors.isSelected())
			requestData.type = ImagesType.VECTORS;
		else if (radioIllustration.isSelected())
			requestData.type = ImagesType.ILLUSTRATIONS;
			
		if (backgroundLoadTask != null && backgroundLoadTask.isRunning())
			backgroundLoadTask.cancel(true);

		cleanResults();
		ShutterRequest.execute(requestData, this);
	}

	public void updateAllMatchesCountLabel(int count) {
		Platform.runLater(new Runnable() {
			public void run() {
				allMatchesCountLabel.setText("Total images: " + count);
			}
		});
	}

	public void addRelatedKeywords(List<String> keywords) {
		Platform.runLater(new Runnable() {
			public void run() {
				for (String key : keywords) {
					Button button = new Button(key);
					button.setStyle("-fx-background-radius: 20px; " + "-fx-min-width: 20px; " + "-fx-min-height: 5px; "
							+ "-fx-background-color: #E3E6FF;" + " -fx-text-fill: black;" // +
					);

					button.setOnAction(click -> {
						queryInput.setText(key);
						search();
					});

					relatedKeywordsContainer.getChildren().add(button);
				}
			}
		});

	}

	private void cleanResults() {
				keysPane.getChildren().clear();
				keysButtons.clear();
				allMatchesCountLabel.setText("");
				relatedKeywordsContainer.getChildren().clear();
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

	public boolean select(SelectableBorderPane n, boolean selected) {
		if (n.requestSelection(selected)) {
			if (selected) {
				if (!selectedItems.contains(n))
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
			if (!selectedItems.contains(sN))
				selectedItems.add(sN);
			sN.notifySelection(true);
		}
		keywordsProcessing();
	}

	public void keywordsProcessing() {
		Platform.runLater(new Runnable() {
			public void run() {
				otherKeysArea.setText("...");
				salesCountLabel.setText("");
				otherCountLabel.setText("");
			}
		});
		if (processingTask != null && processingTask.isRunning())
			processingTask.cancel(true);
		createProcessingTask();
		callTask(processingTask);
	}

	public void leftStatusUpdate(String text) {

		Platform.runLater(new Runnable() {
			public void run() {
				leftStatusLabel.setText(text);
			}
		});
	}

	public void rightStatusUpdate(String text) {

		Platform.runLater(new Runnable() {
			public void run() {
				rightStatusLabel.setText(text);
			}
		});

	}

	
	public void setSearchIndicator(boolean isProgress) {
		this.searchIndicator.setVisible(isProgress);
	}
	
	 static class ResizableRectangle extends Rectangle {
	        ResizableRectangle(double w, double h) {
	            super(w, h);
	        }
	        @Override
	        public boolean isResizable() {
	            return true;
	        }
	        @Override
	        public double minWidth(double height) {
	            return 0.0;
	        }
	    }
	
	 
}
