package te.view;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import te.Main;
import te.model.Target;
import te.model.Variable;

public class TitleEditorController implements Initializable {
	public Main app;
	
	@FXML
	private TextArea titleText;
	@FXML
	private ComboBox<String> titleBox;
	@FXML
	private Label countLabel;
	@FXML
	private  CheckBox isTakeFromDescriptionBox;
	private String titleBoxSetting;
	private String titleTextSetting;
	
	private boolean isTakeFromDescription;
	
	ObservableList<String> options1 =  FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		titleBox.setItems(options1);
		titleBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue==null || newValue.isEmpty() || newValue.equals("<текст>")) 
				titleText.setEditable(true);
			else 
				titleText.setEditable(false);
			updateCounter();
		});
		titleBox.getSelectionModel().select("<текст>");
		
		titleText.textProperty().addListener((observable, oldValue, newValue) -> {
			countLabel.setText("(" + getWordsCount(newValue) + "/" + newValue.length() + ")");
		});
		
		isTakeFromDescriptionBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == true) {
				titleText.setDisable(true);
				titleBox.setDisable(true);
			}
			else {
				titleText.setDisable(false);
				titleBox.setDisable(false);
			}
			updateCounter();
		});
		
	}
	
	
	public void updateLists(){
		
		SingleSelectionModel<String> selected = titleBox.selectionModelProperty().getValue();
		String selectedValue = null;
		if (selected!=null && !selected.isEmpty())
			selectedValue = selected.getSelectedItem();
		
		options1.clear();
		options1.add("<текст>");
		titleBox.getSelectionModel().select("<текст>");
		if (!app.getTargetsData().isEmpty()){
			options1.add("Таргет");
			options1.add("Таргет1");
			options1.add("Таргет2");
		}
		options1.addAll(app.variables.stream().map(Variable::getName)
	              .collect(Collectors.toList()));
		
		if (options1.contains(selectedValue))
			titleBox.getSelectionModel().select(selectedValue);
	}
	
	public void updateCounter() {
		String title = "";
		String data = titleBox.getSelectionModel().getSelectedItem();
		if (isTakeFromDescriptionBox.isSelected()) 
			title = app.descriptionEditorController.getMaxLengthDescription();
		else if (data==null || data.equals("<текст>"))
		    title = titleText.getText().trim();
		else if (data.equals("Таргет")) {
			List<String> res = app.getTargetsData().stream().map(Target::getTarget).collect(Collectors.toList());
			title = Collections.max(res, Comparator.comparing(s -> s.length()));
			titleText.setText(title);
		}
		else if (data.equals("Таргет1")) {
			List<String> res = app.getTargetsData().stream().map(Target::getTarget1).collect(Collectors.toList());
			title = Collections.max(res, Comparator.comparing(s -> s.length()));
			titleText.setText(title);
		}
		else if (data.equals("Таргет2")){
			List<String> res = app.getTargetsData().stream().map(Target::getTarget2).collect(Collectors.toList());
			title = Collections.max(res, Comparator.comparing(s -> s.length()));
			titleText.setText(title);
		}	
	    else if (app.variables.stream().anyMatch( v -> data.equals(v.getName()))){
			Optional<Variable> vo = app.variables.stream().filter(v -> data.equals(v.getName())).findFirst();
			if (vo !=null && vo.isPresent()) {
				title = Collections.max(vo.get().getValues(), Comparator.comparing(s -> s.length()));
				titleText.setText(title);
			}
		}
		countLabel.setText("(" + getWordsCount(title) + "/" + title.length() + ")");
	}
	
	public void saveTitleSource(){
		isTakeFromDescription = isTakeFromDescriptionBox.isSelected();
		titleTextSetting = titleText.getText();
		titleBoxSetting = titleBox.getSelectionModel().getSelectedItem()==null ? "" : titleBox.getSelectionModel().getSelectedItem();
	}
	
	public String getTitleForMetadata(){
		if (isTakeFromDescription)
			return app.descriptionEditorController.currentDescription;
		else if (titleBoxSetting == null || titleBoxSetting.equals("<текст>"))
		   return titleTextSetting;
		else if (titleBoxSetting.equals("Таргет")) 
			return app.mainFrameController.currentTarget.getTarget();
		else if (titleBoxSetting.equals("Таргет1")) 
			return app.mainFrameController.currentTarget.getTarget1();
		else if (titleBoxSetting.equals("Таргет2"))
			return app.mainFrameController.currentTarget.getTarget2();
		else if (app.variables.stream().anyMatch( v -> titleBoxSetting.equals(v.getName()))){
			String result = "";
			Optional<Variable> vo = app.variables.stream().filter(v -> titleBoxSetting.equals(v.getName())).findFirst();
			if (vo !=null && vo.isPresent()) {
				result = vo.get().getRandomValue();
			}
			return result;
		}
		else {
			app.log("ERROR: Can't get TITLE. TitleBox value is: " + titleBoxSetting);
			return "";
		}
	}
	
	private int getWordsCount(String text){
		return text.split("\\s+").length;
	}
	
	
	public boolean checkDataIsCorrect(){
		
boolean result = true;
		
		if (!app.isCorrectKey(titleTextSetting)){
			app.log("Недопустимые символы в текстовом поле вкладки Назавание");
			result = false;
		}
		
		if (titleBoxSetting.equals("Таргет"))
			if (!app.getTargetsData().stream().allMatch(t->app.isCorrectKey(t.getTarget()))){
				app.log("Недопустимые символы в одном из полей Таргет");
				result = false;
			}
		if (titleBoxSetting.equals("Таргет1"))
			if (!app.getTargetsData().stream().allMatch(t->app.isCorrectKey(t.getTarget1()))){
				app.log("Недопустимые символы в одном из полей Таргет1");
				result = false;
			}
		if (titleBoxSetting.equals("Таргет2"))
			if (!app.getTargetsData().stream().allMatch(t->app.isCorrectKey(t.getTarget1()))){
				app.log("Недопустимые символы в одном из полей Таргет2");
				result = false;
			}
		if (app.variables.stream().anyMatch( v -> titleBoxSetting.equals(v.getName()))){
			Optional<Variable> vo = app.variables.stream().filter(v -> titleBoxSetting.equals(v.getName())).findFirst();
		    if (vo !=null && vo.isPresent()) {
		    	if(!vo.get().getValues().stream().allMatch(v->app.isCorrectKey(v))) {
		    		app.log("Недопустимые символы в одном значений переменной " + vo.get().getName());
		    		result = false;
		    	}
		   }
		}
		return result;
	}

	
}
