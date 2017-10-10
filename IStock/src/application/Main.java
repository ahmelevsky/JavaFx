package application;
	
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import com.gettyimages.ApiClient;
import com.gettyimages.SdkException;
import com.gettyimages.search.CreativeSearch;
import com.gettyimages.Credentials;
import com.gettyimages.WebHelper;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//launch(args);
		Credentials cr = Credentials.GetInstance("6z4uw9wmsds2s9fvy2me38x8", "wsRt8e8vxJpC6pVSTBAwWFMRZDRkXuhgYRe4Yk4U9jSK6", "ahmelevsky", "Leonart1994", "https://api.gettyimages.com/oauth2/");
		Map<String,String> formParameters = new HashMap<String,String>();
		formParameters.put("client_id", "eb6a6csvmfkdc68ykfcddgjf");
		//formParameters.put("client_secret", "gsY6rAtJhW2kqhJGPkzhsEJCcGzfZnVDMhwc7b57xber2");
		//formParameters.put("grant_type", "implicit");
		//formParameters.put("username", "olga_hmelevska");
		//formParameters.put("password", "JustOlga9");
		//String s = cr.PostForm(formParameters, "token/");
			
		System.out.println(s);
	}
}
