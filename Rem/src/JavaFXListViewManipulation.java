import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

import javafx.application.Application;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
 
public class JavaFXListViewManipulation extends Application {
 
	
	
	
	public void start(Stage primaryStage) throws Exception {
	    ComboBox<String> cba = new ComboBox<>();
	    ComboBox<String> cbb = new ComboBox<>();
	    cba.getItems().addAll("A", "B", "C");
	    cbb.getItems().addAll("123", "456", "789");

	    // Set a cell factory for ComboBox A. A similar thing should be done for B. 
	    cba.setCellFactory(lv ->
	            new ListCell<String>() {
	                // This is the node that will display the text and the cross. 
	                // I chose a hyperlink, but you can change to button, image, etc. 
	                private HBox graphic;

	                // this is the constructor for the anonymous class.
	                {
	                    Label label = new Label();
	                    // Bind the label text to the item property. If your ComboBox items are not Strings you should use a converter. 
	                    label.textProperty().bind(itemProperty());
	                    // Set max width to infinity so the cross is all the way to the right. 
	                    label.setMaxWidth(Double.POSITIVE_INFINITY);
	                    // We have to modify the hiding behavior of the ComboBox to allow clicking on the hyperlink, 
	                    // so we need to hide the ComboBox when the label is clicked (item selected). 
	                    label.setOnMouseClicked(event -> cba.hide());

	                    Hyperlink cross = new Hyperlink("X");
	                    cross.setVisited(true); // So it is black, and not blue. 
	                    cross.setOnAction(event ->
	                            {
	                                // Since the ListView reuses cells, we need to get the item first, before making changes.  
	                                String item = getItem();
	                                System.out.println("Clicked cross on " + item);
	                                if (isSelected()) {
	                                    // Not entirely sure if this is needed. 
	                                    cba.getSelectionModel().select(null);
	                                }
	                                // Remove the item from A and add to B. You can add any additional logic in here. 
	                                cba.getItems().remove(item);
	                                cbb.getItems().add(item);
	                            }
	                    );
	                    // Arrange controls in a HBox, and set display to graphic only (the text is included in the graphic in this implementation). 
	                    graphic = new HBox(label, cross);
	                    graphic.setHgrow(label, Priority.ALWAYS);
	                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	                }

	                @Override
	                protected void updateItem(String item, boolean empty) {
	                    super.updateItem(item, empty);
	                    if (empty) {
	                        setGraphic(null);
	                    } else {
	                        setGraphic(graphic);
	                    }
	                }
	            });

	    // We have to set a custom skin, otherwise the ComboBox disappears before the click on the Hyperlink is registered. 
	    cba.setSkin(new ComboBoxListViewSkin<String>(cba) {
	        @Override
	        protected boolean isHideOnClickEnabled() {
	            return false;
	        }
	    });

	    VBox vb = new VBox(cba, cbb);
	    primaryStage.setScene(new Scene(vb));
	    primaryStage.show();
	}
	
	
	
	//@Override 
	public void start2(final Stage stage) {
    final Label status       = new Label();
    final Label changeReport = new Label();
    
    final ListView<String> listView = new ListView<>();
    initListView(listView);

    listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
      @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        changeReport.setText("Selection changed from '" + oldValue + "' to '" + newValue + "'");
      }
    });

    final Button removeButton = new Button("Remove Selected");
    removeButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override public void handle(ActionEvent event) {
        final int selectedIdx = listView.getSelectionModel().getSelectedIndex();
        if (selectedIdx != -1) {
          String itemToRemove = listView.getSelectionModel().getSelectedItem();
 
          final int newSelectedIdx =
            (selectedIdx == listView.getItems().size() - 1)
               ? selectedIdx - 1
               : selectedIdx;
 
          listView.getItems().remove(selectedIdx);
          status.setText("Removed " + itemToRemove);
          listView.getSelectionModel().select(newSelectedIdx);
        }
      }
    });
    final Button resetButton = new Button("Reset List");
    resetButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override public void handle(ActionEvent event) {
        initListView(listView);
        status.setText("List Reset");
      }
    });
    final HBox controls = new HBox(10);
    controls.setAlignment(Pos.CENTER);
    controls.getChildren().addAll(removeButton, resetButton);
 
    final VBox layout = new VBox(10);
    layout.setAlignment(Pos.CENTER);
    layout.setStyle("-fx-padding: 10; -fx-background-color: cornsilk;");
    layout.getChildren().setAll(
      listView, 
      controls,
      status,
      changeReport
    );
    layout.setPrefWidth(320);
    
    stage.setScene(new Scene(layout));
    stage.show();
  }
 
  private void initListView(ListView<String> listView) {
    listView.getItems().setAll("apples", "oranges", "peaches", "pears");
  }
  
  public static void main(String[] args) { launch(args); }
}