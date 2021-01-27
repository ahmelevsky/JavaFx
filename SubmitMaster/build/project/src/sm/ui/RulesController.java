package sm.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.json.*;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import sm.Main;

public class RulesController implements Initializable {

	public Main app;	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	@FXML
	private TextArea categoriesHelpTxt;
	
	@FXML
	private Button checkJsonBtn;
	
	@FXML
	private TextArea rulesInput;
	
	private File rulesJsonFile;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		rulesJsonFile  =  new File(System.getProperty("user.home") + File.separator + "SubmitMasterFileRules.json");
	}

	
	public void setup() {
		categoriesHelpTxt.setText("Abstract:26\r\n" + 
				"Animals:1\r\n" + 
				"The Arts:11\r\n" + 
				"Backgrounds:3\r\n" + 
				"Beauty/Fashion:27\r\n" + 
				"Buildings/Landmarks:2\r\n" + 
				"Business/Finance:4\r\n" + 
				"Celebrities:31 \r\n" + 
				"Education:5\r\n" + 
				"Food and Drink:6\r\n" + 
				"Healthcare/Medical:7\r\n" + 
				"Holidays:8\r\n" + 
				"Industrial:10\r\n" + 
				"Interiors:21\r\n" + 
				"Miscellaneous:22\r\n" + 
				"Nature:12\r\n" + 
				"Objects:9\r\n" + 
				"Parks/Outdoor:25\r\n" + 
				"People:13\r\n" + 
				"Religion:14\r\n" + 
				"Science:15\r\n" + 
				"Signs/Symbols:17\r\n" + 
				"Sports/Recreation:18\r\n" + 
				"Technology:16\r\n" + 
				"Transportation:0\r\n" + 
				"Vintage:24 \n"
				+ "");
		
		loadRules();
	}
	
	public void unload() {
		 saveRules();
	}
	
	
	private void saveRules() {
		ObservableList<CharSequence> list = rulesInput.getParagraphs();
		try {
			Files.write(rulesJsonFile.toPath(), list);
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
		}
		//Files.write(rulesJsonFile.toPath(),lines);
	}
	
	private void loadRules() {
		if (!rulesJsonFile.exists()) 
			rulesInput.setText("{\r\n" + 
					"\"aaaa\":{\"categories\":[3,26],\"isillustration\":true,release:\"\"},\r\n" + 
					"\"bbbb\":{\"categories\":[3,26],\"isillustration\":false,release:\"releasefilename\"},\r\n" + 
					"\"cccc\":{\"categories\":[3,26],\"isillustration\":true,release:\"\"}\r\n" + 
					"}"
					+ "");
		else
		try {
			List<String> lines = Files.readAllLines(rulesJsonFile.toPath());
			lines.forEach(l -> rulesInput.appendText(l + "\r\n"));
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
		}
		
	}
	
	
@FXML
private void checkRulesJson() {
	if (isJSONValid(rulesInput.getText())) 
		app.showAlertOK("Checked");
			//rulesInput.setStyle("text-area-background: green;");
	else
		app.showAlert("Incorrect Json format");
			//rulesInput.setStyle("text-area-background: red;");
}
	
	
public boolean isJSONValid(String test) {
    try {
        new JSONObject(test);
    } catch (JSONException ex) {
        try {
            new JSONArray(test);
        } catch (JSONException ex1) {
            return false;
        }
    }
    return true;
}





}
