package te.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
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

import te.Main;
import te.model.Target;
import te.model.Variable;
import te.util.DataException;
import te.util.SyntaxParser;
import te.util.TextAreaException;
import te.util.TextException;

import com.sun.javafx.scene.control.behavior.TextAreaBehavior;
import com.sun.javafx.scene.control.skin.TextAreaSkin;

public class DescriptionEditorController extends TargetEditorController implements Initializable {
	
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
			selectors.get(i).setItems(options.get(i));
			options.get(i).add("<текст>");
			selectors.get(i).getSelectionModel().select("<текст>");
			selectors.get(i).valueProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue==null || newValue.isEmpty() || newValue.equals("<текст>")) 
					textFields.get(final_i).setEditable(true);
				else 
					textFields.get(final_i).setEditable(false);
				setError(textFields.get(final_i), false, null);
				 try {	
						updateForm();
					 }
					 catch (TextAreaException e) {
						setError(e.textArea, true, e.getMessage());
					 }
					});
		}
		
		for (TextArea tf:textFields) {
			tf.textProperty().addListener((observable, oldValue, newValue) -> {
				setError(tf, false, null);
			 try {	
				updateForm();
			 }
			 catch (TextAreaException e) {
				setError(e.textArea, true, e.getMessage());
			 }
			});
			denyTab(tf);
		}
	}
	
	private String parseVariablesInText(TextArea tf, boolean isMax) throws TextAreaException{
		String result = null;
		try{
			result = SyntaxParser.pasteVariables(tf.getText(), isMax, " ");	
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
	            //resultText.setText("");
	        }
	}
	
	private boolean checkNoSquareBrackets(String text, int fromIndex){
		return !(text.substring(fromIndex).contains("[") || text.substring(fromIndex).contains("]"));
	}
	

	public void updateForm() throws TextAreaException{
		String maxLengthDescription = getMaxLengthDescription();
		countLabel.setText("Символов: " + maxLengthDescription.length());
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
			ol.addAll(app.variables.stream().map(Variable::getName)
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
			else if (app.variables.stream().anyMatch( v -> d.equals(v.getName()))){
				Optional<Variable> vo = app.variables.stream().filter(v -> d.equals(v.getName())).findFirst();
				if (vo !=null && vo.isPresent()) {
					String variableValue = "";
					if (!vo.get().getValues().isEmpty())
						variableValue = Collections.max(vo.get().getValues(), Comparator.comparing(s -> s.length()));
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
		
		return resultString;
	}
	
			
	public String generateRandomDescription(){
		List<String> result = new ArrayList<String>();
		for (int i=0; i<data.size();i++){
			final String dataValue = data.get(i);
			if (dataValue == null || dataValue.equals("<текст>")){
			    String 	t = textFieldsStored.get(i);
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
			
   		    else if (app.variables.stream().anyMatch( v -> dataValue.equals(v.getName()))){
					Optional<Variable> vo = app.variables.stream().filter(v -> dataValue.equals(v.getName())).findFirst();
					if (vo !=null && vo.isPresent()) {
						String variableValue = vo.get().getRandomValue();
					    if (!variableValue.isEmpty())
					    	result.add(variableValue);
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

	public void saveDescriptionsSource() throws DataException {
		data.clear();
		for (ComboBox<String> sel:selectors){
			String item = sel.getSelectionModel().getSelectedItem();
			if (item==null)
				data.add("<текст>");
			else 
				data.add(item);
		}
		textFieldsStored.clear();
		for (int i=0;i<textFields.size();i++)
			try {
				textFieldsStored.add(parseVariablesInText(textFields.get(i), false));
			} catch (TextException e) {
				setError(textFields.get(i), true, e.getMessage());
				throw new DataException(this.tab);
			}
	}

	public boolean checkDataIsCorrect() {
		boolean result = true;
		
		if (!textFields.stream().allMatch(t->app.isCorrectKey(t.getText()))){
			app.log("Недопустимые символы в одном из текстовых полей вкладки Описание");
			result = false;
		}
		
		if (data.stream().anyMatch(d->d.equals("Таргет")))
			if (!app.getTargetsData().stream().allMatch(t->app.isCorrectKey(t.getTarget()))){
				app.log("Недопустимые символы в одном из полей Таргет");
				result = false;
			}
		if (data.stream().anyMatch(d->d.equals("Таргет1")))
			if (!app.getTargetsData().stream().allMatch(t->app.isCorrectKey(t.getTarget1()))){
				app.log("Недопустимые символы в одном из полей Таргет1");
				result = false;
			}
		if (data.stream().anyMatch(d->d.equals("Таргет2")))
			if (!app.getTargetsData().stream().allMatch(t->app.isCorrectKey(t.getTarget1()))){
				app.log("Недопустимые символы в одном из полей Таргет2");
				result = false;
			}
		for (String d:data){
		if (app.variables.stream().anyMatch( v -> d.equals(v.getName()))){
			Optional<Variable> vo = app.variables.stream().filter(v -> d.equals(v.getName())).findFirst();
		    if (vo !=null && vo.isPresent()) {
		    	if(!vo.get().getValues().stream().allMatch(v->app.isCorrectKey(v))) {
		    		app.log("Недопустимые символы в одном значений переменной " + vo.get().getName());
		    		result = false;
		    	}
		   }
		}
		}
		return result;
	}
	
}
