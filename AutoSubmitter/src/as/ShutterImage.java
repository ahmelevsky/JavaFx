package as;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ShutterImage {

	private StringProperty id = new SimpleStringProperty();
	private StringProperty contributor_id = new SimpleStringProperty();
	private StringProperty uploaded_filename = new SimpleStringProperty();
	private StringProperty created = new SimpleStringProperty();
	private StringProperty description = new SimpleStringProperty();
	private BooleanProperty has_property_release = new SimpleBooleanProperty(false);
	private BooleanProperty is_illustration = new SimpleBooleanProperty(false);
	private BooleanProperty is_adult = new SimpleBooleanProperty(false);
	private BooleanProperty is_editorial = new SimpleBooleanProperty(false);
	private StringProperty previewPath = new SimpleStringProperty();
	public List<String> keywords = new ArrayList<String>();
	public List<String> categories = new ArrayList<String>(); 
	public List<String> releases = new ArrayList<String>(); 
	//public LocalDate createdDate;
    public String extension;
	
	public ShutterImage(String id,	String uploaded_filename) {
		this.id.set(id);
		this.uploaded_filename.set(uploaded_filename);
		try {
			this.extension = uploaded_filename.substring(uploaded_filename.lastIndexOf(".") + 1);
		} catch (IndexOutOfBoundsException e) {
			this.extension = "";
		}
	}

	
	public void setCategories(String cat1, String cat2) {
		categories.clear();
		categories.add(cat1);
		categories.add(cat2);
	}
	
	public String getDescription() {
		return description.get();
	}

	public void setDescription(String description) {
		this.description.set(description);
	}
	
	public String getCreated() {
		return this.created.get();
	}

	public void setCreated(String created) {
		this.created.set(created);
	}

	public String getId() {
		return this.id.get();
	}

	public void setId(String id) {
		this.id.set(id);
	}

	public String getContributor_id() {
		return contributor_id.get();
	}

	public void setContributor_id(String contributor_id) {
		this.contributor_id.set(contributor_id);
	}

	
	public String getUploaded_filename() {
		return uploaded_filename.get();
	}

	public void setUploaded_filename(String uploaded_filename) {
		this.uploaded_filename.set(uploaded_filename);
	}


	public boolean getHas_property_release() {
		return has_property_release.get();
	}

	public void setHas_property_release(boolean has_property_release) {
		this.has_property_release.set(has_property_release);
	}
	

	public boolean getIs_illustration() {
		return is_illustration.get();
	}

	public void setIs_illustration(boolean is_illustration) {
		this.is_illustration.set(is_illustration);
	}
	
	

	public boolean getIs_adult() {
		return is_adult.get();
	}

	public void setIs_adult(boolean is_adult) {
		this.is_adult.set(is_adult);
	}
	
	public boolean getIs_editorial() {
		return is_editorial.get();
	}

	public void setIs_editorial(boolean is_editorial) {
		this.is_editorial.set(is_editorial);
	}
	
	
	public String getPreviewPath() {
		return previewPath.get();
	}

	public void setPreviewPath(String previewPath) {
		this.previewPath.set(previewPath);
	}
}
