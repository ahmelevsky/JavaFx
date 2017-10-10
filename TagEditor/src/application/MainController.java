package application;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import org.apache.commons.lang3.StringUtils;

public class MainController implements Initializable {

	@FXML
	private TextArea obligatoryText;
	
	@FXML
	private TextArea formText;
	
	@FXML
	private TextArea backgroundText;
	
	@FXML
	private TextArea kindText;
	
	@FXML
	private TextArea highText;
	
	@FXML
	private TextArea lowText;

	@FXML
	private TextArea resultText;
	
	@FXML
	private TextField lowCount;
	
	@FXML
	private TextField highCount;
	
	@FXML
	private ComboBox<String> formSelector;
	
	@FXML
	private ComboBox<String> backgroundSelector;
	
	@FXML
	private ComboBox<String> kindSelector;
	
	@FXML
	private Button generateBtn;
	
	@FXML
	private Button clearBtn;
	
	@FXML
	private Label countLabel;
	
	ObservableList<String> formOptions =    FXCollections.observableArrayList();
	ObservableList<String> backgroundOptions =    FXCollections.observableArrayList();
	ObservableList<String> kindOptions =    FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	//	obligatoryText.setText("shampoo, bubble, bubbly, blue, invite, flyer, background, swimming, pool, foam, soap, liquid, cool, bath, shower, cleaning, wash, sea, deep, 3d, oxygen, water, transparent");
	//	highText.setText("abstract, closeup, wet, nature, clean, fresh, bright, drop, drink, light, air, macro, hygiene, bathroom, template, vector, splash, color, rainbow, sphere, shine, orb, circle, glossy, ball, realistic, blowing, reflection");
	//	lowText.setText("fizz, flow, froth, close-up, blow, ripple, wave, purity, beverage, underwater, freshness, powder, washing powder, detergent, diving, aqua park");
	//	otherText.setText("aqua, moisture, beautiful, clear, soapy,  blink, shiny, translucent,  fluid, pure, multitude, bulk, gas, surface, spray");
		
		highCount.setText("12");
		lowCount.setText("5");
		formSelector.setItems(formOptions);
		backgroundSelector.setItems(backgroundOptions);
		kindSelector.setItems(kindOptions);
		
		backgroundSelector.showingProperty().addListener((obs, wasShowing, isShowing) -> {
		    if (! isShowing) {
		    	addSelectionToBackgroundText();
		    }
		});
		formSelector.showingProperty().addListener((obs, wasShowing, isShowing) -> {
		    if (! isShowing) {
		    	addSelectionToFormText();
		    }
		});
		kindSelector.showingProperty().addListener((obs, wasShowing, isShowing) -> {
		    if (! isShowing) {
		    	addSelectionToKindText();
		    }
		});
		
		Utils.addDeleteButtonToCombobox(backgroundSelector);
		Utils.addDeleteButtonToCombobox(formSelector);
		Utils.addDeleteButtonToCombobox(kindSelector);
	}
	@FXML
	public void clearForms(){
		obligatoryText.clear();
		highText.clear();
		lowText.clear();
		kindText.clear();
		formText.clear();
		backgroundText.clear();
		resultText.clear();
	}
	@FXML
	public void generate(){
		List<String> keys = new ArrayList<String>();
		
		addToList(obligatoryText.getText(), keys);
		
		if (formText.getText() != null && !formText.getText().isEmpty() && !formOptions.contains(formText.getText()))
			formOptions.add(formText.getText());
		if (backgroundText.getText()!=null && !backgroundText.getText().isEmpty() && !backgroundOptions.contains(backgroundText.getText()))
			backgroundOptions.add(backgroundText.getText());
		if (kindText.getText()!=null && !kindText.getText().isEmpty() && !kindOptions.contains(kindText.getText()))
			kindOptions.add(kindText.getText());
		addToList(formText.getText(), keys);
		addToList(backgroundText.getText(), keys);
		addToList(kindText.getText(), keys);
		
		int nh = 0;
		int nl = 0;
		try{
			nh = Integer.parseInt(highCount.getText());
			nl = Integer.parseInt(lowCount.getText());
		}
		catch (Exception e){
			showAlert("В каком-то из числовых полей не число");
			return;
		}
		addNRandomToList(highText.getText(), keys, nh);
		addNRandomToList(lowText.getText(), keys, nl);
		
		
		/*Set<String> set = findDuplicates(keys);
		for (String s:set)
			System.err.println(s);
		*/
		keys = keys.stream().distinct().collect(Collectors.toList());
	
		cutList(keys, 50);
		
		resultText.setText(listToString(keys));
		addToClipboad(listToString(keys));
		countLabel.setText("Ключевых слов: " + keys.size());
		
	}
	
	private void addToList(String string, List<String> list){
		if (string==null) return;
		String[] array = string.split(",");
		for (String s:array)
			if (!s.trim().isEmpty()) list.add(s.trim());
	}
	
	
	private String listToString(List<String> list){
		return StringUtils.join(list, ", ");
	}
	
	private void addToClipboad(String theString){
		StringSelection selection = new StringSelection(theString);
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(selection, selection);
	}
	
	private void addNRandomToList(String string, List<String> list, int N){
		Random random = new Random();
		String[] array = string.split(",");
		List<String> l = new ArrayList<String>(Arrays.asList(array));
		while (N>0){
			if (l.isEmpty())
				break;
			String s = l.remove(random.nextInt(l.size()));
			if (!s.trim().isEmpty()){
			list.add(s.trim());
			N--;
			}
		}
	}
	
	private void cutList(List<String>list, int size){
		if (list.size() > size)
			list.subList(size, list.size()).clear();
	}
	
	
	private void showAlert(String text){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("ERROR");
		alert.setContentText(text);
		alert.showAndWait();
	}
		
	public static Set<String> findDuplicates(List<String> listContainingDuplicates) {
		 
		final Set<String> setToReturn = new HashSet<String>();
		final Set<String> set1 = new HashSet<String>();
 
		for (String yourInt : listContainingDuplicates) {
			if (!set1.add(yourInt)) {
				setToReturn.add(yourInt); 
			}
		}
		return setToReturn;
	}
	
	@FXML
	public void addSelectionToFormText(){
		if (formSelector.getSelectionModel().getSelectedItem() != null && !formSelector.getSelectionModel().getSelectedItem().isEmpty())
			formText.setText(formSelector.getSelectionModel().getSelectedItem());
	}
	
	@FXML
	public void addSelectionToBackgroundText(){
		if (backgroundSelector.getSelectionModel().getSelectedItem() != null && !backgroundSelector.getSelectionModel().getSelectedItem().isEmpty())
			backgroundText.setText(backgroundSelector.getSelectionModel().getSelectedItem());
	}
	
	@FXML
	public void addSelectionToKindText(){
		if (kindSelector.getSelectionModel().getSelectedItem() != null && !kindSelector.getSelectionModel().getSelectedItem().isEmpty())
			kindText.setText(kindSelector.getSelectionModel().getSelectedItem());
	}
	
	}

					
