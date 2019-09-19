package stocknote;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class RemoveDuplicatesController implements Initializable{

	
	@FXML
	private TextArea sourceTxt;
	
	@FXML
	private TextArea destTxt;
	
	@FXML
	private Label count;
	

	   private final ChangeListener<String> textListener = new ChangeListener<String>(){
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
					String source = sourceTxt.getText();
					List<String>words = Arrays.asList(source.split(","));
					List<String> result = words.stream().map(String::trim).distinct() .collect(Collectors.toCollection(ArrayList::new)); 
					destTxt.setText(String.join(", ", result));
					count.setText("Ключевых слов: " + result.size());
				}
	   };
		
		@Override
		public void initialize(URL location, ResourceBundle resources) {
			sourceTxt.textProperty().addListener(textListener);
		}


}
