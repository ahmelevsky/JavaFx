package rf.ui;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class WebController implements Initializable {

	@FXML
	private WebView wv;
	
	private WebEngine webEngine;
	private String shutterURL = "https://contributor-accounts.shutterstock.com/login";
	private URI uri = URI.create("https://contributor-accounts.shutterstock.com");
	private Map<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
	public String sessionId = null;
	java.net.CookieManager manager = null;
	@FXML
	private ProgressIndicator progress;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		 webEngine = wv.getEngine();
		 webEngine.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
		 manager = new java.net.CookieManager();
		 java.net.CookieHandler.setDefault(manager);
		headers.put("Set-Cookie", Arrays.asList("name=value"));
		 try {
			 manager.put(uri, headers);
		} catch (IOException e) {
			System.out.println("Cookies init error");
		}
		 Worker<Void> worker = webEngine.getLoadWorker(); 
		 worker.stateProperty().addListener(new ChangeListener<State>() {
			 
	           @Override
	           public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
	              // stateLabel.setText("Loading state: " + newValue.toString());
	               if (newValue == Worker.State.SUCCEEDED) {
	            	   progress.setVisible(false);
	               }
	           }
	       });
		 progress.progressProperty().bind(worker.progressProperty());
		 webEngine.load(shutterURL);
	}

	public void getSessionId() {
		 try {
				Map<String, List<String>> headers2 = manager.get(uri, headers);
				List<String> cookiesList = headers2.get("Cookie");
				if (cookiesList!=null && !cookiesList.isEmpty()) {
					for (String s:cookiesList) {
						System.out.println(s.split("=")[0] + " : " + s.split("=")[1]);
						if (s.startsWith("session="))
							this.sessionId = s.split("=")[1];
					}
				}
			} catch (IOException e) {
				System.out.println("Cookies get error");
			}
	}
	
	public String getCookieString() {
		 try {
				Map<String, List<String>> headers2 = manager.get(uri, headers);
				List<String> cookiesList = headers2.get("Cookie");
				if (cookiesList!=null && !cookiesList.isEmpty()) 
					return cookiesList.get(cookiesList.size()-1);
				else
					return null;
			} catch (IOException e) {
				System.out.println("Cookies get error");
				return null;
			}
	}
	
	
	
}
