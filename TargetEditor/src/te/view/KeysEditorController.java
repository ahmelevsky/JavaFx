package te.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.apache.commons.lang3.StringUtils;

import te.Main;
import te.util.DataException;
import te.util.SyntaxParser;
import te.util.TextAreaException;
import te.util.TextException;

import com.sun.javafx.scene.control.behavior.TextAreaBehavior;
import com.sun.javafx.scene.control.skin.TextAreaSkin;


public class KeysEditorController extends TargetEditorController implements Initializable {

	@FXML
	private TextArea obligatoryText;
	
	@FXML
	private TextArea backgroundText;
	
	@FXML
	private TextArea kindText;
	
	@FXML
	private TextArea highText;
	
	@FXML
	private TextArea lowText;
	
	@FXML
	private TextArea preview;

	@FXML
	private TextField lowCount;
	
	@FXML
	private TextField highCount;
	
	@FXML
	private Button clearBtn;
	
	@FXML
	private Label countLabel;
	
	@FXML
	private CheckBox isTarget;
	
	public Main app;
	
	private String obligatoryKeys;
	private String backgroundKeys;
	private String kindKeys;
	private String highKeys;
	private String lowKeys;
	private boolean isT;
	private int lCount;
	private int hCount;
	
	ObservableList<String> backgroundOptions =    FXCollections.observableArrayList();
	ObservableList<String> kindOptions =    FXCollections.observableArrayList();
	

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//obligatoryText.setText("shampoo, bubble, bubbly, blue, invite, flyer, background, swimming, pool, foam, soap, liquid, cool, bath, shower, cleaning, wash, sea, deep, 3d, oxygen, water, transparent");
		//highText.setText("abstract, closeup, wet, nature, clean, fresh, bright, drop, drink, light, air, macro, hygiene, bathroom, template, vector, splash, color, rainbow, sphere, shine, orb, circle, glossy, ball, realistic, blowing, reflection");
		//lowText.setText("fizz, flow, froth, close-up, blow, ripple, wave, purity, beverage, underwater, freshness, powder, washing powder, detergent, diving, aqua park");
		
		highCount.setText("12");
		lowCount.setText("5");
		
		isTarget.selectedProperty().addListener((observable, oldValue, newValue) -> {
			update();
		});
		obligatoryText.textProperty().addListener((observable, oldValue, newValue) -> {
			setError(obligatoryText, false, null);
			update();
		});
		highText.textProperty().addListener((observable, oldValue, newValue) -> {
			setError(highText, false, null);
			update();
		});
		lowText.textProperty().addListener((observable, oldValue, newValue) -> {
			setError(lowText, false, null);
			update();
		});
		kindText.textProperty().addListener((observable, oldValue, newValue) -> {
			setError(kindText, false, null);
			update();
		});
		backgroundText.textProperty().addListener((observable, oldValue, newValue) -> {
			setError(backgroundText, false, null);
			update();
		});
		highCount.textProperty().addListener((observable, oldValue, newValue) -> {
			update();
		});
		lowCount.textProperty().addListener((observable, oldValue, newValue) -> {
			update();
		});
		
		denyTab(obligatoryText);
		denyTab(backgroundText);
		denyTab(kindText);
		denyTab(highText);
		denyTab(lowText);
	}
	@FXML
	private void clearForms(){
		obligatoryText.clear();
		highText.clear();
		lowText.clear();
		kindText.clear();
		backgroundText.clear();
	}
	
private void update(){
	try{
		List<String> keys = getKeysFromUI();
		preview.setText(StringUtils.join(keys, ", "));
		countLabel.setText("Слов: " + keys.size());
	}
	catch (TextAreaException e){
		setError(e.textArea, true, e.getMessage());
	}
	
}

public List<String> generateKeywordsForMetadata(){
	    List<String> keys = new ArrayList<String>();
		
		addToList(obligatoryKeys, keys);
		addToList(backgroundKeys, keys);
		addToList(kindKeys, keys);
		if (isT) {
			String target = app.mainFrameController.currentTarget.getTarget();
			if (app.isCorrectKey(target))
				addToList(target, keys);
		}
		addNRandomToList(highKeys, keys, hCount);
		addNRandomToList(lowKeys, keys, lCount);
		
		keys = keys.stream().distinct().collect(Collectors.toList());
		
		cutList(keys, 50);
	
		return keys;
	}
	
	public void saveKeywordsSource() throws DataException{
		try{
		obligatoryKeys = parseVariablesInText(obligatoryText, false);
		}
		catch (TextAreaException e) {
			setError(obligatoryText, true, e.getMessage());
			throw new DataException(this.tab);
		}
		try {
		backgroundKeys = parseVariablesInText(backgroundText, false);
		}
		catch (TextAreaException e) {
			setError(backgroundText, true, e.getMessage());
			throw new DataException(this.tab);
		}
		try {
		kindKeys = parseVariablesInText(kindText, false);
		}
		catch (TextAreaException e) {
			setError(kindText, true, e.getMessage());
			throw new DataException(this.tab);
		}
		try {
		highKeys = parseVariablesInText(highText, false);
		}
		catch (TextAreaException e) {
			setError(highText, true, e.getMessage());
			throw new DataException(this.tab);
		}
		try {
		lowKeys = parseVariablesInText(lowText, false);
		}
		catch (TextAreaException e) {
			setError(lowText, true, e.getMessage());
			throw new DataException(this.tab);
		}
		isT = isTarget.isSelected();
		hCount = 0;
		lCount = 0;
		try{
			hCount = Integer.parseInt(highCount.getText());
			lCount = Integer.parseInt(lowCount.getText());
		}
		catch (Exception e){
		}
	}
	
	
	
	private List<String> getKeysFromUI() throws TextAreaException{
        List<String> keys = new ArrayList<String>();
		
		addToList(parseVariablesInText(obligatoryText, false), keys);
		addToList(parseVariablesInText(backgroundText, false), keys);
		addToList(parseVariablesInText(kindText, false), keys);
		
		if (isTarget.isSelected())
			addToList(app.getRandomTarget(), keys);
		
		int nh = 0;
		int nl = 0;
		try{
			nh = Integer.parseInt(highCount.getText());
			nl = Integer.parseInt(lowCount.getText());
		}
		catch (Exception e){
			//return null;
		}
		addNRandomToList(parseVariablesInText(highText,false), keys, nh);
		addNRandomToList(parseVariablesInText(lowText,false), keys, nl);
		
		keys = keys.stream().distinct().collect(Collectors.toList());
		cutList(keys, 50);
		return keys;
	}
	
	
	
	
	private void addToList(String string, List<String> list){
		if (string==null) return;
		String[] array = string.split(",|;");
		for (String s:array)
			if (!s.trim().isEmpty()) list.add(s.trim());
	}
	
	
	private void addNRandomToList(String string, List<String> list, int N){
		Random random = new Random();
		String[] array = string.split(",|;");
		List<String> l = new ArrayList<String>(Arrays.asList(array));
		while (N>0){
			if (l.isEmpty())
				break;
			String s = l.remove(random.nextInt(l.size()));
			if (!s.trim().isEmpty() && !list.contains(s.trim())){
			list.add(s.trim());
			N--;
			}
		}
	}
	
	
	private List<String> cutList(List<String>list, int size){
		if (list.size() > size)
			list.subList(size, list.size()).clear();
		return list;
	}
	
	
	public boolean checkDataIsCorrect(){
		return 
		app.isCorrectKey(this.obligatoryKeys) &&
		app.isCorrectKey(this.backgroundKeys) &&
		app.isCorrectKey(this.kindKeys) &&
		app.isCorrectKey(this.lowKeys) &&
		app.isCorrectKey(this.highKeys);
	}
	
	
	
	private void denyTab(TextArea textArea){
		textArea.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
	        @Override
	        public void handle(KeyEvent event) {
	            if (event.getCode() == KeyCode.TAB) {
	            	 TextAreaSkin skin = (TextAreaSkin) textArea.getSkin();
	                if (skin.getBehavior() instanceof TextAreaBehavior) {
	                    TextAreaBehavior behavior = (TextAreaBehavior) skin.getBehavior();
	                    if (event.isControlDown()) {
	                        behavior.callAction("InsertTab");
	                    } else {
	                        behavior.callAction("TraverseNext");
	                    }
	                    event.consume();
	                }

	            }
	        }
	    });
		}

	
	
	private String parseVariablesInText(TextArea tf, boolean isMax) throws TextAreaException{
		String result = null;
		try{
			result = SyntaxParser.pasteVariables(tf.getText(), isMax, ", ");	
		}
		catch (TextException e) {
			throw new TextAreaException (tf, e.getMessage());
		}
		return result;
	}
	
	private void setError(TextArea tf, boolean setOrUnset, String errorText){
		 ObservableList<String> styleClass = tf.getStyleClass();
		 ObservableList<String> styleClassresultText = countLabel.getStyleClass();
		 if (setOrUnset) {
			 if (! styleClass.contains("red")) {
	                styleClass.add("red");
	            }
			 if (errorText!=null){
				 if (! styleClassresultText.contains("redText")) {
					 styleClassresultText.add("redText");
		            }
				 countLabel.setText(errorText);
			 }
	        } 
		 else {
	            styleClass.removeAll(Collections.singleton("red"));          
	            styleClassresultText.removeAll(Collections.singleton("redText"));
	            countLabel.setText("");
	        }
	}
	
	
	}

					
