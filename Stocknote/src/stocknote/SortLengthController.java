package stocknote;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class SortLengthController implements Initializable {

	@FXML
	private TextArea sourceTxt;

	@FXML
	private TextArea destTxt;

	private final ChangeListener<String> textListener = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			destTxt.setText(sortStrings(sourceTxt.getText()));
		}
	};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		sourceTxt.textProperty().addListener(textListener);

	}

	public String sortStrings(String str) {
		String[] lines = str.split("\\s*(;|,|\\n)\\s*");
		List<String> words = Arrays.asList(lines);
		 Comparator c = new Comparator<String>()
		    {
		        public int compare(String s1, String s2) {
		            return Integer.compare(s1.length(), s2.length());
		        }
		    };
		    Collections.sort(words, c);
		return String.join("\n", words.stream().distinct().collect(Collectors.toList()));
	}

}
