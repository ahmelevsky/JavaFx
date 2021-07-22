package sm.ui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import org.json.JSONException;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import sm.JsonParser;
import sm.web.ShutterProvider;
import sm.Main;
import sm.ShutterImageRejected;
import sm.ui.MainController.TableKeyEventHandler;

public class RejectsController implements Initializable {

	public Main app;
	
	@FXML
	private Button updateBtn;
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
	
	
	public ObservableList<ShutterImageRejected> images = FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.setItems(images);
		setupTableViewColumn();
		tableView.getSelectionModel().setCellSelectionEnabled(true);
	    tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	    tableView.setOnKeyPressed(new TableKeyEventHandler());
	    tableView.getSelectionModel().getSelectedItems().forEach(it->it.setSelected(true));
	    
	    images.addListener(new ListChangeListener<ShutterImageRejected>() {
			@Override
			public void onChanged(Change<? extends ShutterImageRejected> c) {
				
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
		
		//TableHeaderRow header = (TableHeaderRow) tableView.lookup("TableHeaderRow");
		//header.setMouseTransparent(true);
    }

	public void setup() {
		
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
	
	@FXML
	private void updateDatabase() {
		
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				ShutterProvider provider =  app.mainController.getSession();
				if (!provider.isConnection()) {
					return;
				}
				getRejectsForDates(provider, null, null);
				//checkNewRejects();
				//saveLastRejectDate();
				
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
	//	fillTable();
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

}
