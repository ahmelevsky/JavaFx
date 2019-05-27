package te.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import te.Main;
import te.Settings;
import te.model.Variable;

public class VariablesEditorContainerController  extends TargetEditorController implements Initializable {

	@FXML
	private Button addVarBtn;
	@FXML
	private VBox variableLayouts;
	
	public ObservableList<Variable> variables =    FXCollections.observableArrayList();
	public ObservableList<Variable> savedVariables =    FXCollections.observableArrayList();
	public List<VariableLayoutController> controllersList; 
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		this.addVarBtn.setShape(new Circle(25));
	}
	
	public void saveVariables(){
		savedVariables.clear();
		savedVariables.addAll(variables);
	}
	
	@FXML
	public void addVariableLayout(){
		 Variable var = new Variable("", ",", "");
		 this.variables.add(var); 
		 addVariableLayout(var);
	}
	
	public void addVariableLayout(Variable variable){
		try {
			
			 FXMLLoader loader = new FXMLLoader();
	         loader.setLocation(Main.class.getResource("view/VariableLayout.fxml"));
		     AnchorPane sourceLayout = (AnchorPane) loader.load();
	         variableLayouts.getChildren().add(sourceLayout);
	         //app.getPrimaryStage().sizeToScene();
	         VariableLayoutController controller = loader.getController();
	         controller.app = this.app;
	         controller.setVariable(variable);
	         controller.layout = sourceLayout;
	         controllersList.add(controller);
	         
			} catch (IOException e) {
				  Alert alert = new Alert(AlertType.ERROR);
		            alert.setTitle(Settings.bundle.getString("alert.error.title"));
		            alert.setHeaderText(Settings.bundle.getString("alert.error.addtoform.content"));
		            alert.setContentText(e.getMessage());
		            alert.showAndWait();
			}
	}
	
	
	public void removeVariableLayout(VariableLayoutController controller){
		 try {
			 if (!controllersList.contains(controller)) return;
			 variableLayouts.getChildren().remove(controller.layout);
			 variables.remove(controller.getVariable());
			 controllersList.remove(controller);
			 //app.getPrimaryStage().sizeToScene();
		 }
		 catch (Exception e) {
			    Alert alert = new Alert(AlertType.ERROR);
	            alert.setTitle(Settings.bundle.getString("alert.error.title"));
	            alert.setHeaderText(Settings.bundle.getString("alert.error.removeelements.content"));
	            alert.setContentText(e.getMessage());
	            alert.showAndWait();
		 }
		  
	}
	
	
	public void clear() {
		variableLayouts.getChildren().clear();
		app.keyVariableControllers.clear();
		variables =  FXCollections.observableArrayList();
	}

	@Override
	public void loadData() {
		for (Variable v:this.variables) {
			addVariableLayout(v);
		}
		
	}

	@Override
	public void saveData() {
		// TODO Auto-generated method stub
		
	}
	

}
