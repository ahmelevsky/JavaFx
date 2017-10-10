package artiom.view;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import artiom.Main;
import artiom.util.CanvasPanel;
import artiom.util.DefaultImageFactory;
import artiom.util.ImageFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

public class ImageGridController implements Initializable {

	private Main app;
	@FXML
	FlowPane root;
	 private static final double PADDING = 5;
	 private   CanvasPanel canvas ;
	 private ImageFactory factory;
	
	 @Override
	public void initialize(URL location, ResourceBundle resources) {
		 factory = new DefaultImageFactory();
		 
	}
	
	public void showImages(File fromDirectory){
		 Path rootFolder = FileSystems.getDefault().getPath(fromDirectory.getAbsolutePath());
		 List<Path> folders = new ArrayList<Path>();
		 folders.add(rootFolder);
		 canvas = CanvasPanel.createCanvasPanel().
	                imagePath(folders).
	                imageFactory(factory).
	                padding(PADDING).
	                lineBreakLimit(0.1d).
	                selectionListener((x, y, image) -> {
	                    if (image.length == 1) {
	                        ImageContainer myImage = image[0];
	                        System.out.println("selected image: "+myImage.getImagePath().toString());
	                    }
	                });


	        canvas.widthProperty().bind(root.widthProperty().subtract(10));
	        canvas.heightProperty().bind(root.heightProperty().subtract(10));

	        root.getChildren().add(canvas);
	}
	
	public void clearGrid(){
		root.getChildren().clear();
	}
	
	public void setMainApp(Main main) {
		this.app = main;
	}
}
