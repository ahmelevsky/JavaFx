package application;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class DescriptionEditorController implements Initializable {

	@FXML
	private TextArea text1;
	
	@FXML
	private TextArea text2;
	
	@FXML
	private TextArea text3;
	
	@FXML
	private TextArea text4;
	
	@FXML
	private TextArea text5;
	
	@FXML
	private ComboBox<String> selector1;
	
	@FXML
	private ComboBox<String> selector2;
	
	@FXML
	private ComboBox<String> selector3;
	
	@FXML
	private ComboBox<String> selector4;
	
	@FXML
	private ComboBox<String> selector5;
	
	@FXML
	private Button generateBtn;
	
	@FXML
	private Button clearBtn;
	
	@FXML
	private Label countLabel;
	
	@FXML
	private TextArea resultText;
	
	private final int LIMIT = 200;
	
	
	ObservableList<String> options1 =    FXCollections.observableArrayList();
	ObservableList<String> options2 =    FXCollections.observableArrayList();
	ObservableList<String> options3 =    FXCollections.observableArrayList();
	ObservableList<String> options4 =    FXCollections.observableArrayList();
	ObservableList<String> options5 =    FXCollections.observableArrayList();
	
	private List<TextArea> textFields = new ArrayList<TextArea>();
	private List<ObservableList<String>> options = new ArrayList<ObservableList<String>>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		selector1.setItems(options1);
		selector2.setItems(options2);
		selector3.setItems(options3);
		selector4.setItems(options4);
		selector5.setItems(options5);
		countLabel.setText("");
		
		Utils.addDeleteButtonToCombobox(selector1);
		Utils.addDeleteButtonToCombobox(selector2);
		Utils.addDeleteButtonToCombobox(selector3);
		Utils.addDeleteButtonToCombobox(selector4);
		Utils.addDeleteButtonToCombobox(selector5);
		
		textFields.add(text1);
		textFields.add(text2);
		textFields.add(text3);
		textFields.add(text4);
		textFields.add(text5);
		options.add(options1);
		options.add(options2);
		options.add(options3);
		options.add(options4);
		options.add(options5);
	}

	@FXML
	public void clearForms(){
		text1.clear();
		text2.clear();
		text3.clear();
		text4.clear();
		text5.clear();
		resultText.clear();
		countLabel.setText("");
		options1.clear();
		options2.clear();
		options3.clear();
		options4.clear();
		options5.clear();
	}
	
	@FXML
	public String add(){
		
		if (!checkLimit())
			return "";
		
		StringBuilder sb = new StringBuilder();
	
		
		sb.append(text1.getText().trim());
		if (!text2.getText().trim().isEmpty()) sb.append(" ");
		sb.append(text2.getText().trim());
		if (!text3.getText().trim().isEmpty()) sb.append(" ");
		sb.append(text3.getText().trim());
		if (!text4.getText().trim().isEmpty()) sb.append(" ");
		sb.append(text4.getText().trim());
		if (!text5.getText().trim().isEmpty()) sb.append(" ");
		sb.append(text5.getText().trim());
		
		if (text1.getText()!=null && !text1.getText().isEmpty() && !options1.contains(text1.getText()))
			options1.add(text1.getText());
		if (text2.getText()!=null && !text2.getText().isEmpty() && !options2.contains(text2.getText()))
			options2.add(text2.getText());
		if (text3.getText()!=null && !text3.getText().isEmpty() && !options3.contains(text3.getText()))
			options3.add(text3.getText());
		if (text4.getText()!=null && !text4.getText().isEmpty() && !options4.contains(text4.getText()))
			options4.add(text4.getText());
		if (text5.getText()!=null && !text5.getText().isEmpty() && !options5.contains(text5.getText()))
			options5.add(text5.getText());
		
		resultText.setText(sb.toString());
		//addToClipboad(sb.toString());
		countLabel.setText("Символов: " + sb.toString().length());
		return sb.toString();
		
	}
	
	
	public String generateRandomDescription(){
		StringBuilder sb = new StringBuilder();
		String text = getRandomOption(options1);
		sb.append(text);
		text = getRandomOption(options2);
		if (!text.isEmpty()) 
			sb.append(" " + text);
		text = getRandomOption(options3);
		if (!text.isEmpty()) 
			sb.append(" " + text);
		text = getRandomOption(options4);
		if (!text.isEmpty()) 
			sb.append(" " + text);
		text = getRandomOption(options5);
		if (!text.isEmpty()) 
			sb.append(" " + text);
		return sb.toString();
	}
	
	private String getRandomOption(ObservableList<String> variants){
		if (variants.isEmpty()) return "";
		return variants.get(new Random().nextInt(variants.size()));
	}
	
	private void addToClipboad(String theString){
		StringSelection selection = new StringSelection(theString);
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(selection, selection);
	}
	
	
	private boolean checkLimit(){
		
		for (int i=0;i<textFields.size();i++ ){
			int newTextlength = textFields.get(i).getText().trim().length();
			if (newTextlength==0) continue;
			
			for  (int j=0;j<options.size();j++ ){
				if (j==i) continue;
				int optstringlength = longestString(options.get(j));
				if (optstringlength>0){
					newTextlength++;
					newTextlength+=optstringlength;
				}
			}
			if (newTextlength>this.LIMIT){
			
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error!");
				alert.setHeaderText("Ошибка добавления");
				alert.setContentText("Текст <" + textFields.get(i).getText() + "> в некоторых комбинациях приводит к превышению ограничения в " + this.LIMIT + " символов.");
				alert.showAndWait();
				textFields.get(i).setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
				
			return false;
			}
			else {
				textFields.get(i).setStyle("-fx-border-color: black ; -fx-border-width: 0px ;");
			}
		}
		return true;
	}
	
	private int longestString(List<String> list){
		if (list.isEmpty()) return 0;
		return Collections.max(list, Comparator.comparing(s -> s.length())).length();
	}
	
}
