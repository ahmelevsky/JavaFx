package te.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import te.Main;

public class DescriptionLayoutController implements Initializable {
	
	
	public Main app;
	public HBox layout;
	public boolean isInitialLoad;
	
	@FXML
	public TextArea text;
	@FXML
	public ComboBox<String> selector;
	@FXML
	public CheckBox isRandom;
	@FXML
	private Button removeBtn;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.removeBtn.setShape(new Circle(25));
	}

	
	@FXML
    private void remove(){
		//app.descriptionEditorController.textFields.remove(text);
		//app.descriptionEditorController.selectors.remove(selector);
		app.descriptionEditorController.randomBoxes.remove(isRandom);
		app.descriptionEditorController.removeDescriptionLayout(this);
	}
}
