package am.ui;

import java.net.URL;
import java.util.ResourceBundle;

import am.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class TabsController implements Initializable {

	public Main app;	
	@FXML
	private TabPane tabs;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	public void setup() {
		tabs.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
			if (newTab.equals(app.mainController.tab))
				app.mainController.loadData();
			if (newTab.equals(app.setController.tab))
				app.setController.loadData();
		});
	}
	
	public void addTab(Tab tab){
		tabs.getTabs().add(tab);
	}
	
}
