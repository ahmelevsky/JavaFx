package te.view;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
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
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import org.apache.commons.lang3.StringUtils;

import te.model.KeysEditorWrapper;
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
	private Button refreshBtn;
	
	@FXML
	private Button addBtn;
	
	@FXML
	private Spinner<String> keyCountBox;
	
	@FXML
	private Label countLabel;
	
	@FXML
	private CheckBox isTarget;
	
	@FXML
	private CheckBox isFolderVariable;
	
	private ObservableList<String> items = FXCollections.observableArrayList("Все");
	private String savedKeywordsTemplate;
	private boolean isT;
	private boolean isF;
	
	public KeysEditorWrapper wrapper;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
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

		for (int i=1;i<=50;i++)
			items.add(String.valueOf(i));
		
		SpinnerValueFactory<String> valueFactory =   new SpinnerValueFactory.ListSpinnerValueFactory<String>(items);
		keyCountBox.setValueFactory(valueFactory);
		keyCountBox.focusedProperty().addListener((arg, oldVal, newVal) -> { if (newVal) return; correctSpinnerValue();});
		keyCountBox.valueProperty().addListener((arg, oldVal, newVal) -> correctSpinnerValue());
		
		ImageView imageView = new ImageView(new Image(new File(
				"resources/add.png").toURI().toString()));
		imageView.setFitWidth(40);
		imageView.setFitHeight(22);
		addBtn.setGraphic(imageView);
		
		isTarget.selectedProperty().addListener((observable, oldValue, newValue) -> {
			update();
		});
		isFolderVariable.selectedProperty().addListener((observable, oldValue, newValue) -> {
			update();
		});
		keysField.textProperty().addListener((observable, oldValue, newValue) -> {
			update();
		});
		
		denyTab(keysField);
	}
	
private void correctSpinnerValue(){
	 String text = keyCountBox.getEditor().getText();
     SpinnerValueFactory.ListSpinnerValueFactory<String> vFactory = (SpinnerValueFactory.ListSpinnerValueFactory<String>) keyCountBox.getValueFactory();
     if (!vFactory.getItems().contains(text)) {
  		   vFactory.setValue("1");
     } else {
  		   vFactory.setValue(text);
     }
     keyCountBox.increment(0);
}
	
public void setup(){
	variablesCombo.setItems(app.keyVariableEditorContainerController.variables);
}


@FXML
private void addVariable(){
	Variable d =  variablesCombo.getSelectionModel().getSelectedItem();
	if (d==null)
		return;
	String previous = keysField.getText().trim();
	StringBuilder sb = new StringBuilder(previous);
	if (!previous.isEmpty())
		sb.append(", ");
	sb.append("<");
	sb.append(d.getName());
	sb.append(">");
	
	String repeat = keyCountBox.getValueFactory().getValue();
	if (repeat != null) {
		sb.append("[");
		if (!repeat.equals("Все"))
			sb.append(repeat);
		sb.append("]");
	}
	
	keysField.setText(sb.toString());
}


public void update(){
	try{
		setError(keysField, false, null);
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
		
	    try {
	    	if (isT)
	 			addToList(app.mainFrameController.currentTarget.getTargetKwd(), keys);
	    	if (isF)
	 			addToList(app.mainFrameController.currentFolder.getKeyVariable(), keys);
			addToList(SyntaxParser.pasteVariablesUnique(app.keyVariableEditorContainerController.savedVariables, this.savedKeywordsTemplate, false, ", "), keys);
			keys = keys.stream().distinct().collect(Collectors.toList());
			cutList(keys, 50);
		} catch (TextException e) {
			LOGGER.warning("ERROR: Ошибка вставки переменных в строку: " + this.savedKeywordsTemplate);
			app.isProblem = true;
		}
		
		return keys;
	}
	
	public void saveKeywordsSource() throws DataException{
		try {
			SyntaxParser.checkVariables(app.keyVariableEditorContainerController.variables, keysField.getText());
			this.isT = isTarget.isSelected();
			this.isF = isFolderVariable.isSelected();
			this.savedKeywordsTemplate = keysField.getText().trim();
			
		} catch (TextException e) {
			setError(keysField, true, e.getMessage());
			throw new DataException(this.tab);
		}
	}
	
	
	
	private List<String> getKeysFromUI() throws TextAreaException{
        List<String> keys = new ArrayList<String>();
		
        if (isTarget.isSelected())
			addToList(app.getRandomTargetKwd(), keys);
        if (isFolderVariable.isSelected()) 
			addToList(app.getRandomFolderKwd(), keys);
        
		addToList(parseVariablesInText(keysField, false), keys);
		
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
			result = SyntaxParser.pasteVariablesUnique(app.keyVariableEditorContainerController.variables, tf.getText(), isMax, ", ");	
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
	
	public void clearAll(){
		//variablesCombo.getSelectionModel().select(0);
		keysField.clear();
		isTarget.setSelected(false);
		isFolderVariable.setSelected(false);
	}


	@Override
	public void loadData() {
		if (this.wrapper!=null) {
			this.keysField.setText(this.wrapper.keysField);
			this.isTarget.setSelected(this.wrapper.isTarget);
			this.isFolderVariable.setSelected(this.wrapper.isFolderVariable);
		}
		
	}
	
	public void saveData() {
		this.wrapper = new KeysEditorWrapper(this.keysField.getText(), this.isTarget.isSelected(), this.isFolderVariable.isSelected());
	}
	
	}

					
