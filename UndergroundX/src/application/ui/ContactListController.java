package application.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import application.Contact;
import application.Manager;
import application.Profile;

public class ContactListController extends BaseConroller {

	 private Profile loggedUser;
	 
	 @FXML
	 private ImageView profilePic;
	 
	 @FXML
	 private  Label username;
	 
	 @FXML
	 private Button settingsBtn;
	 
	 @FXML
	 private Button changeUserBtn;
	 
	 @FXML
	 private Button somethingBtn;
	 
	 @FXML
	 private Button statusBtn;
	 
	 @FXML
	 private TableView<Contact> contacts;
	 
	 @FXML
	 private TableColumn<Contact, Image> columnOne;
	 
	 @FXML
	 private TableColumn<Contact, String> columnTwo;
	 
	 private ObservableList<Contact> usersData = FXCollections.observableArrayList();
	 
	 public void setProfile(Profile profile){
	        this.loggedUser = profile;
	    }
	 public Contact activeContact; 
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		 logger.debug("Окно контактов");
		 columnOne.setCellValueFactory(new PropertyValueFactory<Contact, Image>("pic"));
		 columnOne.setCellFactory(new Callback <TableColumn<Contact, Image>, TableCell<Contact,Image>>() {

			@Override
			public TableCell<Contact, Image> call(TableColumn<Contact, Image> param) {
				 TableCell<Contact, Image> cell = new TableCell<Contact, Image>(){
						@Override
			            public void updateItem(Image img, boolean empty) {                        
			            if(img!=null){                            
			                    ImageView imageview = new ImageView();
			                    imageview.setFitHeight(50);
			                    imageview.setFitWidth(50);
			                    imageview.setImage(img); 
			                    HBox box= new HBox();
			                    box.setSpacing(10) ;
			                    box.getChildren().add(imageview);
			                    setGraphic(box);
			                }
			            }
			        };
			       // cell.setOnMouseEntered( (MouseEvent event) -> {System.out.println("Навели на " + cell.getId());});
			        //Tooltip.install(cell, new Tooltip("text"));
			        Tooltip t = new Tooltip("sdsadsfsdfiblsgrelsicgmtg rtrtgetr grt gijtr grt gjerthdrt  tribntrbn rtubhrdk uhbdbn kb d");
			        
			       /* ImageView imageview = new ImageView();
                    imageview.setFitHeight(50);
                    imageview.setFitWidth(50);
                    imageview.setImage(cell.getItem()); 
                    HBox box= new HBox();
                    box.setSpacing(10) ;
                    box.getChildren().add(imageview);
                    t.setGraphic(box);*/
			        
			        
			        cell.setTooltip(t);
			       
			        return cell;
			    }

			});
		 
		 columnTwo.setCellValueFactory(new PropertyValueFactory<Contact, String>("name"));
		 contacts.getSelectionModel().selectedItemProperty().addListener(
				 (ObservableValue<? extends Contact> observale, Contact oldValue,Contact newValue) -> {
				 activeContact = newValue; }
		 );
		 
		 
		 contacts.setOnMouseClicked(( MouseEvent event ) -> {
			 if (event.getButton().equals(javafx.scene.input.MouseButton.PRIMARY) && event.getClickCount() > 1)
				try {
					beginChat() ;
				} catch (Exception e) {
					e.printStackTrace();
				}
		 });
	}

	public void loadContactList() throws IOException {
		usersData.addAll(Manager.loadUserContacts(loggedUser));
		contacts.setItems(usersData);
	}


	public void setUp() {
		profilePic.setImage(loggedUser.getPic());
		username.setText(loggedUser.getUsername());
	}

	public void openLogin(ActionEvent event) throws Exception {
		application.gotoLogin();
	}
	
	
	public void beginChat() throws Exception {
		application.openChat(activeContact.getName());
	}
	
}
