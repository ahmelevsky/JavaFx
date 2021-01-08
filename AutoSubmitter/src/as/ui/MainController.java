package as.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.imageio.IIOException;

import org.json.JSONException;
import as.JsonParser;
import as.Main;
import as.ShutterImage;
import as.web.ContentResponse;
import as.web.ShutterProvider;
import as.web.SubmitResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MainController implements Initializable {

	public Main app;
	
	@FXML
	private Button getFilesListBtn;
	
	@FXML
	private Button submitBtn;
	
	@FXML
	private TextField sessionIdText;

	@FXML
	private TextFlow logTxt;
	
	public List<ShutterImage> images = new ArrayList<ShutterImage>();
	
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadSessionId();
	}	
	
	public void log(String message) {
		Platform.runLater(new Runnable() {
            public void run() {
            	 Text t1 = new Text();
                 //t1.setStyle("-fx-fill: #4F8A10;-fx-font-weight:bold;");
                 t1.setText(message + "\n");
                 logTxt.getChildren().add(t1);
            }
		 });
	}
	
	public void logError(String message) {
		Platform.runLater(new Runnable() {
            public void run() {
            	 Text t1 = new Text();
                 t1.setStyle("-fx-fill: red;-fx-font-weight:bold;");
                 t1.setText(message + "\n");
                 logTxt.getChildren().add(t1);
            }
		 });
	}
	
	public void log(Collection<String> messages) {
		Platform.runLater(new Runnable() {
            public void run() {
            	 Text t1 = new Text();
                 //t1.setStyle("-fx-fill: #4F8A10;-fx-font-weight:bold;");
                 t1.setText(String.join("\n", messages));
            }
		 });
	}
	
	@FXML
	private void submitBtnClick() {
		
		if (this.images.isEmpty()) {
			log("Нет файлов для сабмита либо не нажали сначала Get Files List");
			return;
		}
		
		
		List<ShutterImage> tempList = new ArrayList<ShutterImage>(); 
		ShutterImage im1 = images.get(0);
		im1.setCategories("26", "3");
		im1.setIs_illustration(true);
		tempList.add(im1);	
		
		

		saveSessionId();
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				
				String sessionId = sessionIdText.getText().trim();
				if (sessionId.isEmpty()) {
					showAlert("Пустой sessionId");
					return;
				}
				ShutterProvider provider = new ShutterProvider(sessionId);
				if (!provider.isConnection()) {
					showAlert("Ошибка соединения");
					return;
				}
				
				disableControl();
				
				submitImages(provider, tempList);
				
				enableControl();
				
			}
		});
		t1.start();
		
		
		/*String contentJson = JsonParser.createContentPayload(tempList);
		log(contentJson);
		System.out.println(contentJson);
		*/
		/*
		List<ShutterImage> list = new ArrayList<ShutterImage>();
		
		ShutterImage im1 = new ShutterImage ("testId1", "filename1.eps"); 
		ShutterImage im2 = new ShutterImage ("testId2", "filename2.eps"); 
		im1.setCategories("26", "3");
		list.add(im1);
		list.add(im2);
		String json = JsonParser.createContentPayload(list);
		log(json);
		System.out.println(json);
		*/
	}
	
	
	
	@FXML
	private void getFilesList() {
		
	
		
		
		logTxt.getChildren().clear();
		
		saveSessionId();
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				
				String sessionId = sessionIdText.getText().trim();
				if (sessionId.isEmpty()) {
					showAlert("Пустой sessionId");
					return;
				}
				ShutterProvider provider = new ShutterProvider(sessionId);
				if (!provider.isConnection()) {
					showAlert("Ошибка соединения");
					return;
				}
				
				disableControl();
				
				getLoadedFilesList(provider);
				
				enableControl();
				
			}
		});
		t1.start();
	}
	
	
	private void getLoadedFilesList(ShutterProvider provider) {
		int per_page = 100;
		int page = 1;
		String filesList = null;
		try {
		while (true) {
			filesList = provider.getLoadedFilesList(per_page,page);
			if (filesList == null) {
				logError("Ошибка соединения");
				showAlert("Ошибка соединения с сервером");
				return;
				}
			if (filesList.isEmpty()) break;
		    
			//writeToFile("D:\\Response.txt", rejectesString);
		    System.out.println(filesList);
		    
		    List<ShutterImage> imagesTemp;
		    imagesTemp = JsonParser.parseImagesData(filesList);
			if (imagesTemp.isEmpty()) break;
			images.addAll(imagesTemp);
			for (ShutterImage im:imagesTemp) {
				log(im.getUploaded_filename());
			}
			page++;
		}
		}
		catch (JSONException e) {
			if (filesList.contains("Redirecting to")) {
				logError("Autorization error");
				showAlert("Неправильный sessionId.");
			}
			else if (filesList.startsWith("{")){
				logError("JSON parsing error");
				showAlert("Данные загружены, но приложение не смогло их правильно интерпретировать.");
			}
			else {
				int endIndex = filesList.length()>300 ? 300 : filesList.length();
				logError("Unknown error");
				showAlert("Некорректные данные:\n" + filesList.substring(0, endIndex)); 
			}
			}
	}
	
	
	private void submitImages(ShutterProvider provider, List<ShutterImage> files) {
		
	//	int per_post = 10;
		
		try {
			
			
			
			ContentResponse reponse = provider.contentPost(files);
			log(reponse.toString());
			System.out.println(reponse.toString());
			
			SubmitResponse sresponse = provider.submitPost(files);
			log(sresponse.print());
			System.out.println(sresponse.print());
		}
		catch (JSONException e) {
				logError("JSON error" + e.getMessage());
			}
		catch (IOException e) {
			logError(e.getMessage());
		}
	}
	
	private void disableControl() {
		Platform.runLater(new Runnable() {
            public void run() {
            	getFilesListBtn.setDisable(true);
            	submitBtn.setDisable(true);
            }
		 });
	}
	
private void enableControl() {
	Platform.runLater(new Runnable() {
        public void run() {
        	getFilesListBtn.setDisable(false);
        	submitBtn.setDisable(false);
        }
	 });
	}
	
	private void showAlert(String text) {
		Platform.runLater(new Runnable() {
            public void run() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("ERROR");
		alert.setHeaderText("ERROR");
		alert.setContentText(text);
		alert.showAndWait();
            }
		 });
	}
	
	
	private void showMessage(String text) {
		Platform.runLater(new Runnable() {
            public void run() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Info");
		alert.setContentText(text);
		alert.showAndWait();
            }
		 });
	}
	
	
	private void saveSessionId() {
	  Preferences prefs = Preferences.userNodeForPackage(Main.class);
      if (!this.sessionIdText.getText().trim().isEmpty()) {
          prefs.put("sessionid", this.sessionIdText.getText());
      } else {
          prefs.remove("sessionid");
      }
	}
	
	private void loadSessionId() {
		Preferences prefs = Preferences.userNodeForPackage(Main.class);
        String sessionId = prefs.get("sessionid", null);
        if (sessionId != null) {
           this.sessionIdText.setText(sessionId);
        }
	}
	
	private void saveString(String key, String data) {
		  Preferences prefs = Preferences.userNodeForPackage(Main.class);
		  if (data!=null) {
			  prefs.put(key,data);
	      } else {
	          prefs.remove(key);
	      }
		}
		
		private String loadString(String key) {
			Preferences prefs = Preferences.userNodeForPackage(Main.class);
	        String saved = prefs.get(key, null);
	        return saved;
		}

}
