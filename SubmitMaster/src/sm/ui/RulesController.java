package sm.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.text.ParseException;

import org.json.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import sm.JsonParser;
import sm.Main;
import sm.PropertyRelease;
import sm.ShutterImage;
import sm.web.ShutterProvider;

public class RulesController implements Initializable {

	public Main app;	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	@FXML
	private TextArea categoriesHelpTxt;
	
	@FXML
	private TextArea releasesHelpTxt;
	
	@FXML
	private Button loadPropertyReleasesBtn;
	
	@FXML
	private Button checkJsonBtn;
	
	@FXML
	private TextArea rulesInput;
	
	private File rulesJsonFile;
	private File releasesJsonFile;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		rulesJsonFile  =  new File(System.getProperty("user.home") + File.separator + "SubmitMasterFileRules.json");
		releasesJsonFile  =  new File(System.getProperty("user.home") + File.separator + "SubmitMasterPropertyReleases.json");
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
		
		if (this.releasesJsonFile.exists()) {
			String releasesString = null;	
				try {
					releasesString = new String(Files.readAllBytes(releasesJsonFile.toPath()));
				} catch (IOException e) {
					LOGGER.severe(e.getMessage());
				}
				if (releasesString!=null && !releasesString.isEmpty()) {
					try {
						List<PropertyRelease> releases = JsonParser.parsePropertyReleases(releasesString);
						fillReleasesArea(releases);
					}
					catch(JSONException e) {
						LOGGER.severe(e.getMessage());
					}
					catch(ParseException e) {
						LOGGER.severe(e.getMessage());
					}
				}
	}
	}
	
	private void fillReleasesArea(List<PropertyRelease> releases) {
			Collections.sort(releases);
					for (PropertyRelease release : releases) {
						releasesHelpTxt.appendText(release.name + ":" + release.id + "\r\n");
					}
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

@FXML
private void loadPropertyReleases(){
	ShutterProvider provider = app.mainController.getSession();
	List<PropertyRelease> releasesList = getPropertyReleases(provider);
	fillReleasesArea(releasesList);
}

private List<PropertyRelease> getPropertyReleases(ShutterProvider provider) {
	int per_page = 100;
	int page = 1;
	List<String> jsonList = new ArrayList<String>();
	String releasesListString = null;
	List<PropertyRelease> releasesList = new ArrayList<PropertyRelease>();
	try {
	while (true) {
		releasesListString = provider.getPropertyReleasesList(per_page,page);
		if (releasesListString == null) {
			//logError("Ошибка соединения");
			app.showAlert("Ошибка соединения с сервером");
			return releasesList;
			}
		if (releasesListString.isEmpty()) break;
		 System.out.println(releasesListString);
		jsonList.add(releasesListString);
	    
		List<PropertyRelease> releasesTemp = JsonParser.parsePropertyReleases(releasesListString);
	    if (releasesTemp.isEmpty()) break;
	    
	    releasesList.addAll(releasesTemp);
	    
		page++;
	}
	
	if (!jsonList.isEmpty()) {
		String releasesJson = JsonParser.joinJsonArrays(jsonList);
	try {
		Files.write(releasesJsonFile.toPath(), releasesJson.getBytes());
	} catch (IOException e) {
		LOGGER.severe(e.getMessage());
	}
	}
	}
	catch (JSONException e) {
		if (releasesListString.contains("Redirecting to")) {
			//logError("Autorization error");
			app.showAlert("Неправильный sessionId.");
		}
		else if (releasesListString.startsWith("{")){
			//logError("JSON parsing error");
			app.showAlert("Данные загружены, но приложение не смогло их правильно интерпретировать.");
		}
		else {
			int endIndex = releasesListString.length()>300 ? 300 : releasesListString.length();
			//logError("Unknown error");
			app.showAlert("Некорректные данные:\n" + releasesListString.substring(0, endIndex)); 
		}
		} 
	    catch (ParseException e) {
			LOGGER.severe(e.getMessage());
		}
	
	return releasesList;
}


}
