package am.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import com.sun.javafx.charts.Legend;

import am.DialogConditions;
import am.Main;
import am.SetData;
import am.ShutterImage;
import am.db.SQLManager;
import am.ui.MainController.TableKeyEventHandler;
import am.utils.NumberDateToStringConverter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DateTimeStringConverter;

public class SetController implements Initializable {

	
	public Main app;
	
	public Tab tab;
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
	
	@FXML
	private TableView<SetData> tableView;
	
	@FXML
	private TableColumn<SetData, String> columnName;
	
	@FXML
	private TableColumn<SetData, String> columnStartDate;
	
	@FXML
	private TableColumn<SetData, String> columnLastDate;
	
	@FXML
	private TableColumn<SetData, Integer> columnImagesCount;
	
	@FXML
	private TableColumn<SetData, Integer> columnDownloads;
	
	@FXML
	private TableColumn<SetData, Integer> columnSoldImagesCount;
	
	@FXML
	private TableColumn<SetData, Double> columnSoldPercent;
	
	@FXML
	private TableColumn<SetData, Double> columnSum;
	
	@FXML
	private TableColumn<SetData, Double> columnRPI;
	
	@FXML
	private TableColumn<SetData, Double> columnDPI;
	
	@FXML
	private TableColumn<SetData, Double> columnRPD;
	
	@FXML
	private MLineChart<String, Number> lineChart;
	
	@FXML
	private TableColumn<SetData, Double> columnUploadMonths;
	
	@FXML
	private TableColumn<SetData, Double> columnImagesMonths;
	
	@FXML
	private ProgressBar progress;
	
	@FXML
	private Label waitLabel;
	
	@FXML
	private HBox chartBox;
	
	@FXML
	private Button refreshButton;
	
	@FXML
	private Button deleteButton;
	
	@FXML
	private Button applyButton;
	
	@FXML
	private Button exportButton;
	
	@FXML
	private MenuBar menuBar;
	
	@FXML
	private Menu menuOperations;
	
	@FXML
	private MenuItem refreshItem;
	@FXML
	private MenuItem exportItem;
	@FXML
	private MenuItem deleteItem;
	@FXML
	private MenuItem saveToFileItem;
	@FXML
	private MenuItem loadFromFileItem;
	
	
	@FXML
	private Button loadSetDataButton;
	
	@FXML
	private CheckBox detailedBox;
	
	@FXML
	private CheckBox autoclearBox;
	
	@FXML
	private CheckBox allBox;
	
	@FXML
	private CheckBox vectorBox;
	
	@FXML
	private CheckBox rasterBox;
	
	
	ToggleGroup graphTypeSelectGroup = new ToggleGroup();
	
	@FXML
	private RadioButton downloadsRadio;
	
	@FXML
	private RadioButton earningsRadio;
	
	@FXML
	private RadioButton soldDivDownloadsRadio;
	
	@FXML
	private Button clearGraphsBtn;
	
	
	public ObservableList<SetData> sets = FXCollections.observableArrayList();
	public SortedList<SetData> setsSorted = new SortedList<SetData>(sets, SetData.getComparator());
	public List<String> hiddenSets = new ArrayList<String>();
	private SQLManager sqlManager;
	public Thread loadThread;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.setItems(setsSorted);
		setupTableViewColumn();
		//tableView.getSelectionModel().setCellSelectionEnabled(true);
	    //tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	    tableView.setOnKeyPressed(new TableKeyEventHandler());
	    
	    tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
	        if (newSelection != null) {
	        	//drawLineChart(newSelection.getName());
	        }
	    });
	    
	    
	    tableView.setOnKeyPressed( new EventHandler<Event>()
	    {
	      @Override
	      public void handle( final Event keyEvent )
	      {
	        final SetData selectedItem = tableView.getSelectionModel().getSelectedItem();

	        if ( selectedItem != null )
	        {
	          if (((KeyEvent) keyEvent).getCode().equals( KeyCode.DELETE ) )
	          {
	        	  deleteSetButton();
	          }

	           //... other keyevents
	        }
	      }

	
	    } );
	    

	    
	    tableView.setRowFactory(
	    	    new Callback<TableView<SetData>, TableRow<SetData>>() {
	    	        @Override
	    	        public TableRow<SetData> call(TableView<SetData> tableView) {
	    	            final TableRow<SetData> row = new TableRow<>();
	    	            final ContextMenu rowMenu = new ContextMenu();
	    	            MenuItem editItem = new MenuItem("Join Sets");
	    	            editItem.setOnAction(new EventHandler<ActionEvent>() {
	    	        
	    	                @Override
	    	                public void handle(ActionEvent event) {
	    	                	ArrayList<SetData> p = new ArrayList<>(tableView.getSelectionModel().getSelectedItems());
	    	                	if (p.size()<2) {
	    	                		app.showAlert("Please select multiple sets to join");
	    	                		return;
	    	                	}
	    	                	List<String> conditionsList = new ArrayList<String>();
	    	                	for (SetData res : p) {     
	    	                	    String sql = sqlManager.getSetSQL(res.getName());
	    	                	    if (!sql.contains("WHERE")) {
	    	                	    	app.showAlert("Can't joint sets, one of the selected sets includes ALL images (no filter)");
	    	                	    	return;
	    	                	    }
	    	                	    conditionsList.add("(" + sql.split("WHERE")[1].trim() + ")");
	    	                	}
	    	                	TextInputDialog td = new TextInputDialog(p.get(0).getName());
	    	                	td.setHeaderText("Enter new set name"); 
	    	                	  
	    	                	//td.setContentText("Set:");
	    	                	td.getEditor().textProperty().addListener(new ChangeListener<String>() {
	    	            	        @Override
	    	            	        public void changed(ObservableValue<? extends String> observable, String oldValue, 
	    	            	            String newValue) {
	    	            	        	if (!newValue.matches("[a-zA-Z0-9-_]+")) {
	    	            	        		td.getEditor().setText(newValue.replaceAll("[^a-zA-Z0-9-_]", ""));
	    	            	            }
	    	            	        }
	    	            	    });
	    	                	td.showAndWait();
	    	                	String viewName = td.getEditor().getText().trim();
	    	                	if (!viewName.isEmpty()) {
	    	                		String sql = String.join(" OR ", conditionsList);
	    	                		createSet(viewName, sql);
	    	                		SetData set = loadSetFromView(viewName);
	    	                		tableView.getItems().add(tableView.getSelectionModel().getSelectedIndex(), set);
	    	                	}
	    	                }
	    	            });
	    	            
	    	            
	    	            MenuItem removeItem = new MenuItem("Hide");
	    	            removeItem.setOnAction(new EventHandler<ActionEvent>() {
	    	        
	    	                @Override
	    	                public void handle(ActionEvent event) {
	    	                	sets.remove(row.getItem());
	    	                	hiddenSets.add(row.getItem().getName());
	    	                }
	    	            });
	    	            
	    	            MenuItem refreshItem = new MenuItem("Refresh");
	    	            refreshItem.setOnAction(new EventHandler<ActionEvent>() {
	    	        
	    	                @Override
	    	                public void handle(ActionEvent event) {
	    	                	SetData set = loadSetFromView(row.getItem().getName());
	    	                	sets.remove(row.getItem());
	    	                	sets.add(set);
	    	                }
	    	            });
	    	            
	    	            MenuItem renameItem = new MenuItem("Rename");
	    	            renameItem.setOnAction(new EventHandler<ActionEvent>() {
	    	        
	    	                @Override
	    	                public void handle(ActionEvent event) {
	    	                	//tableView.getItems().remove(row.getItem());
	    	                	SetData set = row.getItem();
	    	                	String name = set.getName();
	    	                	String sql = sqlManager.getSetSQL(name);
	    	                	
	    	                	TextInputDialog td = new TextInputDialog(name);
	    	                	td.setHeaderText("Enter new set name"); 
	    	                	td.showAndWait();
	    	                	String newName = td.getEditor().getText().trim();  
	    	                	
	    	                	if (!sqlManager.deleteSet("DROP VIEW " + name)) {
	    	                		app.showAlert("Can't drop rename Set " + name);
	    	                		return;
	    	                	}
	    	                	if (!sqlManager.createSet(sql.replaceFirst(name, newName))){
	    	                		app.showAlert("Can't rename Set " + name);
	    	                		return;
	    	                	}
	    	                	set = loadSetFromView(newName);
	    	                	sets.add(set);
	    	                	//tableView.getItems().add(tableView.getSelectionModel().getSelectedIndex(), set);
	    	                	sets.remove(row.getItem());
	    	                }
	    	            });
	    	            
	    	            
	    	            rowMenu.getItems().addAll(editItem,renameItem,removeItem,refreshItem);
	    	            
	    	            // only display context menu for non-empty rows:
	    	            row.contextMenuProperty().bind(
	    	              Bindings.when(row.emptyProperty())
	    	              .then((ContextMenu) null)
	    	              .otherwise(rowMenu));
	    	            
	    	            
	    	            
	    	            row.setOnDragDetected(event -> {
	    	                if (! row.isEmpty()) {
	    	                    Integer index = row.getIndex();
	    	                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
	    	                    db.setDragView(row.snapshot(null, null));
	    	                    ClipboardContent cc = new ClipboardContent();
	    	                    cc.put(SERIALIZED_MIME_TYPE, index);
	    	                    db.setContent(cc);
	    	                    event.consume();
	    	                }
	    	            });

	    	            row.setOnDragOver(event -> {
	    	                Dragboard db = event.getDragboard();
	    	                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
	    	                    if (row.getIndex() != ((Integer)db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
	    	                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
	    	                        event.consume();
	    	                    }
	    	                }
	    	            });

	    	            row.setOnDragDropped(event -> {
	    	                Dragboard db = event.getDragboard();
	    	                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
	    	                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
	    	                    SetData draggedPerson = tableView.getItems().remove(draggedIndex);

	    	                    int dropIndex ; 

	    	                    if (row.isEmpty()) {
	    	                        dropIndex = tableView.getItems().size() ;
	    	                    } else {
	    	                        dropIndex = row.getIndex();
	    	                    }

	    	                    tableView.getItems().add(dropIndex, draggedPerson);

	    	                    event.setDropCompleted(true);
	    	                    tableView.getSelectionModel().select(dropIndex);
	    	                    event.consume();
	    	                }
	    	            });

	    	            
	    	            row.setOnMouseClicked(event -> {
	    	                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
	    	                    SetData rowData = row.getItem();
	    	                    String sql = sqlManager.getSetSQL(rowData.getName());
	    	                    if (!sql.contains("WHERE"))
	    	                    	  app.showAlertOK("no filter");
	    	                    else {
	    	                        sql = sql.split("WHERE")[1].trim();
	    	                    	app.showAlertOK(sql);
	    	                    }
	    	                }
	    	            });
	    	            
	    	            return row;
	    	    }
	    	});
	    
	    tableView.getSelectionModel().setSelectionMode(
	    	    SelectionMode.MULTIPLE
	    	);
	    
	 
	}
	
	

	
	
	public void setup() {
		this.sqlManager = app.sqlManager;
		this.downloadsRadio.setToggleGroup(graphTypeSelectGroup);
		this.earningsRadio.setToggleGroup(graphTypeSelectGroup);
	    this.downloadsRadio.setSelected(true);
		this.soldDivDownloadsRadio.setToggleGroup(graphTypeSelectGroup);
	}
	
	
	public void loadData() {
		
		 Runnable r = new Runnable() {
	         public void run() {
	        	 List<String> setsNames = sets.stream().map(urEntity -> urEntity.getName()).collect(Collectors.toList());
	        	 List<String> views = sqlManager.getSetsNames();
	        	 Collections.sort(views,  String.CASE_INSENSITIVE_ORDER);
	        	 progress.setProgress(0.0);
	        	 double p = 0.0;
	        	 waitLabel.setVisible(true);
	        	 //sets.clear();
	        	 
	        	 
	        	 
	     		for (String view:views) {
	     			if (loadThread.isInterrupted()) {
	     				return;
	     			}
	     			if (hiddenSets.contains(view))
	     				continue;
	     			if (!setsNames.contains(view))
	     				sets.add(loadSetFromView(view));
	     			p++;
	     			progress.setProgress(p/views.size());
	     			
	     			//if (sets.size()>0)
	     				// break;
	     		}
	     		Comparator<SetData> setDataComparator = Comparator.comparing(SetData::getName);
	     		if (!sets.isEmpty())
	     			sets.sort(setDataComparator);
	     		waitLabel.setVisible(false);
	         }
	     };
	    
	    loadThread = new Thread(r);
	    loadThread.setDaemon(true);
	    loadThread.start();
	    	
	}
	
	
	
	
	private SetData loadSetFromView(String view) {
		SetData set = new SetData(view);
			String startUpload = sqlManager.executeRequestForString("SELECT MIN(uploaded_date) FROM " + view + ";");
			set.setStartUpload(startUpload);
			String lastUpload = sqlManager.executeRequestForString("SELECT MAX(uploaded_date) FROM " + view + ";");
			set.setLastUpload(lastUpload);
			int imagesCount = sqlManager.executeRequestForInt("SELECT COUNT(*) FROM " + view + ";");
			set.setImagesCount(imagesCount);
			int downloads = sqlManager.executeRequestForInt("SELECT SUM(downloads) FROM " + view + ";");
			set.setDownloads(downloads);
			int soldImagesCount = sqlManager.executeRequestForInt("SELECT COUNT(*) FROM " + view + " WHERE downloads>0;");
			set.setSoldImagesCount(soldImagesCount);
			double val = soldImagesCount/(double)imagesCount*10000;
			set.setSoldPercent( Math.round(val)/100.0);
			double total = sqlManager.executeRequestForDouble("SELECT SUM(earnings) FROM " + view + " ;");
			set.setSum(Math.round(total*100.0)/100.0);
			set.setRpi( Math.round(total*100.0/imagesCount)/100.0);
			set.setDpi( Math.round(downloads*10000.0/imagesCount)/100.0);
			set.setRpd(Math.round(total*100.0/downloads)/100.0);
			if (startUpload!=null && lastUpload!=null) {
				double uploadMonths =  Math.round(100.0*ChronoUnit.DAYS.between(parseDate(startUpload), parseDate(lastUpload))/30.0)/100.0;
				set.setUploadMonths(uploadMonths);
				int imagesMonths =  (int)Math.round(imagesCount/uploadMonths);
 			set.setImagesMonths(imagesMonths);
			}
		return set;
	}
	
	
	private LocalDate parseDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return LocalDate.parse(date, formatter);
	}
	
	
	

	
	private void setupTableViewColumn() {
		columnName.setPrefWidth(200);
		columnStartDate.setCellValueFactory(new PropertyValueFactory<>("startUpload"));
		columnLastDate.setCellValueFactory(new PropertyValueFactory<>("lastUpload"));
		columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		columnImagesCount.setCellValueFactory(new PropertyValueFactory<>("imagesCount"));
		columnDownloads.setCellValueFactory(new PropertyValueFactory<>("downloads"));
		columnSoldImagesCount.setCellValueFactory(new PropertyValueFactory<>("soldImagesCount"));
		columnSoldPercent.setCellValueFactory(new PropertyValueFactory<>("soldPercent"));
		columnSum.setCellValueFactory(new PropertyValueFactory<>("sum"));
		columnRPI.setCellValueFactory(new PropertyValueFactory<>("rpi"));
		columnDPI.setCellValueFactory(new PropertyValueFactory<>("dpi"));
		columnRPD.setCellValueFactory(new PropertyValueFactory<>("rpd"));
		columnUploadMonths.setCellValueFactory(new PropertyValueFactory<>("uploadMonths"));
		columnImagesMonths.setCellValueFactory(new PropertyValueFactory<>("imagesMonths"));
    }
	
	
	@FXML
	private void deleteSetButton() {
	    SetData selectedItem = tableView.getSelectionModel().getSelectedItem();
		Alert alert = new Alert(AlertType.CONFIRMATION, "Delete " + selectedItem.getName() + " ?", ButtonType.YES, ButtonType.NO);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.YES) {
			    sets.remove(selectedItem);
		        deleteSet(selectedItem.getName());
		        
		}
	   
	}

	private void deleteSet(String name) {
		if (!sqlManager.getSetsNames().contains(name))
			app.showAlert("Can't find set");
		else {
			if (!sqlManager.deleteSet("DROP VIEW " + name))
				app.showAlert("Error deletng set");
		}
	}
	
	
	@FXML
	private void aplySet() {
	    SetData selectedItem = tableView.getSelectionModel().getSelectedItem();
	    if (selectedItem == null) return;
	    String sql = sqlManager.getSetSQL(selectedItem.getName());
	    System.out.println(sql);
	    app.tabsController.openTab(app.mainController.tab);
	   // app.mainController.restoreFilter(sql);
	}
	
	
	@FXML
	private void refreshSets() {
	   loadThread.interrupt();
	   while (loadThread.isAlive()) {
		   try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	   }
	   hiddenSets.clear();
	   sets.clear();
	   loadData();
	}

	@FXML
	private void exportToExcel() {

        Workbook workbook = new HSSFWorkbook();
        Sheet spreadsheet = workbook.createSheet("Statistics");

        
        // Create header CellStyle
        Font headerFont = workbook.createFont();
        headerFont.setColor(IndexedColors.WHITE.index);
        CellStyle headerCellStyle = spreadsheet.getWorkbook().createCellStyle();
        // fill foreground color ...
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.index);
        // and solid fill pattern produces solid grey cell fill
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setFont(headerFont);
        
        Row row = spreadsheet.createRow(0);

        for (int j = 0; j < tableView.getColumns().size(); j++) {
            Cell cell = row.createCell(j);
            cell.setCellValue(tableView.getColumns().get(j).getText());
            cell.setCellStyle(headerCellStyle);
            spreadsheet.autoSizeColumn(j);
        }

        for (int i = 0; i < tableView.getItems().size(); i++) {
            row = spreadsheet.createRow(i + 1);
            for (int j = 0; j < tableView.getColumns().size(); j++) {
                if(tableView.getColumns().get(j).getCellData(i) != null) { 
                    row.createCell(j).setCellValue(tableView.getColumns().get(j).getCellData(i).toString()); 
                }
                else {
                    row.createCell(j).setCellValue("");
                }   
            }
        }
        
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xls)", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter);
        
        
      //Set to user directory or go to default if cannot access
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if(userDirectory.canRead()) {
        	   fileChooser.setInitialDirectory(userDirectory);
        }
     
        
        File file = fileChooser.showSaveDialog(app.getPrimaryStage());
        if (file != null) {
        	 try   {
        	        FileOutputStream fileOut = new FileOutputStream(file);
        	    
        				workbook.write(fileOut);
        				   fileOut.close();
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        }
       
     
	}
	
	
	
	private void createSet(String name, String conditions) {
		if (sqlManager.getSetsNames().contains(name)) {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Set with this name already exist, rewrite?", ButtonType.YES, ButtonType.NO);
			alert.showAndWait();

			if (alert.getResult() == ButtonType.YES) {
				if (!sqlManager.deleteSet("DROP VIEW " + name)) {
            		app.showAlert("Can't drop rename Set " + name);
            		return;
            	}
			}
			else 
				return;
		}
	
			if (!sqlManager.createSet("CREATE VIEW " + name + " AS SELECT * FROM " + this.sqlManager.IMAGESTABLE + " LEFT JOIN " + this.sqlManager.IMAGESTOPTABLE
                    + " on " + this.sqlManager.IMAGESTOPTABLE + ".media_id = " + this.sqlManager.IMAGESTABLE + ".media_id WHERE " + conditions))
				app.showAlert("Error creating new set");
	}
	
	
	@FXML
	private void menuRefreshClicked() {
		refreshSets();
	}

	
	@FXML
	private void menuExportClicked() {
		exportToExcel();
	}
	
	@FXML
	private void menuDeleteClicked() {
		deleteSetButton();
	}
	
	@FXML
	private void menuSaveToFileClicked() {
		
		    FileChooser fileChooser = new FileChooser();
	        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Script files (*.sql)", "*.sql");
	        fileChooser.getExtensionFilters().add(extFilter);
	        
	        String userDirectoryString = System.getProperty("user.home");
	        File userDirectory = new File(userDirectoryString);
	        if(userDirectory.canRead()) {
	        	   fileChooser.setInitialDirectory(userDirectory);
	        }
	        
	        File file = fileChooser.showSaveDialog(app.getPrimaryStage());
	        if (file != null) {
	        	List<String> sqls = new ArrayList<String>();
	    		for (SetData rowData : tableView.getItems()) {
	    			String sql = sqlManager.getSetSQL(rowData.getName());
	    			sqls.add(sql);
	    		}
	    		try {
					FileUtils.writeLines(file, "UTF-8", sqls);
				} catch (IOException e) {
					e.printStackTrace();
					LOGGER.severe(e.getMessage());
				}
	        }
	}
	
	@FXML
	private void menuLoadFromFileClicked() {
		
		 FileChooser fileChooser = new FileChooser();
	        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Script files (*.sql)", "*.sql");
	        fileChooser.getExtensionFilters().add(extFilter);
	        
	        String userDirectoryString = System.getProperty("user.home");
	        File userDirectory = new File(userDirectoryString);
	        if(userDirectory.canRead()) {
	        	   fileChooser.setInitialDirectory(userDirectory);
	        }
	        
	        File file = fileChooser.showOpenDialog(app.getPrimaryStage());
	        if (file == null) 
	        	return;
	        
		Alert alert = new Alert(AlertType.CONFIRMATION, "Remove All Existed Sets and Load from File?", ButtonType.YES, ButtonType.CANCEL);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.YES) {
			if (loadThread != null)
				loadThread.interrupt();
			
			long timeout = 30000;
			long now = System.currentTimeMillis();
			while (System.currentTimeMillis()<now+timeout) {
			 if (!loadThread.isAlive())
				 break;
			 try {
				Thread.sleep(300);
				System.out.println("waiting....");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			}
			
			for (String setName : sqlManager.getSetsNames()) {
    			deleteSet(setName);
    		}
			hiddenSets.clear();
			sets.clear();
		    
			List<String> sqls;
			try {
				sqls = FileUtils.readLines(file, "UTF-8");
				for (String sql: sqls) {
					sqlManager.createSet(sql);
				}
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.severe(e.getMessage());
			}
			app.getPrimaryStage().setTitle(app.getPrimaryStage().getTitle() + "  - Sets File: " + file.getName());
			loadData();
		}
	}
	
	
	
	
	@FXML
	private void loadSetDataClicked() {
		SetData selectedItem = tableView.getSelectionModel().getSelectedItem();
		    if (selectedItem == null) return;
		        	 waitLabel.setVisible(true);
		        	 loadSetDataButton.setDisable(true);
		             drawLineChart(selectedItem);
		             waitLabel.setVisible(false);
			     		loadSetDataButton.setDisable(false);
		             
	}
	

	@FXML
	private void clearAllGraphsClicked() {
		chartBox.getChildren().clear();
	}
	
	
	private void drawLineChart(SetData set) {
		
		    if (this.autoclearBox.isSelected())
		    	chartBox.getChildren().clear();
			
		   ArrayList<SetData> multiselection = new ArrayList<>(tableView.getSelectionModel().getSelectedItems());
		   multiselection.remove(set);
		
		    String startUploadString = set.getStartUpload();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate startDate = LocalDate.parse(startUploadString, formatter);
			List<LocalDate> dates = new ArrayList<LocalDate>();
					
			
			while (startDate.isBefore(LocalDate.now())) {
				dates.add(startDate);
				startDate = startDate.plusMonths(3);
			}
		
		
			final CategoryAxis xAxis = new CategoryAxis ();
	        final NumberAxis yAxis = new NumberAxis();
	        lineChart =  new MLineChart<String,Number>(xAxis,yAxis);
	        lineChart.setTitle(set.getName());
	        lineChart.setAnimated(false);
			chartBox.getChildren().add(lineChart);
			chartBox.setHgrow(lineChart, Priority.ALWAYS); 
			xAxis.setLabel("Date");
			if (this.downloadsRadio.isSelected()) 
				  yAxis.setLabel("Downloads Count");
			else if (this.earningsRadio.isSelected())
				yAxis.setLabel("Earnings Sum, $");
			else if (this.soldDivDownloadsRadio.isSelected())
				yAxis.setLabel("Sold/Downloads");
			else 
				yAxis.setLabel("UNKNOWN");
			
			
		   buildSeriesForSet(lineChart, set.getName(), set, dates);
		 
		 if (!multiselection.isEmpty()) {
			   for (SetData mset:multiselection) {
				   buildSeriesForSet(lineChart, mset.getName(), mset, dates);
			   }
		   }
		   
		 if (allBox.isSelected()) {
			 SetData allSet = sets.stream().filter(s->s.getName().equals("All_Images")).findFirst().orElse(null);
			 if (allSet!=null) {
				 buildSeriesForSet(lineChart, "ALL IMAGES", allSet, dates);
			 }
		 }
		 
		 
		 if (vectorBox.isSelected()) {
			 SetData allVectorSet = sets.stream().filter(s->s.getName().equals("Vector")).findFirst().orElse(null);
			 if (allVectorSet!=null) {
				 buildSeriesForSet(lineChart, "ALL VECTOR", allVectorSet, dates);
			 }
		 }
		 
		 if (rasterBox.isSelected()) {
			 SetData allRasterSet = sets.stream().filter(s->s.getName().equals("Raster")).findFirst().orElse(null);
			 if (allRasterSet!=null) {
				 buildSeriesForSet(lineChart, "ALL RASTER", allRasterSet, dates);
			 }
		 }
		 
         
        if (detailedBox.isSelected()) {
        	 for (int k = 1; k<dates.size(); k++) {
            
              LocalDate startUploadPeriod = dates.get(k-1);
              LocalDate endUploadPeriod = dates.get(k); 
        		
              LocalDate lastUpload = LocalDate.parse(set.getLastUpload(), formatter);
        	  if (dates.get(k).isAfter(lastUpload)) break;
        		
              ObservableList<Data<String, Number>> dataDetailed = FXCollections.observableArrayList() ;
 	        
              SortedList<Data<String, Number>> sortedDataDetailed = new SortedList<>(dataDetailed, (data1, data2) -> 
 	                data1.getXValue().compareTo(data2.getXValue()));
 	        
              Series<String, Number> seriesDetailed = new Series<String, Number>(sortedDataDetailed);
              seriesDetailed.setName(endUploadPeriod.format(formatter));
 	        
		      lineChart.getData().add(seriesDetailed);
              
		     
		     
 	         if (this.downloadsRadio.isSelected()) {
 			               yAxis.setLabel("Downloads Count");
 			    
 				        	 
 				        	for (int i = 1; i<dates.size(); i++) {
 			 		        	LocalDate startPeriod = dates.get(i-1);
 			 		        	LocalDate endPeriod = dates.get(i);
 			 		        	dataDetailed.add(new Data<String,Number>(endPeriod.format(formatter), getDownloadsForPeriodUploadetInPeriod(set, startPeriod, endPeriod, startUploadPeriod, endUploadPeriod)));
 			 		        }
 		       }
 	         
 	        else if (this.earningsRadio.isSelected()) {
 				  yAxis.setLabel("Earnings Sum, $");
 				        	 
 				        	  for (int i = 1; i<dates.size(); i++) {
 						        	LocalDate startPeriod = dates.get(i-1);
 						        	LocalDate endPeriod = dates.get(i);
 						        	dataDetailed.add(new Data<String,Number>(endPeriod.format(formatter), getEarningsForPeriodUploadetInPeriod(set, startPeriod, endPeriod, startUploadPeriod, endUploadPeriod)));
 						        }
 				        	 
 			}
 	         
 	        for (Data<String, Number> entry : seriesDetailed.getData()) {
	            Tooltip t = new Tooltip(String.valueOf(entry.getYValue()));
	            Tooltip.install(entry.getNode(), t);
	            
	        }
        	 }
        }
        
        

        for (Node n : lineChart.getChildrenUnmodifiable()) {
            if (n instanceof Legend) {
                Legend l = (Legend) n;
                for (Legend.LegendItem li : l.getItems()) {
                    for (XYChart.Series<String, Number> s : lineChart.getData()) {
                        if (s.getName().equals(li.getText())) {
                            li.getSymbol().setCursor(Cursor.HAND); // Hint user that legend symbol is clickable
                            li.getSymbol().setOnMouseClicked(me -> {
                                if (me.getButton() == MouseButton.PRIMARY) {
                                    s.getNode().setVisible(!s.getNode().isVisible()); // Toggle visibility of line
                                    for (XYChart.Data<String, Number> d : s.getData()) {
                                        if (d.getNode() != null) {
                                            d.getNode().setVisible(s.getNode().isVisible()); // Toggle visibility of every node in the series
                                        }
                                    }
                                }
                                if (me.getButton() == MouseButton.SECONDARY) {
                                	
                                	 s.getNode().setVisible(!s.getNode().isVisible()); // Toggle visibility of line
                                     for (XYChart.Data<String, Number> d : s.getData()) {
                                         if (d.getNode() != null) {
                                             d.getNode().setVisible(s.getNode().isVisible()); // Toggle visibility of every node in the series
                                         }
                                     }
                                	
                                	
                                	
                                	for (XYChart.Series<String, Number> s2 : lineChart.getData()) {
                                		if (s2.equals(s)) continue;
                                		s2.getNode().setVisible(!s.getNode().isVisible());
                                		for (XYChart.Data<String, Number> d : s2.getData()) {
                                            if (d.getNode() != null) {
                                                d.getNode().setVisible(s2.getNode().isVisible()); // Toggle visibility of every node in the series
                                            }
                                		}
                                    }
                                }
                                lineChart.updateAxisRange();
                            });
                            break;
                        }
                        
                    }
                }
            }
        }
        
		
	}
	
	
	public void drawLineChartForImages(List<ShutterImage> images) {
		
	   chartBox.getChildren().clear();
		
	   ShutterImage olderImage = images.stream().min(Comparator.comparing(ShutterImage::getUploadDate)).orElseGet(null);
	   if (olderImage==null)
		   return;
	   
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate startDate = olderImage.getUploadDate();
		List<LocalDate> dates = new ArrayList<LocalDate>();
				
		
		while (startDate.isBefore(LocalDate.now())) {
			dates.add(startDate);
			startDate = startDate.plusMonths(3);
		}
	
	
		final CategoryAxis xAxis = new CategoryAxis ();
        final NumberAxis yAxis = new NumberAxis();
        lineChart =  new MLineChart<String,Number>(xAxis,yAxis);
        lineChart.setTitle("Images");
        lineChart.setAnimated(false);
		chartBox.getChildren().add(lineChart);
		chartBox.setHgrow(lineChart, Priority.ALWAYS); 
		xAxis.setLabel("Date");
		if (this.downloadsRadio.isSelected()) 
			  yAxis.setLabel("Downloads Count");
		else if (this.earningsRadio.isSelected())
			yAxis.setLabel("Earnings Sum, $");
		else 
			yAxis.setLabel("UNKNOWN");
		
	 
		   for (ShutterImage im:images) {
			   buildSeriesForImage(lineChart, im, dates);
		   }
    

    for (Node n : lineChart.getChildrenUnmodifiable()) {
        if (n instanceof Legend) {
            Legend l = (Legend) n;
            for (Legend.LegendItem li : l.getItems()) {
                for (XYChart.Series<String, Number> s : lineChart.getData()) {
                    if (s.getName().equals(li.getText())) {
                        li.getSymbol().setCursor(Cursor.HAND); // Hint user that legend symbol is clickable
                        li.getSymbol().setOnMouseClicked(me -> {
                            if (me.getButton() == MouseButton.PRIMARY) {
                                s.getNode().setVisible(!s.getNode().isVisible()); // Toggle visibility of line
                                for (XYChart.Data<String, Number> d : s.getData()) {
                                    if (d.getNode() != null) {
                                        d.getNode().setVisible(s.getNode().isVisible()); // Toggle visibility of every node in the series
                                    }
                                }
                            }
                            if (me.getButton() == MouseButton.SECONDARY) {
                            	
                            	 s.getNode().setVisible(!s.getNode().isVisible()); // Toggle visibility of line
                                 for (XYChart.Data<String, Number> d : s.getData()) {
                                     if (d.getNode() != null) {
                                         d.getNode().setVisible(s.getNode().isVisible()); // Toggle visibility of every node in the series
                                     }
                                 }
                            	
                            	
                            	
                            	for (XYChart.Series<String, Number> s2 : lineChart.getData()) {
                            		if (s2.equals(s)) continue;
                            		s2.getNode().setVisible(!s.getNode().isVisible());
                            		for (XYChart.Data<String, Number> d : s2.getData()) {
                                        if (d.getNode() != null) {
                                            d.getNode().setVisible(s2.getNode().isVisible()); // Toggle visibility of every node in the series
                                        }
                            		}
                                }
                            }
                            lineChart.updateAxisRange();
                        });
                        break;
                    }
                    
                }
            }
        }
    }
    
	
}
	
	
	private Series<String, Number> buildSeriesForImage(LineChart<String,Number> lineChart, ShutterImage image, List<LocalDate> dates){
    	
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    ObservableList<Data<String, Number>> data = FXCollections.observableArrayList() ;
        
        SortedList<Data<String, Number>> sortedData = new SortedList<>(data, (data1, data2) -> 
                data1.getXValue().compareTo(data2.getXValue()));
        
        Series<String, Number> series = new Series<String, Number>(sortedData);
        
        
        for (int i = 1; i<dates.size(); i++) {
	        	LocalDate startPeriod = dates.get(i-1);
	        	LocalDate endPeriod = dates.get(i);
	            if (this.earningsRadio.isSelected()) 
	        		data.add(new Data<String,Number>(endPeriod.format(formatter), getEarningsForPeriodForImage(image.getMedia_id(), startPeriod, endPeriod)));
	            else 
	            	data.add(new Data<String,Number>(endPeriod.format(formatter), getDownloadsForPeriodForImage(image.getMedia_id(), startPeriod, endPeriod)));
	        }
        series.setName(image.getOriginal_filename());
        lineChart.getData().add(series);
        
        for (Data<String, Number> entry : series.getData()) {
            Tooltip t = new Tooltip(String.valueOf(entry.getYValue()));
            Tooltip.install(entry.getNode(), t);
            
            entry.getNode().setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                        if(mouseEvent.getClickCount() == 2){
                        	loadFilteredData(entry.getXValue(), series.getName());
                        }
                    }
                }
            });
        }
       
        return series;
	
}
	
	
	
	
	private Series<String, Number> buildSeriesForSet(LineChart<String,Number> lineChart, String seriesName, SetData set, List<LocalDate> dates){
	    	
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		    ObservableList<Data<String, Number>> data = FXCollections.observableArrayList() ;
	        
	        SortedList<Data<String, Number>> sortedData = new SortedList<>(data, (data1, data2) -> 
	                data1.getXValue().compareTo(data2.getXValue()));
	        
	        Series<String, Number> series = new Series<String, Number>(sortedData);
	        
	        for (int i = 1; i<dates.size(); i++) {
		        	LocalDate startPeriod = dates.get(i-1);
		        	LocalDate endPeriod = dates.get(i);
		            if (this.earningsRadio.isSelected()) 
		        		data.add(new Data<String,Number>(endPeriod.format(formatter), getEarningsForPeriod(set, startPeriod, endPeriod)));
		            else if (this.downloadsRadio.isSelected())
		            	data.add(new Data<String,Number>(endPeriod.format(formatter), getDownloadsForPeriod(set, startPeriod, endPeriod)));
		            else if (this.soldDivDownloadsRadio.isSelected()) 
		            	data.add(new Data<String,Number>(endPeriod.format(formatter), getSoldForPeriod(set, startPeriod, endPeriod)/getDownloadsForPeriod(set, startPeriod, endPeriod)));
		            else return null;
		        }
	        series.setName(seriesName);
	        lineChart.getData().add(series);
	        
	        for (Data<String, Number> entry : series.getData()) {
	            Tooltip t = new Tooltip(String.valueOf(entry.getYValue()));
	            Tooltip.install(entry.getNode(), t);
	            
	            entry.getNode().setOnMouseClicked(new EventHandler<MouseEvent>() {
	                @Override
	                public void handle(MouseEvent mouseEvent) {
	                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
	                        if(mouseEvent.getClickCount() == 2){
	                        	loadFilteredData(entry.getXValue(), seriesName);
	                        }
	                    }
	                }
	            });
	        }
	       
	        return series;
		
	}
	
	
	private void loadFilteredData(String date, String viewName) {
		
		ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
		Dialog<DialogConditions> dialog = new Dialog<>();
		 dialog.getDialogPane().getButtonTypes().add(okButtonType);
		 dialog.getDialogPane().lookupButton(okButtonType).setDisable(true);
		    dialog.setHeaderText("Lopkup for date: " + date);
		    
 		    GridPane gridPane = new GridPane();
		    gridPane.setHgap(10);
		    gridPane.setVgap(10);
		    gridPane.setPadding(new Insets(20, 150, 10, 10));

		    TextField monthBeforeTxt = new TextField();
		    monthBeforeTxt.setPromptText("Месяцев до");
		    TextField monthAfterTxt = new TextField();
		    monthAfterTxt.setPromptText("Месяцев после");
		    TextField downloadsBeforeTxt = new TextField();
		    downloadsBeforeTxt.setPromptText("С кол-вом загрузок");
		    TextField downloadsAfterTxt = new TextField();
		    downloadsAfterTxt.setPromptText("С кол-вом загрузок");
		    
		    RadioButton isLessBeforeRadioBtn = new RadioButton("ДО: Было меньше чем");
		    RadioButton isMoreBeforeRadioBtn = new RadioButton("или больше чем");
		    ToggleGroup toggleGroupBefore = new ToggleGroup();
		    isLessBeforeRadioBtn.setToggleGroup(toggleGroupBefore);
		    isMoreBeforeRadioBtn.setToggleGroup(toggleGroupBefore);
		    isMoreBeforeRadioBtn.setSelected(true);
		    
		    
		    RadioButton isLessAfterRadioBtn = new RadioButton("ПОСЛЕ: Было меньше чем");
		    RadioButton isMoreAfterRadioBtn = new RadioButton("или больше чем");
		    ToggleGroup toggleGroupAfter = new ToggleGroup();
		    isLessAfterRadioBtn.setToggleGroup(toggleGroupAfter);
		    isMoreAfterRadioBtn.setToggleGroup(toggleGroupAfter);
		    isLessAfterRadioBtn.setSelected(true);
		    
		    
		    RadioButton andRadioBtn = new RadioButton("AND");
		    RadioButton orRadioBtn = new RadioButton("OR");
		    ToggleGroup toggleGroupAndOr = new ToggleGroup();
		    andRadioBtn.setToggleGroup(toggleGroupAndOr);
		    orRadioBtn.setToggleGroup(toggleGroupAndOr);
		    andRadioBtn.setSelected(true);
		    
		    
		    gridPane.add(monthBeforeTxt, 0, 0);
		    gridPane.add(downloadsBeforeTxt, 1, 0);
		    gridPane.add(isLessBeforeRadioBtn, 0, 1);
		    gridPane.add(isMoreBeforeRadioBtn, 1, 1);
		    
		    
		    gridPane.add(andRadioBtn, 0, 2);
		    gridPane.add(orRadioBtn, 1, 2);
		    
		    gridPane.add(monthAfterTxt, 0, 3);
		    gridPane.add(downloadsAfterTxt, 1, 3);
		    gridPane.add(isLessAfterRadioBtn, 0, 4);
		    gridPane.add(isMoreAfterRadioBtn, 1, 4);

		    dialog.getDialogPane().setContent(gridPane);

		    monthBeforeTxt.textProperty().addListener(new ChangeListener<String>() {
		        @Override
		        public void changed(ObservableValue<? extends String> observable, String oldValue, 
		            String newValue) {
		            if (!newValue.matches("\\d*")) {
		            	monthBeforeTxt.setText(newValue.replaceAll("[^\\d]", ""));
		            }
		            if (monthBeforeTxt.getText().isEmpty() || monthAfterTxt.getText().isEmpty() || downloadsBeforeTxt.getText().isEmpty() || downloadsAfterTxt.getText().isEmpty())
		            	 dialog.getDialogPane().lookupButton(okButtonType).setDisable(true);
		            else
		           	 dialog.getDialogPane().lookupButton(okButtonType).setDisable(false);
		        }
		    });
		    monthAfterTxt.textProperty().addListener(new ChangeListener<String>() {
		        @Override
		        public void changed(ObservableValue<? extends String> observable, String oldValue, 
		            String newValue) {
		            if (!newValue.matches("\\d*")) {
		            	monthAfterTxt.setText(newValue.replaceAll("[^\\d]", ""));
		            }
		            if (monthBeforeTxt.getText().isEmpty() || monthAfterTxt.getText().isEmpty() || downloadsBeforeTxt.getText().isEmpty() || downloadsAfterTxt.getText().isEmpty())
			           	 dialog.getDialogPane().lookupButton(okButtonType).setDisable(true);
			            else
			           	 dialog.getDialogPane().lookupButton(okButtonType).setDisable(false);
		        }
		    });
		    downloadsBeforeTxt.textProperty().addListener(new ChangeListener<String>() {
		        @Override
		        public void changed(ObservableValue<? extends String> observable, String oldValue, 
		            String newValue) {
		            if (!newValue.matches("\\d*")) {
		            	downloadsBeforeTxt.setText(newValue.replaceAll("[^\\d]", ""));
		            }
		            if (monthBeforeTxt.getText().isEmpty() || monthAfterTxt.getText().isEmpty() || downloadsBeforeTxt.getText().isEmpty() || downloadsAfterTxt.getText().isEmpty())
			           	 dialog.getDialogPane().lookupButton(okButtonType).setDisable(true);
			            else
			           	 dialog.getDialogPane().lookupButton(okButtonType).setDisable(false);
		        }
		    });
		    downloadsAfterTxt.textProperty().addListener(new ChangeListener<String>() {
		        @Override
		        public void changed(ObservableValue<? extends String> observable, String oldValue, 
		            String newValue) {
		            if (!newValue.matches("\\d*")) {
		            	downloadsAfterTxt.setText(newValue.replaceAll("[^\\d]", ""));
		            }
		            if (monthBeforeTxt.getText().isEmpty() || monthAfterTxt.getText().isEmpty() || downloadsBeforeTxt.getText().isEmpty() || downloadsAfterTxt.getText().isEmpty())
			           	 dialog.getDialogPane().lookupButton(okButtonType).setDisable(true);
			            else
			           	 dialog.getDialogPane().lookupButton(okButtonType).setDisable(false);
		        }
		    });
		 
		    dialog.setResultConverter(dialogButton -> {
		        if (dialogButton == okButtonType) {
		        	
		        	 if (monthBeforeTxt.getText().isEmpty() || monthAfterTxt.getText().isEmpty() || downloadsBeforeTxt.getText().isEmpty() || downloadsAfterTxt.getText().isEmpty())
		        		 return null;
		        	
		        	 DialogConditions dc = new DialogConditions(Integer.parseInt(monthBeforeTxt.getText()), Integer.parseInt(monthAfterTxt.getText()),
			            		Integer.parseInt(downloadsBeforeTxt.getText()), Integer.parseInt(downloadsAfterTxt.getText()),
			            		isLessBeforeRadioBtn.isSelected(), isLessAfterRadioBtn.isSelected(), andRadioBtn.isSelected());
		             
		            
		            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		    		LocalDate dateBetween = LocalDate.parse(date, formatter);
		    		LocalDate dateBefore = dateBetween.minusMonths(dc.monthBefore);
		    		LocalDate dateAfter = dateBetween.plusMonths(dc.monthAfter);
		    		
		    		app.mainController.loadDataByRequest(sqlManager.getImagesSalesGoodAndBadSQL(viewName, date,	dateBefore.format(formatter), dateAfter.format(formatter),
		    				dc.downloadBefore, dc.downloadsAfter, dc.isLessBefore, dc.isLessAfter, dc.isAnd));
		            
		    		return dc;
		            
		        }
		        return null;
		    });
		 
		    Optional<DialogConditions> result = dialog.showAndWait();
		    
		   result.ifPresent(cond -> {
		        System.out.println(cond);
		    });
		
	}
	
	
	
	
	private int getDownloadsForPeriodUploadetInPeriod(SetData set, LocalDate startDate, LocalDate endDate, LocalDate startUpload, LocalDate endUpload){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return sqlManager.getDownloadsCountForImagesUploadetInPeriod(set.getName(), startDate.format(formatter), endDate.format(formatter),
				startUpload.format(formatter), endUpload.format(formatter));
		
	}
	
	private double getEarningsForPeriodUploadetInPeriod(SetData set, LocalDate startDate, LocalDate endDate, LocalDate startUpload, LocalDate endUpload){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return sqlManager.getEarningsCountForImagesUploadetInPeriod(set.getName(), startDate.format(formatter), endDate.format(formatter),
				startUpload.format(formatter), endUpload.format(formatter));
		
	}
	
	
	private double getEarningsForPeriod(SetData set, LocalDate startPeriod, LocalDate endPeriod) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return sqlManager.getEarningsCount(set.getName(), startPeriod.format(formatter), endPeriod.format(formatter));
		
	}
	
	private int getDownloadsForPeriod(SetData set, LocalDate startPeriod, LocalDate endPeriod) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return sqlManager.getDownloadsCount(set.getName(), startPeriod.format(formatter), endPeriod.format(formatter));
		
	}
	
	
	private double getSoldForPeriod(SetData set, LocalDate startPeriod, LocalDate endPeriod) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return sqlManager.getSoldCount(set.getName(), startPeriod.format(formatter), endPeriod.format(formatter));
		
	}
	
	
	private double getEarningsForPeriodForImage(long mediaId, LocalDate startPeriod, LocalDate endPeriod) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return sqlManager.getEarningsCountForImage(mediaId, startPeriod.format(formatter), endPeriod.format(formatter));
		
	}
	
	private int getDownloadsForPeriodForImage(long mediaId, LocalDate startPeriod, LocalDate endPeriod) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return sqlManager.getDownloadsCountForImage(mediaId, startPeriod.format(formatter), endPeriod.format(formatter));
		
	}
	
	
}