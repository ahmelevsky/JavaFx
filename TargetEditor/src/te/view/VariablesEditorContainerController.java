package te.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import te.Main;
import te.model.Variable;

public class VariablesEditorContainerController  implements Initializable {

	@FXML
	private Button addVarBtn;
	public Main app;
	
	@FXML
	private VBox variableLayouts;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		this.addVarBtn.setShape(new Circle(25));
		
	}
	
	
	@FXML
	public void addVariableLayout(){
		 Variable var = new Variable("", "", "");
		 app.variables.add(var); 
		 addVariableLayout(var);
	}
	
	public void addVariableLayout(Variable variable){
		try {
			 FXMLLoader loader = new FXMLLoader();
	         loader.setLocation(Main.class.getResource("view/VariableLayout.fxml"));
		     HBox sourceLayout = (HBox) loader.load();
	         variableLayouts.getChildren().add(sourceLayout);
	         app.getPrimaryStage().sizeToScene();
	         VariableLayoutController controller = loader.getController();
	         controller.app = this.app;
	         controller.setVariable(variable);
	         controller.layout = sourceLayout;
	         app.variableControllers.add(controller);
	        
			} catch (IOException e) {
				  Alert alert = new Alert(AlertType.ERROR);
		            alert.setTitle("Ошибка");
		            alert.setHeaderText("Не могу добавить элементы на форму");
		            alert.setContentText(e.getMessage());
		            alert.showAndWait();
			}
	}
	
	
	public void removeVariableLayout(VariableLayoutController controller){
		 try {
			 
			 variableLayouts.getChildren().remove(controller.layout);
			 app.variables.remove(controller.getVariable());
			 app.variableControllers.remove(controller);
			 //app.getPrimaryStage().sizeToScene();
		 }
		 catch (Exception e) {
			    Alert alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Ошибка");
	            alert.setHeaderText("Не могу удалить элементы");
	            alert.setContentText(e.getMessage());
	            alert.showAndWait();
		 }
		  
	}
	
	
	public void clear() {
		variableLayouts.getChildren().clear();
		app.variableControllers.clear();
		app.getPrimaryStage().sizeToScene();
	}

}
