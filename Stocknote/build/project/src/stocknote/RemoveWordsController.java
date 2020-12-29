package stocknote;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringJoiner;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class RemoveWordsController implements Initializable{

	@FXML
	private TextArea sourceTxt;
	
	@FXML
	private TextArea removeTxt;
	
	@FXML
	private TextArea destTxt;
	
	
   private final ChangeListener<String> textListener = new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				String source = sourceTxt.getText();
				String rem = removeTxt.getText() + " ";
				String[] wordsToRemove = rem.split("\\s*(;|,|\\n)\\s*");
				List<String> arr  =  new ArrayList<String>(Arrays.asList(source.split("\\s*(;|,|\\n)\\s*")));
				 
				for (String w:wordsToRemove)
					arr.removeIf(a -> a.equals(w.trim()));
			    StringJoiner sj = new StringJoiner(",");
			    for (String s : arr) {
			    	if (!s.trim().isEmpty())
			        sj.add(s);
			    }
			    source = sj.toString();
				destTxt.setText(source);
			}
   };
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		sourceTxt.textProperty().addListener(textListener);
		removeTxt.textProperty().addListener(textListener);
	
	}

}
