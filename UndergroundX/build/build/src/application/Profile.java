package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

public class Profile {

	private String username;
	private String password;
	private String login;
	private Image pic;
	private File currentPic;
	public ContactData cd;
	
	public static Map<String, Profile> profilesMap = new HashMap<String, Profile>(); 
	
	private static int profilesCount = 3;
	private static int num = 1;
	
	public Profile (String login){
		this.login = login;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Image getPic() {
		return pic;
	}

	public void setPic(File pic) throws FileNotFoundException {
		this.pic = new Image(new FileInputStream(pic.getAbsolutePath()));
		this.currentPic = pic;
	}
	
	public static Profile getCustomProfile(){
	//	String random = String.valueOf(new Random(System.currentTimeMillis()).nextInt(profilesCount));
        if (num>profilesCount) { 
        	System.err.println("Превышено количество заготовленных профилей");
        	return null;
        }
		Profile profile = new Profile("Пользователь " + num);
		profile.password = "123";
		profile.pic = new Image("./sampleImages/" + num + ".jpg");
		num++;
		profilesMap.put(profile.getUsername(), profile);
		return profile;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public File getCurrentPic() {
		return currentPic;
	}

}
