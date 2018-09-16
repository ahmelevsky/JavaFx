package te.view;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

public class PresetsController extends TargetEditorController implements Initializable {

	@FXML
	private Button loadBtn;
	
	@FXML
	private Button saveBtn;
	
	@FXML
	private Button clearBtn;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	@FXML
	private void loadVariables(){
		 FileChooser fileChooser = new FileChooser(); 

		 fileChooser.setTitle("Выберите файл с источниками");
		// fileChooser.setInitialDirectory(app.sourcesFile.getParentFile());
		 fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files (.xml)", "*.xml"));
         File file = fileChooser.showOpenDialog(app.getPrimaryStage());
        /*
        if(file!=null){
       	   app.loadVariables(file);
        }*/
	}
	
	@FXML
	private void saveVariables(){
		 FileChooser fileChooser = new FileChooser(); 

		 fileChooser.setTitle("Сохраните файл с источниками");
		 //fileChooser.setInitialDirectory(app.sourcesFile.getParentFile());
         File file = fileChooser.showSaveDialog(app.getPrimaryStage());

        if(file!=null){
        	if(!file.getName().contains(".")) {
        		file = new File(file.getAbsolutePath() + ".xml");
        		}
       	 // app.saveLastSources(file);
        }
	}
	
	@FXML
	private void clearAllData(){
		app.clearAllData();
	}
}
