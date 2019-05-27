package be.view;

import java.io.File;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import be.Main;
import be.model.BatchSource;
import be.util.Utils;

public class SourceLayoutController {

	@FXML
	private Circle statusCircle;
	
	@FXML
	public TextField captionText;
	
	@FXML
	private Hyperlink sourcePath;
	private Tooltip tooltip =  new Tooltip();
	
	@FXML
	private Label filesInfo;
	
	@FXML
	private CheckBox isJpgOnly;
	private Tooltip checkboxtooltip =  new Tooltip("Raster only (.jpg)");
	
	@FXML
	public Spinner<Integer> filesCount;
	SpinnerValueFactory<Integer> valueFactory;
	
	@FXML
	private Button removeBtn;
	
	private ObjectProperty<Integer> spinnerValueSource;
	private ObjectProperty<Integer> spinnerValueUI;
	private Main app;
	
	private static File lastDirectory;
	
	@FXML
    private void initialize() {
		this.statusCircle.setFill(Color.WHITE);
	    this.sourcePath.setTooltip(tooltip);
	    this.isJpgOnly.setTooltip(checkboxtooltip);
		this.removeBtn.setShape(new Circle(15));
		
		filesCount.focusedProperty().addListener((observable, oldValue, newValue) -> {
			  if (!newValue) {
				  filesCount.increment(0); // won't change value, but will commit editor
			  }
			});
	}

	public void setMainApp(Main app) {
		this.app = app;
	} 
	
	@FXML
    private void remove(){
		if (app.sourceControllers.size()>1)
			app.rootController.removeSourceLayout(app.sourceControllers.indexOf(this));
	}

	public void setSource(BatchSource source) {
		    //Assigning Fields
		    source.setStatus("underfined");
		    this.captionText.setText(source.getCaption());
		    this.sourcePath.setText(source.getPath());
		    this.valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, source.getFilesCount());
		    this.spinnerValueUI =  this.valueFactory.valueProperty();
		    filesCount.setValueFactory(valueFactory);
		    this.valueFactory.setValue(source.getFilesCount());
		    this.isJpgOnly.setSelected(source.isIsJpgOnly());
		    this.tooltip.setText(source.getPath());
		    
		    //Bindings
			this.captionText.textProperty().bindBidirectional(source.captionProperty());
			this.sourcePath.textProperty().bindBidirectional(source.pathProperty());
			this.spinnerValueSource = source.filesCountProperty().asObject();
			
			this.spinnerValueUI.bindBidirectional(spinnerValueSource);
			this.isJpgOnly.selectedProperty().bindBidirectional(source.isJpgOnlyProperty());
			this.tooltip.textProperty().bind(source.pathProperty());
			this.statusCircle.fillProperty().bind(Bindings.when(source.statusProperty().isEqualTo("underfined"))
						.then(Color.web("#36c6ed")).otherwise(Bindings.when(source.statusProperty().isEqualTo("progress"))
								.then(Color.AQUA).otherwise(Bindings.when(source.statusProperty().isEqualTo("error"))
										.then(Color.web("#fb5534")).otherwise(Bindings.when(source.statusProperty().isEqualTo("warning"))
												.then(Color.web("#ffde00")).otherwise(Bindings.when(source.statusProperty().isEqualTo("success"))
														.then(Color.web("#36ed72")).otherwise(Color.BLACK))))));
			Utils.hackTooltipStartTiming(tooltip);
			countImages();
	}
	
	
	@FXML
    private void selectPath(){
		  DirectoryChooser directoryChooser = new DirectoryChooser(); 

          directoryChooser.setTitle("Choose a folder with your files");
          File selected = new File(this.sourcePath.getText());
          if (selected.exists())
        	  directoryChooser.setInitialDirectory(selected);
          else if (lastDirectory!=null && lastDirectory.exists())
        	  directoryChooser.setInitialDirectory(lastDirectory);
          File file = directoryChooser.showDialog(app.getPrimaryStage());

         if(file!=null) {
        	 lastDirectory = file.getParentFile();
        	 this.sourcePath.setText(file.getAbsolutePath());
        	 countImages();
         }
         
	}
	
	public void countImages(){
		File location = new File(sourcePath.getText());
		 if(location.exists()){
			 int count = 0;
			     if (app.rootController.isSetRandom())
			    	 count = Utils.countFilesInDirectoryIfNoSubDirectories(location, ".jpg");
			     else
			    	 count = Utils.countFilesInDirectory(location, ".jpg");
        		 this.filesInfo.setText("Images count: " + count);
        	 }
        	 else
        		 this.filesInfo.setText("");
	}
	
}
