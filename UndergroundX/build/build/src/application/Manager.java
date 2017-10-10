package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import javafx.scene.image.Image;

public class Manager {

	
	public static Collection<Profile> loadAvailableProfiles(){
	return Profile.profilesMap.values();	
	}
	
	public static Collection<Contact> loadUserContacts(Profile p) throws IOException{
		//¬ будущем контакты подгружаютс€ с сервера, затем дл€ них обновл€ютс€ данные, хранимые на диске, затем эти данные подгружаютс€ в UI
		Collection<Contact> result = new HashSet<Contact>();
		File contactsFolder = new File (ProfilesManager.getUserHome() + "/" + p.getLogin() + "/Contacts/");
	/*	contactsFolder.listFiles(new FilenameFilter() { 
        public boolean accept(File directory, String fileName) {
            return fileName.endsWith(".contact");
        }
        };)
		 */
		
		File[] contacts = contactsFolder.listFiles((File file, String fileName) -> fileName.endsWith(".contact"));
		//File[] contacts = contactsFolder.listFiles();
		   for (File c:contacts) {
			     Properties props = new Properties();
			     InputStream fis;
				// OutputStream fos;
				 try {
						fis = new FileInputStream(c);
						try {
							props.load(fis);
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							fis.close();
						}
					} catch (FileNotFoundException e2) {
						// акое-нибудь сообщение в лог
						throw new FileNotFoundException();
					}
            Contact contact = new Contact(Integer.parseInt(c.getName().replace(".contact", "")));
            contact.setName(props.getProperty("name"));
           
            InputStream picStream = new FileInputStream(contactsFolder.getAbsolutePath() + "\\" + props.getProperty("pic"));
            try {
            contact.setPic(new Image(picStream));
            } 
            finally {
            	picStream.close();
            }
            contact.cd.gender = props.getProperty("gender");
            contact.cd.about = props.getProperty("about");
            result.add(contact);
		   }
		return result;
	}

	
}
