package sm.ui;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.json.JSONException;

import com.sun.javafx.scene.control.skin.TableHeaderRow;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import sm.JsonParser;
import sm.Main;
import sm.ShutterImageRejected;
import sm.db.SQLManager;
import sm.ui.MainController.TableKeyEventHandler;
import sm.web.ShutterProvider;

public class RejectsController implements Initializable {

	public Main app;
	
	@FXML
	private Button updateBtn;
	@FXML
	private Button displayLastBtn;
	@FXML
	private Button testBtn;
	@FXML
	private TableView<ShutterImageRejected> tableView;
	
	@FXML
	private TableColumn<ShutterImageRejected, Boolean> columnSelect;
	
	@FXML
	private TableColumn<ShutterImageRejected, String> columnName;
	
	@FXML
	private TableColumn<ShutterImageRejected, String> columnType;
	
	@FXML
	private TableColumn<ShutterImageRejected, String> columnStatus;
	
	@FXML
	private TableColumn<ShutterImageRejected, ImageView> columnPreview;
	
	@FXML
	private TableColumn<ShutterImageRejected, String> columnReason;
	
	@FXML
	private TableColumn<ShutterImageRejected, String> columnSubmitDate;
	
	@FXML
	private TableColumn<ShutterImageRejected, String> columnReviewDate;
	
	@FXML
	private TableColumn<ShutterImageRejected, String> columnDescription;
	
	@FXML
	private TableColumn<ShutterImageRejected, String> columnKeywords;
	
	@FXML
	private TableColumn<ShutterImageRejected, Integer> columnKeywordsCount;
	
	@FXML
	private Slider slider;
	
	@FXML
	private Label filesCountTxt;
	
	@FXML
	private CheckBox correctFilenameBox;
	
	@FXML
	private CheckBox showDescriptionBox;
	
	@FXML
	private CheckBox showKeywordsBox;
	
	@FXML
	private Pagination pagination; 
	
	private SQLManager sqlmanager;
	
	int rowsOnPage = 100;
	
	
	public Tab tab;
	
	public ObservableList<ShutterImageRejected> images = FXCollections.observableArrayList();
	private final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.setItems(images);
		setupTableViewColumn();
		tableView.getSelectionModel().setCellSelectionEnabled(true);
	    tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	    tableView.setOnKeyPressed(new TableKeyEventHandler());
	    
	    this.columnDescription.setVisible(false);
	    this.columnKeywords.setVisible(false);
	    
	}
	private void setupTableViewColumn() {
		columnPreview.setPrefWidth(200);
		columnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		columnSubmitDate.setCellValueFactory(new PropertyValueFactory<>("uploaded_date"));
		columnReviewDate.setCellValueFactory(new PropertyValueFactory<>("verdict_time"));
		columnReason.setCellValueFactory(new PropertyValueFactory<>("reasonsString"));
		columnName.setCellValueFactory(new PropertyValueFactory<>("original_filename"));
		columnType.setCellValueFactory(new PropertyValueFactory<>("type"));
		columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
		columnDescription.setCellFactory(tc -> {
		    TableCell<ShutterImageRejected, String> cell = new TableCell<>();
		    Text text = new Text();
		    cell.setGraphic(text);
		    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
		    text.wrappingWidthProperty().bind(columnDescription.widthProperty());
		    text.textProperty().bind(cell.itemProperty());
		    return cell ;
		});
		
		columnKeywords.setCellValueFactory(cl -> new SimpleStringProperty(String.join(", ", cl.getValue().keywords)));
		columnKeywords.setCellFactory(tc -> {
		    TableCell<ShutterImageRejected, String> cell = new TableCell<>();
		    Text text = new Text();
		    cell.setGraphic(text);
		    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
		    text.wrappingWidthProperty().bind(columnKeywords.widthProperty());
		    text.textProperty().bind(cell.itemProperty());
		    return cell ;
		});
		columnKeywordsCount.setCellValueFactory(new PropertyValueFactory<>("keywordsCount"));
		
		columnPreview.setCellValueFactory(new PropertyValueFactory<ShutterImageRejected, ImageView>("image"));
		
    }

	public void setup() {
		
	}
	
	public void loadData() {
		
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
        			if (sqlmanager==null || sqlmanager.connection.isClosed() || !sqlmanager.connection.isValid(1)) {
        				sqlmanager = new SQLManager(app);
        				getAllImages();
        			}
        			
        		} catch (SQLException e) {
        			LOGGER.severe(e.getMessage());
        			System.out.println(e.getMessage());
        		}
				
			}
		});
		t1.start();
            	
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
	
	private void updateFilesCount(int count) {
		Platform.runLater(new Runnable() {
            public void run() {
            	filesCountTxt.setText("Files count: " + count);
              }
		});
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
	
	
	@FXML
	private void getLastContent() {

		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				ShutterProvider provider =  app.mainController.getSession();
				if (provider==null || !provider.isConnection()) {
					return;
				}
				getRejectsForDates(provider, null, null);
				//checkNewRejects();
				//saveLastRejectDate();
				
			}
		});
		t1.start();

	}
	
	
	@FXML
	private void updateDatabase() {
		
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

	}
	
	
	private void getRejectsForDates(ShutterProvider provider, LocalDate from, LocalDate to) {
		int per_page = 100;
		int page = 1;
		String rejectesString = null;
		try {
		while (true) {
			rejectesString = provider.getRejects(per_page,page);
			if (rejectesString == null) {
				//setStatus("Ошибка соединения");
				app.showAlert("Ошибка соединения с сервером");
				return;
				}
			if (rejectesString.isEmpty()) break;
		    
			//writeToFile("D:\\Response.txt", rejectesString);
		    
			List<ShutterImageRejected> listFromShutter = JsonParser.parseImagesRejectedData(rejectesString);
			if (listFromShutter.isEmpty()) break;
			if (from!=null && listFromShutter.stream().allMatch(d -> d.uploadedDate.isBefore(from))) break;
			for (ShutterImageRejected im:listFromShutter) {
				if((from==null || !from.isAfter(im.uploadedDate)) && (to==null || !to.isBefore(im.uploadedDate)))
					images.add(im);
			}
			page++;
		}
		}
		catch (JSONException e) {
			if (rejectesString.contains("Redirecting to")) {
				//setStatus("Autorization error");
				app.showAlert("Неправильный sessionId. Очистите поле sessionId и повторите авторизацию");
			}
			else if (rejectesString.startsWith("{")){
				//setStatus("JSON parsing error");
				app.showAlert("Данные загружены, но приложение не смогло их правильно интерпретировать. Обратитесь к разработчику.");
			}
			else {
				int endIndex = rejectesString.length()>300 ? 300 : rejectesString.length();
				//setStatus("Unknown error");
				app.showAlert("Некорректные данные:\n" + rejectesString.substring(0, endIndex)); 
			}
			}
}

	
	private void getRejectsForDatesAndWriteDB(ShutterProvider provider, SQLManager sqlmanager, LocalDateTime from) {
		int per_page = 10;
		int page = 1;
		String rejectesString = null;
		try {
		while (true) {
			rejectesString = provider.getRejects(per_page,page);
			if (rejectesString == null) {
				app.showAlert("Ошибка соединения с сервером");
				return;
				}
			if (rejectesString.isEmpty()) break;
		    
			List<ShutterImageRejected> listFromShutter = JsonParser.parseImagesRejectedData(rejectesString);
			if (listFromShutter.isEmpty()) break;
			//if (from!=null && listFromShutter.stream().noneMatch(d -> d.verdictTime.isAfter(from))) break;
			
			List<ShutterImageRejected> toRemove = new ArrayList<ShutterImageRejected>();
			
			for (ShutterImageRejected im:listFromShutter) {
				if(sqlmanager.isInDB(im.getMedia_id())) 
					toRemove.add(im);
				else	
					im.setPreviewBytes(provider.downloadImage(im.getPreviewPath()));
				/*
				im.setPreviewBytes(provider.downloadByteArray(im.getPreviewPath()));
				try (FileOutputStream fos = new FileOutputStream(System.getProperty("user.home") + File.separator +  im.getOriginal_filename() + ".jpg")) {
					   fos.write(im.getPreviewBytes());
					}
				 */
				
			}
			listFromShutter.removeAll(toRemove);
			if (!listFromShutter.isEmpty())
				sqlmanager.insert(listFromShutter);
			else 
				break;
			page++;
		}
		}
		catch (IOException e) {
			app.showAlert("Can't get preview image");
			LOGGER.severe("Can't get preview image, error:  " +  e.getMessage());
		}
		catch (JSONException e) {
			if (rejectesString.contains("Redirecting to")) {
				//setStatus("Autorization error");
				app.showAlert("Неправильный sessionId. Очистите поле sessionId и повторите авторизацию");
			}
			else if (rejectesString.startsWith("{")){
				//setStatus("JSON parsing error");
				app.showAlert("Данные загружены, но приложение не смогло их правильно интерпретировать. Обратитесь к разработчику.");
			}
			else {
				int endIndex = rejectesString.length()>300 ? 300 : rejectesString.length();
				//setStatus("Unknown error");
				app.showAlert("Некорректные данные:\n" + rejectesString.substring(0, endIndex)); 
			}
			}
}
	
	
	@FXML
	private void testBtnClick() {
		System.out.println(sqlmanager.getLastUpdateDate().toString());
	}
	
	/*
	public void selectAllImages(int page) {
		this.images.clear();
		String sql = "SELECT * FROM " + this.sqlmanager.TABLENAME + " ORDER BY media_id DESC LIMIT " + this.rowsOnPage + " OFFSET " + this.rowsOnPage*page;
		this.images.addAll(this.sqlmanager.getImagesFromDB(sql));
		bindSlider();
	}
	*/
	
	public void selectImages(String sql, int page) {
		sql = sql + " LIMIT " +  + this.rowsOnPage + " OFFSET " + this.rowsOnPage*page;
		this.images.clear();
		this.images.addAll(this.sqlmanager.getImagesFromDB(sql));
		bindSlider();
	}
	
	private void bindSlider() {
		for (ShutterImageRejected im:images) {
			ImageView view = im.getImage();
			view.fitHeightProperty().bind(slider.valueProperty()); 
		}
	}
	
	private void setupPagination(String sqldata, int datasize) {
		//  pagination.setStyle("-fx-border-color:red;");
		 Platform.runLater(new Runnable() {
	            public void run() {
		    pagination.setPageCount(datasize/rowsOnPage + 1);
	        pagination.setPageFactory(new Callback<Integer, Node>() {
	            @Override
	            public Node call(Integer pageIndex) {
	            	 //selectAllImages(pageIndex);
	            	 selectImages(sqldata, pageIndex);
	            	 Pane node = new Pane();
	            	 node.setVisible(false);
	            	 return node;
	            }
	        });
	            }
		 });
	}
	
	
	private void disableControls() {
		Platform.runLater(new Runnable() {
            public void run() {
            	updateBtn.setDisable(true);
            	testBtn.setDisable(true);
            	displayLastBtn.setDisable(true);
            	pagination.setDisable(true);
            }
		 });
            	
	}
	
	private void enableControls() {
		Platform.runLater(new Runnable() {
            public void run() {
            	updateBtn.setDisable(false);
            	testBtn.setDisable(false);
            	displayLastBtn.setDisable(false);
            	pagination.setDisable(false);
            }
		 });
            	
	}
	
	private void getAllImages() {
		String sqldata = "SELECT * FROM " + this.sqlmanager.TABLENAME + " ORDER BY media_id DESC"; 
		String sqlcount = "SELECT COUNT(*) FROM " + sqlmanager.TABLENAME;
		loadData(sqldata, sqlcount);
	}
	
	
	private void loadData(String sqldata, String sqlcount) {
		int count = sqlmanager.getImagesCount(sqlcount);
		updateFilesCount(count);
		setupPagination(sqldata, count);
	}
	
}


