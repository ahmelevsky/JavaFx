package application;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class NewController implements Initializable {

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
	
	ObservableList<String> options1 =    FXCollections.observableArrayList();
	ObservableList<String> options2 =    FXCollections.observableArrayList();
	ObservableList<String> options3 =    FXCollections.observableArrayList();
	ObservableList<String> options4 =    FXCollections.observableArrayList();
	ObservableList<String> options5 =    FXCollections.observableArrayList();
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		selector1.setItems(options1);
		selector2.setItems(options2);
		selector3.setItems(options3);
		selector4.setItems(options4);
		selector5.setItems(options5);
		countLabel.setText("");
		
		selector1.showingProperty().addListener((obs, wasShowing, isShowing) -> {
		    if (! isShowing) {
		    	addSelectionToSelector1();
		    }
		});
		selector2.showingProperty().addListener((obs, wasShowing, isShowing) -> {
		    if (! isShowing) {
		    	addSelectionToSelector2();
		    }
		});
		selector3.showingProperty().addListener((obs, wasShowing, isShowing) -> {
		    if (! isShowing) {
		    	addSelectionToSelector3();
		    }
		});
		selector4.showingProperty().addListener((obs, wasShowing, isShowing) -> {
		    if (! isShowing) {
		    	addSelectionToSelector4();
		    }
		});
		selector5.showingProperty().addListener((obs, wasShowing, isShowing) -> {
		    if (! isShowing) {
		    	addSelectionToSelector5();
		    }
		});
		
		Utils.addDeleteButtonToCombobox(selector1);
		Utils.addDeleteButtonToCombobox(selector2);
		Utils.addDeleteButtonToCombobox(selector3);
		Utils.addDeleteButtonToCombobox(selector4);
		Utils.addDeleteButtonToCombobox(selector5);
		
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
		selector1.getSelectionModel().selectFirst();
		selector2.getSelectionModel().selectFirst();
		selector3.getSelectionModel().selectFirst();
		selector4.getSelectionModel().selectFirst();
		selector5.getSelectionModel().selectFirst();
	}
	
	@FXML
	public void generate(){
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
		addToClipboad(sb.toString());
		countLabel.setText("Символов: " + sb.toString().length());
		
	}
	
	private void addToClipboad(String theString){
		StringSelection selection = new StringSelection(theString);
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(selection, selection);
	}
	
	
	@FXML
	public void addSelectionToSelector1(){
		if (selector1.getSelectionModel().getSelectedItem()!=null)
			text1.setText(selector1.getSelectionModel().getSelectedItem());
	}
	

	@FXML
	public void addSelectionToSelector2(){
		if (selector2.getSelectionModel().getSelectedItem()!=null)
			text2.setText(selector2.getSelectionModel().getSelectedItem());
	}
	

	@FXML
	public void addSelectionToSelector3(){
		if (selector3.getSelectionModel().getSelectedItem()!=null)
			text3.setText(selector3.getSelectionModel().getSelectedItem());
	}
	

	@FXML
	public void addSelectionToSelector4(){
		if (selector4.getSelectionModel().getSelectedItem()!=null)
			text4.setText(selector4.getSelectionModel().getSelectedItem());
	}
	

	@FXML
	public void addSelectionToSelector5(){
		if (selector5.getSelectionModel().getSelectedItem()!=null)
			text5.setText(selector5.getSelectionModel().getSelectedItem());
	}
	
}
