package klt.ui;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import klt.data.ImageData;
import klt.data.JsonParser;
import klt.web.ShutterProvider;

public class MainWindowController implements Initializable {

	@FXML
	private FlowPane imagesContainer;
	
	@FXML
	private ScrollPane sp;
	
	@FXML
	private TextArea ta;
	
	@FXML
	private TextField queryInput;
	
	@FXML
	private Button searchBtn;
	
	@FXML
	private Button selectAllBtn;
	
	@FXML
	private Button unSelectAllBtn;
	
	
	
	private List<SelectableBorderPane> images = new ArrayList<SelectableBorderPane>();
	
	private ObservableList<SelectableNode> selectedItems = FXCollections.observableArrayList();

	public ObservableList<SelectableNode> getSelectedItems() {
		return selectedItems;
	}
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		SelectionHandler selectionHandler = new SelectionHandler(imagesContainer);
		imagesContainer.addEventHandler(MouseEvent.MOUSE_PRESSED, selectionHandler.getMousePressedEventHandler());
		imagesContainer.prefWidthProperty().bind(sp.widthProperty());
		imagesContainer.prefHeightProperty().bind(sp.heightProperty());
		
		
		queryInput.setOnKeyReleased(event -> {
			  if (event.getCode() == KeyCode.ENTER){
				  search();
			  }
			});
	}

	
	public void addImageThumbnail(ImageData imageData) {
		SelectableBorderPane pane = SelectableBorderPane.create(imageData);
		imagesContainer.getChildren().add(pane);
		images.add(pane);
	}
	
	
	@FXML
	private void search() {
		imagesContainer.getChildren().clear();
		String query = queryInput.getText();
		if (query!=null && !query.trim().isEmpty()) {
			String result = ShutterProvider.findImages(query);
			List<ImageData> data = JsonParser.parseImagesData(result);
			for (ImageData imdata:data) {
				addImageThumbnail(imdata);
			}
			/*
			 * String text = ""; for (ImageData d:data) text += d.toString();
			 * this.ta.setText(text);
			 */
		}
	}
	
	
	public boolean select(SelectableNode n, boolean selected) {
		if(n.requestSelection(selected)) {
			if (selected) {
				selectedItems.add(n);
			} else {
				selectedItems.remove(n);
			}
			n.notifySelection(selected);
			keywordsProcessing();
			return true;
		} else {
			return false;
		}
	}

	public void unselectAll() {
		List<SelectableNode> unselectList = new ArrayList<>();
		unselectList.addAll(selectedItems);

		for (SelectableNode sN : unselectList) {
			select(sN, false);
		}
	}
	
	public void keywordsProcessing() {
		
		
		this.ta.clear();
		List<String> kwds = new ArrayList<String>();
		for (SelectableNode pane:this.selectedItems) {
			SelectableBorderPane p = (SelectableBorderPane)pane;
			kwds.addAll(p.imageData.keywords);
		}
		this.ta.setText(String.join(", ", kwds));
	}
	
	
	@FXML
	private void selectAll() {
		
	}
	
	
	private void startLoading() {
		
	}
	
	private void stopLoading() {
		
	}
}
