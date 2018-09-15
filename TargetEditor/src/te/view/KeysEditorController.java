package te.view;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;

import org.apache.commons.lang3.StringUtils;

import te.model.Variable;
import te.util.DataException;
import te.util.SyntaxParser;
import te.util.TextAreaException;
import te.util.TextException;

import com.sun.javafx.scene.control.behavior.TextAreaBehavior;
import com.sun.javafx.scene.control.skin.TextAreaSkin;


public class KeysEditorController extends TargetEditorController implements Initializable {

	@FXML
	private TextArea keysField;
	
	@FXML
	private TextArea preview;
	
	@FXML
	private ComboBox<Variable> variablesCombo;
	
	@FXML
	private Button clearBtn;
	
	@FXML
	private Button addBtn;
	
	@FXML
	private Spinner keyCountBox;
	
	@FXML
	private Label countLabel;
	
	@FXML
	private CheckBox isTarget;
	
	private boolean isT;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		variablesCombo.setConverter(new StringConverter<Variable>(){

			@Override
			public String toString(Variable object) {
				// TODO Auto-generated method stub
				return object.getName();
			}

			@Override
			public Variable fromString(String string) {
				return variablesCombo.getItems().stream().filter(v -> v.getName().equals(string)).findFirst().orElse(null);
			}
			
		});

		
		ImageView imageView = new ImageView(new Image(new File(
				"resources/add.png").toURI().toString()));
		imageView.setFitWidth(40);
		imageView.setFitHeight(22);
		addBtn.setGraphic(imageView);
		variablesCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
			update();
		});
		isTarget.selectedProperty().addListener((observable, oldValue, newValue) -> {
			update();
		});
		keysField.textProperty().addListener((observable, oldValue, newValue) -> {
			update();
		});
		
		denyTab(keysField);
	}
	@FXML
	private void clearForms(){
		keysField.clear();
	}
	
public void setup(){
	variablesCombo.setItems(app.keyVariableEditorContainerController.variables);
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
		
		if (isT) {
			String target = app.mainFrameController.currentTarget.getTarget();
			if (app.isCorrectKey(target))
				addToList(target, keys);
		}
		
		keys = keys.stream().distinct().collect(Collectors.toList());
		
		cutList(keys, 50);
	
		return keys;
	}
	
	public void saveKeywordsSource() throws DataException{
	}
	
	
	
	private List<String> getKeysFromUI() throws TextAreaException{
        List<String> keys = new ArrayList<String>();
		
		addToList(parseVariablesInText(keysField, false), keys);
		
		if (isTarget.isSelected())
			addToList(app.getRandomTarget(), keys);
		
		
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
			result = SyntaxParser.pasteVariables(app.keyVariableEditorContainerController.variables, tf.getText(), isMax, ", ");	
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

					
