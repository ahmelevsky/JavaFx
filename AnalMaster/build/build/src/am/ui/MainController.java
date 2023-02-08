package am.ui;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import am.Main;
import am.ShutterImage;
import am.db.FilterConstructor;
import am.db.SQLManager;
import am.web.ShutterProvider;
import am.web.UpdateDBKeywordsThread;
import am.web.UpdateDBThread;
import am.web.UpdateDBTopThread;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import javafx.util.StringConverter;




public class MainController implements Initializable {

	
	public Tab tab;
	@FXML
	private TextField sessionIdText;
	@FXML
	private Slider slider;
	@FXML
	private Button updateBtn;
	
	@FXML
	private Button updateTopBtn;
	
	@FXML
	private Button updateTopKeywordsBtn;
	
	@FXML
	private TableView<ShutterImage> tableView;
	
	@FXML
	private TableColumn<ShutterImage, String> columnDate;
	
	@FXML
	private TableColumn<ShutterImage, ImageView> columnPreview;
	
	@FXML
	private TableColumn<ShutterImage, ImageView> columnType;
	
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
	
	@FXML
	private TextField downloadsLessFilter;
	
	@FXML
	private TextField downloadsMoreFilter;
	
	@FXML
	private TextField earningsLessFilter;
	
	@FXML
	private TextField earningsMoreFilter;
	
	@FXML
	private Button createSetBtn;
	
	@FXML
	private TextField setNameField;
	
	
	@FXML
	private Label downloadsCountLbl;
	
	@FXML
	private Label earningsSumLbl;
	
	public Main app;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public ObservableList<ShutterImage> images = FXCollections.observableArrayList();
	
	private SQLManager sqlManager;
	private ShutterProvider provider;
	int rowsOnPage = 100;
	private String lastQuery = ""; 
	private UpdateDBThread updateDbThread;
	private UpdateDBTopThread updateDbTopThread;
	private UpdateDBKeywordsThread updateDbTopKeywordsThread;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		loadSessionId();
		this.provider = new ShutterProvider("");
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.setItems(images);
		setupTableViewColumn();
		tableView.getSelectionModel().setCellSelectionEnabled(true);
	    tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	    tableView.setOnKeyPressed(new TableKeyEventHandler());
	    this.columnDescription.setVisible(false);
	    this.columnKeywords.setVisible(false);
	    
	    
	    filenameFilter.textProperty().addListener((observable, oldValue, newValue) -> {
	    	if (!newValue.equals(oldValue))
	    		getFilteredData();
	    });
	    descriptionFilter.textProperty().addListener((observable, oldValue, newValue) -> {
	    	getFilteredData();
	    });
	    keywordsFilter.textProperty().addListener((observable, oldValue, newValue) -> {
	    	getFilteredData();
	    });
	    
	    selectRaster.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	        	getFilteredData();
	        }
	    });
	    
	    selectVector.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	        	getFilteredData();
	        }
	    });
	    
	    uploadDateFrom.valueProperty().addListener((ov, oldValue, newValue) -> {
	    //	LocalDate localDate = uploadDateFrom.getValue();
	    	getFilteredData();
        });
	    
	    uploadDateTo.valueProperty().addListener((ov, oldValue, newValue) -> {
		    //	LocalDate localDate = uploadDateFrom.getValue();
		    	getFilteredData();
	        });
		    
	    uploadDateFrom.setConverter(new StringConverter<LocalDate>() {
	    	 String pattern = "yyyy-MM-dd";
	    	 DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

	    	 {
	    		 uploadDateFrom.setPromptText(pattern.toLowerCase());
	    	 }

	    	 @Override public String toString(LocalDate date) {
	    	     if (date != null) {
	    	         return dateFormatter.format(date);
	    	     } else {
	    	         return "";
	    	     }
	    	 }

	    	 @Override public LocalDate fromString(String string) {
	    	     if (string != null && !string.isEmpty()) {
	    	         return LocalDate.parse(string, dateFormatter);
	    	     } else {
	    	         return null;
	    	     }
	    	 }
	    	});    
	    
	    
	    uploadDateTo.setConverter(new StringConverter<LocalDate>() {
	    	 String pattern = "yyyy-MM-dd";
	    	 DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

	    	 {
	    		 uploadDateTo.setPromptText(pattern.toLowerCase());
	    	 }

	    	 @Override public String toString(LocalDate date) {
	    	     if (date != null) {
	    	         return dateFormatter.format(date);
	    	     } else {
	    	         return "";
	    	     }
	    	 }

	    	 @Override public LocalDate fromString(String string) {
	    	     if (string != null && !string.isEmpty()) {
	    	         return LocalDate.parse(string, dateFormatter);
	    	     } else {
	    	         return null;
	    	     }
	    	 }
	    	});    
		
	    tableView.setRowFactory( tv -> {
		    TableRow<ShutterImage> row = new TableRow<ShutterImage>() {
		    	 private Tooltip tooltip = new Tooltip();
		            @Override
		            public void updateItem(ShutterImage im, boolean empty) {
		            	super.updateItem(im, empty);
		                if (im == null) {
		                    setTooltip(null);
		                } else {
		                	Map<String, Double> keywords = sqlManager.getKeywordsMapForImage(im.getMedia_id());
		                	Map<String, Double> keywordsSorted = 
		                			keywords.entrySet().stream()
		                		    .sorted(Entry.<String, Double>comparingByValue().reversed())
		                		    .collect(Collectors.toMap(Entry<String, Double>::getKey, Entry<String, Double>::getValue,
		                		                              (e1, e2) -> e1, LinkedHashMap::new));
		                	  String mapAsString = keywordsSorted.keySet().stream()
		                		      .map(key -> key + "   " + keywordsSorted.get(key))
		                		      .collect(Collectors.joining("\n", "", "" ));                	
		                    tooltip.setText(mapAsString);
		                    setTooltip(tooltip);
		                }
		            }
		    };
		    
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && (! row.isEmpty())) { 
		        	ShutterImage rowData = row.getItem();
		           if (rowData!=null) {
						app.openLink(rowData.getImage_url());
		           }
		        }
		    });
		    return row ;
		});
	    
	    
	    
	    downloadsLessFilter.textProperty().addListener((observable, oldValue, newValue) -> {
	    	if (!newValue.matches("\\d*")) {
	    		downloadsLessFilter.setText(newValue.replaceAll("[^\\d]", ""));
	        }
	    	getFilteredData();
	    });
	    
	    downloadsMoreFilter.textProperty().addListener((observable, oldValue, newValue) -> {
	    	if (!newValue.matches("\\d*")) {
	    		downloadsMoreFilter.setText(newValue.replaceAll("[^\\d]", ""));
	        }
	    	getFilteredData();
	    });
	 
	    
	    earningsLessFilter.textProperty().addListener((observable, oldValue, newValue) -> {
	    	if (!newValue.matches("[\\d*|\\d+\\.\\d*]")) {
	    		earningsLessFilter.setText(newValue.replaceAll("[^\\d*|\\d+\\.\\d*]", ""));
	        }
	    	getFilteredData();
	    });
	    
	    earningsMoreFilter.textProperty().addListener((observable, oldValue, newValue) -> {
	    	if (!newValue.matches("[\\d*|\\d+\\.\\d*]")) {
	    		earningsMoreFilter.setText(newValue.replaceAll("[^\\d*|\\d+\\.\\d*]", ""));
	        }
	    	getFilteredData();
	    });
	    
	    setNameField.textProperty().addListener(new ChangeListener<String>() {
	        @Override
	        public void changed(ObservableValue<? extends String> observable, String oldValue, 
	            String newValue) {
	            if (!newValue.matches("[A-Z0-9]+")) {
	            	setNameField.setText(newValue.replaceAll("^[A-Z0-9]+", ""));
	            }
	        }
	    });
	    
	}
	
	public void setup() {
		this.sqlManager = app.sqlManager;
	}
	
	
	
	@FXML
	private void updateDatabase() {
		this.provider = getSession();
		if (!UpdateDBThread.isStarted) {
			UpdateDBThread.isStarted = true;
			updateDbThread = new UpdateDBThread(this.app, provider, sqlManager);
			disableControls();
			updateDbThread.start();
		}
		else {
			updateDbThread.isStop = true;
			//updateDbThread.interrupt();
			UpdateDBThread.isStarted = false;
		}
	}
	
	
	@FXML
	private void updateTopPerformance() {
		this.provider = getSession();
		if (!UpdateDBTopThread.isStarted) {
			UpdateDBTopThread.isStarted = true;
			updateDbTopThread = new UpdateDBTopThread(this.app, provider, sqlManager);
			disableControlsTop();
			updateDbTopThread.start();
		}
		else {
			updateDbTopThread.isStop = true;
			UpdateDBTopThread.isStarted = false;
		}
	}
	
	@FXML
	private void updateTopKeywords() {
		this.provider = getSession();
		if (!UpdateDBKeywordsThread.isStarted) {
			UpdateDBKeywordsThread.isStarted = true;
			updateDbTopKeywordsThread = new UpdateDBKeywordsThread(this.app, provider, sqlManager);
			disableControlsKeywords();
			updateDbTopKeywordsThread.start();
		}
		else {
			updateDbTopKeywordsThread.isStop = true;
			UpdateDBKeywordsThread.isStarted = false;
		}
	}
	
	
	
	public void loadData() {
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
        			if (sqlManager==null || sqlManager.connection.isClosed() || !sqlManager.connection.isValid(1)) {
        				sqlManager = new SQLManager(app);
        			}
        			getAllImages();
        		} catch (SQLException e) {
        			LOGGER.severe(e.getMessage());
        			System.out.println(e.getMessage());
        		}
				
			}
		});
		t1.start();
            	
	}
	
	
	public void getAllImages() {
		String sqldata = "SELECT * FROM " + this.sqlManager.IMAGESTABLE + " LEFT JOIN " + this.sqlManager.IMAGESTOPTABLE
	                      + " on " + this.sqlManager.IMAGESTOPTABLE + ".media_id = " + this.sqlManager.IMAGESTABLE + ".media_id "
	                      + " ORDER BY uploaded_date DESC"; 
		String sqlcount = "SELECT COUNT(*) FROM " + sqlManager.IMAGESTABLE;
		loadData(sqldata, sqlcount);
	}
	
	@FXML
	private void createSet() {
		
	}
	
	
	private List<ShutterImage> createTestData(int imagesCount) {
		List<ShutterImage> list = new ArrayList<ShutterImage>();
		for (int i=0; i<imagesCount; i++) {
			ShutterImage im = new ShutterImage(i); 
			int randomNum = ThreadLocalRandom.current().nextInt(0, 100);
			im.setDownloads(randomNum);
			im.setEarnings(ThreadLocalRandom.current().nextDouble());
			im.setImage_url("https://www.shutterstock.com/pic-1594457860");

			Map<String, Double> k1 = new LinkedHashMap<String, Double>() {{
			    put("cat", 10.0);
			    put("maau", 20.0);
			    put("bob", 213.0);
			    put("BOB", 0.0);
			}};
			im.keywordsRate.putAll(k1);
			
			im.setUpload_id(i);
			im.setOriginal_filename("oroginal_filename.jpg");
			im.setUploaded_date("2022-07-19T11:01:58.963Z".split("T")[0]);
			im.setIs_illustration(true);
			im.keywords.addAll(Arrays.asList("cat", "maau", "bob", "uuuu"));
			im.setDescription("Some Description " + i);
			im.setPreviewPath("https://image.shutterstock.com/display_pic_with_logo/4307413/2179834505/stock-vector-hologram-texture-blur-multicolor-invitation-hipster-mesh-purple-retro-gradient-shiny-banner-2179834505.jpg");
		 /*
			try {
				im.setPreviewBytes(provider.downloadByteArray(im.getPreviewPath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
	     */
			list.add(im);
		}
		return list;
	}
	
	
	
	
	private void fillDBWithTestData() {
		List<ShutterImage> testImages = createTestData(10);
		List<ShutterImage> toRemove = new ArrayList<ShutterImage>();
		for (ShutterImage im:testImages) {
			if(this.sqlManager.isInDB(im.getMedia_id())) 
				toRemove.add(im);
			else
				try {
					im.setPreviewBytes(this.provider.downloadByteArray(im.getPreviewPath()));
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		testImages.removeAll(toRemove);
			if (!testImages.isEmpty())
				this.sqlManager.insertNewImages(testImages);
			
		List<Integer> toUpdateData = this.sqlManager.getEmptyImages();
		for (int media_id:toUpdateData) {
			ShutterImage image = testImages.stream().filter(im->media_id==im.getMedia_id()).findAny().orElse(null);
			if (image!=null)
				this.sqlManager.insertImageData(image);
		}
		
		 testImages = createTestData(10);
		 this.sqlManager.insertImagesTop(testImages);
		 for (ShutterImage im:testImages) {
			 this.sqlManager.insertKeywords(im.getMedia_id(), im.keywordsRate);
		 }
		
	}
	
	
	private void getFilteredData() {
            FilterConstructor fc = new FilterConstructor(this.app);
            fc.name = filenameFilter.getText();
            fc.description = descriptionFilter.getText();
            if (selectRaster.isSelected() && !selectVector.isSelected())
            	fc.type = "Raster"; 
            if (!selectRaster.isSelected() && selectVector.isSelected())
            	fc.type = "Vector";
            if (!keywordsFilter.getText().trim().isEmpty()) {
            	String[] lines = keywordsFilter.getText().split("\\s*(;|,|\\s)\\s*");
            	fc.keywords.clear();
            	fc.keywords.addAll(Arrays.asList(lines));	
            }
            
            if (!downloadsLessFilter.getText().trim().isEmpty()) {
            	fc.downloadsLess = Integer.parseInt(downloadsLessFilter.getText().trim());
            }
            
            if (!downloadsMoreFilter.getText().trim().isEmpty()) {
            	fc.downloadsMore = Integer.parseInt(downloadsMoreFilter.getText().trim());
            }
            
            if (!earningsLessFilter.getText().trim().isEmpty()) {
            	fc.earningsLess = Double.parseDouble(earningsLessFilter.getText().trim());
            }
            
            if (!earningsMoreFilter.getText().trim().isEmpty()) {
            	fc.earningsMore = Double.parseDouble(earningsMoreFilter.getText().trim());
            }
            
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate ld = uploadDateFrom.getValue();
            if (ld!=null) 
            	fc.uploadDateFrom = ld.format(formatter);
            
            ld = uploadDateTo.getValue();
            if (ld!=null) 
            	fc.uploadDateTo = ld.format(formatter);
            
            loadData(fc.getImagesSQL(), fc.getCountSQL());
	}

	
	private void loadData(String sqldata, String sqlcount) {
		int count = this.sqlManager.getImagesCount(sqlcount);
		updateFilesCount(count);
		setupPagination(sqldata, count);
	}
	
	
	
	private void updateFilesCount(int count) {
		Platform.runLater(new Runnable() {
            public void run() {
            	filesCountTxt.setText("Files count: " + count);
              }
		});
	}
	
	

	
	
	public ShutterProvider getSession() {
		String sessionId = sessionIdText.getText().trim();
		if (sessionId.isEmpty()) {
			showAlert("������ sessionId");
			return null;
		}
		saveSessionId();
		ShutterProvider provider = new ShutterProvider(sessionId);
		if (!provider.isConnection()) {
			showAlert("������ ����������");
			return null;
		}
		return provider;
	}
	
	private void setupTableViewColumn() {
		columnPreview.setPrefWidth(200);
		columnDate.setCellValueFactory(new PropertyValueFactory<>("uploaded_date"));
		columnType.setCellValueFactory(new PropertyValueFactory<>("type"));
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

		public void log(String message) {
			Platform.runLater(new Runnable() {
	            public void run() {
	            	 String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS").format(Calendar.getInstance().getTime());
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
		            	 correctFilename();
		            	 return node;
		            }
		        });
		            }
			 });
		}
		
		public void selectImages(String sql, int page) {
			sql = sql + " LIMIT " +  + this.rowsOnPage + " OFFSET " + this.rowsOnPage*page;
			if (this.lastQuery.equals(sql)) return;
			this.lastQuery = sql;
			System.out.println(sql);
			this.images.clear();
			this.images.addAll(this.sqlManager.getImagesFromDB(sql));
			Comparator<ShutterImage> comparator = Comparator.comparing(ShutterImage::getUploaded_date); 
			comparator = comparator.reversed();
			FXCollections.sort(this.images, comparator);
			bindSlider();
		}
		
		private void bindSlider() {
			for (ShutterImage im:images) {
				ImageView view = im.getImage();
				view.fitHeightProperty().bind(slider.valueProperty()); 
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
		
		public void disableControls() {
			Platform.runLater(new Runnable() {
	            public void run() {
	            	updateBtn.setText("STOP UPDATING");
	            	pagination.setDisable(true);
	            	updateTopKeywordsBtn.setDisable(true);
	            	updateTopBtn.setDisable(true);
	            }
			 });
		}
		
		public void disableControlsTop() {
			Platform.runLater(new Runnable() {
	            public void run() {
	            	updateTopBtn.setText("STOP UPDATING");
	            	updateBtn.setDisable(true);
	            	updateTopKeywordsBtn.setDisable(true);
	            	pagination.setDisable(true);
	            }
			 });
		}
		
		public void enableControls() {
			Platform.runLater(new Runnable() {
	            public void run() {
	            	updateBtn.setText("UPDATE DATABASE");
	            	pagination.setDisable(false);
	            	updateTopBtn.setDisable(false);
	            	updateTopKeywordsBtn.setDisable(false);
	            }
			 });
	            	
		}
		
		public void enableControlsTop() {
			Platform.runLater(new Runnable() {
	            public void run() {
	            	updateTopBtn.setText("UPDATE TOP PERFORMANCE");
	            	pagination.setDisable(false);
	            	updateBtn.setDisable(false);
	            	updateTopKeywordsBtn.setDisable(false);
	            }
			 });
	            	
		}
		
		public void enableControlsKeywords() {
			Platform.runLater(new Runnable() {
	            public void run() {
	            	updateTopKeywordsBtn.setText("UPDATE TOP KEYWORDS");
	            	pagination.setDisable(false);
	            	updateTopBtn.setDisable(false);
	            	updateBtn.setDisable(false);
	            }
			 });
	            	
		}
		
		public void disableControlsKeywords() {
			Platform.runLater(new Runnable() {
	            public void run() {
	            	updateTopKeywordsBtn.setText("STOP UPDATING");
	            	updateBtn.setDisable(true);
	            	updateTopBtn.setDisable(true);
	            	pagination.setDisable(true);
	            }
			 });
		}
		
		public void showAlert(String text) {
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
		
}
