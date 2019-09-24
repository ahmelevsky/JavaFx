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
import javafx.scene.control.TextArea;

public class CapitalizeController implements Initializable {

	@FXML
	private TextArea sourceTxt;

	@FXML
	private TextArea destTxt;

	private final ChangeListener<String> textListener = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			destTxt.setText(capitalizeWords(sourceTxt.getText()));
		}
	};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		sourceTxt.textProperty().addListener(textListener);

	}

	public String capitalizeWords(String str) {
		String lines[] = str.split("\\r?\\n");
		List<String> result = new ArrayList<String>();
		for (String line : lines) {
			String words[] = line.split("\\s");
			String capitalizeWord = "";
			for (String w : words) {
				String first = w.substring(0, 1);
				String afterfirst = w.substring(1);
				capitalizeWord += first.toUpperCase() + afterfirst + " ";
			}
			result.add(capitalizeWord.trim());
		}
		
		return String.join("\n", result);
	}

}
