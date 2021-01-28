package sm.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import org.json.JSONException;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
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
import sm.JsonParser;
import sm.Main;
import sm.ShutterImage;
import sm.web.ContentResponse;
import sm.web.ShutterProvider;
import sm.web.SubmitResponse;

public class MainController implements Initializable {

	public Main app;
	
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
	private CheckBox correctFilenameBox;
	
	@FXML
	private CheckBox showDescriptionBox;
	
	@FXML
	private CheckBox showKeywordsBox;
	
	@FXML
	private Spinner<Integer> submitCountSpinner;
	
	@FXML
	private TextFlow logTxt;
	
	@FXML
	private TableView<ShutterImage> tableView;
	
	@FXML
	private TableColumn<ShutterImage, Boolean> columnSelect;
	
	@FXML
	private TableColumn<ShutterImage, String> columnStatus;
	
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
	
	public ObservableList<ShutterImage> images = FXCollections.observableArrayList();
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadSessionId();
		columnPreview.setPrefWidth(200);
		columnSelect.setCellValueFactory(new PropertyValueFactory<>("selected"));
		columnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
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
	    
	}	
	
	
	public void setup() {
		
		 SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = //
	                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 100);
		 valueFactory.amountToStepByProperty().set(1);
	     submitCountSpinner.setValueFactory(valueFactory);
	     TextFormatter<Integer> integerFormatter = new TextFormatter<Integer>(valueFactory.getConverter(), valueFactory.getValue());
	     submitCountSpinner.getEditor().setTextFormatter(integerFormatter);
		
	}
	
	private void setupTableViewColumn() {
		columnSelect.setCellFactory(column -> new CheckBoxTableCell<ShutterImage, Boolean>());
		columnSelect.setCellValueFactory(cellData -> {
            ShutterImage cellValue = cellData.getValue();
            BooleanProperty property = cellValue.getSelected();
            // Add listener to handler change
            property.addListener((observable, oldValue, newValue) -> cellValue.setSelected(newValue));
            property.addListener((observable, oldValue, newValue) -> tableView.getSelectionModel().getSelectedItems().forEach(it->it.setSelected(newValue)));
            return property;
        });
    }
	
	public void log(String message) {
		Platform.runLater(new Runnable() {
            public void run() {
            	 Text t1 = new Text();
                 //t1.setStyle("-fx-fill: #4F8A10;-fx-font-weight:bold;");
                 t1.setText(message + "\n");
                 logTxt.getChildren().add(t1);
            }
		 });
	}
	
	public void logError(String message) {
		Platform.runLater(new Runnable() {
            public void run() {
            	 Text t1 = new Text();
                 t1.setStyle("-fx-fill: red;-fx-font-weight:bold;");
                 t1.setText(message + "\n");
                 logTxt.getChildren().add(t1);
            }
		 });
	}
	
	public void log(Collection<String> messages) {
		Platform.runLater(new Runnable() {
            public void run() {
            	 Text t1 = new Text();
                 //t1.setStyle("-fx-fill: #4F8A10;-fx-font-weight:bold;");
                 t1.setText(String.join("\n", messages));
            }
		 });
	}
	
	@FXML
	private void submitBtnClick() {
		
		if (this.images.isEmpty()) {
			log("Нет файлов для сабмита либо не нажали сначала Get Files List");
			return;
		}
		
		
		List<ShutterImage> tempList = new ArrayList<ShutterImage>(); 
		
		for (ShutterImage im:images) {
			if (im.getUploaded_filename().contains("holo")
					 || im.getUploaded_filename().contains("bauhaus")
					 || im.getUploaded_filename().contains("magic_")
					 || im.getUploaded_filename().contains("gradien")
					 || im.getUploaded_filename().contains("fishscale")
					 || im.getUploaded_filename().contains("electro")
					 || im.getUploaded_filename().contains("cover")
					 //|| im.getUploaded_filename().contains("big_data")
						 )
			im.setCategories("26", "3");
			im.setIs_illustration(true);
			tempList.add(im);	
		}
		
		
		/*
		for (int k=images.size()-1; k>images.size()-3;k--) {
			ShutterImage im1 = images.get(k);
			im1.setCategories("26", "3");
			im1.setIs_illustration(true);
			tempList.add(im1);	
		}
		*/
		
		disableControl();
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					ShutterProvider provider = getSession();
					submitImages(provider, tempList);
				}
				finally{
					enableControl();
				}
			}
		});
		t1.start();
		
		
		/*String contentJson = JsonParser.createContentPayload(tempList);
		log(contentJson);
		System.out.println(contentJson);
		*/
		/*
		List<ShutterImage> list = new ArrayList<ShutterImage>();
		
		ShutterImage im1 = new ShutterImage ("testId1", "filename1.eps"); 
		ShutterImage im2 = new ShutterImage ("testId2", "filename2.eps"); 
		im1.setCategories("26", "3");
		list.add(im1);
		list.add(im2);
		String json = JsonParser.createContentPayload(list);
		log(json);
		System.out.println(json);
		*/
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
				log(im.getUploaded_filename());
				ImageView view = im.getImage();
				view.fitHeightProperty().bind(slider.valueProperty()); 
			}
			page++;
		}
		this.images.forEach(im->im.setStatus("Uploaded"));
		correctFilename();
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
	
	
	private void submitImages(ShutterProvider provider, List<ShutterImage> files) {
		
	//	int per_post = 10;
		
		try {
			
			ContentResponse reponse = provider.contentPost(files);
			log(reponse.toString());
			System.out.println(reponse.toString());
			
			SubmitResponse sresponse = provider.submitPost(files);
			log(sresponse.print());
			System.out.println(sresponse.print());
		}
		catch (JSONException e) {
				logError("JSON error" + e.getMessage());
			}
		catch (IOException e) {
			logError(e.getMessage());
		}
	}
	
	public void disableControl() {
		Platform.runLater(new Runnable() {
            public void run() {
            	getFilesListBtn.setDisable(true);
            	submitAllBtn.setDisable(true);
            }
		 });
	}
	
	public void enableControl() {
	Platform.runLater(new Runnable() {
        public void run() {
        	getFilesListBtn.setDisable(false);
        	submitAllBtn.setDisable(false);
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
		
}
