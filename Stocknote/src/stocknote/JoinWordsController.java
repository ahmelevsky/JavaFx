package stocknote;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class JoinWordsController implements Initializable{

	@FXML
	private TextArea beginTxt;
	
	@FXML
	private TextArea endTxt;
	
	@FXML
	private TextArea destTxt;
	
	
   private final ChangeListener<String> textListener = new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				String[]wordsBegin = beginTxt.getText().split(",|\\r?\\n");
				String[]wordsEnd = endTxt.getText().split(", |\\r?\\n");
				
				StringBuilder sb = new StringBuilder();
				for (String wb:wordsBegin) {
					for (String we:wordsEnd) {
						sb.append(wb.trim() + " "  + we.trim() + "\n");
					}

				}
				destTxt.setText(sb.toString());
			}
   };
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		beginTxt.textProperty().addListener(textListener);
		endTxt.textProperty().addListener(textListener);
	
	}

}
