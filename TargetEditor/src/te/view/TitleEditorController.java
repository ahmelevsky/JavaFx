package te.view;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import te.Settings;
import te.model.DescriptionEditorWrapper;
import te.model.FolderVariable;
import te.model.Target;
import te.model.TitleEditorWrapper;
import te.model.Variable;
import te.util.DataException;
import te.util.SyntaxParser;
import te.util.TextAreaException;
import te.util.TextException;
import te.util.Tuple;

public class TitleEditorController extends TargetEditorController implements Initializable {
	
	@FXML
	private TextArea titleText;
	@FXML
	private ComboBox<String> titleBox;
	@FXML
	private Label countLabel;
	@FXML
	private  CheckBox isTakeFromDescriptionBox;
	private String titleBoxSetting;
	public String titleTextSetting;
	
	private boolean isTakeFromDescription;
	
	ObservableList<String> options1 =  FXCollections.observableArrayList();

	public TitleEditorWrapper wrapper;
	private final String targetDescr1 = "TargetDescr1";
	private final String targetDescr2 = "TargetDescr2";		
	private final String folderDescr = "FolderDescr";
	private boolean isInitialized;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private final ChangeListener<String> boxListener = new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				if (newValue==null || (oldValue!=null && newValue.equals(oldValue)))	return;
				removeListeners();
				if (newValue.equals(targetDescr1) || newValue.equals(targetDescr2) || newValue.equals(folderDescr)) {
					titleText.setEditable(false);
				}
				else {
					titleText.setEditable(true);
					Optional<Variable> opt = app.descriptionVariableEditorContainerController.variables.stream().filter(v->v.getName().equals(newValue)).findFirst();
					if (oldValue!=null && !oldValue.equals(newValue) && opt!=null && opt.isPresent()){
						addVariableToText(opt.get(), titleText);
					}
					if (oldValue!=null && !oldValue.equals(Settings.bundle.getString("ui.selector.text")) && newValue.equals(Settings.bundle.getString("ui.selector.text")))
						titleText.setText("");
				}
				setError(titleText, false, null);
				try {	
					updateCounter();
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
						removeListeners();
						setError(titleText, false, null);
						titleBox.getSelectionModel().select(Settings.bundle.getString("ui.selector.text"));
						 try {	
							 updateCounter();
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
								if (newValue == true) {
									titleText.setDisable(true);
									titleBox.setDisable(true);
								}
								else {
									titleText.setDisable(false);
									titleBox.setDisable(false);
								}
								setError(titleText, false, null);
								 try {	
									 updateCounter();
									 }
									 catch (TextAreaException e) {
										setError(e.textArea, true, e.getMessage());
									 }
								 finally {
									 addListeners();
								 }
							};
					};
					
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		titleBox.setItems(options1);
		titleBox.getSelectionModel().select(Settings.bundle.getString("ui.selector.text"));
		addListeners();
		
	}
	
    private void addListeners(){
    	titleBox.valueProperty().removeListener(boxListener);
		isTakeFromDescriptionBox.selectedProperty().removeListener(checkBoxListener);
		titleText.textProperty().removeListener(textListener);
    	titleBox.valueProperty().addListener(boxListener);
		isTakeFromDescriptionBox.selectedProperty().addListener(checkBoxListener);
		titleText.textProperty().addListener(textListener);
	}
	
	
	private void removeListeners(){
		titleBox.valueProperty().removeListener(boxListener);
		isTakeFromDescriptionBox.selectedProperty().removeListener(checkBoxListener);
		titleText.textProperty().removeListener(textListener);
	}
	
	
	
	private void addVariableToText(Variable d, TextArea ta){
		if (d==null)
			return;
		String previous = ta.getText().trim();
		if (!this.isInitialized)
			previous = "";
		StringBuilder sb = new StringBuilder(previous);
		if (!previous.isEmpty())
			sb.append(", ");
		sb.append("<");
		sb.append(d.getName());
		sb.append(">[1]");
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
	
	
	public void updateLists(){
		removeListeners();
		SingleSelectionModel<String> selected = titleBox.selectionModelProperty().getValue();
		String selectedValue = null;
		if (selected!=null && !selected.isEmpty())
			selectedValue = selected.getSelectedItem();
		
		options1.clear();
		options1.add(Settings.bundle.getString("ui.selector.text"));
		titleBox.getSelectionModel().select(Settings.bundle.getString("ui.selector.text"));
		if (!app.getFolderVariableData().isEmpty())
			options1.add(this.folderDescr);
		if (!app.getTargetsData().isEmpty()){
			options1.add(this.targetDescr1);
			options1.add(this.targetDescr2);
		}
		options1.addAll(app.descriptionVariableEditorContainerController.variables.stream().map(Variable::getName)
	              .collect(Collectors.toList()));
		
		if (options1.contains(selectedValue))
			titleBox.getSelectionModel().select(selectedValue);
		
		try {
			updateCounter();
		} catch (TextAreaException e) {
			setError(e.textArea, true, e.getMessage());
		}
		
		addListeners();
	}
	
	
	
	public void updateCounter() throws TextAreaException {
		String title = "";
		String data = titleBox.getSelectionModel().getSelectedItem();
		if (isTakeFromDescriptionBox.isSelected()) 
			title = app.descriptionEditorController.getMaxLengthDescription();
		else if (data==null || data.equals(Settings.bundle.getString("ui.selector.text"))){
			    	title  = parseVariablesInText(titleText, true);
		}
		else if (data.equals(this.folderDescr)){
			List<String> res = app.getFolderVariableData().stream().map(FolderVariable::getDescriptionVariable).collect(Collectors.toList());
			title = Collections.max(res, Comparator.comparing(s -> s.length()));
			titleText.setText(title);
		}
		else if (data.equals(this.targetDescr1)) {
			Target target = app.getTargetWithMaxLength();
			if (target!=null){
				title = target.getTargetDescr1();
			titleText.setText(title);
			}
			
		}
		else if (data.equals(this.targetDescr2)){
			Target target = app.getTargetWithMaxLength();
			if (target!=null){
				title = target.getTargetDescr2();
			titleText.setText(title);
			}
		}
	   /* else {
	    	String variableValue = Variable.getMaxValueByName(app.descriptionVariableEditorContainerController.variables, data);
		    if (variableValue==null) 
		    	titleText.setText("");
		    else
		    	titleText.setText(variableValue);
		}*/
		countLabel.setText("(" + getWordsCount(title) + "/" + title.length() + ")");
	}
	
	public void saveTitleSource() throws DataException{
		isTakeFromDescription = isTakeFromDescriptionBox.isSelected();
		  try {
			  SyntaxParser.checkVariables(app.descriptionVariableEditorContainerController.variables, titleText.getText());
			  titleTextSetting = titleText.getText();
		    }
		    catch (TextException e) {
		    	setError(titleText, true, e.getMessage());
				throw new DataException(this.tab);
		    }
		titleBoxSetting = titleBox.getSelectionModel().getSelectedItem()==null ? "" : titleBox.getSelectionModel().getSelectedItem();
	}
	
	public String getTitleForMetadata(){
		if (isTakeFromDescription)
			return app.descriptionEditorController.currentDescription;
		else if  (titleBoxSetting.equals(this.folderDescr))
			return app.mainFrameController.currentFolder.getDescriptionVariable();
		else if (titleBoxSetting.equals(this.targetDescr1)) 
			return app.mainFrameController.currentTarget.getTargetDescr1();
		else if (titleBoxSetting.equals(this.targetDescr2))
			return app.mainFrameController.currentTarget.getTargetDescr2();
		
		else {
			String variableValue = null;
			try {
				variableValue = SyntaxParser.pasteVariablesUnique(app.descriptionVariableEditorContainerController.savedVariables, this.titleTextSetting, false, " ");
			} catch (TextException e) {
				LOGGER.warning("ERROR: Ошибка вставки переменных в строку: " + this.titleTextSetting);
				app.isProblem = true;
			}
			if (variableValue==null)
				return "";
			else
				return variableValue;
		}
	}
	
	private int getWordsCount(String text){
		return text.split("\\s+").length;
	}

	public void clearAll(){
		titleBox.getSelectionModel().select(Settings.bundle.getString("ui.selector.text"));
		titleText.clear();
		isTakeFromDescriptionBox.setSelected(false);
	}

	@Override
	public void loadData() {
		updateLists();
		if (this.wrapper != null){
			titleText.setText(this.wrapper.inputValue);
			titleBox.getSelectionModel().select(this.wrapper.comboValue);
			isTakeFromDescriptionBox.setSelected(this.wrapper.isTakeFromDescription);
		}
		isInitialized = true;
	}

	@Override
	public void saveData() {
		this.wrapper = new TitleEditorWrapper(this.isTakeFromDescriptionBox.isSelected(), this.titleBox.getSelectionModel().getSelectedItem(), this.titleText.getText());
	}
}
