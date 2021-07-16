package sm.ui;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import org.json.JSONException;

import com.sun.javafx.scene.control.skin.TableHeaderRow;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import sm.Data;
import sm.JsonParser;
import sm.Main;
import sm.ShutterImage;
import sm.web.ContentResponse;
import sm.web.ShutterProvider;
import sm.web.SubmitResponse;
import sm.web.SubmitResponse.ItemError;

public class MainController implements Initializable {

	public Main app;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	@FXML
	private Button getFilesListBtn;
	
	@FXML
	private Button submitAllBtn;
	
	@FXML
	private Button submitSelectedBtn;
	
	@FXML
	private TextField sessionIdText;

	@FXML
	private CheckBox isLimitSubmitCountBox;
	
	@FXML
	private CheckBox isLimitVectorRasterSubmitCountBox;
	
	@FXML
	private CheckBox isRandomOrderBox;
	
	@FXML
	private CheckBox correctFilenameBox;
	
	@FXML
	private CheckBox showDescriptionBox;
	
	@FXML
	private CheckBox showKeywordsBox;
	
	@FXML
	private Spinner<Integer> submitCountSpinner;
	
	@FXML
	private Spinner<Integer> vectorCountSpinner;
	
	@FXML
	private Spinner<Integer> rasterCountSpinner;
	
	@FXML
	private TextFlow logTxt;
	
	@FXML
	private CheckBox selectAllBox;
	
	@FXML
	private TableView<ShutterImage> tableView;
	
	@FXML
	private TableColumn<ShutterImage, Boolean> columnSelect;
	
	@FXML
	private TableColumn<ShutterImage, String> columnStatus;
	
	@FXML
	private TableColumn<ShutterImage, String> columnDate;
	
	@FXML
	private TableColumn<ShutterImage, ImageView> columnPreview;
	
	@FXML
	private TableColumn<ShutterImage, String> columnName;
	
	@FXML
	private TableColumn<ShutterImage, String> columnDescription;
	
	@FXML
	private TableColumn<ShutterImage, String> columnKeywords;
	
	@FXML
	private TableColumn<ShutterImage, Integer> columnKeywordsCount;
	
	@FXML
	private TableColumn<ShutterImage, String> columnCategories;
	
	@FXML
	private TableColumn<ShutterImage, String> columnPropertyRelease;
	
	@FXML
	private TableColumn<ShutterImage, Boolean> columnIsIllustration;
	
	@FXML
	private Slider slider;
	
	@FXML
	private Label filesCountTxt;
	
	@FXML
	private Button applyBtn;
	
	@FXML
	private CheckBox testModeBox;
	
	public ObservableList<ShutterImage> images = FXCollections.observableArrayList();
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadSessionId();
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.setItems(images);
		setupTableViewColumn();
		tableView.getSelectionModel().setCellSelectionEnabled(true);
	    tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	    tableView.setOnKeyPressed(new TableKeyEventHandler());
	    tableView.getSelectionModel().getSelectedItems().forEach(it->it.setSelected(true));
	    
	    images.addListener(new ListChangeListener<ShutterImage>() {
			@Override
			public void onChanged(Change<? extends ShutterImage> c) {
				
				Platform.runLater(new Runnable() {
		            public void run() {
		            	updateFilesCount();
		            }
				 });
			}
	    });
	    this.columnDescription.setVisible(false);
	    this.columnKeywords.setVisible(false);
	    
	    this.isLimitSubmitCountBox.selectedProperty().addListener(new ChangeListener<Boolean >() {  
				@Override
				public void changed(ObservableValue observable, Boolean  oldValue, Boolean  newValue) {
					 isLimitVectorRasterSubmitCountBox.setSelected(false);
				}
	    });
	    this.isLimitVectorRasterSubmitCountBox.selectedProperty().addListener(new ChangeListener<Boolean>() {  
			@Override
			public void changed(ObservableValue observable, Boolean  oldValue, Boolean  newValue) {
				isLimitSubmitCountBox.setSelected(false);
			}
    });
	}	
	
	
	public void setup() {
		
		 SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = //
	                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, 100, 1);
	     submitCountSpinner.setValueFactory(valueFactory);
	     TextFormatter<Integer> integerFormatter = new TextFormatter<Integer>(valueFactory.getConverter(), valueFactory.getValue());
	     submitCountSpinner.getEditor().setTextFormatter(integerFormatter);
	     submitCountSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
	    	  if (!newValue) {
	    		  submitCountSpinner.increment(0); // won't change value, but will commit editor
	    	  }
	    	});
	     
	     SpinnerValueFactory.IntegerSpinnerValueFactory valueFactoryRaster = //
	                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500, 50, 1);
	     rasterCountSpinner.setValueFactory(valueFactoryRaster);
	     TextFormatter<Integer> integerFormatterRaster = new TextFormatter<Integer>(valueFactoryRaster.getConverter(), valueFactoryRaster.getValue());
	     rasterCountSpinner.getEditor().setTextFormatter(integerFormatterRaster);
	     rasterCountSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
	    	  if (!newValue) {
	    		  rasterCountSpinner.increment(0); // won't change value, but will commit editor
	    	  }
	    	});
	     
	     SpinnerValueFactory.IntegerSpinnerValueFactory valueFactoryVector = //
	                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500, 50, 1);
	     vectorCountSpinner.setValueFactory(valueFactoryVector);
	     TextFormatter<Integer> integerFormatterVector = new TextFormatter<Integer>(valueFactoryVector.getConverter(), valueFactoryVector.getValue());
	     vectorCountSpinner.getEditor().setTextFormatter(integerFormatterVector);
	     vectorCountSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
	    	  if (!newValue) {
	    		  vectorCountSpinner.increment(0); // won't change value, but will commit editor
	    	  }
	    	});
	}
	
	private void setupTableViewColumn() {
		columnSelect.setCellValueFactory(new PropertyValueFactory<>("selected"));
		columnSelect.setCellFactory(column -> new CheckBoxTableCell<ShutterImage, Boolean>());
		columnSelect.setCellValueFactory(cellData -> {
            ShutterImage cellValue = cellData.getValue();
            BooleanProperty property = cellValue.getSelected();
            // Add listener to handler change
            property.addListener((observable, oldValue, newValue) -> cellValue.setSelected(newValue));
            property.addListener((observable, oldValue, newValue) -> tableView.getSelectionModel().getSelectedItems().forEach(it->it.setSelected(newValue)));
            return property;
        });
		
		columnPreview.setPrefWidth(200);
		columnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		columnName.setCellValueFactory(new PropertyValueFactory<>("uploaded_filename"));
		columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
		columnDescription.setCellFactory(tc -> {
		    TableCell<ShutterImage, String> cell = new TableCell<>();
		    Text text = new Text();
		    cell.setGraphic(text);
		    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
		    text.wrappingWidthProperty().bind(columnDescription.widthProperty());
		    text.textProperty().bind(cell.itemProperty());
		    return cell ;
		});
		
		columnKeywords.setCellValueFactory(cl -> new SimpleStringProperty(String.join(", ", cl.getValue().keywords)));
		columnKeywords.setCellFactory(tc -> {
		    TableCell<ShutterImage, String> cell = new TableCell<>();
		    Text text = new Text();
		    cell.setGraphic(text);
		    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
		    text.wrappingWidthProperty().bind(columnKeywords.widthProperty());
		    text.textProperty().bind(cell.itemProperty());
		    return cell ;
		});
		columnKeywordsCount.setCellValueFactory(new PropertyValueFactory<>("keywordsCount"));
		
		columnCategories.setCellValueFactory(cl -> new SimpleStringProperty(String.join(", ", cl.getValue().categoriesNames)));
		columnPropertyRelease.setCellValueFactory(cl -> new SimpleStringProperty(String.join(", ", cl.getValue().releasesNames)));
		columnIsIllustration.setCellValueFactory(new PropertyValueFactory<>("is_illustration"));
		
		columnPreview.setCellValueFactory(new PropertyValueFactory<ShutterImage, ImageView>("image"));
		
		//TableHeaderRow header = (TableHeaderRow) tableView.lookup("TableHeaderRow");
		//header.setMouseTransparent(true);
    }
	
	public void log(String message) {
		Platform.runLater(new Runnable() {
            public void run() {
            	 String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
            	 Text t1 = new Text();
                 //t1.setStyle("-fx-fill: #4F8A10;-fx-font-weight:bold;");
                 t1.setText(timeStamp + "\t" + message + "\n");
                 logTxt.getChildren().add(t1);
            }
		 });
	}
	
	public void logError(String message) {
		Platform.runLater(new Runnable() {
            public void run() {
            	String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
            	 Text t1 = new Text();
                 t1.setStyle("-fx-fill: red;-fx-font-weight:bold;");
                 t1.setText(timeStamp + "\t" + message + "\n");
                 logTxt.getChildren().add(t1);
            }
		 });
	}
	
	public void logGreen(String message) {
		Platform.runLater(new Runnable() {
            public void run() {
            	String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
            	 Text t1 = new Text();
                 t1.setStyle("-fx-fill: green;-fx-font-weight:bold;");
                 t1.setText(timeStamp + "\t" + message + "\n");
                 logTxt.getChildren().add(t1);
            }
		 });
	}
	
	@FXML
	private void selectAllClick() {
		images.forEach(im->im.setSelected(this.selectAllBox.isSelected()));
	}
	
	@FXML
	private void submitBtnClick() {
		submit(false);
	}
	
	@FXML
	private void submitSelectedBtnClick() {
		submit(true);
	}
	
	private void submit(boolean onlySelected) {
		
		if (this.testModeBox.isSelected())
			logGreen("TEST MODE! No real submit or data update will be done!");
		
		if (this.images.isEmpty()) {
			log("Нет файлов для сабмита либо не нажали сначала Get Files List");
			return;
		}
		
		if (this.isRandomOrderBox.isSelected())
			Collections.shuffle(this.images);
		
		List<ShutterImage> tempList = new ArrayList<ShutterImage>(); 
		int takecount = this.submitCountSpinner.getValue();
		int takecountraster = this.rasterCountSpinner.getValue();
		int takecountvector = this.vectorCountSpinner.getValue();
		ListIterator<ShutterImage> li = this.images.listIterator(this.images.size());

		int raster = 0;
		int vector = 0;
		while(li.hasPrevious()) {
			if (this.isLimitSubmitCountBox.isSelected() && tempList.size()>=takecount) break;
			if (this.isLimitVectorRasterSubmitCountBox.isSelected() && tempList.size()>=(takecountraster + takecountvector)) break;
			ShutterImage image = li.previous();
			if (this.isLimitVectorRasterSubmitCountBox.isSelected()) {
				if (image.getStatus().equals("Ready") && image.isVector() && vector < takecountvector
						&& (onlySelected==false || (image.getSelected().get()))) {
					tempList.add(image);
					vector++;
				}
				else if (image.getStatus().equals("Ready") && !image.isVector() && raster < takecountraster
						&& (onlySelected==false || (image.getSelected().get()))) {
					tempList.add(image);
					raster++;
				}
			}
			else if (image.getStatus().equals("Ready") & (onlySelected==false || (image.getSelected().get())))
				tempList.add(image);
		}
		
		if (tempList.isEmpty()) {
			app.showAlert("Nothing to submit. Please apply rules to add categories");
			return;
		}
		
		
		int prepareForSubmit = tempList.size();
		disableControl();
		AtomicInteger unsuccessfull = new AtomicInteger(0);
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				int code = 0;
				try {
					ShutterProvider provider = getSession();
					while (!tempList.isEmpty()) {
						if (tempList.size()>100) {
							code = submitImages(provider, tempList.subList(0, 99));
							if (code == -1) 
								break;
							unsuccessfull.addAndGet(code);
							LOGGER.fine("submitImages return code: " + code);
							tempList.removeAll(tempList.subList(0, 99));
						}
						else {
							code = submitImages(provider, tempList);
							unsuccessfull.addAndGet(code);
							LOGGER.fine("submitImages return code: " + code);
							break;
						}
					}
				}
				finally{
					enableControl();
					if (code < 0 ) {
						app.showAlert("Submit Failed");
					}
					else if (unsuccessfull.get()>0) {
						app.showAlert("Successfully Submitted: " + String.valueOf(prepareForSubmit-unsuccessfull.get()) + "\n"
								+ "Unsuccessfull: " + unsuccessfull.get());
					}
					else 
						app.showAlertOK("Successfully Submitted: " + prepareForSubmit);
				}
			}
		});
		t1.start();
	}
	
	
	
	@FXML
	private void getFilesList() {
		
		logTxt.getChildren().clear();
		this.images.clear();
		disableControl();
		
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
				ShutterProvider provider = getSession();
				getLoadedFilesList(provider);
				}
				finally {
				enableControl();
				}
			}
		});
		t1.start();
	}
	
	
	private void getLoadedFilesList(ShutterProvider provider) {
		int per_page = 100;
		int page = 1;
		String filesList = null;
		try {
		logGreen("Loading uploaded files list...");
		while (true) {
			filesList = provider.getLoadedFilesList(per_page,page);
			if (filesList == null) {
				logError("Ошибка соединения");
				showAlert("Ошибка соединения с сервером");
				return;
				}
			if (filesList.isEmpty()) break;
		    
			//writeToFile("D:\\Response.txt", rejectesString);
		    System.out.println(filesList);
		    
		    List<ShutterImage> imagesTemp;
		    imagesTemp = JsonParser.parseImagesData(filesList);
			if (imagesTemp.isEmpty()) break;
			images.addAll(imagesTemp);
			for (ShutterImage im:imagesTemp) {
				ImageView view = im.getImage();
				view.fitHeightProperty().bind(slider.valueProperty()); 
			}
			page++;
		}
		this.images.forEach(im->im.setStatus("Uploaded"));
		correctFilename();
		logGreen("Loaded");
		}
		catch (JSONException e) {
			if (filesList.contains("Redirecting to")) {
				logError("Autorization error");
				showAlert("Неправильный sessionId.");
			}
			else if (filesList.startsWith("{")){
				logError("JSON parsing error");
				showAlert("Данные загружены, но приложение не смогло их правильно интерпретировать.");
			}
			else {
				int endIndex = filesList.length()>300 ? 300 : filesList.length();
				logError("Unknown error");
				showAlert("Некорректные данные:\n" + filesList.substring(0, endIndex)); 
			}
			}
	}
	
	
	private int submitImages(ShutterProvider provider, List<ShutterImage> files) {
		
		if (this.testModeBox.isSelected()) {
			log("Files To Submit: " + String.join(", ", files.stream().map(ShutterImage::getUploaded_filename).collect(Collectors.toList())));
			
			String json = JsonParser.createContentPayload(files);
			LOGGER.fine("CONTENT PAYLOAD: " + json);

			json = JsonParser.createSubmitPayload(files);
			LOGGER.fine("SUBMIT PAYLOAD: " + json);
			
			return files.size();
		}
		
		
		try {
			logGreen("Submit operation in progress...");
			logGreen("Starting content update");
			ContentResponse response = provider.contentPost(files);
			System.out.println(response.toString());
			if (response.notSaved.isEmpty() && response.error.isEmpty())
				log("Content update. Response: " + response.toString());
			else
				logError("Content update. Response has errors: " + response.toString()  + ", Error: " + response.error);
		
			
			List<ShutterImage> savedImages = new ArrayList<ShutterImage>();
			this.images.stream().filter(im -> response.saved.contains(im.getId())).forEach(im -> { 
				im.setStatus("Saved...");
				savedImages.add(im);
			});
			
			this.images.stream().filter(im -> response.notSaved.contains(im.getId())).forEach(im -> im.setStatus("Not saved..."));
			
			logGreen("Content update done");
			logGreen("Starting submit");
			SubmitResponse sresponse = provider.submitPost(savedImages);
			
			if (sresponse.batch_error_code!=null) {
				logError("Submit. Response has errors: " + sresponse.batch_error_message);
				return -1;
			}
			
			
			if (sresponse.itemErrors.isEmpty())
				log("Submit. Response: " + sresponse.toString());
			else {
				logError("Submit. Response has errors: " + sresponse.toString());
				logError(String.join("; ", sresponse.itemErrors.stream().map(ItemError::getIdAndMessage).collect(Collectors.toList())));
			}
			System.out.println(sresponse.toString());
			
			List<ShutterImage> submittedImages = new ArrayList<ShutterImage>();
			
			for (ShutterImage image:savedImages) {
				if (sresponse.successImages.stream().filter(o -> o.getUploadId().equals(image.getId())).findFirst().isPresent()) {
					image.setStatus("Submitted!");
					submittedImages.add(image);
				}
				else {
					image.setStatus("ERROR!");
				}
			}
			
			
			this.images.removeAll(submittedImages);
			this.images.forEach(im -> {
				if (sresponse.itemErrors.stream().map(ItemError::getUploadId).collect(Collectors.toList()).contains(im.getId())) {
					//im.setStatus(sresponse.itemErrors.stream().filter(er->er.upload_id.equals(im.getId())).findFirst().get().getMessage());
					im.setStatus("ERROR");
				}
			});
			logGreen("Submit operation completed");
			
			return files.size() - submittedImages.size();
		}
		catch (JSONException e) {
				logError("JSON error" + e.getMessage());
				return -1;
			}
		catch (IOException e) {
			logError(e.getMessage());
			return -1;
		}
	}
	
	public void disableControl() {
		Platform.runLater(new Runnable() {
            public void run() {
            	getFilesListBtn.setDisable(true);
            	submitAllBtn.setDisable(true);
            	submitSelectedBtn.setDisable(true);
            }
		 });
	}
	
	public void enableControl() {
	Platform.runLater(new Runnable() {
        public void run() {
        	getFilesListBtn.setDisable(false);
        	submitAllBtn.setDisable(false);
        	submitSelectedBtn.setDisable(false);
        }
	 });
	}
	
	private void showAlert(String text) {
		Platform.runLater(new Runnable() {
            public void run() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("ERROR");
		alert.setHeaderText("ERROR");
		alert.setContentText(text);
		alert.showAndWait();
            }
		 });
	}
	
	
	private void showMessage(String text) {
		Platform.runLater(new Runnable() {
            public void run() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Info");
		alert.setContentText(text);
		alert.showAndWait();
            }
		 });
	}
	
	
	private void saveSessionId() {
	  Preferences prefs = Preferences.userNodeForPackage(Main.class);
      if (!this.sessionIdText.getText().trim().isEmpty()) {
          prefs.put("sessionid", this.sessionIdText.getText());
      } else {
          prefs.remove("sessionid");
      }
	}
	
	private void loadSessionId() {
		Preferences prefs = Preferences.userNodeForPackage(Main.class);
        String sessionId = prefs.get("sessionid", null);
        if (sessionId != null) {
           this.sessionIdText.setText(sessionId);
        }
	}
	
	private void saveString(String key, String data) {
		  Preferences prefs = Preferences.userNodeForPackage(Main.class);
		  if (data!=null) {
			  prefs.put(key,data);
	      } else {
	          prefs.remove(key);
	      }
		}
		
		private String loadString(String key) {
			Preferences prefs = Preferences.userNodeForPackage(Main.class);
	        String saved = prefs.get(key, null);
	        return saved;
		}

		
		
		public static class TableKeyEventHandler implements EventHandler<KeyEvent> {

	        KeyCodeCombination copyKeyCodeCompination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
	        KeyCodeCombination copyKeyCodeCompinationMac = new KeyCodeCombination(KeyCode.C, KeyCombination.META_ANY);
	        public void handle(final KeyEvent keyEvent) {

	            if (copyKeyCodeCompination.match(keyEvent) || copyKeyCodeCompinationMac.match(keyEvent)) {

	                if( keyEvent.getSource() instanceof TableView) {

	                    // copy to clipboard
	                    copySelectionToClipboard( (TableView<?>) keyEvent.getSource());

	                    // event is handled, consume it
	                    keyEvent.consume();

	                }

	            }

	        }

	   
	 public static void copySelectionToClipboard(TableView<?> table) {

	        StringBuilder clipboardString = new StringBuilder();

	        ObservableList<TablePosition> positionList = table.getSelectionModel().getSelectedCells();

	        int prevRow = -1;

	        for (TablePosition position : positionList) {

	            int row = position.getRow();
	            int col = position.getColumn();

	            Object cell = (Object) table.getColumns().get(col).getCellData(row);

	            // null-check: provide empty string for nulls
	            if (cell == null) {
	                cell = "";
	            }

	            // determine whether we advance in a row (tab) or a column
	            // (newline).
	            if (prevRow == row) {

	                clipboardString.append('\t');

	            } else if (prevRow != -1) {

	                clipboardString.append('\n');

	            }

	            // create string from cell
	            String text = cell.toString();

	            // add new item to clipboard
	            clipboardString.append(text);

	            // remember previous
	            prevRow = row;
	        }

	        // create clipboard content
	        final ClipboardContent clipboardContent = new ClipboardContent();
	        clipboardContent.putString(clipboardString.toString());

	        // set clipboard content
	        Clipboard.getSystemClipboard().setContent(clipboardContent);
	    }
	 }
		
		
		@FXML
		private void correctFilename() {
			if (this.correctFilenameBox.isSelected())
				images.forEach(im->im.correctName());
			else
				images.forEach(im->im.restoreName());
			tableView.refresh();
		}
		
		public void refreshTable() {
			tableView.refresh();
		}
		
		private void updateFilesCount() {
			this.filesCountTxt.setText("Files count: " + images.size());
		}
		
		
		@FXML
		private void showDescriptionColumn() {
			this.columnDescription.setVisible(showDescriptionBox.isSelected());
			//tableView.refresh();
		}
		
		
		@FXML
		private void showKeywordsColumn() {
			this.columnKeywords.setVisible(showKeywordsBox.isSelected());
			//tableView.refresh();
		}
		
		public ShutterProvider getSession() {
			String sessionId = sessionIdText.getText().trim();
			if (sessionId.isEmpty()) {
				showAlert("Пустой sessionId");
				return null;
			}
			saveSessionId();
			ShutterProvider provider = new ShutterProvider(sessionId);
			if (!provider.isConnection()) {
				showAlert("Ошибка соединения");
				return null;
			}
			return provider;
		}
		
		
		@FXML
		private void applyRules() {
			this.app.rulesController.applyRules();
		}
		
		
		public boolean validateImageForSubmit(ShutterImage image) {
			if (
			image.keywords.size()>50 ||
			image.keywords.isEmpty() ||
			image.getDescription().isEmpty() ||
			image.getDescription().length()>200 ||
			image.categories.isEmpty() ||
			image.categories.size() >2 
			)
				return false;
			else return true;
		}
		
}
