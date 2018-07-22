package te.view;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import te.Main;
import te.model.Target;

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
			if (newValue==null || newValue.isEmpty() || newValue.equals("<�����>")) 
				titleText.setEditable(true);
			else 
				titleText.setEditable(false);
			updateCounter();
		});
		
		
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
		options1.add("<�����>");
		options1.add("������");
		options1.add("������1");
		options1.add("������2");
	}
	
	private void updateCounter() {
		String title = "";
		String data = titleBox.getSelectionModel().getSelectedItem();
		if (isTakeFromDescriptionBox.isSelected()) 
			title = app.descriptionEditorController.getMaxLengthDescription();
		else if (data==null || data.equals("<�����>"))
		    title = titleText.getText().trim();
		else if (data.equals("������")) {
			List<String> res = app.getTargetsData().stream().map(Target::getTarget).collect(Collectors.toList());
			title = Collections.max(res, Comparator.comparing(s -> s.length()));
			titleText.setText(title);
		}
		else if (data.equals("������1")) {
			List<String> res = app.getTargetsData().stream().map(Target::getTarget1).collect(Collectors.toList());
			title = Collections.max(res, Comparator.comparing(s -> s.length()));
			titleText.setText(title);
		}
		else if (data.equals("������2")){
			List<String> res = app.getTargetsData().stream().map(Target::getTarget2).collect(Collectors.toList());
			title = Collections.max(res, Comparator.comparing(s -> s.length()));
			titleText.setText(title);
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
		else if (titleBoxSetting == null || titleBoxSetting.equals("<�����>"))
		   return titleTextSetting;
		else if (titleBoxSetting.equals("������")) 
			return app.mainFrameController.currentTarget.getTarget();
		else if (titleBoxSetting.equals("������1")) 
			return app.mainFrameController.currentTarget.getTarget1();
		else if (titleBoxSetting.equals("������2"))
			return app.mainFrameController.currentTarget.getTarget2();
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
			app.log("������������ ������� � ��������� ���� ������� ���������");
			result = false;
		}
		
		if (titleBoxSetting.equals("������"))
			if (!app.getTargetsData().stream().allMatch(t->app.isCorrectKey(t.getTarget()))){
				app.log("������������ ������� � ����� �� ����� ������");
				result = false;
			}
		if (titleBoxSetting.equals("������1"))
			if (!app.getTargetsData().stream().allMatch(t->app.isCorrectKey(t.getTarget1()))){
				app.log("������������ ������� � ����� �� ����� ������1");
				result = false;
			}
		if (titleBoxSetting.equals("������2"))
			if (!app.getTargetsData().stream().allMatch(t->app.isCorrectKey(t.getTarget1()))){
				app.log("������������ ������� � ����� �� ����� ������2");
				result = false;
			}
		return result;
	}

	
}
