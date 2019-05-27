package te.view;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import te.Settings;
import te.model.FolderTheme;
import te.model.FolderVariable;
import te.model.FolderVariablesWrapper;
import te.model.Target;
import te.util.TextAreaException;

public class FolderVariableController  extends TargetEditorController implements Initializable {

	 @FXML
	 private TableView<FolderVariable> folderVariablesTable;
	 @FXML
	 private PTableColumn<FolderVariable, String> folderPathColumn;
	 @FXML
	 private PTableColumn<FolderVariable, String> keyVariableColumn;
	 @FXML
	 private PTableColumn<FolderVariable, String> descriptionVariableColumn;
	 @FXML
	 private TextArea inputKeyVariable;
	 @FXML
	 private TextArea inputDescriptionVariable;
	 @FXML 
	 private Button addDataBtn;
	 @FXML 
	 private Button savePresetBtn;
	 @FXML 
	 private ComboBox<FolderTheme> selectPresetBox;
	 
	 private ObservableList<FolderTheme> themesList  = FXCollections.observableArrayList();
	 
	 public FolderVariablesWrapper wrapper;
	 private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	 
    public FolderVariableController() {
	    }

    
    final	ChangeListener<FolderTheme> selectionListener = new ChangeListener<FolderTheme>(){
		@Override
		public void changed(ObservableValue<? extends FolderTheme> o, FolderTheme ol, FolderTheme nw){
			 if (nw!=null) {
	        	  
		        	ButtonType yes = new ButtonType(Settings.bundle.getString("alert.yes"));
		      		ButtonType no = new ButtonType(Settings.bundle.getString("alert.no"));
		      		Alert alert = new Alert(AlertType.CONFIRMATION, Settings.bundle.getString("alert.select.theme"), yes, no);
		      		alert.initOwner(app.getPrimaryStage());
    	      		alert.initModality(Modality.APPLICATION_MODAL); 
		      		alert.showAndWait();

		      		if (alert.getResult() == yes) {
		        	  
		      			AtomicBoolean allFoldersGotVariables = new AtomicBoolean(true);
		        	     app.folderVariableData.forEach(v -> {
		     			 v.setDescriptionVariable("");
		     			 v.setKeyVariable("");
		     			
		     			 Optional<FolderVariable> opt = nw.getFolderVariables().stream().filter(fw -> fw.getFolderPath().equals(v.getFolderPath())).findFirst();
		     			if (opt!=null && opt.isPresent()){
		     				 FolderVariable presetVariable = opt.get();
		     				 v.setDescriptionVariable(presetVariable.getDescriptionVariable());
		         			 v.setKeyVariable(presetVariable.getKeyVariable());
		     			}
		     			else 
		     				allFoldersGotVariables.set(false);
		     		 });
		        	  
		        	  
		        	  if (!allFoldersGotVariables.get()) {
		        		  alert = new Alert(AlertType.WARNING);
				          alert.setContentText(Settings.bundle.getString("alert.warn.missedfolvars"));
				          alert.initOwner(app.getPrimaryStage());
		    	      	  alert.initModality(Modality.APPLICATION_MODAL); 
				          alert.showAndWait();
		        	  }
		        	//  selectPresetBox.getSelectionModel().clearSelection();
		          }
		      		else 
		      			selectTheme(ol);
		      		
		}
	}
	};
    
	
	private void selectTheme(FolderTheme ft) {
		Platform.runLater(() -> {
				selectPresetBox.getSelectionModel().selectedItemProperty().removeListener(selectionListener);
				selectPresetBox.getSelectionModel().select(ft);
				selectPresetBox.getSelectionModel().selectedItemProperty().addListener(selectionListener);
        });
	}
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		setTableEditable();
		Callback<TableColumn<FolderVariable,String>, TableCell<FolderVariable,String>> cellFactory =
	             new Callback<TableColumn<FolderVariable,String>, TableCell<FolderVariable,String>>() {
	                 public TableCell<FolderVariable,String> call(TableColumn<FolderVariable,String> p) {
	                    return new EditCell<FolderVariable,String> (new DefaultStringConverter());
	                 }
	             };
		
		folderPathColumn.setCellValueFactory(cellData -> cellData.getValue().folderPathProperty());
		keyVariableColumn.setCellValueFactory(cellData -> cellData.getValue().keyVariableProperty());
		keyVariableColumn.setCellFactory(cellFactory);
		
		// Easy WAY to make Editable -  NO EDITCELL CLASS
		
		keyVariableColumn.setOnEditCommit(
	            new EventHandler<CellEditEvent<FolderVariable, String>>() {
	                @Override
	                public void handle(CellEditEvent<FolderVariable, String> t) {
	                    ((FolderVariable) t.getTableView().getItems().get(
	                            t.getTablePosition().getRow())
	                            ).setKeyVariable(t.getNewValue());
	                }
	            }
	        );
	 
		descriptionVariableColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionVariableProperty());
		descriptionVariableColumn.setCellFactory(cellFactory);
		
		// Easy WAY to make Editable -  NO EDITCELL CLASS
		descriptionVariableColumn.setOnEditCommit(
	            new EventHandler<CellEditEvent<FolderVariable, String>>() {
	                @Override
	                public void handle(CellEditEvent<FolderVariable, String> t) {
	                    ((FolderVariable) t.getTableView().getItems().get(
	                            t.getTablePosition().getRow())
	                            ).setDescriptionVariable(t.getNewValue());
	                }
	            }
	        );
		
		inputKeyVariable.textProperty().addListener((observable, oldValue, newValue) ->{ 
		    setError(inputKeyVariable, false);
		    try {
				app.checkSyntax(inputKeyVariable);
			} catch (TextAreaException e) {
				setError(e.textArea, true);
			}
		});
		inputDescriptionVariable.textProperty().addListener((observable, oldValue, newValue) ->{ 
		    setError(inputDescriptionVariable, false);
		    try {
				app.checkSyntax(inputDescriptionVariable);
			} catch (TextAreaException e) {
				setError(e.textArea, true);
			}
		});
		
		setTableHeadersUnmovable();
		addDeleteButtonToCombobox(selectPresetBox);
		//Selection Theme (Preset) functionality
		selectPresetBox.setItems(themesList);
		selectPresetBox.setConverter(new StringConverter<FolderTheme>() {
		    @Override
		    public String toString(FolderTheme object) {
		       return object.nameProperty().get();
		    }
		    @Override
		    public FolderTheme fromString(String string) {
		       return null;
		    }
		});
		
		
	
		
		selectPresetBox.getSelectionModel().selectedItemProperty().addListener(selectionListener);
		
		//selectPresetBox.getItems().add(null);
	}

	 public void setup() {
	        folderVariablesTable.setItems(app.getFolderVariableData());
	    }
	
	 public void fillFolderVariables(File baseDir){
		 app.getFolderVariableData().clear();
		 if (!baseDir.exists())
			 return;
		 if (!baseDir.isDirectory())
			 return;
		 File[] directories = baseDir.listFiles(new FilenameFilter() {
			  @Override
			  public boolean accept(File current, String name) {
			    return new File(current, name).isDirectory();
			  }
			});
		 
		 for (File dir:directories){
			 app.getFolderVariableData().add(new FolderVariable(dir));
		 }
		 
		 FolderTheme ft = selectPresetBox.getSelectionModel().getSelectedItem();
		 if (ft!=null) {
			 app.folderVariableData.forEach(v -> {
     			
     			 Optional<FolderVariable> opt = ft.getFolderVariables().stream().filter(fw -> fw.getFolderPath().equals(v.getFolderPath())).findFirst();
     			if (opt!=null && opt.isPresent()){
     				 FolderVariable presetVariable = opt.get();
     				 v.setDescriptionVariable(presetVariable.getDescriptionVariable());
         			 v.setKeyVariable(presetVariable.getKeyVariable());
     			}
		 });
		 }
	 }
	
	  private void setTableEditable() {
		  folderVariablesTable.setEditable(true);
	        // allows the individual cells to be selected
		  folderVariablesTable.getSelectionModel().cellSelectionEnabledProperty().set(true);
	        // when character or numbers pressed it will start edit in editable
	        // fields
		  folderVariablesTable.setOnKeyPressed(event -> {
	            if (event.getCode().isLetterKey() || event.getCode().isDigitKey()) {
	                editFocusedCell();
	            } else if (event.getCode() == KeyCode.RIGHT ||
	                event.getCode() == KeyCode.TAB) {
	            	folderVariablesTable.getSelectionModel().selectNext();
	                event.consume();
	            } else if (event.getCode() == KeyCode.LEFT) {
	                // work around due to
	                // TableView.getSelectionModel().selectPrevious() due to a bug
	                // stopping it from working on
	                // the first column in the last row of the table
	                selectPrevious();
	                event.consume();
	            }
	        });
	    }
	  
	  private void setTableHeadersUnmovable() {
		  folderVariablesTable.getColumns().addListener(new ListChangeListener() {
	          public boolean suspended;
	          @Override
	          public void onChanged(Change change) {
	              change.next();
	              if (change.wasReplaced() && !suspended) {
	                  this.suspended = true;
	                  folderVariablesTable.getColumns().setAll(folderPathColumn, keyVariableColumn, descriptionVariableColumn);
	                  this.suspended = false;
	              }
	          }
	      });
	  }
	 
	  @SuppressWarnings("unchecked")
	    private void editFocusedCell() {
	        final TablePosition < FolderVariable, ? > focusedCell = folderVariablesTable
	            .focusModelProperty().get().focusedCellProperty().get();
	        folderVariablesTable.edit(focusedCell.getRow(), focusedCell.getTableColumn());
	    }
	  
	  @SuppressWarnings("unchecked")
	    private void selectPrevious() {
	        if (folderVariablesTable.getSelectionModel().isCellSelectionEnabled()) {
	            // in cell selection mode, we have to wrap around, going from
	            // right-to-left, and then wrapping to the end of the previous line
	            TablePosition < FolderVariable, ? > pos = folderVariablesTable.getFocusModel()
	                .getFocusedCell();
	            if (pos.getColumn() - 1 >= 0) {
	                // go to previous row
	            	folderVariablesTable.getSelectionModel().select(pos.getRow(),
	                    getTableColumn(pos.getTableColumn(), -1));
	            } else if (pos.getRow() < folderVariablesTable.getItems().size()) {
	                // wrap to end of previous row
	            	folderVariablesTable.getSelectionModel().select(pos.getRow() - 1,
	            			folderVariablesTable.getVisibleLeafColumn(
	            					folderVariablesTable.getVisibleLeafColumns().size() - 1));
	            }
	        } else {
	            int focusIndex = folderVariablesTable.getFocusModel().getFocusedIndex();
	            if (focusIndex == -1) {
	            	folderVariablesTable.getSelectionModel().select(folderVariablesTable.getItems().size() - 1);
	            } else if (focusIndex > 0) {
	            	folderVariablesTable.getSelectionModel().select(focusIndex - 1);
	            }
	        }
	    }
	  
	  private TableColumn < FolderVariable, ? > getTableColumn(final TableColumn < FolderVariable, ? > column, int offset) {
		        int columnIndex = folderVariablesTable.getVisibleLeafIndex(column);
		        int newColumnIndex = columnIndex + offset;
		        return folderVariablesTable.getVisibleLeafColumn(newColumnIndex);
		    }


	@Override
	public void loadData() {
		if (this.wrapper != null){
	    this.themesList.clear();
	    this.themesList.addAll(this.wrapper.themes);
         if (app.mainFrameController.setRootFolder(wrapper.rootFolderPath)){
        	 for (FolderVariable variable:app.getFolderVariableData()){
        		 Optional<FolderVariable> opt = wrapper.variables.stream().filter(v -> v.getFolderPath().equals(variable.getFolderPath())).findFirst();
        		 if (opt!=null && opt.isPresent()){
        			 FolderVariable savedVariable = opt.get();
        			 //variable.setFolder(new File(this.wrapper.rootFolderPath + File.separator + savedVariable.getFolderPath()));
        			 //variable.setFolderPath(savedVariable.getFolderPath());
        			 variable.setDescriptionVariable(savedVariable.getDescriptionVariable());
        			 variable.setKeyVariable(savedVariable.getKeyVariable());
        		 }
        	 }
         }
		}
	}

	@FXML
    private void addData(){
		boolean editKwds = !this.inputKeyVariable.getText().trim().isEmpty();
		boolean editDescr = !this.inputDescriptionVariable.getText().trim().isEmpty();
		
    	String[] varKwds = this.inputKeyVariable.getText().split("\\r?\\n");
		String[] varDescr = this.inputDescriptionVariable.getText().split("\\r?\\n");
		
		for (int i=0; i<app.folderVariableData.size();i++){
			String varKwdsRow = varKwds.length>i ? varKwds[i] : "";
			String varDescrRow = varDescr.length>i ? varDescr[i] : "";
			FolderVariable variable = app.folderVariableData.get(i);
			if (editKwds)
				variable.setKeyVariable(varKwdsRow);
			if (editDescr)
				variable.setDescriptionVariable(varDescrRow);
		}
		this.inputKeyVariable.clear();
		this.inputDescriptionVariable.clear();
    }
	
	

	@Override
	public void saveData() {
		this.wrapper = new FolderVariablesWrapper();
		this.wrapper.variables = app.folderVariableData;
		this.wrapper.themes = this.themesList;
		this.wrapper.rootFolderPath = app.mainFrameController.getRootFolder();
		
	}


	public void saveVariables(){
			app.savedFolderVariableData.clear();
			app.savedFolderVariableData.addAll(app.folderVariableData);
		}
		
	private void setError(TextArea tf, boolean setOrUnset){
		 ObservableList<String> styleClass = tf.getStyleClass();
		 if (setOrUnset) {
			 if (! styleClass.contains("red")) {
	                styleClass.add("red");
	            }
			 }
		 else {
	            styleClass.removeAll(Collections.singleton("red"));          
	        }
	}
	
	
	
	

	public static void addDeleteButtonToCombobox(ComboBox<FolderTheme> cb){
		  cb.setCellFactory(lv ->
          new ListCell<FolderTheme>() {
              // This is the node that will display the text and the cross. 
              // I chose a hyperlink, but you can change to button, image, etc. 
              private HBox graphic;

              // this is the constructor for the anonymous class.
              {
                  Label label = new Label();
                  // Bind the label text to the item property. If your ComboBox items are not Strings you should use a converter. 
                  
                //  label.textProperty().bind(itemProperty().getValue().nameProperty());
                  label.textProperty().bind(Bindings.convert(itemProperty()));
                  // Set max width to infinity so the cross is all the way to the right. 
                  label.setMaxWidth(Double.POSITIVE_INFINITY);
                  // We have to modify the hiding behavior of the ComboBox to allow clicking on the hyperlink, 
                  // so we need to hide the ComboBox when the label is clicked (item selected). 
                  label.setOnMouseClicked(event -> cb.hide());
                  Hyperlink cross = new Hyperlink("X");
                  cross.setVisited(true); // So it is black, and not blue. 
                  cross.setOnAction(event ->
                          {
                        		ButtonType yes = new ButtonType("Да");
                	      		ButtonType no = new ButtonType("Нет");
                	      		Alert alert = new Alert(AlertType.CONFIRMATION, Settings.bundle.getString("alert.conf.removetheme"), yes, no);
                	      		alert.initOwner(cb.getScene().getWindow());
                	      		alert.initModality(Modality.APPLICATION_MODAL); 
                	      		alert.setTitle(Settings.bundle.getString("alert.conf.clear.title"));
                	      		alert.setHeaderText(Settings.bundle.getString("alert.conf.clear.header"));
                	      		alert.showAndWait();

                	      		if (alert.getResult() == yes) {
                              // Since the ListView reuses cells, we need to get the item first, before making changes.  
                        	  FolderTheme item = getItem();
                              if (isSelected()) {
                                  // Not entirely sure if this is needed. 
                                  cb.getSelectionModel().select(null);
                              }
                              cb.getItems().remove(item);
                	      		}
                          }
                  );
                  // Arrange controls in a HBox, and set display to graphic only (the text is included in the graphic in this implementation). 
                  graphic = new HBox(label, cross);
                  graphic.setHgrow(label, Priority.ALWAYS);
                  setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
              }

              @Override
              protected void updateItem(FolderTheme item, boolean empty) {
                  super.updateItem(item, empty);
                  if (empty) {
                      setGraphic(null);
                  } else {
                      setGraphic(graphic);
                  }
              }
          });

  // We have to set a custom skin, otherwise the ComboBox disappears before the click on the Hyperlink is registered. 
      cb.setSkin(new ComboBoxListViewSkin<FolderTheme>(cb) {
      @Override
      protected boolean isHideOnClickEnabled() {
          return false;
      }
  });
	}
	
	@FXML
	private void savePreset() {
		 TextInputDialog td = new TextInputDialog(); 
		 td.initOwner(app.getPrimaryStage());
		 td.initModality(Modality.APPLICATION_MODAL); 
		  td.setHeaderText(Settings.bundle.getString("alert.save.theme")); 
		  Optional<String> res = td.showAndWait();
		  if (res!=null && res.isPresent() && !res.get().isEmpty()) {
			  
			  if (this.themesList.stream().anyMatch(t -> t.getName().equals(res.get()))) {
				  Alert alert = new Alert(AlertType.WARNING);
		          alert.setContentText(Settings.bundle.getString("alert.warn.existedtheme"));
		          alert.showAndWait();
			  }
			  else {
			  
			   FolderTheme ft = new FolderTheme();
			   ft.setName(res.get());
			   app.folderVariableData.forEach(fv -> {
				   FolderVariable newfv = new FolderVariable(fv.getFolder());
				   newfv.setFolderPath(fv.getFolderPath());
				   newfv.setDescriptionVariable(fv.getDescriptionVariable());
				   newfv.setKeyVariable(fv.getKeyVariable());
				   ft.getFolderVariables().add(newfv);
			   });
			   themesList.add(ft);
			   selectTheme(ft);
			  }
			  LOGGER.fine("FolderVariable Theme Preset was saved with name: " + res.get());
		  }
			 
	}
}
