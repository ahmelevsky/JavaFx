package te.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import org.apache.commons.lang3.StringUtils;

import te.model.Target;
import te.model.Variable;
import te.util.DataException;
import te.util.SyntaxParser;
import te.util.TextAreaException;
import te.util.TextException;

import com.sun.javafx.scene.control.behavior.TextAreaBehavior;
import com.sun.javafx.scene.control.skin.TextAreaSkin;

public class DescriptionEditorController extends TargetEditorController implements Initializable {
	
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
	
	private List<TextArea> textFields = new ArrayList<TextArea>();
	private List<ComboBox<String>> selectors = new ArrayList<ComboBox<String>>();
	private List<ObservableList<String>> options = new ArrayList<ObservableList<String>>();
	
	public List<String> textFieldsStored = new ArrayList<String>();
	private List<String> data = new ArrayList<String>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		countLabel.setText("");
		
		textFields.add(text1);
		textFields.add(text2);
		textFields.add(text3);
		textFields.add(text4);
		textFields.add(text5);
		selectors.add(selector1);
		selectors.add(selector2);
		selectors.add(selector3);
		selectors.add(selector4);
		selectors.add(selector5);
		
		
		for (int i=0;i<selectors.size();i++) {
			options.add(FXCollections.observableArrayList());
			final int final_i = i;
			selectors.get(i).setItems(options.get(i));
			options.get(i).add("<текст>");
			selectors.get(i).getSelectionModel().select("<текст>");
			
			selectors.get(i).valueProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue==null) return;
				if (newValue.equals("<текст>")) {
					textFields.get(final_i).setEditable(true);
					if (oldValue!=null && !oldValue.equals("<текст>"))
						textFields.get(final_i).setText("");
				}
				else 
					textFields.get(final_i).setEditable(false);
				setError(textFields.get(final_i), false, null);
				 try {	
						getMaxLengthDescription();
					 }
					 catch (TextAreaException e) {
						setError(e.textArea, true, e.getMessage());
					 }
					});
		}
		
		for (TextArea tf:textFields) {
			
			denyTab(tf);
			
			tf.textProperty().addListener((observable, oldValue, newValue) -> {
				setError(tf, false, null);
			 try {	
				getMaxLengthDescription();
			 }
			 catch (TextAreaException e) {
				setError(e.textArea, true, e.getMessage());
			 }
			});
			
		}
	}
	
	private String parseVariablesInText(TextArea tf, boolean isMax) throws TextAreaException{
		String result = null;
		try{
			result = SyntaxParser.pasteVariables(app.descriptionVariableEditorContainerController.variables, tf.getText(), isMax, " ");	
		}
		catch (TextException e) {
			throw new TextAreaException (tf, e.getMessage());
		}
		return result;
	}
	
	
	
	private void setError(TextArea tf, boolean setOrUnset, String errorText){
		 ObservableList<String> styleClass = tf.getStyleClass();
		 ObservableList<String> styleClassresultText = resultText.getStyleClass();
		 if (setOrUnset) {
			 if (! styleClass.contains("red")) {
	                styleClass.add("red");
	            }
			 if (errorText!=null){
				 if (! styleClassresultText.contains("redText")) {
					 styleClassresultText.add("redText");
		            }
				 resultText.setText(errorText);
			 }
	        } 
		 else {
	            styleClass.removeAll(Collections.singleton("red"));          
	            styleClassresultText.removeAll(Collections.singleton("redText"));
	        }
	}
	
	public void updateLists(){
		for (int i=0; i<selectors.size();i++){
			
			SingleSelectionModel<String> selected = selectors.get(i).selectionModelProperty().getValue();
			String selectedValue = null;
			if (selected!=null && !selected.isEmpty())
				selectedValue = selected.getSelectedItem();
			ObservableList<String> ol = options.get(i);
			ol.clear();
			ol.add("<текст>");
			if (!app.getTargetsData().isEmpty()){
				ol.add("Таргет");
				ol.add("Таргет1");
				ol.add("Таргет2");
			}
			ol.addAll(app.descriptionVariableEditorContainerController.variables.stream().map(Variable::getName)
		              .collect(Collectors.toList()));
			if (selectedValue!=null){
				if	(ol.contains(selectedValue))
					selectors.get(i).getSelectionModel().select(selectedValue);
				else {
					selectors.get(i).getSelectionModel().select("<текст>");
					textFields.get(i).setText("");
				}
			}
		}
	}
	
	
	
	public String getMaxLengthDescription() throws TextAreaException{
		
		List<String> result = new ArrayList<String>();
		for (int i=0; i<selectors.size();i++){
			String d =  selectors.get(i).getSelectionModel().getSelectedItem();
			//Target maxTarget = app.getTargetWithMaxLength();
			if (d == null || d.isEmpty() || d.equals("<текст>")){
			    String t = parseVariablesInText(textFields.get(i), true);
			    if (!t.isEmpty())
			    	result.add(t);
			}
			else if (d.equals("Таргет")) {
				//String t = maxTarget.getTarget();
				List<String> res = app.getTargetsData().stream().map(Target::getTarget).collect(Collectors.toList());
				String t = Collections.max(res, Comparator.comparing(s -> s.length()));
				textFields.get(i).setText(t);
				if (!t.isEmpty())
				    	result.add(t);
				    
			}
			else if (d.equals("Таргет1")) {
				//String t = maxTarget.getTarget1();
				List<String> res = app.getTargetsData().stream().map(Target::getTarget1).collect(Collectors.toList());
				String t = Collections.max(res, Comparator.comparing(s -> s.length()));
				textFields.get(i).setText(t);
			    if (!t.isEmpty())
			    	result.add(t);
			}
			else if (d.equals("Таргет2")){
				//String t = maxTarget.getTarget2();
				List<String> res = app.getTargetsData().stream().map(Target::getTarget2).collect(Collectors.toList());
				String t = Collections.max(res, Comparator.comparing(s -> s.length()));
				textFields.get(i).setText(t);
			    if (!t.isEmpty())
			    	result.add(t);
			}
			else {
				String variableValue = Variable.getMaxValueByName(app.descriptionVariableEditorContainerController.variables, d);
			    if (d!=null) {
			    	textFields.get(i).setText(variableValue);
			        if (!variableValue.isEmpty())
				    	result.add(variableValue);
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
		countLabel.setText("Символов: " + resultString.length());
		
		return resultString;
	}
	
			
	public String generateDescriptionForMetadata(){
		List<String> result = new ArrayList<String>();
		for (int i=0; i<data.size();i++){
			final String dataValue = data.get(i);
			if (dataValue == null || dataValue.equals("<текст>")){
			    String t = "";
				try {
					t = SyntaxParser.pasteVariables(app.descriptionVariableEditorContainerController.savedVariables, textFieldsStored.get(i), false, " ");
				} catch (TextException e) {
					app.log("ERROR: Ошибка вставки переменных в строку: " + textFieldsStored.get(i));
					app.isProblem = true;
				}
			    if (!t.isEmpty())
			    	result.add(t);
			}
			else if (dataValue.equals("Таргет")){
				String t = app.mainFrameController.currentTarget.getTarget();
			    if (!t.isEmpty())
			    	result.add(t);
			}
				
			else if (dataValue.equals("Таргет1")){
				String t = app.mainFrameController.currentTarget.getTarget1();
			    if (!t.isEmpty())
			    	result.add(t);
			}
			else if (dataValue.equals("Таргет2")){
				String t = app.mainFrameController.currentTarget.getTarget2();
			    if (!t.isEmpty())
			    	result.add(t);
			}
			
   		    else {
 				String variableValue = Variable.getRandomValueByName(app.descriptionVariableEditorContainerController.savedVariables, dataValue);
					    if (variableValue!=null && !variableValue.isEmpty())
					    	result.add(variableValue);
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

	public void saveDescriptionsSource() throws DataException {
		data.clear();
		selectors.forEach(sel -> data.add(sel.getSelectionModel().getSelectedItem()));
		textFieldsStored.clear();
		for (int i=0;i<textFields.size();i++)
			try {
				SyntaxParser.checkVariables(app.descriptionVariableEditorContainerController.variables, textFields.get(i).getText());
				textFieldsStored.add(textFields.get(i).getText());
			} catch (TextException e) {
				setError(textFields.get(i), true, e.getMessage());
				throw new DataException(this.tab);
			}
	}
	
	public void clearAll(){
		selectors.forEach(sel -> sel.getSelectionModel().select("<текст>"));
		textFields.forEach(tf -> tf.clear());
	}

}
