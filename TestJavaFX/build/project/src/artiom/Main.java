package artiom;
	
import javafx.application.Application;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import artiom.model.Person;
import artiom.model.PersonListWrapper;
import artiom.view.BirthdayStatisticsController;
import artiom.view.PersonEditDialogController;
import artiom.view.PersonOverviewController;
import artiom.view.RootLayoutController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    /**
     * ������, � ���� ������������ ������ ���������.
     */
    private ObservableList<Person> personData = FXCollections.observableArrayList();

    /**
     * �����������
     */
    public Main() {
        // � �������� ������� ��������� ��������� ������
        personData.add(new Person("Hans", "Muster"));
        personData.add(new Person("Ruth", "Mueller"));
        personData.add(new Person("Heinz", "Kurz"));
        personData.add(new Person("Cornelia", "Meier"));
        personData.add(new Person("Werner", "Meyer"));
        personData.add(new Person("Lydia", "Kunz"));
        personData.add(new Person("Anna", "Best"));
        personData.add(new Person("Stefan", "Meier"));
        personData.add(new Person("Martin", "Mueller"));
    }

    /**
     * ���������� ������ � ���� ������������ ������ ���������.
     * @return
     */
    public ObservableList<Person> getPersonData() {
        return personData;
    }

    
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressApp");
        this.primaryStage.getIcons().add(new Image("file:resources/images/Address_Book.png"));
        initRootLayout();
        showPersonOverview();
    }

    /**
     * �������������� �������� ����� � �������� ��������� ��������� ��������
     * ���� � ����������.
     */
    public void initRootLayout() {
        try {
            // ��������� �������� ����� �� fxml �����.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class
                    .getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // ���������� �����, ���������� �������� �����.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // ��� ����������� ������ � �������� ����������.
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // �������� ��������� ��������� �������� ���� � ����������.
        File file = getPersonFilePath();
        if (file != null) {
            loadPersonDataFromFile(file);
        }
    }
    /**
     * ���������� � �������� ������ �������� �� ���������.
     */
    public void showPersonOverview() {
        try {
            // ��������� �������� �� ���������.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/PersonOverview.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();

            // �������� �������� �� ��������� � ����� ��������� ������.
            rootLayout.setCenter(personOverview);
            // ��� ����������� ������ � �������� ����������.
            PersonOverviewController controller = loader.getController();
            controller.setMainApp(this);

            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ��������� ���������� ���� ��� ��������� ������� ���������� ��������.
     * ���� ������������ ������� OK, �� ��������� ����������� � ���������������
     * ������� �������� � ������������ �������� true.
     * 
     * @param person - ������ ��������, ������� ���� ��������
     * @return true, ���� ������������ ������� OK, � ��������� ������ false.
     */
    public boolean showPersonEditDialog(Person person) {
        try {
            // ��������� fxml-���� � ������ ����� �����
            // ��� ������������ ����������� ����.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/PersonEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // ������ ���������� ���� Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Person");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // ������� �������� � ����������.
            PersonEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPerson(person);

            // ���������� ���������� ���� � ���, ���� ������������ ��� �� �������
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * ���������� preference ����� ���������, �� ����, ��������� �������� ����.
     * ���� preference ����������� �� �������, ������������ ��� ����������
     * ������������ �������. ���� preference �� ��� ������, �� ������������ null.
     * 
     * @return
     */
    public File getPersonFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * ����� ���� �������� ������������ �����. ���� ���� �����������
     * � �������, ����������� ��� ���������� ������������ �������.
     * 
     * @param file - ���� ��� null, ����� ������� ����
     */
    public void setPersonFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());

            // ���������� �������� �����.
            primaryStage.setTitle("AddressApp - " + file.getName());
        } else {
            prefs.remove("filePath");

            // ���������� �������� �����.
            primaryStage.setTitle("AddressApp");
        }
    }
    
    /**
     * ��������� ���������� �� ��������� �� ���������� �����.
     * ������� ���������� �� ��������� ����� ��������.
     * 
     * @param file
     */
    public void loadPersonDataFromFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(PersonListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            // ������ XML �� ����� � ��������������.
            PersonListWrapper wrapper = (PersonListWrapper) um.unmarshal(file);

            personData.clear();
            personData.addAll(wrapper.getPersons());

            // ��������� ���� � ����� � �������.
            setPersonFilePath(file);

        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    /**
     * ��������� ������� ���������� �� ��������� � ��������� �����.
     * 
     * @param file
     */
    public void savePersonDataToFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(PersonListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // ��������� ���� ������ �� ���������.
            PersonListWrapper wrapper = new PersonListWrapper();
            wrapper.setPersons(personData);

            // ������������ � ��������� XML � ����.
            m.marshal(wrapper, file);

            // ��������� ���� � ����� � �������.
            setPersonFilePath(file);
        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }
    
    /**
     * ��������� ���������� ���� ��� ������ ���������� ���� ��������.
     */
    public void showBirthdayStatistics() {
        try {
            // ��������� fxml-���� � ������ ����� ����� ��� ������������ ����.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/BirthdayStatistics.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Birthday Statistics");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // ������� ��������� � ����������.
            BirthdayStatisticsController controller = loader.getController();
            controller.setPersonData(personData);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * ���������� ������� �����.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}