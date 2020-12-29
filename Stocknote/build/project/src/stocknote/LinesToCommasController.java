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

public class LinesToCommasController implements Initializable {

	@FXML
	private TextArea sourceTxt;

	@FXML
	private TextArea destTxt;

	private final ChangeListener<String> textListener = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			destTxt.setText(addDots(sourceTxt.getText()));
		}
	};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		sourceTxt.textProperty().addListener(textListener);

	}

	public String addDots(String str) {
		String lines[] = str.split("\\r?\\n");
		StringBuilder result = new StringBuilder();
		for (String line : lines) {
			String l = line.trim();
			while (l.endsWith(",")) {
				l = l.replaceAll(",$","");
				l = l.trim();
			}
			if (!l.isEmpty()) { 
				result.append(l + ", ");
				}
		}
		
		return result.toString().trim();
	}

}
