package am;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ShutterImage {

	private LongProperty media_id = new SimpleLongProperty();
	private LongProperty upload_id = new SimpleLongProperty();
	private StringProperty id = new SimpleStringProperty();
	private StringProperty uploaded_filename = new SimpleStringProperty();
	private String original_filename_backup;
	private StringProperty created = new SimpleStringProperty();
	private StringProperty date = new SimpleStringProperty();
	private StringProperty description = new SimpleStringProperty();
	private BooleanProperty is_illustration = new SimpleBooleanProperty(false);
	private StringProperty previewPath = new SimpleStringProperty();
	private IntegerProperty keywordsCount = new SimpleIntegerProperty();
	private IntegerProperty downloads = new SimpleIntegerProperty();
	private DoubleProperty earnings = new SimpleDoubleProperty();
	public ObservableList<String> keywords = FXCollections.observableArrayList();
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
	
	 public void setImage(ImageView value) {
	        image = value;
	        image.setPreserveRatio(true);
	    }

	    public ImageView getImage() {
	        return image;
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

	
	public String getUploaded_filename() {
		return uploaded_filename.get();
	}

	public void setUploaded_filename(String uploaded_filename) {
		this.uploaded_filename.set(uploaded_filename);
	}

	public boolean getIs_illustration() {
		return is_illustration.get();
	}

	public void setIs_illustration(boolean is_illustration) {
		this.is_illustration.set(is_illustration);
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

	@Override
	public String toString() {
		return "ShutterImage [id=" + id + ", filename=" + uploaded_filename + "]";
	}
	
	
}
