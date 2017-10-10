package application.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.Main;
import javafx.fxml.Initializable;

public abstract class BaseConroller implements Initializable {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	Main application;
	
	public void setApp(Main application) {
	    this.application = application;
	}

}