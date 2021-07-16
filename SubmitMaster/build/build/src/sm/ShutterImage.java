package sm;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ShutterImage {

	private BooleanProperty selected = new SimpleBooleanProperty(false);
	private StringProperty status = new SimpleStringProperty();
	private StringProperty id = new SimpleStringProperty();
	private StringProperty contributor_id = new SimpleStringProperty();
	private StringProperty uploaded_filename = new SimpleStringProperty();
	private String original_filename_backup;
	private StringProperty created = new SimpleStringProperty();
	private StringProperty date = new SimpleStringProperty();
	private StringProperty description = new SimpleStringProperty();
	private BooleanProperty has_property_release = new SimpleBooleanProperty(false);
	private BooleanProperty is_illustration = new SimpleBooleanProperty(false);
	private BooleanProperty is_adult = new SimpleBooleanProperty(false);
	private BooleanProperty is_editorial = new SimpleBooleanProperty(false);
	private StringProperty previewPath = new SimpleStringProperty();
	private IntegerProperty keywordsCount = new SimpleIntegerProperty();
	public ObservableList<String> keywords = FXCollections.observableArrayList();
	public ObservableList<String> categories = FXCollections.observableArrayList();
	public ObservableList<String> categoriesNames = FXCollections.observableArrayList();
	public List<String> releases = new ArrayList<String>(); 
	public List<String> releasesNames = new ArrayList<String>(); 
	private ImageView image;
    public String extension;
    public LocalDateTime uploadDate;
	
	public ShutterImage(String id,	String uploaded_filename) {
		this.id.set(id);
		this.uploaded_filename.set(uploaded_filename);
		this.original_filename_backup = uploaded_filename;
		try {
			this.extension = uploaded_filename.substring(uploaded_filename.lastIndexOf(".") + 1);
		} catch (IndexOutOfBoundsException e) {
			this.extension = "";
		}
		/*
		keywords.addListener((ListChangeListener<String>) change -> {
			keywordsCount.set(keywords.size());
			System.out.println(getKeywordsCount());
        });
        */
	}

	
	public boolean isVector() {
		return uploaded_filename.get().endsWith("eps");
	}
	
	public BooleanProperty getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected.set(selected);
	}
	
	 public void setImage(ImageView value) {
	        image = value;
	        image.setPreserveRatio(true);
	    }

	    public ImageView getImage() {
	        return image;
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
		DateTimeFormatter formatterTimeOutput = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		DateTimeFormatter formatterTimeInput = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
		this.uploadDate = LocalDateTime.parse(created, formatterTimeInput);
		this.date.set(this.uploadDate.format(formatterTimeOutput));
	}
	
	public String getDate() {
		return this.date.get();
	}

	public void setDate(String date) {
		this.date.set(date);
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
		this.setImage(new ImageView(new Image(previewPath, true)));
	}
	
	public int getKeywordsCount() {
		return keywords.size();
		//return keywordsCount.get();
	}

	
	public void correctName() {
		int pos = this.original_filename_backup.indexOf('_');
		if (pos > -1)
			this.uploaded_filename.set(this.original_filename_backup.substring(pos + 1));
	}
	
	public void restoreName() {
		this.uploaded_filename.set(this.original_filename_backup);
	}

	public String getStatus() {
		return this.status.get();
	}

	public void setStatus(String status) {
		this.status.set(status);
	}

	@Override
	public String toString() {
		return "ShutterImage [id=" + id + ", filename=" + uploaded_filename + "]";
	}
	
	
}
