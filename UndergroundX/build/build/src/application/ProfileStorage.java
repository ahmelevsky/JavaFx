package application;

import java.io.File;
import java.util.List;

public class ProfileStorage {

	 private Profile profile;
	 private File rootPath;
	 private File logsPath;
	 private File contactsPath;
	 private File userpicsPath;
	 public List<File> userpics;
	
	public ProfileStorage(String folder, Profile profile){
		setProfile(profile);
		setRootPath(new File (ProfilesManager.userHome + "/" + folder));
		setLogsPath(new File(rootPath.getAbsolutePath() + "/Logs"));
		setUserpicsPath(new File(rootPath.getAbsolutePath() + "/Userpics"));
		setContactsPath(new File(rootPath.getAbsolutePath() + "/Contacts"));
		if(!rootPath.exists())
			rootPath.mkdir();
		
		if(!logsPath.exists())
			logsPath.mkdir();
		
		if(!userpicsPath.exists())
			userpicsPath.mkdir();
		
		if(!contactsPath.exists())
			contactsPath.mkdir();
	}
	
	public ProfileStorage(File folder){
		setRootPath(folder);
		setLogsPath(new File(folder.getAbsolutePath() + "/Logs"));
		setUserpicsPath(new File(folder.getAbsolutePath() + "/Userpics"));
		setContactsPath(new File(rootPath.getAbsolutePath() + "/Contacts"));
		
		if(!rootPath.exists())
			rootPath.mkdir();
		
		if(!logsPath.exists())
			logsPath.mkdir();
		
		if(!userpicsPath.exists())
			userpicsPath.mkdir();
		
		if(!contactsPath.exists())
			contactsPath.mkdir();
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public File getRootPath() {
		return rootPath;
	}

	public void setRootPath(File path) {
		this.rootPath = path;
	}

	public File getLogsPath() {
		return logsPath;
	}

	public void setLogsPath(File logsPath) {
		this.logsPath = logsPath;
	}

	public File getUserpicsPath() {
		return userpicsPath;
	}

	public void setUserpicsPath(File userpicsPath) {
		this.userpicsPath = userpicsPath;
	}

	public File getContactsPath() {
		return contactsPath;
	}

	public void setContactsPath(File contactsPath) {
		this.contactsPath = contactsPath;
	}
	
}
