package te.view;

import java.net.URL;
import java.util.ResourceBundle;

import te.Main;
import te.model.Variable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

public class VariableLayoutController implements Initializable {
	public Main app;
	public HBox layout;
	private Variable variable;
	
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
			if (!variableNameTxt.getText().isEmpty())
				updateVariable();
		} );
		variableValuesTxt.textProperty().addListener((observable, oldValue, newValue) -> updateVariable());
		variableDelimiterBox.valueProperty().addListener((observable, oldValue, newValue) -> updateVariable());
		this.variableDelimiterBox.setItems(delimiters);
		this.variableDelimiterBox.getSelectionModel().select(0);
		
	}


	public void setVariable(Variable variable) {
		this.variable = variable;
		//this.variableDelimiterBox.setItems(delimiters);
		
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
		app.variableEditorContainerController.removeVariableLayout(this);
	}


	public Variable getVariable() {
		return variable;
	}
	

}
