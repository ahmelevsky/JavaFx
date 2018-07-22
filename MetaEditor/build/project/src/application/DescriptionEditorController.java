package application;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.StringJoiner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import com.sun.javafx.scene.control.behavior.TextAreaBehavior;
import com.sun.javafx.scene.control.skin.TextAreaSkin;

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
	private Text countLabel;
	
	@FXML
	private TextArea resultText;
	
	@FXML
	private Text addedTexts;
	
	private final int LIMIT = 200;
	
	
	
	ObservableList<String> options1 =    FXCollections.observableArrayList();
	ObservableList<String> options2 =    FXCollections.observableArrayList();
	ObservableList<String> options3 =    FXCollections.observableArrayList();
	ObservableList<String> options4 =    FXCollections.observableArrayList();
	ObservableList<String> options5 =    FXCollections.observableArrayList();
	
	private List<TextArea> textFields = new ArrayList<TextArea>();
	private List<ObservableList<String>> options = new ArrayList<ObservableList<String>>();
	
	List<String> descriptions1 = new ArrayList<String>();
	List<String> descriptions2 = new ArrayList<String>();
	List<String> descriptions3 = new ArrayList<String>();
	List<String> descriptions4 = new ArrayList<String>();
	List<String> descriptions5 = new ArrayList<String>();
	 
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		ImageView imageView = new ImageView( new Image(new File("resources/add.png").toURI().toString()));

		imageView.setFitWidth(50);

		imageView.setFitHeight(50);

		generateBtn.setGraphic(imageView);
		
		//Background background = new Background(backgroundImage);
	    //generateBtn.setBackground(background);
		
		
		
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
		
		
		for (TextArea tf:textFields) {
			tf.textProperty().addListener((observable, oldValue, newValue) -> {
				
				countLabel.setText("Символов: " + recount());
			});
			denyTab(tf);
		}
	}

	
	
	
	private int recount(){
		StringJoiner field = new StringJoiner(" ");
		for (TextArea tf:textFields)
			if (!tf.getText().trim().isEmpty())
				field.add(tf.getText().trim());
		
		resultText.setText(field.toString());
		
		if (field.toString().length() > this.LIMIT){
			generateBtn.setDisable(true);
			countLabel.setFill(Color.RED);
		}
		else{
			generateBtn.setDisable(false);
			countLabel.setFill(Color.BLACK);
		}
			
		
		return field.toString().length();
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
		
		//addToClipboad(sb.toString());
		countLabel.setText("Символов: " + sb.toString().length());
		return sb.toString();
		
	}
	
	public void saveDescriptionsSource(){
		descriptions1.clear();
		descriptions1.addAll(options1);
		
		descriptions2.clear();
		descriptions2.addAll(options2);
		
		descriptions3.clear();
		descriptions3.addAll(options3);
		
		descriptions4.clear();
		descriptions4.addAll(options4);
		
		descriptions5.clear();
		descriptions5.addAll(options5);
	}
	
	public String generateRandomDescriptionForMetadata(){
		StringBuilder sb = new StringBuilder();
		String text = getRandomOption(descriptions1);
		sb.append(text);
		text = getRandomOption(descriptions2);
		if (!text.isEmpty()) 
			sb.append(" " + text);
		text = getRandomOption(descriptions3);
		if (!text.isEmpty()) 
			sb.append(" " + text);
		text = getRandomOption(descriptions4);
		if (!text.isEmpty()) 
			sb.append(" " + text);
		text = getRandomOption(descriptions5);
		if (!text.isEmpty()) 
			sb.append(" " + text);
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
	
	private String getRandomOption(List<String> variants){
		if (variants.isEmpty()) return "";
		return variants.get(new Random().nextInt(variants.size()));
	}
	
	private void addToClipboad(String theString){
		StringSelection selection = new StringSelection(theString);
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(selection, selection);
	}
	
	
	private boolean checkLimit(){

		int partsCount = 0;
		int longest = 0; 
		for (int i=0;i<textFields.size();i++ ) {
			List<String> variants = new ArrayList<String>();
			variants.addAll(options.get(i));
			if (!textFields.get(i).getText().trim().isEmpty())
				variants.add(textFields.get(i).getText().trim());
			int optstringlength = longestString(variants);
			if (optstringlength>0) {
				partsCount++;
			    longest +=optstringlength;
			}
		}
		if (partsCount>1) {
			longest += (partsCount-1);
		}
		
			addedTexts.setText("Наиболее длинная комбинация, символов: " + longest);
			
			if (longest > this.LIMIT){
				addedTexts.setFill(Color.RED);
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error!");
				alert.setHeaderText("Ошибка добавления");
				alert.setContentText("Некоторые комбинации приводит к превышению ограничения в " + this.LIMIT + " символов.");
				alert.showAndWait();
				
			 return false;
			}
			else{
				addedTexts.setFill(Color.BLACK);
			    return true;
			}
		  
	}
	
/*	
	private int getCurrentLength(){
		StringJoiner field = new StringJoiner(" ");
		for (TextArea tf:textFields)
			if (!tf.getText().trim().isEmpty())
				field.add(tf.getText().trim());
		
		return field.toString().length();
	}
	*/
	
	private int longestString(List<String> list){
		if (list.isEmpty()) return 0;
		return Collections.max(list, Comparator.comparing(s -> s.length())).length();
	}
	
	
	public boolean checkDataIsCorrect(){
		return 
		descriptions1.stream().allMatch(s -> MetadataWriter.isCorrectKey(s)) &&
		descriptions2.stream().allMatch(s -> MetadataWriter.isCorrectKey(s)) &&
		descriptions3.stream().allMatch(s -> MetadataWriter.isCorrectKey(s)) &&
		descriptions4.stream().allMatch(s -> MetadataWriter.isCorrectKey(s)) &&
		descriptions5.stream().allMatch(s -> MetadataWriter.isCorrectKey(s));
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
	
}
