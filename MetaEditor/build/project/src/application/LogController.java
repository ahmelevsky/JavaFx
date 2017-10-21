package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class LogController implements Initializable {

	@FXML
	private Button cleanBtn;
	
	@FXML
	private TextArea logInput;

	
	@FXML 
	private void clean(){
		logInput.clear();
	}
	
	public void log(String text){
		if (null!=text)
			logInput.appendText(text + "\n");
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
}
