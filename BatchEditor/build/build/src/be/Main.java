package be;
	
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import be.model.BatchSource;
import be.model.BatchSourceWrapper;
import be.model.GlobalSettings;
import be.util.Logger;
import be.view.RootLayoutController;
import be.view.SourceLayoutController;
import javafx.beans.Observable;
import javafx.util.Callback;

public class Main extends Application {
	
	private VBox rootLayout;
	private Stage primaryStage;
	public ObservableList<BatchSource> sources = FXCollections.observableArrayList(new Callback<BatchSource, Observable[]>() {
        @Override
        public Observable[] call(BatchSource param) {
            return new Observable[]{
                    param.captionProperty(),
                    param.pathProperty(),
                    param.filesCountProperty()
            };
        }
    }
);
	public List<SourceLayoutController> sourceControllers = new ArrayList<SourceLayoutController>();
	public RootLayoutController rootController;
	public String batchFolderPath;
	public int batchesCount;
	public File sourcesFile = new File(System.getProperty("user.home") + File.separator + "batchcompilersources.xml");
	public File settingsfile = new File(System.getProperty("user.home") + File.separator + "batchcompilersettings.xml");
	public GlobalSettings gs;
	private Logger l = Logger.getLogger();
	public boolean wasChanged;
	
	@Override
	public void start(Stage primaryStage) {
		 if (settingsfile.exists()) 
			 loadLastSettings();
		 else
			 gs = new GlobalSettings("<Click and select an output folder for the batches>", 1);
		 this.primaryStage = primaryStage;
	     this.primaryStage.setTitle("BatchEditor v2.1");
	     this.primaryStage.getIcons().add(new Image("file:resources/icon.png"));
	     this.primaryStage.setMaxHeight(650);
	     this.primaryStage.setMinHeight(200);
	     this.primaryStage.setMinWidth(500);
	     initRootLayout();
	     File file = getPersonFilePath();
	        if (file != null  && file.getParentFile()!=null && file.getParentFile().exists()) {
	        	loadLastSources(file);
	        }
	        else if (this.sourcesFile.exists()) {
	        	 loadLastSources(this.sourcesFile);
	        }
	    /* else
	    	 BatchSourceWrapper.setDefaults();*/
	     addSouceLayouts(); 
	     sources.addListener(new ListChangeListener<BatchSource>() {
			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends BatchSource> c) {
			    	Main.this.wasChanged = true;
			}
	         });
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public Stage getPrimaryStage() {
	        return primaryStage;
	    }
	  
	 public void initRootLayout() {
	        try {
	            // Загружаем корневой макет из fxml файла.
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
	            rootLayout = (VBox) loader.load();
	            
	            rootController = loader.getController();
	            rootController.setMainApp(this);
	            
	            // Отображаем сцену, содержащую корневой макет.
	            Scene scene = new Scene(rootLayout);
	            primaryStage.setScene(scene);
	            primaryStage.sizeToScene();
	            primaryStage.show();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	 
	 
	 public void loadLastSources(File file){
		  try {
	            JAXBContext context = JAXBContext
	                    .newInstance(BatchSourceWrapper.class);
	            Unmarshaller um = context.createUnmarshaller();

	            // Чтение XML из файла и демаршализация.
	            BatchSourceWrapper wrapper = (BatchSourceWrapper) um.unmarshal(file);
	            sources.clear();
	            sources.addAll(wrapper.getBatchSources());
	            setPersonFilePath(file);
	            this.wasChanged = false;
	        } catch (Exception e) { // catches ANY exception
	            Alert alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Error");
	            alert.setHeaderText("Source load error");
	            //alert.setContentText("Can't load data from file:\n" + file.getPath());
	            StringBuilder sb = new StringBuilder(e.getMessage());
	            for (StackTraceElement s:e.getStackTrace())
	            	sb.append(s.toString() + "\n");
             	alert.setContentText(sb.toString());
	            alert.showAndWait();
	        }
	 }
	 
	 public void saveLastSources(File file){
		 try {
	            JAXBContext context = JAXBContext
	                    .newInstance(BatchSourceWrapper.class);
	            Marshaller m = context.createMarshaller();
	            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	            // Обёртываем наши данные об адресатах.
	            BatchSourceWrapper wrapper = new BatchSourceWrapper();
	            wrapper.setBatchSources(sources);
	            // Маршаллируем и сохраняем XML в файл.
	            m.marshal(wrapper, file);
	            
	            setPersonFilePath(file);
	        } catch (Exception e) { // catches ANY exception
	            Alert alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Error");
	            alert.setHeaderText("Source save error");
	          // alert.setContentText("Can't save data to file:\n" + file.getPath());
	            StringBuilder sb = new StringBuilder(e.getMessage());
	            for (StackTraceElement s:e.getStackTrace())
	            	sb.append(s.toString() + "\n");
             	alert.setContentText(sb.toString());
	            alert.showAndWait();
	        }
	 }
	 
	 public void loadLastSettings(){
		 l.debug("Loading last settings");
		  try {
	            JAXBContext context = JAXBContext
	                    .newInstance(GlobalSettings.class);
	            Unmarshaller um = context.createUnmarshaller();

	            // Чтение XML из файла и демаршализация.
	            this.gs = (GlobalSettings) um.unmarshal(this.settingsfile);
	        } catch (Exception e) { // catches ANY exception
	            Alert alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Error");
	            alert.setHeaderText("Error loading parameters");
	            alert.setContentText("Can't get parameters from file:\n" + this.settingsfile.getPath());

	            alert.showAndWait();
	        }
		  l.debug("Loaded settings");
	 }
	 
	 public void saveLastSettings(){
		 l.debug("Saving last settings");
		 try {
	            JAXBContext context = JAXBContext
	                    .newInstance(GlobalSettings.class);
	            Marshaller m = context.createMarshaller();
	            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	            m.marshal(this.gs, this.settingsfile);
	        } catch (Exception e) { // catches ANY exception
	            Alert alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Error");
	            alert.setHeaderText("Error saving parameters");
	            alert.setContentText("Can't save parameters to file:\n" + settingsfile.getPath());

	            alert.showAndWait();
	        }
		 l.log("Saved settings");
	 }
	 
	 
	 public void addSouceLayouts(){
		if (this.sources.isEmpty())
			this.rootController.addSourceLayout();
		else
			for (BatchSource bs:sources)
				this.rootController.addSourceLayout(bs);
	 }
	 
	 
	 @Override
	 public void stop(){
		 try{
	     updateData();
	     saveLastSettings();
	     if (this.wasChanged) {
	     Alert alert = new Alert(AlertType.CONFIRMATION);
	     alert.setTitle("Confirmation");
	     alert.setHeaderText("Save the changes?");

	     Optional<ButtonType> result = alert.showAndWait();
	     if (result.get() == ButtonType.OK){
	    	 saveLastSources(this.sourcesFile);
	     } 
		 }
		 }
		 catch (Exception e) {
			 Alert alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Error");
	            alert.setHeaderText("Error saving the data to xml");
	            alert.setContentText(e.getMessage());
	            alert.showAndWait();
	            throw e;
		 }
	 }
	 
	 public void updateData(){
		 for (SourceLayoutController slc:sourceControllers)
			 slc.filesCount.increment(0); 
		 rootController.batchesCount.increment(0);
	 }
	 
	 
	    public File getPersonFilePath() {
	        Preferences prefs = Preferences.userNodeForPackage(Main.class);
	        String filePath = prefs.get("filePath", null);
	        if (filePath != null && new File(filePath).exists()) {
	        	this.sourcesFile =  new File(filePath);
	            return this.sourcesFile;
	        } else {
	            return null;
	        }
	    }

	    public void setPersonFilePath(File file) {
	        Preferences prefs = Preferences.userNodeForPackage(Main.class);
	        if (file != null && file.getParentFile()!=null && file.getParentFile().exists()) {
	            prefs.put("filePath", file.getPath());
	            this.sourcesFile = file;
	            primaryStage.setTitle("BatchEditor - " + file.getName());
	        } else {
	            prefs.remove("filePath");
	            primaryStage.setTitle("BatchEditor");
	        }
	    }

		public void setFilesCount() {
			int count = 0;
			for (BatchSource source : sources) {
				count=count+source.getFilesCount();
			}
			rootController.setCount(count);
		}
}
