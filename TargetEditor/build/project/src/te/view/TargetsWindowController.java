package te.view;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import te.Settings;
import te.model.Target;
import te.util.TextAreaException;

public class TargetsWindowController extends TargetEditorController implements Initializable {

	
	
	 @FXML
	 private TableView<Target> targetsTable;
	 @FXML
	 private PTableColumn<Target, String> targetKwdColumn;
	 @FXML
	 private PTableColumn<Target, String> targetDescr1Column;
	 @FXML
	 private PTableColumn<Target, String> targetDescr2Column;
	 @FXML
	 private PTableColumn<Target, Button> removeRowColumn;
	 @FXML
	 private Button addBtn;
	 @FXML
	 private Button removeAllBtn;
	 @FXML
	 private TextArea inputTargetKwd;
	 @FXML
	 private TextArea inputTargetDescr1;
	 @FXML
	 private TextArea inputTargetDescr2;
	 @FXML 
	 private Button addDataBtn;
	 
	 
     public TargetsWindowController() {
	    }
 
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setTableEditable();
		this.addBtn.setShape(new Circle(25));
		this.removeAllBtn.setShape(new Circle(25));
		Callback<TableColumn<Target,String>, TableCell<Target,String>> cellFactory =
	             new Callback<TableColumn<Target,String>, TableCell<Target,String>>() {
	                 public TableCell<Target,String> call(TableColumn<Target,String> p) {
	                    return new EditCell<Target,String> (new DefaultStringConverter());
	                 }
	             };
	             
		targetKwdColumn.setCellValueFactory(cellData -> cellData.getValue().targetKwdProperty());
		targetKwdColumn.setCellFactory(cellFactory);
		targetDescr1Column.setCellValueFactory(cellData -> cellData.getValue().targetDescr1Property());
		targetDescr1Column.setCellFactory(cellFactory);
		removeRowColumn.setCellFactory(ActionButtonTableCell.<Target>forTableColumn("-", (Target p) -> {
			targetsTable.getItems().remove(p);
		    return p;
		})); 
		// Easy WAY to make Editable -  NO EDITCELL CLASS
		//target1Column.setCellFactory(TextFieldTableCell.forTableColumn());
		
		
		targetKwdColumn.setOnEditCommit(
	            new EventHandler<CellEditEvent<Target, String>>() {
	                @Override
	                public void handle(CellEditEvent<Target, String> t) {
	                    ((Target) t.getTableView().getItems().get(
	                            t.getTablePosition().getRow())
	                            ).setTargetKwd(t.getNewValue());
	                }
	            }
	        );
		
		
		targetDescr1Column.setOnEditCommit(
	            new EventHandler<CellEditEvent<Target, String>>() {
	                @Override
	                public void handle(CellEditEvent<Target, String> t) {
	                    ((Target) t.getTableView().getItems().get(
	                            t.getTablePosition().getRow())
	                            ).setTargetDescr1(t.getNewValue());
	                }
	            }
	        );
	 
		targetDescr2Column.setCellValueFactory(cellData -> cellData.getValue().targetDescr2Property());
		targetDescr2Column.setCellFactory(cellFactory);
		
		// Easy WAY to make Editable -  NO EDITCELL CLASS
		//target2Column.setCellFactory(TextFieldTableCell.forTableColumn());
		targetDescr2Column.setOnEditCommit(
	            new EventHandler<CellEditEvent<Target, String>>() {
	                @Override
	                public void handle(CellEditEvent<Target, String> t) {
	                    ((Target) t.getTableView().getItems().get(
	                            t.getTablePosition().getRow())
	                            ).setTargetDescr2(t.getNewValue());
	                }
	            }
	        );
		
		
		inputTargetKwd.textProperty().addListener((observable, oldValue, newValue) ->{ 
			    setError(inputTargetKwd, false);
			    try {
					app.checkSyntax(inputTargetKwd);
				} catch (TextAreaException e) {
					setError(e.textArea, true);
				}
			});
		inputTargetDescr1.textProperty().addListener((observable, oldValue, newValue) ->{ 
		    setError(inputTargetDescr1, false);
		    try {
				app.checkSyntax(inputTargetDescr1);
			} catch (TextAreaException e) {
				setError(e.textArea, true);
			}
		});
		inputTargetDescr2.textProperty().addListener((observable, oldValue, newValue) ->{ 
		    setError(inputTargetDescr2, false);
		    try {
				app.checkSyntax(inputTargetDescr2);
			} catch (TextAreaException e) {
				setError(e.textArea, true);
			}
		});
		
		setTableHeadersUnmovable();
		
	}

	 public void setup() {
	        targetsTable.setItems(app.getTargetsData());
	    }

	 
	  
	  private void setTableHeadersUnmovable() {
		  targetsTable.getColumns().addListener(new ListChangeListener() {
	          public boolean suspended;
	          @Override
	          public void onChanged(Change change) {
	              change.next();
	              if (change.wasReplaced() && !suspended) {
	                  this.suspended = true;
	                  targetsTable.getColumns().setAll(targetKwdColumn, targetDescr1Column, targetDescr2Column, removeRowColumn);
	                  this.suspended = false;
	              }
	          }
	      });
	  }
	 
	 
	 @FXML
	 private void addRow(){
		 app.getTargetsData().add(new Target());
	 }
	 
	 @FXML
	 private void removeAll(){
		 Alert alert = new Alert(AlertType.CONFIRMATION);
         alert.setTitle("Confirmation");
         alert.setContentText(Settings.bundle.getString("ui.tabs.targets.removeconfirmation"));
         alert.showAndWait();
         if (alert.getResult().equals(ButtonType.OK)) {
        	 targetsTable.getItems().clear();
        	 app.getTargetsData().clear();
         }
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


	@Override
	public void loadData() {
		
	}
	
    @FXML
    private void addData(){
    	String[] targetKwds = this.inputTargetKwd.getText().split("\\r?\\n");
		String[] targetDescr1 = this.inputTargetDescr1.getText().split("\\r?\\n");
		String[] targetDescr2 = this.inputTargetDescr2.getText().split("\\r?\\n");
		for (int i=0; i<targetKwds.length || i<targetDescr1.length || i<targetDescr2.length;i++){
			String targetKwdRow = targetKwds.length>i ? targetKwds[i] : "";
			String targetDescr1Row = targetDescr1.length>i ? targetDescr1[i] : "";
			String targetDescr2Row = targetDescr2.length>i ? targetDescr2[i] : "";
			if (!targetKwdRow.trim().isEmpty() || !targetDescr1Row.trim().isEmpty() || !targetDescr2Row.trim().isEmpty())
				app.targetsData.add(new Target(targetKwdRow, targetDescr1Row, targetDescr2Row));
		}
		this.inputTargetKwd.clear();
		this.inputTargetDescr1.clear();
		this.inputTargetDescr2.clear();
    }
	
	
	@Override
	public void saveData() {
		// TODO Auto-generated method stub
		
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
