package te.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import org.apache.commons.lang3.StringUtils;

import te.Settings;
import te.model.DescriptionEditorWrapper;
import te.model.FolderVariable;
import te.model.Target;
import te.model.Variable;
import te.util.DataException;
import te.util.SyntaxParser;
import te.util.TextAreaException;
import te.util.TextException;
import te.util.Tuple;

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
	private Button refreshBtn;
	
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
	private CheckBox isRandom1;
	@FXML
	private CheckBox isRandom2;
	@FXML
	private CheckBox isRandom3;
	@FXML
	private CheckBox isRandom4;
	@FXML
	private CheckBox isRandom5;
	
	
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
	private List<CheckBox> randomBoxes  = new ArrayList<CheckBox>();
	public List<String> textFieldsStored = new ArrayList<String>();
	private List<String> data = new ArrayList<String>();
	private List<Boolean> isRandomStored = new ArrayList<Boolean>();
	
	private final String targetDescr1 = "TargetDescr1";
	private final String targetDescr2 = "TargetDescr2";		
	private final String folderDescr = "FolderDescr";	
	
	public DescriptionEditorWrapper wrapper;
	private boolean isInitialized;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	
	private final ChangeListener<String> boxListener  = new ChangeListener<String>(){
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
			if (newValue==null || (oldValue!=null && newValue.equals(oldValue)))	return;
			
			removeListeners();
			
			SimpleObjectProperty prop = (SimpleObjectProperty) observable ;
	        ComboBox combo = (ComboBox) prop.getBean();
	        int index = selectors.indexOf(combo);
	        
			if (newValue.equals(targetDescr1) || newValue.equals(targetDescr2) || newValue.equals(folderDescr)) {
				textFields.get(index).setEditable(false);
			}
			else {
				textFields.get(index).setEditable(true);
				Optional<Variable> opt = app.descriptionVariableEditorContainerController.variables.stream().filter(v->v.getName().equals(newValue)).findFirst();
				if (oldValue!=null && !oldValue.equals(newValue) && opt!=null && opt.isPresent()){
					addVariableToText(opt.get(), textFields.get(index));
				}
				if (oldValue!=null && !oldValue.equals(Settings.bundle.getString("ui.selector.text")) && newValue.equals(Settings.bundle.getString("ui.selector.text")))
					textFields.get(index).setText("");
			}
			setError(textFields.get(index), false, null);
			 try {	
					getMaxLengthDescription();
					getRandomDescription();
				 }
				 catch (TextAreaException e) {
					setError(e.textArea, true, e.getMessage());
				 }
			 finally{
					addListeners();
				}
		}
	};
	
	
	 private final ChangeListener<String> textListener = new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				StringProperty prop = (StringProperty) observable ;
		        TextArea tf = (TextArea) prop.getBean();
		        int index = textFields.indexOf(tf);
		        removeListeners();
		    	setError(tf, false, null);
		        selectors.get(index).getSelectionModel().select(Settings.bundle.getString("ui.selector.text"));
			
				 try {	
					getMaxLengthDescription();
					getRandomDescription();
				 }
				 catch (TextAreaException e) {
					setError(e.textArea, true, e.getMessage());
				 }
				 finally{
					 addListeners();
				 }
			}
	 };
	
		private final ChangeListener<Boolean> checkBoxListener = new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue){	
				removeListeners();
				try {
					getMaxLengthDescription();
					getRandomDescription();
				} catch (TextAreaException e) {
					setError(e.textArea, true, e.getMessage());
				}
				 finally {
					 addListeners();
				 }
		    }
		};
	
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
		randomBoxes.add(isRandom1);
		randomBoxes.add(isRandom2);
		randomBoxes.add(isRandom3);
		randomBoxes.add(isRandom4);
		randomBoxes.add(isRandom5);
		
		for (int i=0;i<selectors.size();i++) {
			options.add(FXCollections.observableArrayList());
			selectors.get(i).setItems(options.get(i));
			options.get(i).add(Settings.bundle.getString("ui.selector.text"));
			selectors.get(i).getSelectionModel().select(Settings.bundle.getString("ui.selector.text"));
		}
		addListeners();
	}
	
	
	 private void addListeners(){
			for (TextArea tf:textFields) {
				tf.textProperty().removeListener(textListener);
				tf.textProperty().addListener(textListener);
			}
			for (ComboBox<String> combo:selectors){
				combo.valueProperty().removeListener(boxListener);
				combo.valueProperty().addListener(boxListener);
			}
			for (CheckBox box:randomBoxes){
				box.selectedProperty().removeListener(checkBoxListener);
				box.selectedProperty().addListener(checkBoxListener);
			}
		}
		
		
		private void removeListeners(){
			for (TextArea tf:textFields) {
				tf.textProperty().removeListener(textListener);
			}
			for (ComboBox<String> combo:selectors){
				combo.valueProperty().removeListener(boxListener);
			}
			for (CheckBox box:randomBoxes){
				box.selectedProperty().removeListener(checkBoxListener);
			}
		}
		
		
	private void addVariableToText(Variable d, TextArea ta){
			if (d==null)
				return;
			String previous = ta.getText().trim();
			if (!this.isInitialized)
				previous = "";
			//int cp = ta.getCaretPosition();
			//if (!ta.isFocused()) cp = previous.length();
			//StringBuilder sb = new StringBuilder(previous.substring(0, cp));
			StringBuilder sb = new StringBuilder(previous);
			if (!previous.isEmpty())
				sb.append(" ");
			sb.append("<");
			sb.append(d.getName());
			sb.append(">[1]");
			//sb.append(previous.substring(cp));
			ta.setText(sb.toString());
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
		removeListeners();
		for (int i=0; i<selectors.size();i++){
			
			SingleSelectionModel<String> selected = selectors.get(i).selectionModelProperty().getValue();
			String selectedValue = null;
			if (selected!=null && !selected.isEmpty())
				selectedValue = selected.getSelectedItem();
			ObservableList<String> ol = options.get(i);
			ol.clear();
			ol.add(Settings.bundle.getString("ui.selector.text"));
			if (!app.getFolderVariableData().isEmpty())
				ol.add(this.folderDescr);
			if (!app.getTargetsData().isEmpty()){
				ol.add(this.targetDescr1);
				ol.add(this.targetDescr2);
			}
			ol.addAll(app.descriptionVariableEditorContainerController.variables.stream().map(Variable::getName)
		              .collect(Collectors.toList()));
			if (selectedValue!=null){
				if	(ol.contains(selectedValue))
					selectors.get(i).getSelectionModel().select(selectedValue);
				else {
					selectors.get(i).getSelectionModel().select(Settings.bundle.getString("ui.selector.text"));
					textFields.get(i).setText("");
				}
			}
		}
		refresh();
		addListeners();
	}
	
	@FXML
	private void refresh(){
		try {
			removeListeners();
			getMaxLengthDescription();
			getRandomDescription();
			addListeners();
		} catch (TextAreaException e) {
			setError(e.textArea, true, e.getMessage());
		}
	}
	
	
	public String getRandomDescription() throws TextAreaException{
		
		Target target = app.getRandomTarget();
		List<Tuple<Integer, String>> result = new ArrayList<Tuple<Integer, String>>();
		for (int i=0; i<selectors.size();i++){
			String d =  selectors.get(i).getSelectionModel().getSelectedItem();
			int position = i;
			if(randomBoxes.get(i).isSelected())
				position = -1;
			if (d == null || d.isEmpty()){
				   continue;
				}
			else if (d.equals(this.folderDescr)){
				String t = app.getRandomFolderDescr();
				if (t!=null){
					textFields.get(i).setText(t);
					if (!t.isEmpty())
						result.add( new Tuple<Integer, String>(position, t));
				}
			}
			else if (d.equals(this.targetDescr1)) {
				if (target!=null){
					String t = target.getTargetDescr1();
					textFields.get(i).setText(t);
				    if (!t.isEmpty())
				    	result.add( new Tuple<Integer, String>(position, t));
				}
				
			}
			else if (d.equals(this.targetDescr2)){
				if (target!=null){
					String t = target.getTargetDescr2();
					textFields.get(i).setText(t);
				    if (!t.isEmpty())
				    	result.add( new Tuple<Integer, String>(position, t));
				}
			}
			else {
				 String t = parseVariablesInText(textFields.get(i), false);
				    if (!t.isEmpty()) 
				    	result.add( new Tuple<Integer, String>(position, t));
			}
			app.checkSyntax(textFields.get(i));
		}
		
		String resultString = joinWithPositions(result);
        resultText.setText(resultString);
		return resultString;
	}
	
	public String getMaxLengthDescription() throws TextAreaException{
		Target target = app.getTargetWithMaxLength();
		List<Tuple<Integer, String>> result = new ArrayList<Tuple<Integer, String>>();
		for (int i=0; i<selectors.size();i++){
			String d =  selectors.get(i).getSelectionModel().getSelectedItem();
			int position = i;
			if(randomBoxes.get(i).isSelected())
				position = -1;
			if (d == null || d.isEmpty()){
			   continue;
			}
			else if (d.equals(this.folderDescr)){
				List<String> res = app.getFolderVariableData().stream().map(FolderVariable::getDescriptionVariable).collect(Collectors.toList());
				String t = Collections.max(res, Comparator.comparing(s -> s.length()));
				textFields.get(i).setText(t);
			    if (!t.isEmpty())
			    	result.add( new Tuple<Integer, String>(position, t));
			}
			else if (d.equals(this.targetDescr1)) {
				if (target!=null){
					String t = target.getTargetDescr1();
					textFields.get(i).setText(t);
				    if (!t.isEmpty())
				    	result.add( new Tuple<Integer, String>(position, t));
				}
				
			}
			else if (d.equals(this.targetDescr2)){
				if (target!=null){
					String t = target.getTargetDescr2();
					textFields.get(i).setText(t);
				    if (!t.isEmpty())
				    	result.add( new Tuple<Integer, String>(position, t));
				}
			}
			else {
				 String t = parseVariablesInText(textFields.get(i), true);
			    if (!t.isEmpty()) 
			    	result.add( new Tuple<Integer, String>(position, t));
			}
			app.checkSyntax(textFields.get(i));
		}
		
		String resultString = joinWithPositions(result);
		
		if (resultString.length() > this.LIMIT){
			countLabel.setFill(Color.RED);
		}
		else{
			countLabel.setFill(Color.BLACK);
		}
		countLabel.setText(Settings.bundle.getString("ui.tabs.descriptions.maxchars") + resultString.length());
		
		return resultString;
	}
	
	private String joinWithPositions(List<Tuple<Integer, String>> data){
		if (data == null || data.isEmpty()) return ""; 
		List<Integer> freePositions = new ArrayList<Integer>();
		for (int i=0; i<data.size();i++) 
			freePositions.add(i);
         for (Tuple<Integer,String> tuple : data) {
			if (tuple.x>=0)
				freePositions.remove(tuple.x);
		}
         for (Tuple<Integer,String> tuple : data) {
			if (tuple.x<0) {
				int index = ThreadLocalRandom.current().nextInt(freePositions.size());		
				tuple.x = freePositions.remove(index);
			}
		}		
		Map<Integer, String> treeMap = new TreeMap<>();
		for (Tuple<Integer,String> tuple : data) {
			treeMap.put(tuple.x, tuple.y);
		}
		return StringUtils.join(treeMap.values(), " ");
	}
	
			
	public String generateDescriptionForMetadata(){
		List<Tuple<Integer, String>> result = new ArrayList<Tuple<Integer, String>>();
		for (int i=0; i<data.size();i++){
			int position = i;
			if(isRandomStored.get(i))
				position = -1;
			final String dataValue = data.get(i);
			if (dataValue.equals(this.folderDescr)){
				String t = app.mainFrameController.currentFolder.getDescriptionVariable();
			    if (t!=null && !t.isEmpty())
			    	result.add( new Tuple<Integer, String>(position, t));
			}
			else if (dataValue.equals(this.targetDescr1)){
				String t = app.mainFrameController.currentTarget.getTargetDescr1();
			    if (!t.isEmpty())
			    	result.add( new Tuple<Integer, String>(position, t));
			}
				
			else if (dataValue.equals(this.targetDescr2)){
				String t = app.mainFrameController.currentTarget.getTargetDescr2();
			    if (!t.isEmpty())
			    	result.add( new Tuple<Integer, String>(position, t));
			}
			else {
			    String t = "";
				try {
					t = SyntaxParser.pasteVariables(app.descriptionVariableEditorContainerController.savedVariables, textFieldsStored.get(i), false, " ");
				} catch (TextException e) {
					LOGGER.warning(Settings.bundle.getString("alert.error.pastevars") + textFieldsStored.get(i));
					app.isProblem = true;
				}
			    if (!t.isEmpty())
			    	result.add( new Tuple<Integer, String>(position, t));
			}
		}
		this.currentDescription = joinWithPositions(result);
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
				isRandomStored.add(randomBoxes.get(i).isSelected());
			} catch (TextException e) {
				setError(textFields.get(i), true, e.getMessage());
				throw new DataException(this.tab);
			}
	}
	
	public void clearAll(){
		selectors.forEach(sel -> sel.getSelectionModel().select(Settings.bundle.getString("ui.selector.text")));
		textFields.forEach(tf -> tf.clear());
		randomBoxes.forEach(b -> b.setSelected(false));
	}

	@Override
	public void loadData() {
		updateLists();
		if (this.wrapper != null)
		for (int i=0; i<this.textFields.size(); i++) {
			if (this.wrapper.textFields.size()<=i) break;
			this.textFields.get(i).setText(this.wrapper.textFields.get(i));
			this.randomBoxes.get(i).setSelected(this.wrapper.isRandomBoxes.get(i));
			if (this.wrapper.comboSelectedText.size()>i && this.selectors.get(i).getItems().contains(this.wrapper.comboSelectedText.get(i)))
				this.selectors.get(i).getSelectionModel().select(this.wrapper.comboSelectedText.get(i));
		}
		isInitialized = true;
	}

	@Override
	public void saveData() {
		this.wrapper = new DescriptionEditorWrapper();
		for (int i=0; i<this.textFields.size(); i++){
			this.wrapper.textFields.add(this.textFields.get(i).getText());
			this.wrapper.comboSelectedText.add(this.selectors.get(i).getSelectionModel().getSelectedItem());
			this.wrapper.isRandomBoxes.add(this.randomBoxes.get(i).isSelected());
		}
	}

}
