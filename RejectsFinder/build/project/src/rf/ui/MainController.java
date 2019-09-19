package rf.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import rf.JsonParser;
import rf.ShutterImage;
import rf.ShutterProvider;
import rf.Main;

public class MainController implements Initializable {

	public Main app;
	
	@FXML
	private Button getDataBtn;
	
	@FXML
	private TextField sessionIdText;
	
	@FXML
	private TableView<ShutterImage> tableView;
	
	@FXML
	private TableColumn<ShutterImage, Boolean> columnSelect;
	
	@FXML
	private TableColumn<ShutterImage, String> columnName;
	
	@FXML
	private TableColumn<ShutterImage, String> columnReason;
	
	@FXML
	private TableColumn<ShutterImage, String> columnSubmitDate;
	
	@FXML
	private TableColumn<ShutterImage, String> columnRejectDate;
	
	@FXML
	private CheckBox selectAllBox;
	
	@FXML
	private CheckBox correctNames;
	
	
	@FXML
	private DatePicker fromDate;
	
	@FXML
	private DatePicker toDate;
	
	@FXML
	private RadioButton copyOption;
	
	@FXML
	private RadioButton moveOption;
	
	@FXML
	private Hyperlink sourceFolder;
	
	@FXML
	private Hyperlink destFolder;
	
	@FXML
	private Button moveBtn;
	
	@FXML
	private Label statusLabel;
    ToggleGroup group = new ToggleGroup();
	
	ObservableList<ShutterImage> list = FXCollections.observableArrayList();
	
	private File source;
	private File destination;
	LocalDate fromDateValue;
	LocalDate toDateValue;
	 
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadSessionId();
		columnName.setCellValueFactory(new PropertyValueFactory<>("original_filename"));
		columnReason.setCellValueFactory(new PropertyValueFactory<>("reasonsString"));
		columnSubmitDate.setCellValueFactory(new PropertyValueFactory<>("uploaded_date"));
		columnRejectDate.setCellValueFactory(new PropertyValueFactory<>("verdict_time"));
		setupTableViewColumn();
		tableView.setItems(list);
		tableView.setEditable(true);
		//list.add(new ShutterImage(123, "VNF_FMSK_newee", new ArrayList<String>(),"dsf", "VNF_FMSK_newee", "dsf43"));
		copyOption.setToggleGroup(group);
		copyOption.setSelected(true);
		moveOption.setToggleGroup(group);
		
		
		fromDate.setOnAction(new EventHandler() {
		     public void handle(Event t) {
		         fromDateValue = fromDate.getValue();
		     }
		 });
		
		toDate.setOnAction(new EventHandler() {
		     public void handle(Event t) {
		         toDateValue = toDate.getValue();
		     }
		 });
	}
	
	
	private void setupTableViewColumn() {
		columnSelect.setCellFactory(column -> new CheckBoxTableCell<ShutterImage, Boolean>());
		columnSelect.setCellValueFactory(cellData -> {
            ShutterImage cellValue = cellData.getValue();
            BooleanProperty property = cellValue.getSelected();
            // Add listener to handler change
            property.addListener((observable, oldValue, newValue) -> cellValue.setSelected(newValue));
            return property;
        });
    }
	
	@FXML
	private void getRejected() {
		
		if (fromDateValue!=null && toDateValue!=null && fromDateValue.isAfter(toDateValue)) {
			showAlert("���� ������ ������� ���������� ����� ���� �����");
			return;
		}
		
		setStatus("");
		saveSessionId();
		list.clear();
		List<ShutterImage> rejectsImages = new ArrayList<ShutterImage>();
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				disableControl();
				String sessionId = sessionIdText.getText().trim();
				if (sessionId.isEmpty()) {
					setStatus("������ sessionId");
					return;
				}
				ShutterProvider provider = new ShutterProvider(sessionId);
				if (!provider.isConnection()) {
					setStatus("������ �����������");
					return;
				}
				getRejectsForDates(provider, fromDateValue, toDateValue, 60);
				
				
				/*
				 * String rejectesString = provider.getRejects(100,1);
				 * writeToFile("D:\\Shutter.html", rejectesString); List<ShutterImage>
				 * listFromShutter = JsonParser.parseImagesData(rejectesString);
				 * list.addAll(listFromShutter);
				 */
				
				
				enableControl();
				setStatus("���������� ��������� �� ��������� ������: " + list.size());
			}
		});
		t1.start();

	}
	
	
	private void getRejectsForDates(ShutterProvider provider, LocalDate from, LocalDate to, int timeoutSeconds) {
		int per_page = 100;
		int page = 1;
		long before = new Date().getTime();
		while (before + timeoutSeconds*1000 >  new Date().getTime()) {
			String rejectesString = provider.getRejects(per_page,page);
			if (rejectesString.isEmpty()) break;
			List<ShutterImage> listFromShutter = JsonParser.parseImagesData(rejectesString);
			if (listFromShutter.isEmpty()) break;
			if (from!=null && listFromShutter.stream().allMatch(d -> d.uploadedDate.isBefore(from))) break;
			for (ShutterImage im:listFromShutter) {
				if((from==null || !from.isAfter(im.uploadedDate)) && (to==null || !to.isBefore(im.uploadedDate)))
					list.add(im);
			}
			page++;
		}
	}
	
	
	 
	  private void writeToFile(String fileName, String data) {
		 File file = new File(fileName);
		 if (file.exists()) file.delete();
		 try {
			file.createNewFile();
		} catch (IOException e1) {
		}
		  try {
			PrintWriter out = new PrintWriter(fileName);
			out.write(data);
			out.close();
		} catch (FileNotFoundException e) {
		}
	  }
	
	/*
	 * private void printRejected(List<String> list) { rejectedListText.clear();
	 * list.forEach(item->rejectedListText.appendText(item + "\n")); }
	 */
	  
	private void setStatus(String string) {
		Platform.runLater(new Runnable() {
            public void run() {
            	statusLabel.setText("Status: " + string);
            }
		 });
		
	}
	
	
	private void disableControl() {
		Platform.runLater(new Runnable() {
            public void run() {
            	 getDataBtn.setDisable(true);
            }
		 });
	}
	
private void enableControl() {
	Platform.runLater(new Runnable() {
        public void run() {
        	 getDataBtn.setDisable(false);
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
	
	@FXML
	private void selectAllClick() {
		list.forEach(im->im.setSelected(this.selectAllBox.isSelected()));
	}
	

	@FXML
	private void correctFilename() {
		if (this.correctNames.isSelected())
			list.forEach(im->im.correctName());
		else
			list.forEach(im->im.restoreName());
		tableView.refresh();
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
	
	
	@FXML
    private void selectDestPath(){
		  DirectoryChooser directoryChooser = new DirectoryChooser(); 
          directoryChooser.setTitle("�������� ����� ��� ���������� ���������");

          File file = directoryChooser.showDialog(app.getPrimaryStage());

         if(file!=null){
        	 this.destination = file;
        	 this.destFolder.setText(file.getAbsolutePath());
         }
	}
	
	@FXML
    private void selectSourcePath(){
		  DirectoryChooser directoryChooser = new DirectoryChooser(); 
          directoryChooser.setTitle("�������� �����, �� ������� �� ��������� �����");
		/*
		 * File selected = new File(this.folderPath.getText()); if (selected.exists())
		 * directoryChooser.setInitialDirectory(selected.getParentFile());
		 */

          File file = directoryChooser.showDialog(app.getPrimaryStage());

         if(file!=null){
        	 this.source = file;
        	 this.sourceFolder.setText(file.getAbsolutePath());
         }
	}
	
	@FXML
    private void moveFiles() {
		setStatus("");
		int moved = 0;
		if (source==null || !source.exists() || !source.isDirectory() ||
				destination==null || !destination.exists() || !destination.isDirectory())
			showAlert("��������� ��� �������� � �������� ����� ������� � ����������");
		
		else if (source.getAbsolutePath().equals(destination.getAbsolutePath()))
			showAlert("�������� � �������� ����� �� ������ ���������");
		else {
			try {
			for (ShutterImage image:list) {
				if (image.getSelected().get()) {
					Path file = findFile(image.getOriginal_filename());
					if (file==null) continue;
					
					if (this.moveOption.isSelected())
 						Files.move(file, Paths.get(destination.getAbsolutePath() + File.separator + file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
			    	else
						   Files.copy(file, Paths.get(destination.getAbsolutePath() + File.separator + file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
					moved++;
				}
			}
			} catch (IOException e) {
				setStatus("������ �����������");
				showAlert("������ �����������. ���������� " + moved + " ������");
		 }
		}
		showMessage("���������� " + moved + " ������");
	}
	
	private Path findFile(String name) {
		try {
			return Files.walk(Paths.get(source.getAbsolutePath()))
			.filter(Files::isRegularFile)
			.filter((f)->f.getFileName().toString().equals(name)).findFirst().orElse(null);
		} catch (IOException e) {
			return null;
		}
	}
}
