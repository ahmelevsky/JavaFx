package rf.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import rf.ShutterImage;

public class ImageInfoController implements Initializable {

	@FXML
	private Label filenameTxt;
	
	@FXML
	private Label descriptionTxt;
	
	@FXML
	private Label submitterNoteTxt;
	
	@FXML
	private CheckBox isIllustrationBox;
	
	@FXML
	private CheckBox hasPropertyReleaseBox;
	
	@FXML
	private ImageView previewImg;
	
	@FXML
	private Button closeBtn;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
	public void loadData(ShutterImage image) {
		if (image==null) {
			filenameTxt.setText("ERROR Loading Image data");
			return;
		}
		filenameTxt.setText(image.getOriginal_filename());
		descriptionTxt.setText(image.getDescription());
		previewImg.setImage(new Image(image.getPreviewPath(), true));
		submitterNoteTxt.setText(image.getSubmitter_note());
		isIllustrationBox.setSelected(image.getIs_illustration());
		hasPropertyReleaseBox.setSelected(image.getHas_property_release());
		isIllustrationBox.setDisable(true);
		hasPropertyReleaseBox.setDisable(true);
	}

	@FXML
	private void close() {
		Stage stage = (Stage) closeBtn.getScene().getWindow();
	    stage.close();
	}
	
	
}
