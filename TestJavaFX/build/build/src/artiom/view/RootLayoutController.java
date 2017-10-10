package artiom.view;


import java.io.File;

import artiom.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;


/**
 * ���������� ��� ��������� ������. �������� ����� ������������� �������
 * ����� ����������, ���������� ������ ���� � �����, ��� ����� ���������
 * ��������� �������� JavaFX.
 * 
 * @author Marco Jakob
 */
public class RootLayoutController {

    // ������ �� ������� ����������
    private Main mainApp;

    /**
     * ���������� ������� �����������, ����� �������� ������ �� ������ ����.
     * 
     * @param mainApp
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * ������ ������ �������� �����.
     */
    @FXML
    private void handleNew() {
        mainApp.getPersonData().clear();
        mainApp.setPersonFilePath(null);
    }

    /**
     * ��������� FileChooser, ����� ������������ ���� �����������
     * ������� �������� ����� ��� ��������.
     */
    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();

        // ����� ������ ����������
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // ���������� ������ �������� �����
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
            mainApp.loadPersonDataFromFile(file);
        }
    }

    /**
     * ��������� ���� � ���� ���������, ������� � ��������� ����� ������.
     * ���� ���� �� ������, �� ������������ ������ "save as".
     */
    @FXML
    private void handleSave() {
        File personFile = mainApp.getPersonFilePath();
        if (personFile != null) {
            mainApp.savePersonDataToFile(personFile);
        } else {
            handleSaveAs();
        }
    }

    /**
     * ��������� FileChooser, ����� ������������ ���� �����������
     * ������� ����, ���� ����� ��������� ������
     */
    @FXML
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();

        // ����� ������ ����������
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // ���������� ������ ���������� �����
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            mainApp.savePersonDataToFile(file);
        }
    }

    /**
     * ��������� ���������� ���� about.
     */
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("AddressApp");
        alert.setHeaderText("About");
        alert.setContentText("Author: Marco Jakob\nWebsite: http://code.makery.ch");

        alert.showAndWait();
    }

    /**
     * ��������� ���������� ���� ��������.
     */
    @FXML
    private void handleShowBirthdayStatistics() {
      mainApp.showBirthdayStatistics();
    }
    
    /**
     * ��������� ����������.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }
}