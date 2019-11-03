package klt.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import klt.Main;
import klt.data.ImageData;
import klt.data.JsonParser;
import klt.data.KeywordsCache;
import klt.web.ShutterProvider;

public class MainWindowController implements Initializable {

	public Main app;

	@FXML
	private Label leftStatusLabel;

	@FXML
	private Label rightStatusLabel;

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

	@FXML
	private ProgressBar loadBar;

	@FXML
	private VBox progressBarBox;

	private List<SelectableBorderPane> images = new ArrayList<SelectableBorderPane>();
	private List<SelectableBorderPane> previousImages = new ArrayList<SelectableBorderPane>();

	private ObservableList<SelectableNode> selectedItems = FXCollections.observableArrayList();

	public ObservableList<SelectableNode> getSelectedItems() {
		return selectedItems;
	}

	Task<Void> backgroundLoadTask;

	Task<Void> processingTask;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		SelectionHandler selectionHandler = new SelectionHandler(imagesContainer);
		imagesContainer.addEventHandler(MouseEvent.MOUSE_PRESSED, selectionHandler.getMousePressedEventHandler());
		imagesContainer.prefWidthProperty().bind(sp.widthProperty());
		imagesContainer.prefHeightProperty().bind(sp.heightProperty());

		queryInput.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				search();
			}
		});

	}

	public void setup() {
		loadBar.prefWidthProperty().bind(progressBarBox.widthProperty().subtract(20));
	}

	private void createProcessingTask() {
		processingTask = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {
				System.out.println("Run Processing Task");
				rightStatusUpdate("Waiting for keywords data to be downloaded...");
				while (!selectedItems.stream().filter(SelectableBorderPane.class::isInstance)
						.map(result -> (SelectableBorderPane) result).allMatch(n -> !n.imageData.keywords.isEmpty())) {
					if (isCancelled()) {
						System.out.println("Stop Processing Task");
						break;
					}
					Thread.sleep(200);
				}
				rightStatusUpdate("Keywords data downloaded. Processing...");
				List<String> kwds = new ArrayList<String>();
				for (SelectableNode pane : selectedItems) {
					SelectableBorderPane p = (SelectableBorderPane) pane;
					kwds.addAll(p.imageData.keywords);
				}
				ta.setText(String.join(", ", kwds));
				System.out.println("Completed Processing Task");
				rightStatusUpdate("Keywords result updated.");
				return null;
			}
		};
	}

	private void createBackgroundLoadTask() {
		backgroundLoadTask = new Task<Void>() {
			@Override
			public Void call() {
				System.out.println("Run Background Task");
				for (int i = 0; i < images.size(); i++) {
					leftStatusUpdate("Task in progress... Loading data for image " + i + " from " + images.size());
					ImageData imageData = images.get(i).imageData;
					updateKeywords(imageData);
					if (isCancelled()) {
						System.out.println("Stop Background Task");
						break;
					}
					updateProgress(i, images.size());
				}
				loadBar.progressProperty().unbind();
				System.out.println("Completed Background Task");
				leftStatusUpdate("Loading keywords data for all images done");
				return null;
			}
		};
		loadBar.progressProperty().bind(backgroundLoadTask.progressProperty());
	}

	private void callTask(Task t) {
		if (t.isRunning())
			t.cancel(true);
		Thread thr = new Thread(t);
		thr.setDaemon(true);
		thr.start();
	}

	public void addImageThumbnail(ImageData imageData) {
		SelectableBorderPane pane = SelectableBorderPane.create(imageData);
		imagesContainer.getChildren().add(pane);
		images.add(pane);
	}

	private void loadKeywordsBackground(int index) {
		ImageData imageData = this.images.get(index).imageData;
		updateKeywords(imageData);
	}

	
	
	private void updateKeywords(ImageData image) {
		if (image==null) {
			System.out.println("updateKeywords methods error: Image object is null");
			return;
		}
		else if (!image.keywords.isEmpty()) {
			System.out.println("updateKeywords methods message: imageData keywords is already present");
			return;
		}
		else if (KeywordsCache.get(image)) {
			System.out.println("updateKeywords methods message: found in cache");
		}
		else {
			Set<String> kwds = null;
			try {
				kwds = ShutterProvider.getKeywords(image.link);
			} catch (IOException e) {
				System.out.println("updateKeywords methods error: Can't get Keywords data. IOException when requesting data");
			}
			if (kwds==null) {
				System.out.println("updateKeywords methods error: Can't get Keywords data. NULL");
			}
			else {
				image.keywords.addAll(kwds);
				KeywordsCache.add(image);
			}
		}
	}
	
	

	@FXML
	private void search() {

		if (backgroundLoadTask != null && backgroundLoadTask.isRunning())
			backgroundLoadTask.cancel(true);

		cleanResults();

		String query = queryInput.getText();
		if (query != null && !query.trim().isEmpty()) {
			String result = ShutterProvider.findImages(query);
			List<ImageData> data = JsonParser.parseImagesData(result);
			for (ImageData imdata : data) {
				addImageThumbnail(imdata);
			}

			boolean needProcessing = false;
			for (SelectableBorderPane newimage : this.images) {
				SelectableBorderPane same = this.previousImages.stream()
						.filter(im -> im.imageData.id.equals(newimage.imageData.id)).findFirst().orElse(null);
				if (same != null) {
					needProcessing = true;
					newimage.imageData.keywords.addAll(same.imageData.keywords);
					selectedItems.add(newimage);
					newimage.notifySelection(true);
				}
			}
			if (needProcessing)
				keywordsProcessing();

			createBackgroundLoadTask();
			callTask(backgroundLoadTask);
		}
	}

	private void cleanResults() {
		imagesContainer.getChildren().clear();
		previousImages.clear();
		previousImages.addAll((Collection<? extends SelectableBorderPane>) selectedItems);
		images.clear();
		selectedItems.clear();
	}

	public boolean select(SelectableNode n, boolean selected) {
		if (n.requestSelection(selected)) {
			if (selected) {
				selectedItems.add(n);
				ImageData imData = ((SelectableBorderPane) n).imageData;
				if (imData != null)
					updateKeywords(imData);
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

	@FXML
	private void unselectAll() {
		if (selectedItems.isEmpty())
			return;
		List<SelectableNode> unselectList = new ArrayList<>();
		unselectList.addAll(selectedItems);
		for (SelectableNode sN : unselectList) {
			selectedItems.remove(sN);
			sN.notifySelection(false);
		}
		keywordsProcessing();
	}

	@FXML
	private void selectAll() {
		if (selectedItems.containsAll(images))
			return;
		for (SelectableNode sN : images) {
			selectedItems.add(sN);
			sN.notifySelection(true);
		}
		keywordsProcessing();
	}

	public void keywordsProcessing() {
		this.ta.setText("...");
		createProcessingTask();
		callTask(processingTask);
	}

	private void leftStatusUpdate(String text) {

		Platform.runLater(new Runnable() {
			public void run() {
				leftStatusLabel.setText(text);
			}
		});
	}

	private void rightStatusUpdate(String text) {

		Platform.runLater(new Runnable() {
			public void run() {
				rightStatusLabel.setText(text);
			}
		});

	}

}
