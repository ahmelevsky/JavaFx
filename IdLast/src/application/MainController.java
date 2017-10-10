package application;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class MainController  implements Initializable{

	
	@FXML
	private Label statusLabel;
   
	@FXML
	private Label lastLabel;
	
	@FXML
	public TextField periodInput;
	
	@FXML
	public TextField alertTimeInput;
	
	@FXML
	public Button setBtn;
	
	@FXML
	public Button okBtn;
	
	@FXML
	public Button alertTimeBtn;
	
	@FXML
	public TextArea information;
	
	private Main application;
		
	   public void setApp(Main app){
		   application = app;
	   }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setBtn.setDisable(true);
		alertTimeBtn.setDisable(true);
		alertTimeInput.setText("60");
		periodInput.setText("60");
		statusLabel.setText("Запуск");
		okBtn.setVisible(false);
		
		periodInput.textProperty().addListener(new ChangeListener<String>() {
		        @Override
		        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		            if (!newValue.matches("\\d*")) {
		            	periodInput.setText(newValue.replaceAll("[^\\d]", ""));
		            }
		            setBtn.setDisable(false);
		        }
		    });
		
		
		alertTimeInput.textProperty().addListener(new ChangeListener<String>() {
	        @Override
	        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
	            if (!newValue.matches("\\d*")) {
	            	alertTimeInput.setText(newValue.replaceAll("[^\\d]", ""));
	            }
	            alertTimeBtn.setDisable(false);
	        }
	    });
	}
	
	
	public void setStatus(String status){
		statusLabel.setTextFill(Color.BLACK);
		statusLabel.setText(status);
	}
	
	public void setRedStatus(String status){
		statusLabel.setTextFill(Color.RED);
		statusLabel.setText(status);
	}
	
	public void setInformation(String info){
		information.setText(info);
	}
	
	public void appendInformation(String info){
		information.appendText("\n" + info);
	}
	
	public void setLastCheck(){
		SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
		lastLabel.setText(sdf.format(new Date()));
	}
	
	public void setLastUpdate(String last){
		statusLabel.setText(last);
	}
	
	@FXML
	public void setCheckPeriod(){
		if (periodInput.getText()==null || periodInput.getText().trim().equals("") || Integer.parseInt(periodInput.getText())<5)
			periodInput.setText("5");
		application.checkEach = Integer.parseInt(periodInput.getText())*1000;
		application.setTask(true);
		setBtn.setDisable(true);
	}
	
	
	@FXML
	public void setAlertTimePeriod(){
		if (alertTimeInput.getText()==null || alertTimeInput.getText().trim().equals("") || Integer.parseInt(alertTimeInput.getText())<5)
			alertTimeInput.setText("5");
		application.remindTime = Integer.parseInt(alertTimeInput.getText());
		application.setTask(true);
		alertTimeBtn.setDisable(true);
	}
	   
	@FXML
	public void stopAlarm(){
		application.isStopAlarm = true;
		okBtn.setVisible(false);
	}
	   
	
}
