package application.ui;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import application.Profile;
import application.ProfilesManager;

public class RegisterUserController extends BaseConroller  {

	
	 private String login;
	 
	 @FXML
	 private ImageView profilePic;
	 
	 @FXML
	 private TextField username;
	 
	 @FXML
	 private TextField password;
	 
	 @FXML
	 private TextArea aboutText;
	 
	 @FXML
	 private Button selectPicBtn;
	 
	 @FXML
	 private Button okBtn;
	 
	 @FXML
	 private Button cancelBtn;
	 
	 @FXML
	 private  Label headerText;
	
	 private File picture;
	 
	 
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}
	
	public void cancel(ActionEvent event) throws Exception {
		  Stage stage = (Stage) cancelBtn.getScene().getWindow();
		    stage.close();
	}
	
	public void loadPicture(ActionEvent event) throws Exception {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Загрузите изображение");
		picture = fileChooser.showOpenDialog(selectPicBtn.getScene().getWindow());
		  if (picture != null) {
			  profilePic.setImage(new Image (new FileInputStream(picture.getAbsolutePath())));
          }
	}
	
	public void registerUser(ActionEvent event) throws Exception { 
		/*if (username.getText().isEmpty()) {
			username.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
		    return;
		}*/
		
		if (username.getText().isEmpty() || password.getText().isEmpty()) {
			headerText.setStyle("-fx-text-fill: red ;");
			headerText.setText("Заполните обязательные поля!");
			return;
		}
		
		else {
			Profile profile = new Profile(this.login);
			profile.setUsername(username.getText());
			profile.setPassword(password.getText());
			
			if (picture!=null)  ProfilesManager.setNewPicture(profile, picture);
			ProfilesManager.updateProfile(profile);
		
			 Stage stage = (Stage) okBtn.getScene().getWindow();
			 stage.close();
			 application.updateLoginWindowListWithNewUser(profile);
		}
		
	}

	public void setLogin(String text) {
		this.login = text;
	}
	
	
}
