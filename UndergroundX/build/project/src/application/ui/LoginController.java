package application.ui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import application.Authenticator;
import application.Profile;
import application.ProfilesManager;


public class LoginController extends BaseConroller  {

	 @FXML
	 private Button loginBtn;
	
	 @FXML
	 private Button registerBtn;
	
	 @FXML
	 private TextField usernameInput;
	
	 @FXML
	 private PasswordField passwordInput;
	 
	 @FXML
	 private ImageView profilePic;
	 
	 @FXML
	 private ComboBox<Profile> profileMenu;
	 
	 @FXML
	 private  Label errorMessage;
	 
	 ObservableList<Profile> profilesList  = FXCollections.observableArrayList();;
	 
	 
	 @Override
	public void initialize(URL location, ResourceBundle resources) {

		 logger.debug("Окно логина");
		 
		//Загружаем доступные профили
		
		//Collection<Profile> profiles = Manager.loadAvailableProfiles();
		List<Profile> profiles = null;
		try {
			profiles = ProfilesManager.getProfiles();
		} catch (IOException e) {
			System.err.println("Невозможно загрузить профили");
			System.exit(-1);
		}
		
		if (profiles.size()>0) {
		for (Profile profile:profiles) {
		    	profilesList.add(profile);
			}			
		loadProfilesToSelector();
		}
		
		//profilePic.setEffect(new Reflection());
	

		// loginBtn.setEffect(new GaussianBlur());
		// registerBtn.setEffect();
	}
	
	private void loadProfilesToSelector(){

		profileMenu.setItems(profilesList);
		
		profileMenu.setCellFactory(new Callback<ListView<Profile>, ListCell<Profile>>() {
              
			   @Override
			   public ListCell<Profile> call(ListView<Profile> param) {
				   return new ListCell<Profile>() {
				   @Override
				   public void updateItem(Profile item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item != null) {
                                setText(item.getUsername());    
                            }
                            }
				 
		};
		}
		});
		
		profileMenu.setConverter(new StringConverter<Profile>() {
		    @Override
		    public String toString(Profile person) {
		        if (person == null) {
		            return null;
		        } else {
		            return person.getUsername();
		        }
		    }
		        @Override
		        public Profile fromString(String personString) {
		            return null; // No conversion fromString needed.
		        }
		}  ); 
	}
	
	
	
	  public void profileSelector(ActionEvent event) {
		     Profile selectedPerson = profileMenu.getSelectionModel().getSelectedItem();
             if (selectedPerson==null) return; 
		     profilePic.setImage(selectedPerson.getPic());
		     usernameInput.setText(selectedPerson.getLogin());
		     passwordInput.setText(selectedPerson.getPassword());
	  };
		 
	
	 public void processLogin(ActionEvent event) throws Exception {
	        if (application == null){
	            // We are running in isolated FXML, possibly in Scene Builder.
	            // NO-OP.
	            errorMessage.setText("Hello " + usernameInput.getText());
	        } else {
	        	if (usernameInput.getText().isEmpty()) {
	        		errorMessage.setText("Введите имя пользователя");
                   return;
	        	}
	        		if (passwordInput.getText().isEmpty()) {
	        		  errorMessage.setText("Введите пароль");
	        		 return;  
	        		}
	        
	            if (!application.validateLogin(usernameInput.getText(), passwordInput.getText())){
	                errorMessage.setText("Имя пользователя или пароль неверны");
	            }
	            
	        }
	    }
	 
	 public void processRegister(ActionEvent event) throws Exception {
		 if (usernameInput.getText().isEmpty()) {
			 errorMessage.setText("Введите логин для регистрации");
			 return;
		 }
		 
		 if (!Authenticator.validateNewLogin(usernameInput.getText())) errorMessage.setText("Этот логин уже существует");
		 else
		 application.registerNewUser(usernameInput.getText());
	 }

	public void addNewUserAndSetCurrent(Profile profile) {
		profilesList.add(profile);
		loadProfilesToSelector();
		profileMenu.setValue(profile);
		profilePic.setImage(profile.getPic());
	    usernameInput.setText(profile.getLogin());
	    passwordInput.setText(profile.getPassword());
	    errorMessage.setText("");
	}

}
