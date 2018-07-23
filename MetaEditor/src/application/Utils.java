package application;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class Utils {

	public static Main app;
	
	public static void addDeleteButtonToCombobox(ComboBox<String> cb){
		  cb.setCellFactory(lv ->
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
                  label.setOnMouseClicked(event -> cb.hide());
                  Hyperlink cross = new Hyperlink("X");
                  cross.setVisited(true); // So it is black, and not blue. 
                  cross.setOnAction(event ->
                          {
                              // Since the ListView reuses cells, we need to get the item first, before making changes.  
                              String item = getItem();
                              if (isSelected()) {
                                  // Not entirely sure if this is needed. 
                                  cb.getSelectionModel().select(null);
                              }
                              cb.getItems().remove(item);
                              app.descriptionEditorController.checkLimit(false);
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
      cb.setSkin(new ComboBoxListViewSkin<String>(cb) {
      @Override
      protected boolean isHideOnClickEnabled() {
          return false;
      }
  });
	}
	
	
}
