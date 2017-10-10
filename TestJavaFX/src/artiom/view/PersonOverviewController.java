package artiom.view;


import artiom.Main;
import artiom.model.Person;
import artiom.util.DateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


public class PersonOverviewController {
    @FXML
    private TableView<Person> personTable;
    @FXML
    private TableColumn<Person, String> firstNameColumn;
    @FXML
    private TableColumn<Person, String> lastNameColumn;

    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label streetLabel;
    @FXML
    private Label postalCodeLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label birthdayLabel;

    // ������ �� ������� ����������.
    private Main mainApp;

    /**
     * �����������.
     * ����������� ���������� ������ ������ initialize().
     */
    public PersonOverviewController() {
    }

    /**
     * ������������� ������-�����������. ���� ����� ���������� �������������
     * ����� ����, ��� fxml-���� ����� ��������.
     */
    @FXML
    private void initialize() {
        // ������������� ������� ��������� � ����� ���������.
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        
     // ������� �������������� ���������� �� ��������.
        showPersonDetails(null);

        // ������� ��������� ������, � ��� ��������� ����������
        // �������������� ���������� �� ��������.
        personTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPersonDetails(newValue));
    }

    /**
     * ���������� ������� �����������, ������� ��� �� ���� ������.
     * 
     * @param mainApp
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        // ���������� � ������� ������ �� ������������ ������
        personTable.setItems(mainApp.getPersonData());
    }
    
    /**
     * ��������� ��� ��������� ����, ��������� ����������� �� ��������.
     * ���� ��������� ������� = null, �� ��� ��������� ���� ���������.
     * 
     * @param person � ������� ���� Person ��� null
     */
    private void showPersonDetails(Person person) {
        if (person != null) {
            // ��������� ����� ����������� �� ������� person.
            firstNameLabel.setText(person.getFirstName());
            lastNameLabel.setText(person.getLastName());
            streetLabel.setText(person.getStreet());
            postalCodeLabel.setText(Integer.toString(person.getPostalCode()));
            cityLabel.setText(person.getCity());
            birthdayLabel.setText(DateUtil.format(person.getBirthday()));
        } else {
            // ���� Person = null, �� ������� ���� �����.
            firstNameLabel.setText("");
            lastNameLabel.setText("");
            streetLabel.setText("");
            postalCodeLabel.setText("");
            cityLabel.setText("");
            birthdayLabel.setText("");
        }
    }
    
    @FXML
    private void handleDeletePerson() {
    	 int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
    	    if (selectedIndex >= 0) {
    	        personTable.getItems().remove(selectedIndex);
    	    } else {
    	        // ������ �� �������.
    	        Alert alert = new Alert(AlertType.WARNING);
    	        alert.initOwner(mainApp.getPrimaryStage());
    	        alert.setTitle("No Selection");
    	        alert.setHeaderText("No Person Selected");
    	        alert.setContentText("Please select a person in the table.");

    	        alert.showAndWait();
    	    }
    }
    
    /**
     * ����������, ����� ������������ ������� �� ������ New...
     * ��������� ���������� ���� � �������������� ����������� ������ ��������.
     */
    @FXML
    private void handleNewPerson() {
        Person tempPerson = new Person();
        boolean okClicked = mainApp.showPersonEditDialog(tempPerson);
        if (okClicked) {
            mainApp.getPersonData().add(tempPerson);
        }
    }

    /**
     * ����������, ����� ������������ ������� �� ������ Edit...
     * ��������� ���������� ���� ��� ��������� ���������� ��������.
     */
    @FXML
    private void handleEditPerson() {
        Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
        if (selectedPerson != null) {
            boolean okClicked = mainApp.showPersonEditDialog(selectedPerson);
            if (okClicked) {
                showPersonDetails(selectedPerson);
            }

        } else {
            // ������ �� �������.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Person Selected");
            alert.setContentText("Please select a person in the table.");

            alert.showAndWait();
        }
    }
    
}