package te.view;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import te.Main;
import te.Settings;
import te.model.Variable;
import te.util.TextAreaException;

public class VariableLayoutController implements Initializable {
	public Main app;
	public AnchorPane layout;
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
	@FXML
	private Label valuesCount;
	
	private ObservableList<String> delimiters =  FXCollections.observableArrayList(","
			                                                                       ,";"
			                                                                       ,Settings.bundle.getString("ui.tabs.vars.delimiter.space")
			                                                                       ,Settings.bundle.getString("ui.tabs.vars.delimiter.newline")
			                                                                       );
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		this.removeBtn.setShape(new Circle(25));
		variableNameTxt.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!variableNameTxt.getText().isEmpty() && !isInitialLoad)
				updateVariable();
		} );
		variableValuesTxt.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
		    @Override
		    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
		    {
		        if (!newPropertyValue)
		        {
		           removeDuplicatesFromTextField();
		        }
		    }
		});
		
		variableValuesTxt.textProperty().addListener((observable, oldValue, newValue) ->{ 
			if (!variableValuesTxt.getText().isEmpty() && !isInitialLoad)
			    updateVariable();
			    setError(variableValuesTxt, false);
			    try {
					app.checkSyntax(variableValuesTxt);
				} catch (TextAreaException e) {
					setError(e.textArea, true);
				}
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
				this.variableDelimiterBox.getSelectionModel().select(Settings.bundle.getString("ui.tabs.vars.delimiter.space"));
				break;
			case "\r\n":
				this.variableDelimiterBox.getSelectionModel().select(Settings.bundle.getString("ui.tabs.vars.delimiter.newline"));
				break;
			default:
				break;
			}
		this.variableNameTxt.setText(variable.getName());
		this.variableValuesTxt.setText(StringUtils.join(variable.getValues(), variable.getDelimiter()));
		valuesCount.setText(String.valueOf(variable.getValues().size()));
		this.isInitialLoad = false;
	}
	
	
	
	public void updateVariable(){
		if (variable == null) return;
		variable.nameProperty().set(variableNameTxt.getText().trim());
		String delimiter = variableDelimiterBox.getSelectionModel().getSelectedItem();
		if (delimiter==null)
			delimiter = "";
		else if (delimiter.equals(Settings.bundle.getString("ui.tabs.vars.delimiter.space")))
				delimiter = " ";
		else if (delimiter.equals(Settings.bundle.getString("ui.tabs.vars.delimiter.newline")))
				delimiter = "\\r?\\n";
		variable.delimiterProperty().set(delimiter);
		variable.setValues(variableValuesTxt.getText());
		valuesCount.setText(String.valueOf(variable.getValues().size()));
	}
	
	
	private void removeDuplicatesFromTextField() {
		if (variable!=null && !variable.getValues().isEmpty())
			variableValuesTxt.setText(String.join(variable.getDelimiter(), variable.getValues()));
	}
	
	@FXML
    private void remove(){
		app.keyVariableEditorContainerController.removeVariableLayout(this);
		app.descriptionVariableEditorContainerController.removeVariableLayout(this);
	}


	public Variable getVariable() {
		return variable;
	}
	
	private void setError(TextArea tf, boolean setOrUnset){
		 ObservableList<String> styleClass = tf.getStyleClass();
		 if (setOrUnset) {
			 if (! styleClass.contains("red")) {
	                styleClass.add("red");
	            }
			 }
		 else {
	            styleClass.removeAll(Collections.singleton("red"));          
	        }
	}
	
}
