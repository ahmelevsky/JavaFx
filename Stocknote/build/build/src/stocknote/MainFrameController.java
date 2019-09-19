package stocknote;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainFrameController implements Initializable{

	
	@FXML
	private TabPane tabs;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

	
	public void addTab(Tab tab){
		tabs.getTabs().add(tab);
	}
}
