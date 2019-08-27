package te.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.sun.javafx.scene.control.behavior.TextAreaBehavior;
import com.sun.javafx.scene.control.skin.TextAreaSkin;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import te.Main;
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

public class DescriptionContainerController  extends TargetEditorController implements Initializable {

	
	@FXML
	private Text countLabel;
	@FXML
	private TextArea resultText;
	@FXML
	private Button refreshBtn;
	@FXML
	private Button addLayoutBtn;
	@FXML
	private VBox descriptionLayouts;
	public List<DescriptionLayoutController> controllersList = new ArrayList<DescriptionLayoutController>(); 
	
	private final int LIMIT = 200;
	public String currentDescription;
	
	public List<TextArea> textFields = new ArrayList<TextArea>();
	public List<ComboBox<String>> selectors = new ArrayList<ComboBox<String>>();
	public List<ObservableList<String>> options = new ArrayList<ObservableList<String>>();
	public List<CheckBox> randomBoxes  = new ArrayList<CheckBox>();
	public List<String> textFieldsStored = new ArrayList<String>();
	private List<String> data = new ArrayList<String>();
	private List<Boolean> isRandomStored = new ArrayList<Boolean>();
	
	private final String targetDescr1 = "TargetDescr1";
	private final String targetDescr2 = "TargetDescr2";		
	private final String folderDescr = "FolderDescr";	
	
	public DescriptionEditorWrapper wrapper;
	private boolean isInitialized;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	
	public String currentDescriptionExample = "";
	
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
				if (oldValue==null || !oldValue.equals(newValue)) {
					if (newValue.equals(Settings.bundle.getString("ui.selector.text")))
						textFields.get(index).setText("");
					else 
						addVariableToText(opt.get(), textFields.get(index));
				}
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
		this.addLayoutBtn.setShape(new Circle(25));
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
	
	
	private void updateSelector(ComboBox<String> selector) {
		SingleSelectionModel<String> selected = selector.selectionModelProperty().getValue();
		int index = selectors.indexOf(selector);
		String selectedValue = null;
		if (selected!=null && !selected.isEmpty())
			selectedValue = selected.getSelectedItem();
		ObservableList<String> ol = options.get(index);
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
				selector.getSelectionModel().select(selectedValue);
			else {
				selector.getSelectionModel().select(Settings.bundle.getString("ui.selector.text"));
				textFields.get(index).setText("");
			}
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
		setError(resultText, false, null);
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
		String randomFolderscription = app.getRandomFolderDescr();
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
				if (randomFolderscription!=null){
					textFields.get(i).setText(randomFolderscription);
					if (!randomFolderscription.isEmpty())
						result.add( new Tuple<Integer, String>(position, randomFolderscription));
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
		this.currentDescriptionExample =resultString;
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
			if (dataValue==null)
				continue;
			else if (dataValue.equals(this.folderDescr)){
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
	
	
	@FXML
	public void addDecriptionLayout(){
		try {
			 FXMLLoader loader = new FXMLLoader();
	         loader.setLocation(Main.class.getResource("view/DescriptionLayout.fxml"));
		     AnchorPane sourceLayout = (AnchorPane) loader.load();
		     descriptionLayouts.getChildren().add(sourceLayout);
	         //app.getPrimaryStage().sizeToScene();
	         DescriptionLayoutController controller = loader.getController();
	         controller.app = this.app;
	         controller.layout = sourceLayout;
	         controllersList.add(controller);
	         selectors.add(controller.selector);
	         textFields.add(controller.text);
	         randomBoxes.add(controller.isRandom);
	         ObservableList<String> oL = FXCollections.observableArrayList();
	         options.add(oL);
	         controller.selector.setItems(oL);
	         updateSelector(controller.selector);
	         
	         controller.text.textProperty().removeListener(textListener);
	         controller.text.textProperty().addListener(textListener);
			 controller.selector.valueProperty().removeListener(boxListener);
			 controller.selector.valueProperty().addListener(boxListener);
			 controller.isRandom.selectedProperty().removeListener(checkBoxListener);
			 controller.isRandom.selectedProperty().addListener(checkBoxListener);
	         
	         
			} catch (IOException e) {
				  Alert alert = new Alert(AlertType.ERROR);
		            alert.setTitle(Settings.bundle.getString("alert.error.title"));
		            alert.setHeaderText(Settings.bundle.getString("alert.error.addtoform.content"));
		            alert.setContentText(e.getMessage());
		            alert.showAndWait();
			}
	}
	
	private void removeAllLayouts() {
		while (!controllersList.isEmpty())
			removeDescriptionLayout(controllersList.get(0));
	}
	
	
	private void addDecriptionLayout(boolean isRandom, String selectorText, String textFieldText){
		try {
			 FXMLLoader loader = new FXMLLoader();
	         loader.setLocation(Main.class.getResource("view/DescriptionLayout.fxml"));
	         AnchorPane sourceLayout = (AnchorPane) loader.load();
		     descriptionLayouts.getChildren().add(sourceLayout);
	         //app.getPrimaryStage().sizeToScene();
	         DescriptionLayoutController controller = loader.getController();
	         controller.app = this.app;
	         controller.layout = sourceLayout;
	         controllersList.add(controller);
	         selectors.add(controller.selector);
	         textFields.add(controller.text);
	         randomBoxes.add(controller.isRandom);
	         ObservableList<String> oL = FXCollections.observableArrayList();
	         options.add(oL);
	         controller.selector.setItems(oL);
	         updateSelector(controller.selector);
	         controller.isRandom.setSelected(isRandom);
	         if (textFieldText!=null)
	        	 controller.text.setText(textFieldText);
	         if (selectorText!=null && controller.selector.getItems().contains(selectorText))
	        	 controller.selector.getSelectionModel().select(selectorText);
	         
			} catch (IOException e) {
				  Alert alert = new Alert(AlertType.ERROR);
		            alert.setTitle(Settings.bundle.getString("alert.error.title"));
		            alert.setHeaderText(Settings.bundle.getString("alert.error.addtoform.content"));
		            alert.setContentText(e.getMessage());
		            alert.showAndWait();
			}
	}
	
	
	
	public void removeDescriptionLayout(DescriptionLayoutController controller){
		 try {
			 if (!controllersList.contains(controller)) return;
			 int index = selectors.indexOf(controller.selector);
			 options.remove(index);
			 selectors.remove(index);
			 randomBoxes.remove(index);
			 textFields.remove(index);
			 descriptionLayouts.getChildren().remove(controller.layout);
			 controllersList.remove(controller);
			 
			 refresh();
		 }
		 catch (Exception e) {
			    Alert alert = new Alert(AlertType.ERROR);
	            alert.setTitle(Settings.bundle.getString("alert.error.title"));
	            alert.setHeaderText(Settings.bundle.getString("alert.error.removeelements.content"));
	            alert.setContentText(e.getMessage());
	            alert.showAndWait();
		 }
		  
	}
	
	
	public void clearAll(){
		removeAllLayouts();
		addDecriptionLayout();
	}

	@Override
	public void loadData() {
		removeAllLayouts();
		if (this.wrapper != null)
		for (int i=0; i<this.wrapper.textFields.size(); i++) {
			if (this.wrapper.comboSelectedText.size()>i)
				addDecriptionLayout(this.wrapper.isRandomBoxes.get(i), this.wrapper.comboSelectedText.get(i), this.wrapper.textFields.get(i));
			else
				addDecriptionLayout(this.wrapper.isRandomBoxes.get(i),null, this.wrapper.textFields.get(i));
		}
		if (this.wrapper == null || this.wrapper.textFields.isEmpty())
			addDecriptionLayout();
		
		refresh();
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
