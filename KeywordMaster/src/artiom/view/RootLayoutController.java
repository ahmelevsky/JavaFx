package artiom.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.controlsfx.control.MasterDetailPane;
import org.eclipse.fx.ui.controls.filesystem.DirItem;
import org.eclipse.fx.ui.controls.filesystem.DirectoryTreeView;
import org.eclipse.fx.ui.controls.filesystem.DirectoryView;
import org.eclipse.fx.ui.controls.filesystem.IconSize;
import org.eclipse.fx.ui.controls.filesystem.ResourceItem;
import org.eclipse.fx.ui.controls.filesystem.ResourcePreview;
import org.eclipse.fx.ui.controls.filesystem.RootDirItem;

import artiom.Main;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

public class RootLayoutController implements Initializable {
	
	private Main app;
	private ImageGridController gridController;
	
	
	@FXML
	AnchorPane rootLayout; 
	
	@FXML
	public MasterDetailPane masterPane;
	
	public RootDirItem rootDirItem;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		  
      //  masterPane = new MasterDetailPane();
      //  masterPane.setMasterNode(new TableView());
      //  masterPane.setDetailNode(new PropertySheet());
    //    masterPane.setDetailSide(Side.LEFT);
     //   masterPane.setShowDetailNode(true);
     //   rootLayout.getChildren().add(masterPane);
		
	  
		//masterPane.setDetailNode(filesTree);
	}



	public void addDirectoryTree(String basePath){
		 rootDirItem = ResourceItem.createObservedPath(
			      Paths.get(basePath));
		 
		 DirectoryTreeView tv = new DirectoryTreeView();
		    tv.setIconSize(IconSize.MEDIUM);
		    tv.setRootDirectories(
		      FXCollections.observableArrayList(rootDirItem));
		 
		    DirectoryView v = new DirectoryView();
		    v.setIconSize(IconSize.BIG);
		 
		    tv.getSelectedItems().addListener( (Observable o) -> {
		      if( ! tv.getSelectedItems().isEmpty() ) {
		    	  System.out.println(tv.getSelectedItems().get(0).getUri());
		    	  try {
		    		  gridController.showImages(new File(new URL(tv.getSelectedItems().get(0).getUri()).toURI()));
		    	  } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	  
		      } else {
		    	//  gridController.clearGrid();
		      }
		    });
		 /*   ResourcePreview prev = new ResourcePreview();
		    v.getSelectedItems().addListener((Observable o) -> {
		      if( v.getSelectedItems().size() == 1 ) {
		        prev.setItem(v.getSelectedItems().get(0));
		      } else {
		        prev.setItem(null);
		      }
		    });*/
		    masterPane.setDividerPosition(0.2);
		    //masterPane.setMasterNode(v);
		    masterPane.setDetailNode(tv);
		 
	}
	

	public void setMainApp(Main main) {
		this.app = main;
		
	}
	

	public void setImageGridController(ImageGridController controller) {
		this.gridController = controller;
		
	}
	
}
