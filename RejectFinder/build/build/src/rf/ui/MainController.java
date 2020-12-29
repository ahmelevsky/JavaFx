package rf.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import org.json.JSONException;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import rf.JsonParser;
import rf.Main;
import rf.ShutterImage;
import rf.ShutterProvider;
import rf.utils.TableUtils;

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
	private Button loadBtn;
	
	@FXML
	private TextArea jsonTxt;
	
	@FXML
	private Label statusLabel;
	
	@FXML
	private CheckBox jpgOption;
	
	@FXML
	private CheckBox epsOption;
	
	@FXML
	private CheckBox uncheckByReasonBox;
	
	
    ToggleGroup groupCopyMoveOption = new ToggleGroup();
    
	ObservableList<ShutterImage> list = FXCollections.observableArrayList();
	List<ShutterImage> fullList = new ArrayList<ShutterImage>();
	private File source;
	private File destination;
	LocalDate fromDateValue;
	LocalDate toDateValue;
	 
	String badreason = "approved_on_site";
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadSessionId();
		columnName.setCellValueFactory(new PropertyValueFactory<>("original_filename"));
		columnReason.setCellValueFactory(new PropertyValueFactory<>("reasonsString"));
		columnSubmitDate.setCellValueFactory(new PropertyValueFactory<>("uploaded_date"));
		columnRejectDate.setCellValueFactory(new PropertyValueFactory<>("verdict_time"));
		setupTableViewColumn();
		tableView.setItems(list);
		//list.add(new ShutterImage(123, "VNF_FMSK_newee", new ArrayList<String>(),"dsf", "VNF_FMSK_newee", "dsf43"));
		copyOption.setToggleGroup(groupCopyMoveOption);
		copyOption.setSelected(false);
		moveOption.setToggleGroup(groupCopyMoveOption);
		moveOption.setSelected(true);
		
		epsOption.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		        fillTable();
		    }
		});
		jpgOption.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		        fillTable();
		    }
		});
		//ScrollBar scrollBarv = (ScrollBar)jsonTxt.lookup(".scroll-bar:vertical");
		//scrollBarv.setDisable(true);
		
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
		
		uncheckByReasonBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		        if (newValue) {
		        	fullList.stream().filter(im -> im.getReasonsString().contains(badreason)).forEach(im-> im.setSelected(false));
		        }
		    }
		});
		
		tableView.getSelectionModel().setCellSelectionEnabled(true);
	    tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	   // TableUtils.installCopyPasteHandler(tableView);
	    tableView.setOnKeyPressed(new TableKeyEventHandler());
	    tableView.getSelectionModel().getSelectedItems().forEach(it->it.setSelected(true));
	    tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
	        if (newSelection != null) {
	            tableView.getSelectionModel().getSelectedItems().forEach(it -> {
	            	if (it.getReasonsString().contains(badreason))
	            		uncheckByReasonBox.setSelected(false);
	            });
	        }
	    });
	    
	    String sourcePath = loadString("sourcepath");
	    		if(sourcePath!=null ){
	    			File sF = new File(sourcePath);
	    			if (sF.exists()) {
	    			 	 this.source = sF;
	    	           	 this.sourceFolder.setText(sourcePath);
	    			}
	    		}
	    		
	     String destPath = loadString("destinationpath");
		    		if(destPath!=null ){
		    			File dF = new File(destPath);
		    			if (dF.exists()) {
		    			 	 this.destination = dF;
		    	           	 this.destFolder.setText(destPath);
		    			}
		    		}
		    		
		    		
		 tableView.setRowFactory( tv -> {
		    		    TableRow<ShutterImage> row = new TableRow<>();
		    		    row.setOnMouseClicked(event -> {
		    		        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
		    		        	ShutterImage rowData = row.getItem();
		    		           if (rowData!=null) {
		    		        	   try {
									app.showImageInfo(rowData);
								} catch (IOException e) {
									showAlert("Can't load ImageInfo");
								}
		    		           }
		    		        }
		    		    });
		    		    return row ;
		    		});
	          
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
	
	@FXML
	private void getRejected() {

		if (this.sessionIdText.getText().trim().isEmpty()) {
			try {
				app.showWeb();
				String sessionId = app.webController.sessionId;
				if (sessionId!=null) {
				Platform.runLater(new Runnable() {
		            public void run() {
		            	sessionIdText.setText(sessionId);
		            }
				 });
				saveSessionId();
				}
			} catch (IOException e) {
				setStatus("Error creating vew view");
			}
			return;
		}
		
		if (fromDateValue!=null && toDateValue!=null && fromDateValue.isAfter(toDateValue)) {
			showAlert("Дата начала периода выставлена позже даты конца");
			return;
		}
		
		
		setStatus("");
		saveSessionId();
		fullList.clear();
		this.uncheckByReasonBox.setSelected(false);
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				disableControl();
				String sessionId = sessionIdText.getText().trim();
				if (sessionId.isEmpty()) {
					setStatus("Пустой sessionId");
					return;
				}
				ShutterProvider provider = new ShutterProvider(sessionId);
				if (!provider.isConnection()) {
					setStatus("Ошибка соединения");
					return;
				}
				getRejectsForDates(provider, fromDateValue, toDateValue);
				checkNewRejects();
				saveLastRejectDate();
				enableControl();
				
			}
		});
		t1.start();

	}
	@FXML
	private void loadJson() {
		if (fromDateValue!=null && toDateValue!=null && fromDateValue.isAfter(toDateValue)) {
			showAlert("Дата начала периода выставлена позже даты конца");
			return;
		}
		
		setStatus("");
		fullList.clear();
		
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				disableControl();
				loadManualRejectsForDates(fromDateValue, toDateValue);
				saveLastRejectDate();
				checkNewRejects();
				enableControl();
			}
		});
		t1.start();
	}
	
	
	private void saveLastRejectDate() {
		if (!list.isEmpty()) {
			saveString("lastrejectdate", list.stream().map(u -> u.verdictTime).max(LocalDateTime::compareTo).get().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		}
	}
	
    private void checkNewRejects() {
		String last  = loadString("lastrejectdate");
		if (last==null)
			fullList.forEach(im -> im.setSelected(true));
		else {
			LocalDateTime lastVerdictTime = LocalDateTime.parse(last,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			fullList.stream().filter(im -> im.verdictTime.isAfter(lastVerdictTime)).forEach(im-> im.setSelected(true));
		}
	}
	
    private void fillTable() {
    	list.clear();
    	if (jpgOption.isSelected() && epsOption.isSelected())
    		list.addAll(fullList);
    	else if (jpgOption.isSelected())
    		list.addAll(fullList.stream().filter(f -> f.extenstion.startsWith("jp")).collect(Collectors.toList()));
    	else if (epsOption.isSelected())
    		list.addAll(fullList.stream().filter(f -> f.extenstion.contentEquals("eps")).collect(Collectors.toList()));
    	correctFilename();
    	setStatus("Количество реджектов за выбранный период: " + list.size());
    }
    
	private void getRejectsForDates(ShutterProvider provider, LocalDate from, LocalDate to) {
		int per_page = 100;
		int page = 1;
		String rejectesString = null;
		try {
		while (true) {
			rejectesString = provider.getRejects(per_page,page);
			if (rejectesString == null) {
				setStatus("Ошибка соединения");
				showAlert("Ошибка соединения с сервером");
				return;
				}
			if (rejectesString.isEmpty()) break;
		    
			//writeToFile("D:\\Response.txt", rejectesString);
		    
			List<ShutterImage> listFromShutter = JsonParser.parseImagesData(rejectesString);
			if (listFromShutter.isEmpty()) break;
			if (from!=null && listFromShutter.stream().allMatch(d -> d.uploadedDate.isBefore(from))) break;
			for (ShutterImage im:listFromShutter) {
				if((from==null || !from.isAfter(im.uploadedDate)) && (to==null || !to.isBefore(im.uploadedDate)))
					fullList.add(im);
			}
			page++;
		}
		fillTable();
		}
		catch (JSONException e) {
			if (rejectesString.contains("Redirecting to")) {
				setStatus("Autorization error");
				showAlert("Неправильный sessionId. Очистите поле sessionId и повторите авторизацию");
			}
			else if (rejectesString.startsWith("{")){
				setStatus("JSON parsing error");
				showAlert("Данные загружены, но приложение не смогло их правильно интерпретировать. Обратитесь к разработчику.");
			}
			else {
				int endIndex = rejectesString.length()>300 ? 300 : rejectesString.length();
				setStatus("Unknown error");
				showAlert("Некорректные данные:\n" + rejectesString.substring(0, endIndex)); 
			}
			}
	}
	
	
	private void loadManualRejectsForDates(LocalDate from, LocalDate to) {
		String json = jsonTxt.getText().trim();
		if (json.isEmpty())
			return;
		try {
		
			List<ShutterImage> listFromShutter = JsonParser.parseImagesData(json);
			for (ShutterImage im:listFromShutter) {
				if((from==null || !from.isAfter(im.uploadedDate)) && (to==null || !to.isBefore(im.uploadedDate)))
					fullList.add(im);
			}
		fillTable();
		}
		catch (JSONException e) {
			setStatus("Неправильный формат");
			showAlert("Неправильный формат данных. Вставьте корректные данные в текстовое поле для загрузки");
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
	
	
	@FXML
    private void selectDestPath(){
		  DirectoryChooser directoryChooser = new DirectoryChooser(); 
          directoryChooser.setTitle("Выберите папку для сохранения реджектов");

          File file = directoryChooser.showDialog(app.getPrimaryStage());

         if(file!=null){
        	 this.destination = file;
        	 this.destFolder.setText(file.getAbsolutePath());
        	 saveString("destinationpath", file.getAbsolutePath());
         }
	}
	
	@FXML
    private void selectSourcePath(){
		  DirectoryChooser directoryChooser = new DirectoryChooser(); 
          directoryChooser.setTitle("Выберите папку, из которой вы загружали файлы");
		/*
		 * File selected = new File(this.folderPath.getText()); if (selected.exists())
		 * directoryChooser.setInitialDirectory(selected.getParentFile());
		 */

          File file = directoryChooser.showDialog(app.getPrimaryStage());

         if(file!=null){
        	 this.source = file;
        	 this.sourceFolder.setText(file.getAbsolutePath());
        	 saveString("sourcepath", file.getAbsolutePath());
         }
	}
	
	@FXML
    private void moveFiles_backup() {
		setStatus("");
		int moved = 0;
		if (source==null || !source.exists() || !source.isDirectory() ||
				destination==null || !destination.exists() || !destination.isDirectory())
			showAlert("Убедитесь что исходная и конечная папки выбраны и существуют");
		
		else if (source.getAbsolutePath().equals(destination.getAbsolutePath()))
			showAlert("Исходная и конечная папки не должны совпадать");
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
				setStatus("Ошибка копирования");
				showAlert("Ошибка копирования. Перенесено " + moved + " файлов");
		 }
		}
		showMessage("Перенесено " + moved + " файлов");
	}
	
	
	@FXML
    private void moveFiles() {
		setStatus("");
		int moved = 0;
		if (source==null || !source.exists() || !source.isDirectory() ||
				destination==null || !destination.exists() || !destination.isDirectory())
			showAlert("Убедитесь что исходная и конечная папки выбраны и существуют");
		
		else if (source.getAbsolutePath().equals(destination.getAbsolutePath()))
			showAlert("Исходная и конечная папки не должны совпадать");
		else {
			try {
			for (ShutterImage image:list) {
				if (image.getSelected().get()) {
					Path file = findFile(image.getOriginal_filename());
					if (file==null) continue;
					Path jpg = null;
					if (image.extenstion.equals("eps"))
							jpg = findJpeg(file.toFile().getAbsolutePath());
					if (this.moveOption.isSelected()) {
							Files.move(file, Paths.get(destination.getAbsolutePath() + File.separator + file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
						if (jpg!=null)
							Files.move(jpg, Paths.get(destination.getAbsolutePath() + File.separator + jpg.getFileName()), StandardCopyOption.REPLACE_EXISTING);
					}
					
			    	else {
			    		   Files.copy(file, Paths.get(destination.getAbsolutePath() + File.separator + file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
			    		if (jpg!=null)   
			    			Files.copy(jpg, Paths.get(destination.getAbsolutePath() + File.separator + jpg.getFileName()), StandardCopyOption.REPLACE_EXISTING);
			    	}
						
					moved++;
				}
			}
			} catch (IOException e) {
				setStatus("Ошибка копирования");
				showAlert("Ошибка копирования. Перенесено " + moved + " файлов");
		 }
		}
		showMessage("Перенесено " + moved + " файлов");
	}
	
	private Path findJpeg(String epsFullPath) {
		File jpgFile = new File(epsFullPath.substring(0, epsFullPath.lastIndexOf(".")) + ".jpg");
		if (jpgFile.exists()) {
			return Paths.get(jpgFile.getAbsolutePath());
		}
		else return null;
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
 
}
