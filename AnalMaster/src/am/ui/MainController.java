package am.ui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import am.ShutterImage;
import am.Main;




public class MainController implements Initializable {

	
	
	@FXML
	private TextField sessionIdText;
	@FXML
	private Slider slider;
	@FXML
	private Button updateBtn;
	
	@FXML
	private Button stopBtn;
	
	
	@FXML
	private TableView<ShutterImage> tableView;
	
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
	private TableColumn<ShutterImage, Integer> columnDownloads;
	
	@FXML
	private TableColumn<ShutterImage, Double> columnEarnings;
	
	@FXML
	private TextFlow logTxt;
	
	@FXML
	private CheckBox correctFilenameBox;
	
	@FXML
	private CheckBox showDescriptionBox;
	
	@FXML
	private CheckBox showKeywordsBox;
	
	@FXML
	private Label filesCountTxt;
	
	@FXML
	private Pagination pagination; 
	
	@FXML
	private TextField filenameFilter;
	
	@FXML
	private TextField descriptionFilter;
	
	@FXML
	private TextField keywordsFilter;
	
	@FXML
	private DatePicker uploadDateFrom;
	
	@FXML
	private DatePicker uploadDateTo;
	
	@FXML
	private CheckBox selectRaster;

	@FXML
	private CheckBox selectVector;
	
	
	public Main app;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public ObservableList<ShutterImage> images = FXCollections.observableArrayList();
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		loadSessionId();
		
		
	}
	
	

	@FXML
	private void updateDatabase() {
		/*
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				disableControls();
				ShutterProvider provider =  app.mainController.getSession();
				if (provider==null || !provider.isConnection()) {
					enableControls();
					return;
				}
				getRejectsForDatesAndWriteDB(provider, sqlmanager, sqlmanager.getLastUpdateDate());
				getAllImages();
				//checkNewRejects();
				//saveLastRejectDate();
				enableControls();
			}
		});
		t1.start();
   */
	}
	
	
	private void setupTableViewColumn() {
		columnPreview.setPrefWidth(200);
		columnDate.setCellValueFactory(new PropertyValueFactory<>("uploaded_date"));
		columnDate.setCellValueFactory(new PropertyValueFactory<>("verdict_time"));
		columnName.setCellValueFactory(new PropertyValueFactory<>("original_filename"));
		
		columnDownloads.setCellValueFactory(new PropertyValueFactory<>("downloads"));
		columnEarnings.setCellValueFactory(new PropertyValueFactory<>("earnings"));
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
		
		columnPreview.setCellValueFactory(new PropertyValueFactory<ShutterImage, ImageView>("image"));
		
    }
	
	@FXML
	private void correctFilename() {
		if (this.correctFilenameBox.isSelected())
			images.forEach(im->im.correctName());
		else
			images.forEach(im->im.restoreName());
		tableView.refresh();
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
		
}
