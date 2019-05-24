package te.view;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import te.model.FolderVariable;
import te.model.FolderVariablesWrapper;
import te.model.Target;
import te.util.TextAreaException;

public class FolderVariableController  extends TargetEditorController implements Initializable {

	 @FXML
	 private TableView<FolderVariable> folderVariablesTable;
	 @FXML
	 private TableColumn<FolderVariable, String> folderPathColumn;
	 @FXML
	 private TableColumn<FolderVariable, String> keyVariableColumn;
	 @FXML
	 private TableColumn<FolderVariable, String> descriptionVariableColumn;
	 @FXML
	 private TextArea inputKeyVariable;
	 @FXML
	 private TextArea inputDescriptionVariable;
	 @FXML 
	 private Button addDataBtn;
	 
	 public FolderVariablesWrapper wrapper;
	 
    public FolderVariableController() {
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
}
