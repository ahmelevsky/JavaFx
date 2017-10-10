package application;

import javafx.scene.image.Image;

public class Contact {

	private int contactId;
	public boolean isOnline;
	private String name;
	private Image pic;
	private String status;
	public ContactData cd = new ContactData();
	
	public Contact(int id){
		this.contactId = id;
	}
	
	public int getContactId() {
		return contactId;
	}
	public void setContactId(int contactId) {
		this.contactId = contactId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Image getPic() {
		return pic;
	}
	public void setPic(Image pic) {
		this.pic = pic;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Contact [contactId=" + contactId + ", name=" + name + "]";
	}
	
}
