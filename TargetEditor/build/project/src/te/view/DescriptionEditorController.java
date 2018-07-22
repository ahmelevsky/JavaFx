package te.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import org.apache.commons.lang3.StringUtils;

import te.Main;
import te.model.Target;

import com.sun.javafx.scene.control.behavior.TextAreaBehavior;
import com.sun.javafx.scene.control.skin.TextAreaSkin;

public class DescriptionEditorController implements Initializable {
	
	public Main app;
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
	private Text countLabel;
	
	@FXML
	private TextArea resultText;
	
	@FXML
	private Text addedTexts;
	
	private final int LIMIT = 200;
	
	public String currentDescription;
	
	ObservableList<String> options1 =    FXCollections.observableArrayList();
	ObservableList<String> options2 =    FXCollections.observableArrayList();
	ObservableList<String> options3 =    FXCollections.observableArrayList();
	ObservableList<String> options4 =    FXCollections.observableArrayList();
	ObservableList<String> options5 =    FXCollections.observableArrayList();
	
	private List<TextArea> textFields = new ArrayList<TextArea>();
	private List<ComboBox<String>> selectors = new ArrayList<ComboBox<String>>();
	private List<ObservableList<String>> options = new ArrayList<ObservableList<String>>();
	
	private List<String> textFieldsStored = new ArrayList<String>();
	
	private List<String> data = new ArrayList<String>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		selector1.setItems(options1);
		selector2.setItems(options2);
		selector3.setItems(options3);
		selector4.setItems(options4);
		selector5.setItems(options5);
		countLabel.setText("");
		
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
		selectors.add(selector1);
		selectors.add(selector2);
		selectors.add(selector3);
		selectors.add(selector4);
		selectors.add(selector5);
		
		for (int i=0;i<selectors.size();i++) {
			final int final_i = i;
			selectors.get(i).valueProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue==null || newValue.isEmpty() || newValue.equals("<�����>")) 
					textFields.get(final_i).setEditable(true);
				else 
					textFields.get(final_i).setEditable(false);
				String maxLengthDescription = getMaxLengthDescription();
				countLabel.setText("��������: " + maxLengthDescription.length());
			});
		}
		
		for (TextArea tf:textFields) {
			tf.textProperty().addListener((observable, oldValue, newValue) -> {
				String maxLengthDescription = getMaxLengthDescription();
				countLabel.setText("��������: " + maxLengthDescription.length());
			});
			denyTab(tf);
		}
	}

	public void updateLists(){
		for (ObservableList<String> ol : options){
			fillListWithStartValues(ol);
			ol.addAll(app.variables.keySet());
		}
	}
	
	private void fillListWithStartValues(List<String> list){
		list.add("<�����>");
		list.add("������");
		list.add("������1");
		list.add("������2");
	}
	
	
	public String getMaxLengthDescription(){
		
		List<String> result = new ArrayList<String>();
		for (int i=0; i<selectors.size();i++){
			String d =  selectors.get(i).getSelectionModel().getSelectedItem();
			Target maxTarget = app.getTargetWithMaxLength();
			if (d == null || d.isEmpty() || d.equals("<�����>")){
			    String 	t = textFields.get(i).getText().trim();
			    if (!t.isEmpty())
			    	result.add(t);
			}
			else if (d.equals("������")) {
				String t = maxTarget.getTarget();
				textFields.get(i).setText(t);
				if (!t.isEmpty())
				    	result.add(t);
				    
			}
			else if (d.equals("������1")) {
				String t = maxTarget.getTarget1();
				textFields.get(i).setText(t);
			    if (!t.isEmpty())
			    	result.add(t);
			}
			else if (d.equals("������2")){
				String t = maxTarget.getTarget2();
				textFields.get(i).setText(t);
			    if (!t.isEmpty())
			    	result.add(t);
			}
			else {
				List<String> list = app.variables.get(d);
				if (list!=null && !list.isEmpty()){
					String t = Collections.max(list, Comparator.comparing(s -> s.length()));
					textFields.get(i).setText(t);
				    if (!t.isEmpty())
				    	result.add(t);
				}
			}
		}
		String resultString = StringUtils.join(result, " ");
        resultText.setText(resultString);
		
		if (resultString.length() > this.LIMIT){
			countLabel.setFill(Color.RED);
		}
		else{
			countLabel.setFill(Color.BLACK);
		}
		
		return resultString;
	}
	
			
	public String generateRandomDescription(){
		List<String> result = new ArrayList<String>();
		for (int i=0; i<data.size();i++){
			if (data.get(i) == null || data.get(i).equals("<�����>")){
			    String 	t = textFieldsStored.get(i);
			    if (!t.isEmpty())
			    	result.add(t);
			}
			else if (data.get(i).equals("������")){
				String t = app.mainFrameController.currentTarget.getTarget();
			    if (!t.isEmpty())
			    	result.add(t);
			}
				
			else if (data.get(i).equals("������1")){
				String t = app.mainFrameController.currentTarget.getTarget1();
			    if (!t.isEmpty())
			    	result.add(t);
			}
			else if (data.get(i).equals("������2")){
				String t = app.mainFrameController.currentTarget.getTarget2();
			    if (!t.isEmpty())
			    	result.add(t);
			}
			else {
				List<String> list = app.variables.get(data.get(i));
				if (list!=null && !list.isEmpty()){
					String t = list.get(ThreadLocalRandom.current().nextInt(list.size()));
				    if (!t.isEmpty())
				    	result.add(t);
			}
		}
		}
		this.currentDescription = StringUtils.join(result, " ");
		return this.currentDescription;
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

	public void saveDescriptionsSource() {
		data.clear();
		for (ComboBox<String> sel:selectors){
			String item = sel.getSelectionModel().getSelectedItem();
			if (item==null)
				data.add("<�����>");
			else 
				data.add(item);
		}
		textFieldsStored.clear();
		for (int i=0;i<textFields.size();i++) 
			textFieldsStored.add(textFields.get(i).getText().trim());
	}

	public boolean checkDataIsCorrect() {
		boolean result = true;
		
		if (!textFields.stream().allMatch(t->app.isCorrectKey(t.getText()))){
			app.log("������������ ������� � ����� �� ��������� ����� ������� ��������");
			result = false;
		}
		
		if (data.stream().anyMatch(d->d.equals("������")))
			if (!app.getTargetsData().stream().allMatch(t->app.isCorrectKey(t.getTarget()))){
				app.log("������������ ������� � ����� �� ����� ������");
				result = false;
			}
		if (data.stream().anyMatch(d->d.equals("������1")))
			if (!app.getTargetsData().stream().allMatch(t->app.isCorrectKey(t.getTarget1()))){
				app.log("������������ ������� � ����� �� ����� ������1");
				result = false;
			}
		if (data.stream().anyMatch(d->d.equals("������2")))
			if (!app.getTargetsData().stream().allMatch(t->app.isCorrectKey(t.getTarget1()))){
				app.log("������������ ������� � ����� �� ����� ������2");
				result = false;
			}
		
		for (String b:data.stream().filter(a->app.variables.keySet().contains(a)).toArray(String[]::new)){
			if (!app.variables.get(b).stream().allMatch(t->app.isCorrectKey(t))){
				app.log("������������ ������� � ��������� ���������� " + b);
				result = false;
		}
		}
		return result;
	}
	
}
