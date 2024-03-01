package klt.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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
import javafx.util.StringConverter;
import klt.Main;
import klt.data.ImageData;
import klt.data.JsonParser;
import klt.data.KeyWord;
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
	private RadioButton radioVectorIllustration;

	@FXML
	private Label salesCountLabel;

	@FXML
	private Label otherCountLabel;
	
	@FXML
	private Spinner<Integer> requestCountSpinner;
	
	@FXML
	private Spinner<Integer> keywordsCountSpinner;
	
	@FXML
	private TextField searchTxt;
	
	@FXML
	private ProgressIndicator searchIndicator;

	ToggleGroup radioTypeGroup = new ToggleGroup();
	
	@FXML
	private CheckBox isSearchByUser;
	
	@FXML
	private TextField userIDTxt;
	
	@FXML
	private TabPane tabPane;
	
	@FXML
	private Tab tabSearch;
	
	@FXML
	private Tab tabEdit;
	
	@FXML
	private FlowPane keysEditPane;
	
	@FXML
	private Button addKeysToEditPaneBtn;
	
	@FXML
	private Button editPaneSelectAllBtn;
	
	@FXML
	private Button editPaneUnselectAllBtn;
	
	@FXML
	private Button editPaneReverseBtn;
	
	@FXML
	private Button editPaneCopyBtn;
	
	@FXML
	private Button editPaneDeleteUnselectedBtn;
	
	@FXML
	private Button editPaneCleanBtn;
	
	@FXML
	private Button editPaneAddBtn;
	
	@FXML
	private TextField editPaneAddText;
	
	@FXML
	private Label editPaneSalesCountLabel;
	
	@FXML
	private TextField editPaneSearchTxt;
	
	@FXML
	private ScrollPane keysEditScrollPane;
	
	@FXML
	private CheckBox useNewApi;
	
	@FXML
	private TextField newApiString;
	
    private int shiftTagPosition;
    private int shiftTagPositionEditPane;

	private List<SelectableBorderPane> images = new ArrayList<SelectableBorderPane>();
	private List<SelectableBorderPane> previousImages = new ArrayList<SelectableBorderPane>();
	private List<SelectableBorderPane> badImages = new ArrayList<SelectableBorderPane>();

	private ObservableList<SelectableBorderPane> selectedItems = FXCollections.observableArrayList();

	public ObservableList<SelectableBorderPane> getSelectedItems() {
		return selectedItems;
	}
	
	public List<TagButton> keysButtons = new ArrayList<TagButton>();
	
	public List<TagButton> keysButtonsEditPane = new ArrayList<TagButton>();
	
	public List<KeyWord> keywordsEdit = new ArrayList<KeyWord>();

	Task<Void> backgroundLoadTask;

	Task<Void> processingTask;
	
	ShutterRequest request;
	
	private final ChangeListener<String> searchListener  = new ChangeListener<String>(){
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
			 highlightKeys();
		}
	};
	
	private final ChangeListener<String> searchListenerEditTab  = new ChangeListener<String>(){
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
			highlightKeysEditPane();
		}
	};
	
	private void highlightKeysEditPane() {
		if (editPaneSearchTxt.getText().trim().isEmpty())
			keysButtonsEditPane.stream().forEach(b -> b.setFound(false));
		else {
			keysButtonsEditPane.stream().filter(b -> b.key.startsWith(editPaneSearchTxt.getText())).forEach(b -> b.setFound(true));
			keysButtonsEditPane.stream().filter(b -> !b.key.startsWith(editPaneSearchTxt.getText())).forEach(b -> b.setFound(false));
		}
	}
	
	
	private void highlightKeys() {
		if (searchTxt.getText().trim().isEmpty())
			keysButtons.stream().forEach(b -> b.setFound(false));
		else {
			keysButtons.stream().filter(b -> b.key.startsWith(searchTxt.getText())).forEach(b -> b.setFound(true));
			keysButtons.stream().filter(b -> !b.key.startsWith(searchTxt.getText())).forEach(b -> b.setFound(false));
		}
	}
	
	
	
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
		editPaneAddText.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				addMoreKeywordsEditPane();
			}
		});
		userIDTxt.disableProperty().bind(isSearchByUser.selectedProperty().not());
		
		
	}

	public void setup() {
		loadBar.prefWidthProperty().bind(progressBarBox.widthProperty().subtract(20));

		radioAll.setToggleGroup(radioTypeGroup);
		radioPhotos.setToggleGroup(radioTypeGroup);
		radioVectors.setToggleGroup(radioTypeGroup);
		radioIllustration.setToggleGroup(radioTypeGroup);
		radioVectorIllustration.setToggleGroup(radioTypeGroup);
		radioAll.setOnAction(value -> search());
		radioPhotos.setOnAction(value -> search());
		radioVectors.setOnAction(value -> search());
		radioIllustration.setOnAction(value -> search());
		radioVectorIllustration.setOnAction(value -> search());
		// testTags();
		selectedItems.addListener((ListChangeListener<SelectableBorderPane>) c -> {
			selectedImagesCountLabel.setText("Selected images: " + selectedItems.size());
		});
		 SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = //
	                new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 100000, 100);
		 valueFactory.amountToStepByProperty().set(100);
	     requestCountSpinner.setValueFactory(valueFactory);
	     TextFormatter<Integer> integerFormatter = new TextFormatter<Integer>(valueFactory.getConverter(), valueFactory.getValue());
	     requestCountSpinner.getEditor().setTextFormatter(integerFormatter);
	     
	     
		 SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory2 = //
	                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10);
		 valueFactory2.amountToStepByProperty().set(1);
	     keywordsCountSpinner.setValueFactory(valueFactory2);
	     TextFormatter<Integer> integerFormatter2 = new TextFormatter<Integer>(valueFactory2.getConverter(), valueFactory2.getValue());
	     keywordsCountSpinner.getEditor().setTextFormatter(integerFormatter2);
	     
	     keywordsCountSpinner.focusedProperty().addListener((s, ov, nv) -> {
	    	   // if (nv) return;
	    	    String text = keywordsCountSpinner.getEditor().getText();
	    	    if (valueFactory2 != null) {
	    	        StringConverter<Integer> converter = valueFactory2.getConverter();
	    	        if (converter != null) {
	    	        	Integer value = converter.fromString(text);
	    	            valueFactory2.setValue(value);
	    	        }
	    	       ImageData.limit =  valueFactory2.getValue();
	    	       recountKeywords();
	    	    }
	    	});
	     
	     keywordsCountSpinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
	         if (!"".equals(newValue)) {
	        	 String text = keywordsCountSpinner.getEditor().getText();
		    	    if (valueFactory2 != null) {
		    	        StringConverter<Integer> converter = valueFactory2.getConverter();
		    	        if (converter != null) {
		    	        	Integer value = converter.fromString(text);
		    	            valueFactory2.setValue(value);
		    	        }
		    	       ImageData.limit =  valueFactory2.getValue();
		    	       recountKeywords();
		    	    }
	         } 
	     });
	     
	     searchIndicator.setVisible(false);
	     searchTxt.textProperty().addListener(searchListener);
	     editPaneSearchTxt.textProperty().addListener(searchListenerEditTab);
	    /*
	     useNewApi.selectedProperty().addListener(
	    	      (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
	    	         if (new_val)
	    	        	 requestCountSpinner.setDisable(true);
	    	         else
	    	        	 requestCountSpinner.setDisable(false);
	    	      });
	     */
	}

	private void createProcessingTask() {
		processingTask = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {
				System.out.println("Run Processing Task");
				Platform.runLater(new Runnable() {
					public void run() {
						keysPane.getChildren().clear();
						keysButtons.clear();
					}});
				
				rightStatusUpdate("Waiting for keywords data to be downloaded...");
				
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
				List<KeyWord> saleskwds = new ArrayList<KeyWord>();
				List<String> otherkwds = new ArrayList<String>();
				for (SelectableBorderPane p : selectedItems) {
					for (String word:p.imageData.getSaleKeywords()) {
						KeyWord kword = saleskwds.stream().filter(kw -> kw.word.equals(word)).findFirst().orElse(null);
						if (kword!=null)
							kword.images.add(p.imageData.id);
						else {
							KeyWord kworddd = new KeyWord(word, p.imageData.id);
							kworddd.unsales.addAll(selectedItems.stream().filter(si -> si.imageData.getOtherKeywords().contains(kworddd.word)).map(SelectableBorderPane::getImageId).collect(Collectors.toSet()));
							saleskwds.add(kworddd);
						}
					}
					otherkwds.addAll(p.imageData.getOtherKeywords());
				}				
				collectKeywords(saleskwds, otherkwds);
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
		list.sort(Entry.comparingByValue(Comparator.reverseOrder()));
		Map<K, V> result = new LinkedHashMap<>();
		for (Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}

	private void collectKeywords(List<KeyWord> salekwds, List<String> otherkwds) {
		Platform.runLater(new Runnable() {
			public void run() {

				//List<TagButton> tempList = new ArrayList<TagButton>();
				//tempList.addAll(keysButtons);
				
				KeyWord.sortByWeight(salekwds);
				Collections.sort(otherkwds);
				
				
				boolean isSeparator = false;
				boolean isOnlyOne = true;
				int position = 1;
				
				for (KeyWord kw : salekwds){
					//long allCount  = kw.images.size() + otherkwds.stream().filter(w -> w.equals(kw.word)).count();
					long allCount  = kw.images.size() +  kw.unsales.size();
					if (kw.images.size()>1) isOnlyOne=false;
					if(kw.images.size()==1 && !isSeparator && !isOnlyOne) {
						isSeparator = true;
						  Rectangle rect = new ResizableRectangle(keysPane.getWidth(),5);
					        rect.setFill(Color.TRANSPARENT);
					        rect.widthProperty().bind(keysPane.widthProperty());
					        keysPane.getChildren().add(rect);
					}
					
					final TagButton  button = new TagButton(kw, allCount);
					keysButtons.add(button);
					keysPane.getChildren().add(button);
					button.position = position;
					//TagButton old= tempList.stream().filter(t->t.key.equals(kw.word)).findFirst().orElse(null);
						//if (old!=null)
							//button.setActive(old.isActive);
					    
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
									 recountSalesCount();
									 shiftTagPosition = button.position;
									}
							 }
						   });
					    
					position++;
				}
				
				
				recountSalesCount();
				salekwds.forEach(kw -> otherkwds.remove(kw.word));
				otherKeysArea.setText(String.join(", ", otherkwds.stream().distinct().collect(Collectors.toList())));
				otherCountLabel.setText("Other keys: " + otherkwds.size());
				highlightKeys();
				System.out.println(keysEditScrollPane.getWidth());
				System.out.println(keysPane.getWidth());
			}
		});
	}
	
	
	@FXML
	private void addKeywordsToEditPane() {
		for (TagButton button:keysButtons)
			if (button.isActive) {
				KeyWord kword = this.keywordsEdit.stream().filter(kw -> kw.word.equals(button.keyword.word)).findFirst().orElse(null);
				if (kword==null)
					this.keywordsEdit.add(button.keyword);
				else 
					kword.images.addAll(button.keyword.images);
			}
		collectKeysEditPane();
		this.tabPane.getSelectionModel().select(tabEdit);
	}
	
	
	private void collectKeysEditPane() {
			Platform.runLater(new Runnable() {
				public void run() {
					
					List<TagButton> tempList = new ArrayList<TagButton>();
					tempList.addAll(keysButtonsEditPane);
					
					keysEditPane.getChildren().clear();
					keysButtonsEditPane.clear();
					
					KeyWord.sortByWeight(keywordsEdit);
					
					boolean isSeparator = false;
					boolean isOnlyOne = true;
					int position = 1;
					
					for (KeyWord kw : keywordsEdit){
						long allCount  = kw.images.size() +  kw.unsales.size();
						if (kw.images.size()>1) isOnlyOne=false;
						if(kw.images.size()==1 && !isSeparator && !isOnlyOne) {
							isSeparator = true;
							  Rectangle rect = new ResizableRectangle(keysEditPane.getWidth(),5);
						        rect.setFill(Color.TRANSPARENT);
						        rect.widthProperty().bind(keysEditPane.widthProperty());
						        keysEditPane.getChildren().add(rect);
						}
						
						final TagButton  button = new TagButton(kw, allCount);
						keysButtonsEditPane.add(button);
						keysEditPane.getChildren().add(button);
						button.position = position;
						TagButton old= tempList.stream().filter(t->t.key.equals(kw.word)).findFirst().orElse(null);
							if (old!=null)
								button.setActive(old.isActive);
						    
							button.setOnMouseClicked(new EventHandler<MouseEvent>(){
								@Override
								public void handle(MouseEvent event){ 
								    if(event.getButton().equals(MouseButton.PRIMARY)){
								    	
								    	 if (event.isShiftDown() && shiftTagPositionEditPane>0) {
											 TagButton previousButton = keysButtonsEditPane.stream().filter(b->b.position == shiftTagPositionEditPane).findFirst().orElse(null);
											 if (previousButton !=null)
												 keysButtonsEditPane.forEach(tag -> {
												 if ((shiftTagPositionEditPane<button.position && tag.position>shiftTagPositionEditPane && tag.position<=button.position)
														  || (shiftTagPositionEditPane>button.position && tag.position<shiftTagPositionEditPane && tag.position>=button.position)) {
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
										 recountSalesCountEditPane();
										 shiftTagPositionEditPane = button.position;
										}
								 }
							   });
						    
						position++;
					}
					
					recountSalesCountEditPane();
					highlightKeysEditPane();
					
				}
			});
	}
	
	
	
	public void recountSalesCount() {
		Platform.runLater(new Runnable() {
			public void run() {
				salesCountLabel.setText("Keys: " + keysButtons.stream().filter(k->k.isActive).count());
			}});
	}
	
	@FXML
	private void selectAllKeywords() {
		for (TagButton button:keysButtons)
			button.setActive(true);
		recountSalesCount();
	}

	@FXML
	private void unselectAllKeywords() {
		for (TagButton button:keysButtons)
			button.setActive(false);
		recountSalesCount();
	}
	
	@FXML
	private void reversKeywords() {
		for (TagButton button:keysButtons)
			if (button.isActive)
				button.setActive(false);
			else
				button.setActive(true);
		recountSalesCount();
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
	
	
	public void recountSalesCountEditPane() {
		Platform.runLater(new Runnable() {
			public void run() {
				editPaneSalesCountLabel.setText("Keys: " + keysButtonsEditPane.stream().filter(k->k.isActive).count());
			}});
	}
	
	@FXML
	private void selectAllKeywordsEditPane() {
		for (TagButton button:keysButtonsEditPane)
			button.setActive(true);
		recountSalesCountEditPane();
	}

	@FXML
	private void unselectAllKeywordsEditPane() {
		for (TagButton button:keysButtonsEditPane)
			button.setActive(false);
		recountSalesCountEditPane();
	}
	
	@FXML
	private void reversKeywordsEditPane() {
		for (TagButton button:keysButtonsEditPane)
			if (button.isActive)
				button.setActive(false);
			else
				button.setActive(true);
		recountSalesCountEditPane();
	}
	
	@FXML
	private void cleanEditPane() {
		Platform.runLater(new Runnable() {
			public void run() {
				
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setHeaderText("Action confirmation");
				alert.setContentText("Are you sure want to clear this list?");
				alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
				
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.YES){
					keysEditPane.getChildren().clear();
					keysButtonsEditPane.clear();
					keywordsEdit.clear();
					recountSalesCountEditPane();
				} else {
				}
			}});
	}
	
	
	@FXML
	private void removeUnselectedEditPane() {
		Platform.runLater(new Runnable() {
			public void run() {
		for (TagButton button:keysButtonsEditPane) {
			if (!button.isActive) {
				keysEditPane.getChildren().remove(button);
				keywordsEdit.remove(button.keyword);
			}
		}	
		keysButtonsEditPane.removeIf(b -> !b.isActive);
		recountSalesCountEditPane();
			}});
	}
	
	@FXML
	private void addMoreKeywordsEditPane() {
		Platform.runLater(new Runnable() {
			public void run() {
		        String text = editPaneAddText.getText();
		        String[] keys = text.split("\\s*(;|,)\\s*");
		        for (String k:keys) {
		        	if (!k.trim().isEmpty() && !keywordsEdit.stream().anyMatch(kw -> kw.word.equals(k)))
		        		keywordsEdit.add(new KeyWord(k.trim()));
		        }
		        editPaneAddText.clear();
		        collectKeysEditPane();
				recountSalesCountEditPane();
			}});
	}
	
	
	@FXML
	private void copyKeywordsEditPane() {
		List<String> keys = new ArrayList<String>();
		for (TagButton button:keysButtonsEditPane)
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
					//System.out.println(imdata.id);
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
	
	private void recountKeywords() {
		ImageData.limit = this.keywordsCountSpinner.getValue();
		for (int i = 0; i < images.size(); i++) {
			ImageData imData = images.get(i).imageData;
			imData.limitKeywords();
		}
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
				String kwordsJson = new ShutterProvider("").getKeywordsJsonOldApi(image.id);
				kwds = new JsonParser().parseKeywords(kwordsJson);
				
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
	    
	    if (this.isSearchByUser.isSelected())
	    	requestData.user = this.userIDTxt.getText();
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
		else if (radioVectorIllustration.isSelected())
			requestData.type = ImagesType.VECTORSILLUSTRATIONS;
		if (backgroundLoadTask != null && backgroundLoadTask.isRunning())
			backgroundLoadTask.cancel(true);
		cleanResults();
		if (this.request!=null)
			request.cancel();
		request = new ShutterRequest(requestData, this, this.isNewApi(), this.getNewApiString());
		request.execute();
		this.tabPane.getSelectionModel().select(tabSearch);
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
				imagesContainer.getChildren().clear();
				previousImages.clear();
				previousImages.addAll((Collection<? extends SelectableBorderPane>) selectedItems);
				images.clear();
				selectedItems.clear();
				leftStatusUpdate("");
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
	
	 
	 public void gotoSearchTab() {
		 this.tabPane.getSelectionModel().select(tabSearch);
	 }
	 
	 
	 public boolean isNewApi() {
		 return this.useNewApi.isSelected();
	 }
	 
	 public String getNewApiString() {
		 return this.newApiString.getText().trim();
	 }
	 
}
