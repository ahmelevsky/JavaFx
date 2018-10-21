package te.view;

import te.Main;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;

public abstract class TargetEditorController implements Initializable {

	public Tab tab;
	public Main app;
	
	public abstract void loadData();
	public abstract void saveData();
}
