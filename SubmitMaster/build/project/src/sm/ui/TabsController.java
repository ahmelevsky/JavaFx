package sm.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import sm.Main;

public class TabsController implements Initializable {

	public Main app;	
	@FXML
	private TabPane tabs;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	
	public void addTab(Tab tab){
		tabs.getTabs().add(tab);
	}
	
}
