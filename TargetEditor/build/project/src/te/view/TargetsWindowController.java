package te.view;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import te.Main;
import te.model.Target;

public class TargetsWindowController implements Initializable {

	 @FXML
	 private TableView<Target> targetsTable;
	 @FXML
	 private TableColumn<Target, String> targetColumn;
	 @FXML
	 private TableColumn<Target, String> target1Column;
	 @FXML
	 private TableColumn<Target, String> target2Column;
	 private Main app;
	
	 
     public TargetsWindowController() {
	    }
 
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//targetsTable.setEditable(true);
		setTableEditable();
		
		Callback<TableColumn<Target,String>, TableCell<Target,String>> cellFactory =
	             new Callback<TableColumn<Target,String>, TableCell<Target,String>>() {
	                 public TableCell<Target,String> call(TableColumn<Target,String> p) {
	                    return new EditCell<Target,String> (new DefaultStringConverter());
	                 }
	             };
	             
		
		targetColumn.setCellValueFactory(cellData -> cellData.getValue().targetProperty());
		target1Column.setCellValueFactory(cellData -> cellData.getValue().targetProperty1());
		target1Column.setCellFactory(cellFactory);
		
		// Easy WAY to make Editable -  NO EDITCELL CLASS
		//target1Column.setCellFactory(TextFieldTableCell.forTableColumn());
		
		target1Column.setOnEditCommit(
	            new EventHandler<CellEditEvent<Target, String>>() {
	                @Override
	                public void handle(CellEditEvent<Target, String> t) {
	                    ((Target) t.getTableView().getItems().get(
	                            t.getTablePosition().getRow())
	                            ).setTarget1(t.getNewValue());
	                }
	            }
	        );
	 
		target2Column.setCellValueFactory(cellData -> cellData.getValue().targetProperty2());
		target2Column.setCellFactory(cellFactory);
		
		// Easy WAY to make Editable -  NO EDITCELL CLASS
		//target2Column.setCellFactory(TextFieldTableCell.forTableColumn());
		target2Column.setOnEditCommit(
	            new EventHandler<CellEditEvent<Target, String>>() {
	                @Override
	                public void handle(CellEditEvent<Target, String> t) {
	                    ((Target) t.getTableView().getItems().get(
	                            t.getTablePosition().getRow())
	                            ).setTarget2(t.getNewValue());
	                }
	            }
	        );
	}

	 public void setMainApp(Main app) {
	        this.app = app;
	        targetsTable.setItems(app.getTargetsData());
	    }
	
	 public void fillTargets(File baseDir){
		 app.getTargetsData().clear();
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
			 app.getTargetsData().add(new Target(dir));
		 }
		 app.descriptionEditorController.updateLists();
		 app.titleEditorController.updateLists();
	 }
	
	  private void setTableEditable() {
		  targetsTable.setEditable(true);
	        // allows the individual cells to be selected
		  targetsTable.getSelectionModel().cellSelectionEnabledProperty().set(true);
	        // when character or numbers pressed it will start edit in editable
	        // fields
		  targetsTable.setOnKeyPressed(event -> {
	            if (event.getCode().isLetterKey() || event.getCode().isDigitKey()) {
	                editFocusedCell();
	            } else if (event.getCode() == KeyCode.RIGHT ||
	                event.getCode() == KeyCode.TAB) {
	            	targetsTable.getSelectionModel().selectNext();
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
	        final TablePosition < Target, ? > focusedCell = targetsTable
	            .focusModelProperty().get().focusedCellProperty().get();
	        targetsTable.edit(focusedCell.getRow(), focusedCell.getTableColumn());
	    }
	  
	  @SuppressWarnings("unchecked")
	    private void selectPrevious() {
	        if (targetsTable.getSelectionModel().isCellSelectionEnabled()) {
	            // in cell selection mode, we have to wrap around, going from
	            // right-to-left, and then wrapping to the end of the previous line
	            TablePosition < Target, ? > pos = targetsTable.getFocusModel()
	                .getFocusedCell();
	            if (pos.getColumn() - 1 >= 0) {
	                // go to previous row
	            	targetsTable.getSelectionModel().select(pos.getRow(),
	                    getTableColumn(pos.getTableColumn(), -1));
	            } else if (pos.getRow() < targetsTable.getItems().size()) {
	                // wrap to end of previous row
	            	targetsTable.getSelectionModel().select(pos.getRow() - 1,
	            			targetsTable.getVisibleLeafColumn(
	            					targetsTable.getVisibleLeafColumns().size() - 1));
	            }
	        } else {
	            int focusIndex = targetsTable.getFocusModel().getFocusedIndex();
	            if (focusIndex == -1) {
	            	targetsTable.getSelectionModel().select(targetsTable.getItems().size() - 1);
	            } else if (focusIndex > 0) {
	            	targetsTable.getSelectionModel().select(focusIndex - 1);
	            }
	        }
	    }
	  
	  private TableColumn < Target, ? > getTableColumn(final TableColumn < Target, ? > column, int offset) {
		        int columnIndex = targetsTable.getVisibleLeafIndex(column);
		        int newColumnIndex = columnIndex + offset;
		        return targetsTable.getVisibleLeafColumn(newColumnIndex);
		    }
}
