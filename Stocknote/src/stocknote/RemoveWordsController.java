package stocknote;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringJoiner;

import javafx.beans.property.StringProperty;
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
				String[]wordsToRemove = rem.split(" |, |\\r?\\n");
				for (String w:wordsToRemove)
				{
					source = source.replaceAll("\\b" +w + "\\b", "");
				}
				//source = source.trim().replaceAll("\\;+", "\\;");
				//source = source.trim().replaceAll("[\\; \\;]+", "\\; ");
				//source = source.trim().replaceAll(",[\\s]+,", ",");
				
			    String[] arr  =  source.split(",");
			    StringJoiner sj = new StringJoiner(",");
			    for (String s : arr) {
			    	if (!s.trim().isEmpty())
			        sj.add(s);
			    }
			    source = sj.toString();
			    source = source.trim().replaceAll(",+", ",");
				source = source.trim().replaceAll(" +", " ");
				source = source.trim().replaceAll("\\r?\\n+", "\n");
				destTxt.setText(source);
			}
   };
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		sourceTxt.textProperty().addListener(textListener);
		removeTxt.textProperty().addListener(textListener);
	
	}

}
