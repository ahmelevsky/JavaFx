package application.ui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

import application.LogHtml;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class ChatController  extends BaseConroller  {

	 @FXML
	 private TextArea textInput;
	 
	 @FXML
	 private WebView display;
	 
	 @FXML
	 private Button sendBtn;
	 
	 @FXML
	 private Button clearBtn;
	 
	 private WebEngine webEngine;
	 private LogHtml log;
	 
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	  webEngine = display.getEngine();
	  webEngine.setUserStyleSheetLocation(getClass().getResource("/chat.css").toExternalForm());
	  sendBtn.setDisable(true);
	  clearBtn.setDisable(true);
	}
	
	public void setLogForChat(LogHtml log){
		this.log=log;
	}

	public void sendBtnPressed(ActionEvent event){
		sendMessage();
	}
	
	public void pressEnter(KeyEvent event) {
		//В будущем сделать варианты CTRL+Enter
		if (event.getCode().equals(KeyCode.ENTER)) { sendMessage();
		event.consume();
		}
	}
	
	
	public void sendMessage(){
		String text = textInput.getText();
		if (text.isEmpty()) return;
		log.addMessage(text);
		webEngine.loadContent(log.getLog());
		webEngine.executeScript("window.scrollTo(0, document.body.scrollHeight);");
		textInput.clear();
		sendBtn.setDisable(true);
		clearBtn.setDisable(false);
	}
	
	
	public void clearWindow(ActionEvent event) {
		log.clearLog();
		webEngine.loadContent(log.getLog());
		clearBtn.setDisable(true);
	}
	
	public void typeSomething(KeyEvent event) {
		if (!textInput.getText().isEmpty()) 
			sendBtn.setDisable(false);
		else sendBtn.setDisable(true);
		}
}
