package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

public class ProfilesManager {
	 static String userHome = System.getProperty("user.home") + "/UndergroundX";



	public static Map<String, ProfileStorage> profileStorageList = new HashMap<String, ProfileStorage>();
	 
	 public static ProfileStorage createNewProfile(Profile profile) {
		 ProfileStorage ps = new ProfileStorage(profile.getLogin(), profile);
		 
		 return ps;
	 }
	 
	 public static void createProfilesFolder(){
		 File root = new File(userHome);
		 if(!root.exists())
			 root.mkdir();
	 }
	 
	 public static void getExistedProfileDirectories() throws IOException{ 
		
		 profileStorageList.clear();
		 File root = new File(userHome);
		 if(!root.exists()) return;
		 File[] inRoot = root.listFiles();
		  for (File file:inRoot){
			  if (file.isDirectory()) {
				  if (checkProfileFolder(file)) {
					  ProfileStorage ps = new ProfileStorage(file);
					  ps.setProfile(readProfile(file));
					  profileStorageList.put(ps.getRootPath().getName(), ps);
				  }
			  }
		  }
	 }
	 
	 public static List<Profile> getProfiles() throws IOException{
		 List<Profile> profiles = new ArrayList<Profile>();
		 getExistedProfileDirectories();
		 for (ProfileStorage ps:profileStorageList.values()) {
			 profiles.add(readProfile(ps.getRootPath()));
		 }
		 return profiles;
	 }
	 
	 
	 public static Profile readProfile(File profileRootPath) throws IOException {
		 Profile profile = new Profile (profileRootPath.getName());
		 Properties props = new Properties();
	     InputStream fis;
		// OutputStream fos;
		 try {
				fis = new FileInputStream(profileRootPath.getAbsolutePath() + "/profile.ini");
				try {
					props.load(fis);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					fis.close();
				}
			} catch (FileNotFoundException e2) {
				//Какое-нибудь сообщение в лог
				
				throw new FileNotFoundException();
			}
		 
		 //По мере добавлять новые параметры профиля
		 profile.setUsername(props.getProperty("username"));
		 profile.setPassword(props.getProperty("password"));
		 File pic = new File(profileRootPath.getAbsolutePath() + "/Userpics/" + props.getProperty("currentImage"));
		 if (pic.exists())  profile.setPic(pic);
		 return profile;
	 }
	 
	 
	 
	 
	 public static ProfileStorage updateProfile(Profile profile) throws IOException {
		 ProfileStorage ps = new ProfileStorage (profile.getLogin(), profile);
		 Properties props = new Properties();
		 OutputStream fos;
		 
		 //Занесение параметров
	     props.setProperty("username", profile.getUsername());
	     props.setProperty("password", profile.getPassword());
	     
	     if (profile.getCurrentPic()!=null) 
	     props.setProperty("currentImage", profile.getCurrentPic().getName());
	     
	     File propfile = new File(userHome + "/" + profile.getLogin() + "/profile.ini");
		
	    //Если не было файла, создаем
	    try {
		 if (!propfile.exists())	
	     propfile.createNewFile();
		} catch (IOException e) {
			
			//
			throw new IOException();
		}
				fos = new FileOutputStream(propfile.getAbsolutePath());
				props.store(fos, "User profile");
				fos.close();	
				
		return ps;
	    }
	 
	 
	 private static boolean checkProfileFolder(File folder){
		 if ((new File(folder.getAbsolutePath() + "/profile.ini").exists()) && 
				 (new File(folder.getAbsolutePath() + "/Logs").exists()) && 
				 (new File(folder.getAbsolutePath() + "/Userpics").exists()))
			 return true;
		 else return false;
	 }
	 
	 
	 public static void setNewPicture(Profile profile, File picture) throws IOException{
		 File destFile = new File (userHome + "/" + profile.getLogin() + "/Userpics/" + picture.getName());
		 FileUtils.copyFile(picture, destFile);
		 profile.setPic(destFile);
	 }

	 
	 public static String getUserHome() {
		return userHome;
	}

	public static void setUserHome(String userHome) {
		ProfilesManager.userHome = userHome;
	}
}
