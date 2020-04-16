package klt.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import klt.Main;
import klt.data.ImageData;
import klt.data.JsonParser;
import klt.web.ShutterProvider;

public class SelectableBorderPane extends BorderPane implements SelectableNode {

	public ImageData imageData;
	public static Main app;
	
	private SelectableBorderPane() {
		super();
	}

	
	public static SelectableBorderPane create(ImageData imageData) {
		SelectableBorderPane pane = new SelectableBorderPane();
		pane.imageData = imageData;
		ImageView im = new ImageView();
		im.addEventHandler(MouseEvent.ANY, evt -> {
	        im.getParent().fireEvent(evt);
	    });
		pane.setPrefSize(280, 220);
        pane.setCenter(im);
        im.setImage(new Image(imageData.src, 260, 200, true, true, true));
	    return pane;
	}

	
	
	@Override
	public boolean requestSelection(boolean select) {
		return true;
	}

	@Override
	public void notifySelection(boolean select) {
		if(select) {
			this.setStyle("-fx-background-color: #BF9EAE;");
			this.imageData.selected = true;
		}
		else {
			this.setStyle(null);
			this.imageData.selected = false;
		}
	}
	
	public String getImageId() {
		if (this.imageData == null) return null;
		else return this.imageData.id;
	}
}
