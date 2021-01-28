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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import sm.Category;
import sm.FileRule;
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
	private Button loadCategoriesBtn;
	
	@FXML
	private Button checkJsonBtn;
	
	@FXML
	private Button applyRulesBtn;
	
	@FXML
	private TextArea rulesInput;
	
	private File rulesJsonFile;
	private File releasesJsonFile;
	private File categoriesJsonFile;
	
	public List<Category> categories;
	public List<PropertyRelease> propertyReleases;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		rulesJsonFile  =  new File(System.getProperty("user.home") + File.separator + "SubmitMasterFileRules.json");
		releasesJsonFile  =  new File(System.getProperty("user.home") + File.separator + "SubmitMasterPropertyReleases.json");
		categoriesJsonFile  =  new File(System.getProperty("user.home") + File.separator + "SubmitMasterCategories.json");
	}

	
	public void setup() {
		/*
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
		*/
		loadRules();
		
		
		if (this.categoriesJsonFile.exists()) {
			fillCategories();
	}
		
		
		if (this.releasesJsonFile.exists()) {
			fillPropertyReleases();
	}
	}
	
	private void fillCategories() {
		String categoriesString = null;	
		try {
			categoriesString = new String(Files.readAllBytes(categoriesJsonFile.toPath()));
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
		}
		if (categoriesString!=null && !categoriesString.isEmpty()) {
			try {
				this.categories = JsonParser.parseCaterogies(categoriesString);
				fillCategoriesArea(this.categories);
			}
			catch(JSONException e) {
				LOGGER.severe(e.getMessage());
			}
		}
	}
	
	private void fillPropertyReleases() {
		String releasesString = null;	
		try {
			releasesString = new String(Files.readAllBytes(releasesJsonFile.toPath()));
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
		}
		if (releasesString!=null && !releasesString.isEmpty()) {
			try {
				this.propertyReleases = JsonParser.parsePropertyReleases(releasesString);
				fillReleasesArea(this.propertyReleases);
			}
			catch(JSONException e) {
				LOGGER.severe(e.getMessage());
			}
			catch(ParseException e) {
				LOGGER.severe(e.getMessage());
			}
		}
	}
	
	
	private void fillReleasesArea(List<PropertyRelease> releases) {
			Collections.sort(releases);
					releasesHelpTxt.clear();
					for (PropertyRelease release : releases) {
						releasesHelpTxt.appendText(release.name + ":" + release.id + "\r\n");
					}
	}
	
	private void fillCategoriesArea(List<Category> categories) {
				categoriesHelpTxt.clear();
				for (Category category : categories) {
					categoriesHelpTxt.appendText(category.name + ":" + category.cat_id + "\r\n");
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
			rulesInput.setText("[\r\n" + 
					"{\"file\":\"aaaa\",\"categories\":[\"3\",\"26\"],\"isillustration\":true,\"releases\":[\"19588837\",\"39554837\"]},\r\n" + 
					"{\"file\":\"bbbb\",\"categories\":[\"3\",\"26\"],\"isillustration\":false,\"releases\":[\"19588843\"]},\r\n" + 
					"{\"file\":\"cccc\",\"categories\":[\"3\",\"26\"],\"isillustration\":true,\"releases\":[]}\r\n" + 
					"]"
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


@FXML
private void loadCategories(){
	ShutterProvider provider = app.mainController.getSession();
	List<Category> categoriesList = getCategories(provider);
	if (categoriesList!=null && !categoriesList.isEmpty())
		fillCategoriesArea(categoriesList);
}

private List<Category> getCategories(ShutterProvider provider) {
	try {
		String categoriesString = provider.getCategoriesList();
		System.out.println(categoriesString);
		List<Category> categories = JsonParser.parseCaterogies(categoriesString);
	try {
		Files.write(categoriesJsonFile.toPath(), categoriesString.getBytes());
	} catch (IOException e) {
		LOGGER.severe(e.getMessage());
	}
		return categories;
	}
	catch (JSONException e) {
			app.showAlert(e.getMessage());
			LOGGER.severe(e.getMessage());
			return null;
	}

}


@FXML
private void applyRules() {
	List<FileRule> rules = null;
	try {
		rules = JsonParser.parseFileRules(rulesInput.getText());
	}
	catch (JSONException e) {
		LOGGER.severe(e.getMessage());
		app.showAlert("Error parsing rules: " + e.getMessage());
		return;
	}
		if (this.categoriesJsonFile.exists())
			fillCategories();
		else {
			app.showAlert("Categories are empty. Please Load.");
			return;
		}
		if (this.releasesJsonFile.exists())
			fillPropertyReleases();
		else {
			app.showAlert("PropertyReleases are empty. Please Load.");
			return;
		}
	
	Collections.sort(rules);
	
	for (ShutterImage im:app.mainController.images) {
		for (FileRule rule:rules) {
			if (im.getUploaded_filename().toLowerCase().startsWith(rule.prefix.toLowerCase())) {
				im.categories.clear();
				im.categories.addAll(rule.categories);
				im.releases.clear();
				im.releases.addAll(rule.releases);
				im.categoriesNames.clear();
				rule.categories.forEach(p->im.categoriesNames.add(getCategoryNameById(p)));
				im.releasesNames.clear();
				rule.releases.forEach(p->im.releasesNames.add(getPropertyReleaseNameById(p)));
				im.setIs_illustration(rule.isIllustration);
				im.setStatus("Ready");
			}
		}
	}
}


private String getCategoryNameById(String id) {
	if (this.categories==null)
		return "";
	Category cat = this.categories.stream().filter(c->c.cat_id.equals(id)).findFirst().orElse(null);
	if (cat==null)
		return "";
	else return cat.name;
}

private String getPropertyReleaseNameById(String id) {
	if (this.propertyReleases==null)
		return "";
	PropertyRelease release = this.propertyReleases.stream().filter(c->c.id.equals(id)).findFirst().orElse(null);
	if (release==null)
		return "";
	else return release.name;
}

}
