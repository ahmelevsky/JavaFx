package application;
	
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class Main extends Application {
	
    int ydt;
	int ldt;
	public  int remindTime = 60;
	public  int checkEach = 60000;
	private MainController mainController;
	private Stage mainStage;
	Alert alert = new Alert(Alert.AlertType.INFORMATION);
	Timer t = new Timer();
	boolean isSent;
	boolean isStopAlarm;
    List<Integer> queue = new ArrayList<>();
    final int queueLength = 10;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		mainStage = primaryStage;
		mainController = (MainController) organizeStage(mainStage, "MainForm.fxml", MainController.class);
		mainController.setApp(this);
		mainStage.setResizable(false);
		mainStage.show();
		mainStage.setOnCloseRequest(e -> t.cancel());
		//checkEach = Integer.parseInt(mainController.periodInput.getText());
		//remindTime = Integer.parseInt(mainController.alertTimeInput.getText());
		setTask(false);
	
	}
	
	
	public static void main(String[] args) throws Exception {
		launch(args);
	}
	
	public void setTask(boolean cancel){
		if (cancel) {t.cancel();
		t = new Timer();
		}
		
		t.scheduleAtFixedRate(new TimerTask() {
		        @Override
		        public void run() {
		            Platform.runLater(() -> {
		            	try {
							update();
						} catch (Exception e) {
							StringBuilder sb = new StringBuilder();
							sb.append(e.getMessage() + "\n");
							 for (StackTraceElement s:e.getStackTrace())
								 sb.append(s + "\n");
							e.printStackTrace();
						}
			    }); 
		        }
			}, 0, checkEach);
		
	}
	
	
	  private Initializable organizeStage(Stage stage, String fxml, @SuppressWarnings("rawtypes") Class c) throws IOException{
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
		
	
public void update() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
	 String  yd = "";
	 String ld = "";
	
	   Connection.Response loginForm;
	try {
		loginForm = Jsoup.connect("http://idlast.com/login.php")
		            .method(Connection.Method.GET)
		            .execute();
	} catch (IOException e1) {
		mainController.setRedStatus("Ошибка соединения");
		mainController.setInformation("");
		 mainController.okBtn.setVisible(false);
		 this.isStopAlarm=false;
		return;
	}

	           Document document;
			try {
				document = Jsoup.connect("http://idlast.com/login.php")
				    .data("login", "glaesarius")
				    .data("pass", "hmovfnde")
				    .cookies(loginForm.cookies())
				    .post();
			} catch (IOException e1) {
				mainController.setRedStatus("Ошибка соединения");
				mainController.setInformation("");
				 mainController.okBtn.setVisible(false);
				 this.isStopAlarm=false;
				return;
			}
	       
			
			Elements es = document.select("div[id*='dellegend']");
		    if (es.size()<2){
		    	mainController.setRedStatus("Ошибка данных");
		    	mainController.setInformation(document.toString());
		    	 mainController.okBtn.setVisible(false);
		    	 this.isStopAlarm=false;
				return;
		    }
		    
		    try {
		    int d1 = delegentToMinutes(es.get(es.size()-2));
		    int d2 = delegentToMinutes(es.get(es.size()-1));
		    ldt = (d1>d2) ? d1 : d2;
		    }
		    catch (Exception ex){
		    	mainController.setRedStatus("Ошибка данных");
		    	mainController.setInformation(document.toString());
		    	 mainController.okBtn.setVisible(false);
		    	 this.isStopAlarm=false;
				return;
		    }
			
	         	           
	          String regexString = Pattern.quote("Времени: ") + "(.*?)" + Pattern.quote(" <br /></p>\");");
	          Pattern pattern = Pattern.compile(regexString);
	          Matcher matcher = pattern.matcher(document.toString());
	       
	          if (matcher.find()) 
	    	     yd = matcher.group(1);
	          else {
	        	  mainController.setStatus("Нет засланцев");
	        	  mainController.setInformation("Время надписи, мин: " + ldt);
	        	  this.isSent = false;
	        	  mainController.okBtn.setVisible(false);
	        	  this.isStopAlarm=false;
	        	  mainController.setLastCheck();
				  return;
	          }
	          
	          try{
	          ydt = toMinutes(yd);
	          putQueue(ydt);
	          }
	          catch (Exception ex){
	        	  mainController.setRedStatus("Ошибка обработки");
	        	  mainController.setInformation(yd);
	        	  mainController.okBtn.setVisible(false);
	        	  this.isStopAlarm=false;
				  return;
	          }
	          
	          
	          if (isReady()){
	          if (!this.isStopAlarm)
	          {
	          mainController.okBtn.setVisible(true);
	          play2();
			  makeActive();
	          }
			  mainController.setLastCheck();
	          mainController.setRedStatus("Пора!");
        	  mainController.setInformation("Время надписи, мин: " + ldt);
        	  mainController.appendInformation("Ваш засланец. Времени: " + yd);
        	  mainController.appendInformation("Осталось: " + getTime(ydt - ldt));
        	  if (!isSent){
        		  List<String> text = new ArrayList<String>();
        		  text.add("Время надписи, мин: " + ldt);
        		  text.add("Ваш засланец. Времени, мин: " + ydt);
        		  text.add("Осталось: " + getTime(ydt - ldt));
        		  this.isSent = MailSender.createEmailAndSend(text);
        		  if (!this.isSent)
        			  mainController.information.appendText("Ошибка при отправке письма");
        	  }
	          }
	          else 
	          {
	          mainController.setStatus("Ожидание");
        	  mainController.setInformation("Время надписи, мин: " + ldt);
        	  mainController.appendInformation("Ваш засланец. Времени: " + yd);
        	  mainController.appendInformation("Осталось: " + getTime(ydt - ldt));
        	  mainController.setLastCheck();
        	  this.isSent = false;
        	  mainController.okBtn.setVisible(false);
        	  this.isStopAlarm=false;
	          }
}


private int delegentToMinutes(Element delegend){
	 String ld = delegend.text();
	 String ldd = ld.split(": ")[1].split(" ")[0];
 	 return Integer.parseInt(ldd);
}



public String getTime(int m){
    int hours = m / 60;
    int minutes = m % 60;
    return  hours + "ч. " + minutes + "мин.";
}

private int toMinutes(String data)
{
	int result = 0;
	if (data.contains("дн."))
		result += Integer.parseInt(data.split("дн.")[0].trim()) * 1400; 
	if (data.contains("ч.")){
		String[] d = data.trim().split("ч.")[0].split(" ");
	    result += Integer.parseInt(d[d.length-1])  * 60; 
	}
	if (data.contains("мин.")){
		String[] dd = data.trim().split(" ");
		result += Integer.parseInt(dd[dd.length-2]); 
	}
	
	return result;
}

/*private void play(){
	String bip = "bip.mp3";
	Media hit = new Media(new File(bip).toURI().toString());
	MediaPlayer mediaPlayer = new MediaPlayer(hit);
	mediaPlayer.play();
}
*/

private void play2() throws UnsupportedAudioFileException, IOException, LineUnavailableException{
	Platform.runLater(new Runnable() {
		  @Override
		  public void run() {
			  String bip = "bip.wav";
			  AudioInputStream audioInputStream = null;
			try {
				audioInputStream = AudioSystem.getAudioInputStream(new File(bip).getAbsoluteFile());
			} catch (UnsupportedAudioFileException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				 Clip clip = null;
				try {
					clip = AudioSystem.getClip();
				} catch (LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			       try {
					clip.open(audioInputStream);
				} catch (LineUnavailableException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				   clip.start();
		  }
		});
	
}


public void messageBox(String s){
	alert.setContentText("Внимание, осталось " + getTime(ydt - ldt));
	if (!alert.isShowing()){
    alert.setTitle("Та-дам!");
    alert.setHeaderText(null);
    alert.setContentText("Внимание, осталось " + getTime(ydt - ldt));
    alert.showAndWait();
	}
}

public void makeActive(){
	Platform.runLater(new Runnable() {
		  @Override
		  public void run() {
    		  mainStage.toFront();
		  }
		});
}

private void putQueue(int value){
	queue.add(0,value);
	if(queue.size() > queueLength)
		queue.remove(queue.size() - 1);
}

private boolean isReady(){
	int timeToCheck = remindTime + queueLength*checkEach/60000;
	return queue.stream().allMatch(q -> (q - this.ldt) < timeToCheck);
	
}


}
