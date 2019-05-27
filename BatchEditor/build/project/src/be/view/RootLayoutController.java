package be.view;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import be.Main;
import be.model.BatchSource;
import be.model.BatchSourceWrapper;
import be.model.GlobalSettings;
import be.util.Logger;
import be.util.Utils;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class RootLayoutController {

	@FXML
	private VBox sourceLayouts;
	
	@FXML
	public Spinner<Integer> batchesCount;
	SpinnerValueFactory<Integer> valueFactory;
	private ObjectProperty<Integer> spinnerValueSource;
	private ObjectProperty<Integer> spinnerValueUI;
	@FXML
	private Hyperlink batchesPath;
	private Tooltip tooltip =  new Tooltip();
	@FXML
	private Button addSourceBtn;
	
	@FXML
	private Button saveBtn;
	@FXML
	private CheckBox isRandom;
	@FXML
	private Button loadBtn;
	
	@FXML
	private Button createBtn;
	private Logger l = Logger.getLogger();
	private Main app;
	
	
	@FXML
    private void initialize() {
		this.valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
		batchesCount.setValueFactory(valueFactory);
		this.batchesPath.setTooltip(tooltip);
		//this.addSourceBtn.setStyle(" -fx-border-radius: 20;");
		this.addSourceBtn.setShape(new Circle(15));
		
		batchesCount.focusedProperty().addListener((observable, oldValue, newValue) -> {
			  if (!newValue) {
				  batchesCount.increment(0); // won't change value, but will commit editor
			  }
			});
	}
	
	public void setMainApp(Main app) {
		this.app = app;
		
		this.batchesPath.setText(app.gs.getPath());
		this.valueFactory.setValue(app.gs.getBatchescount());
		this.tooltip.setText(app.gs.getPath());
		this.spinnerValueUI = this.valueFactory.valueProperty();
		this.batchesPath.textProperty().bindBidirectional(app.gs.pathProperty());
		this.spinnerValueSource = app.gs.batchescountProperty();
		this.spinnerValueUI.bindBidirectional(spinnerValueSource);
		this.tooltip.textProperty().bind(app.gs.pathProperty());
		Utils.hackTooltipStartTiming(tooltip);
	}
	
	@FXML
	public void addSourceLayout(){
		 BatchSource bs = new BatchSource("", "<Click and select a folder>", 0, false);
		 app.sources.add(bs);
		 addSourceLayout(bs);
	}
	
	public void addSourceLayout(BatchSource source){
		try {
			 FXMLLoader loader = new FXMLLoader();
	         loader.setLocation(Main.class.getResource("view/SourceLayout.fxml"));
		     HBox sourceLayout = (HBox) loader.load();
	         sourceLayouts.getChildren().add(sourceLayout);
	         app.getPrimaryStage().sizeToScene();
	         SourceLayoutController controller = loader.getController();
	         controller.setMainApp(this.app);
	         controller.setSource(source);
	         app.sourceControllers.add(controller);
			} catch (IOException e) {
				  Alert alert = new Alert(AlertType.ERROR);
		            alert.setTitle("Error");
		            alert.setHeaderText("Can't add the elements to the form");
		            alert.setContentText(e.getMessage());
		            alert.showAndWait();
			}
	}
	
	
	public void removeSourceLayout(int index){
		 try {
			 sourceLayouts.getChildren().remove(index);
			 app.sources.remove(index);
			 app.sourceControllers.remove(index);
			 app.getPrimaryStage().sizeToScene();
		 }
		 catch (Exception e) {
			    Alert alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Error");
	            alert.setHeaderText("Can't remove the elements");
	            alert.setContentText(e.getMessage());
	            alert.showAndWait();
		 }
		  
	}
	
	
	public void clear() {
		sourceLayouts.getChildren().clear();
		app.sourceControllers.clear();
		app.getPrimaryStage().sizeToScene();
	}
	
	@FXML
    private void selectPath(){
		  DirectoryChooser directoryChooser = new DirectoryChooser(); 

          directoryChooser.setTitle("Select a root folder for the batches");
          File selected = new File(this.batchesPath.getText());
          if (selected.exists())
        	  directoryChooser.setInitialDirectory(selected);

          File file = directoryChooser.showDialog(app.getPrimaryStage());

         if(file!=null){
        	 this.batchesPath.setText(file.getAbsolutePath());
         }
	}
	
	@FXML
	private void loadSourcesFile(){
		 FileChooser fileChooser = new FileChooser(); 

		 fileChooser.setTitle("Select a sources file");
		 fileChooser.setInitialDirectory(app.sourcesFile.getParentFile());
		 fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files (.xml)", "*.xml"));
         File file = fileChooser.showOpenDialog(app.getPrimaryStage());

        if(file!=null){
       	 app.loadLastSources(file);
       	 this.clear();
       	 app.addSouceLayouts();
        }
	}
	
	@FXML
	private void saveSourcesFile(){
		 FileChooser fileChooser = new FileChooser(); 

		 fileChooser.setTitle("Save sources file");
		 fileChooser.setInitialDirectory(app.sourcesFile.getParentFile());
         File file = fileChooser.showSaveDialog(app.getPrimaryStage());

        if(file!=null){
        	if(!file.getName().contains(".")) {
        		file = new File(file.getAbsolutePath() + ".xml");
        		}
       	  app.saveLastSources(file);
        }
	}
	
	@FXML
    private void showCurrentState(){
		StringBuilder sb = new StringBuilder();
		for (BatchSource bs:app.sources)
			sb.append("\nSelected images count: " + bs.getFilesCount());
		sb.append("\nSelected batches count: " + app.gs.getBatchescount());
		 Alert alert = new Alert(AlertType.INFORMATION);
         alert.setTitle("Data");
         alert.setHeaderText("Data in the application: ");
         alert.setContentText(sb.toString());
         alert.showAndWait();
	}
	
	@FXML
    private void createBatches(){
	  app.updateData();
	  this.createBtn.setDisable(true);
	  app.sources.forEach(bs -> bs.setStatus("underfined"));
	  StringBuffer globalsb = new StringBuffer();
	  File outputFolder = new File(app.gs.getPath());
	  if (!outputFolder.exists()){
		  Alert alert = new Alert(AlertType.ERROR);
          alert.setTitle("Error");
          alert.setHeaderText("Can't create the batches");
          alert.setContentText("Given folder doesn't exist: " + app.gs.getPath());
          alert.showAndWait();
          this.createBtn.setDisable(false);
          return;
	  }
      LocalDateTime ld = LocalDateTime.now();
      String datePrefix = ld.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
      for (int batchNumber=1;batchNumber<=app.gs.getBatchescount();batchNumber++){
    		File batchFolder = new File(outputFolder.getAbsolutePath() + File.separator + datePrefix + "_" + batchNumber);
    		int movedcount = 0;
    		
    	    for (BatchSource bs:app.sources){
    	    	StringBuffer sb = new StringBuffer();
    	    	bs.setStatus("progress");
    	    	File sourceFolder = new File(bs.getPath());
    	    	if (!sourceFolder.exists() || bs.getFilesCount()==0) {
    	    		bs.setStatus("unknown");
    	    		continue;
    	    	}
    	    	
    	    	if (this.isRandom.isSelected())
    	    		movedcount = Utils.selectFilesAndMoveRandom(sourceFolder, batchFolder, bs.getFilesCount(), bs.isIsJpgOnly(), sb, movedcount);
    	    	else 
    	    		movedcount = Utils.selectFilesAndMove(sourceFolder, batchFolder, bs.getFilesCount(), bs.isIsJpgOnly(), sb, movedcount);
    				
    			 if (!sb.toString().isEmpty()){
    				globalsb.append("-=" + bs.getCaption() + "=-\n" + sb.toString() + "\n");
    				bs.setStatus("warning");
    			}
    			 else
    				bs.setStatus("success");
    			}
  	}
      			for (SourceLayoutController slc:app.sourceControllers)
      				slc.countImages();
      
    			  if (!globalsb.toString().isEmpty()){
    				  Alert alert = new Alert(AlertType.WARNING);
    		          alert.setTitle("Warning");
    		          alert.setHeaderText("Moving files completed with warnings");
    		          alert.setContentText(globalsb.toString());
    		          alert.showAndWait();
    			  }
    			  else {
    				  Alert alert = new Alert(AlertType.INFORMATION);
    		          alert.setTitle("SUCCESS");
    		          alert.setHeaderText("Moving files completed successfully");
    		          alert.showAndWait();
    			  }
    			 
    			  this.createBtn.setDisable(false);
    	  
	}

	public boolean isSetRandom(){
		return this.isRandom.isSelected();
	}
	
	@FXML
    private void changedAlgorythm(){
	    for (SourceLayoutController slc:app.sourceControllers)
	    	slc.countImages();
	}
}
