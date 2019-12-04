package te.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AboutWindowController implements Initializable {

	@FXML
	private Button closeBtn;
	@FXML
	private Label aboutText;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		aboutText.setText("TargetEditor version 2.4\n\n2019");
		
	}
	
	@FXML
	private void closeButtonAction(){
	    // get a handle to the stage
	    Stage stage = (Stage) closeBtn.getScene().getWindow();
	    // do what you have to do
	    stage.close();
	}

}
