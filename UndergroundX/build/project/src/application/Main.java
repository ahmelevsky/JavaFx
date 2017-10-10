package application;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.ui.ChatController;
import application.ui.ContactListController;
import application.ui.LoginController;
import application.ui.RegisterUserController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable; 
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main extends Application {
      
	
	 private Logger logger = LoggerFactory.getLogger(this.getClass());
	 private Stage loginStage;
	 private Stage contactListStage;
	 private Stage registerUserStage;
	// private Stage settingsStage;
	 private Profile loggedUser;
	private List<Stage> activeStages = new ArrayList<Stage>();
	 private LoginController loginController;
	 private RegisterUserController registerUserController;
	 private ContactListController contactListController;
	 private Map<String, ChatController> chatControllersMap = new HashMap<String, ChatController>();
	 private Map<String, Stage> chatWindowsMap = new HashMap<String, Stage>();
	 
	public void start(Stage primaryStage) throws Exception {
		 logger.debug("Старт!");
		
		ProfilesManager.createProfilesFolder();
		
		try {
			   loginStage = primaryStage;
			   activeStages.add(loginStage);
			   loginStage.setTitle("Выберите свой профиль");
			   gotoLogin();
	        } catch (Exception ex) {
	        	//Собщение об ошибке
	        }
		
	}

	public static void main(String[] args) {
		launch(args);
	}
	

    public void gotoLogin() {
        try {
        	for (Stage s:activeStages){
        		if (!s.equals(loginStage))
        		s.close();
        	}
            loginController = (LoginController) organizeStage(loginStage, "Login.fxml", LoginController.class);
            loginController.setApp(this);
            loginStage.show();
        } catch (Exception ex) {
          //Сообщение об ошибке
        }
    }

    private Initializable organizeStage(Stage stage, String fxml, @SuppressWarnings("rawtypes") Class c) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = c.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(c.getResource(fxml));
        AnchorPane page;
        try {
            page = (AnchorPane) loader.load(in);
        } finally {
            in.close();
        } 
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }
	

    

	   public boolean validateLogin(String userId, String password) throws Exception{
	      Profile profile = Authenticator.validate(userId, password);
		   if (profile!=null) {
			   loggedUser = profile;
			   loginStage.close();
			   activeStages.remove(loginStage);
			   contactListStage = new Stage();
			   activeStages.add(contactListStage);
			   contactListController = (ContactListController) organizeStage(contactListStage, "ContactList.fxml", ContactListController.class);
			   contactListController.setApp(this);
			   contactListController.setProfile(loggedUser);
			   contactListController.loadContactList();
			   contactListStage.setTitle("Underground X");
			   contactListStage.show();
			   contactListController.setUp();
	           return true;
	       } else {
	           return false;
	       }
	   }

		public void registerNewUser(String text) throws Exception {
			registerUserStage = new Stage();
			registerUserController = (RegisterUserController) organizeStage(registerUserStage, "RegisterUserUI.fxml", RegisterUserController.class);
			registerUserController.setApp(this);
			registerUserController.setLogin(text);
			registerUserStage.initModality(Modality.WINDOW_MODAL);
			registerUserStage.initOwner(loginStage.getScene().getWindow());
			registerUserStage.initStyle(StageStyle.UNDECORATED);
			registerUserStage.show();
		}
	   
		public void updateLoginWindowListWithNewUser(Profile profile){
			ProfilesManager.profileStorageList.put(profile.getLogin(), new ProfileStorage(profile.getLogin(), profile));
			loginController.addNewUserAndSetCurrent(profile);
		}

		
		public void openChat(String username) throws Exception {
	  
			
	   Stage chatStage = new Stage();
       ChatController cc = (ChatController) organizeStage(chatStage, "ChatUI.fxml", ChatController.class);
       chatControllersMap.put(username, cc);	
       chatWindowsMap.put(username, chatStage);
	   cc.setApp(this);	
	   //Добавить необходимые установки
	   chatStage.setTitle(username);
	   cc.setLogForChat(new LogHtml(getLoggedUser().getUsername()));
	   //chatStage.
	   chatStage.show();
	}
		
		 public Profile getLoggedUser() {
				return loggedUser;
			}
}
