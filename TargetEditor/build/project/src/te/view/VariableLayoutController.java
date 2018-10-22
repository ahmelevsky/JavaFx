package te.view;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import te.Main;
import te.model.Variable;

public class VariableLayoutController implements Initializable {
	public Main app;
	public HBox layout;
	private Variable variable;
	public boolean isInitialLoad;
	@FXML
	private Button removeBtn;
	@FXML
	private TextField variableNameTxt;
	@FXML
	private TextArea variableValuesTxt;
	@FXML
	private ComboBox<String> variableDelimiterBox;
	
	private ObservableList<String> delimiters =  FXCollections.observableArrayList(","
			                                                                       ,";"
			                                                                       ,"опнаек"
			                                                                       ,"оепемня ярпнйх"
			                                                                       );
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		this.removeBtn.setShape(new Circle(25));
		variableNameTxt.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!variableNameTxt.getText().isEmpty() && !isInitialLoad)
				updateVariable();
		} );
		variableValuesTxt.textProperty().addListener((observable, oldValue, newValue) ->{ 
			if (!variableValuesTxt.getText().isEmpty() && !isInitialLoad)
			    updateVariable();
			});
		variableDelimiterBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!isInitialLoad)
				updateVariable();
			});
		this.variableDelimiterBox.setItems(delimiters);
		this.variableDelimiterBox.getSelectionModel().select(0);
		
	}


	public void setVariable(Variable variable) {
		this.isInitialLoad = true;
		this.variable = variable;
		switch (variable.getDelimiter()) {
			case ",":
				this.variableDelimiterBox.getSelectionModel().select(",");
				break;
			case ";":
				this.variableDelimiterBox.getSelectionModel().select(";");
				break;
			case " ":
				this.variableDelimiterBox.getSelectionModel().select("опнаек");
				break;
			case "\r\n":
				this.variableDelimiterBox.getSelectionModel().select("оепемня ярпнйх");
				break;
			default:
				break;
			}
		this.variableNameTxt.setText(variable.getName());
		this.variableValuesTxt.setText(StringUtils.join(variable.getValues(), variable.getDelimiter()));
		this.isInitialLoad = false;
	}
	
	
	
	public void updateVariable(){
		if (variable == null) return;
		variable.nameProperty().set(variableNameTxt.getText().trim());
		String delimiter = variableDelimiterBox.getSelectionModel().getSelectedItem();
		if (delimiter==null)
			delimiter = "";
		else
			switch (delimiter) {
			case ",":
				delimiter = ",";
				break;
			case ";":
				delimiter = ";";
				break;
			case "опнаек":
				delimiter = " ";
				break;
			case "оепемня ярпнйх":
				delimiter = "\\r?\\n";
				break;
			default:
				break;
			}
		variable.delimiterProperty().set(delimiter);
		variable.setValues(variableValuesTxt.getText());
	}
	
	@FXML
    private void remove(){
		app.keyVariableEditorContainerController.removeVariableLayout(this);
		app.descriptionVariableEditorContainerController.removeVariableLayout(this);
	}


	public Variable getVariable() {
		return variable;
	}
	

	
}
