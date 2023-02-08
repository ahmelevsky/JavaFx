package am;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

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
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ShutterImage {

	private LongProperty media_id = new SimpleLongProperty();
	private IntegerProperty downloads = new SimpleIntegerProperty();
	private DoubleProperty earnings = new SimpleDoubleProperty();
	private StringProperty image_url = new SimpleStringProperty();
	
	
	public Map<String, Double> keywordsRate = new LinkedHashMap<String,Double>();
	
	private LongProperty upload_id = new SimpleLongProperty();
	private StringProperty id = new SimpleStringProperty();
	
	private StringProperty original_filename = new SimpleStringProperty();
	private String original_filename_backup;
	//private StringProperty uploaded_filename = new SimpleStringProperty();
	private StringProperty uploaded_date = new SimpleStringProperty();
	public LocalDate uploadDate;
	
	private StringProperty type = new SimpleStringProperty();
	public String extension;
	private BooleanProperty is_illustration = new SimpleBooleanProperty(false);
	public ObservableList<String> keywords = FXCollections.observableArrayList();
	private StringProperty description = new SimpleStringProperty();

	private StringProperty previewPath = new SimpleStringProperty();
	private byte[] previewBytes = null;
	//private IntegerProperty keywordsCount = new SimpleIntegerProperty();
	
	
	private ImageView image;
    
    
	public ShutterImage(long media_id) {
		this.media_id.set(media_id);
		this.image_url.set("https://www.shutterstock.com/pic-" + media_id);
	}
    
	
	public long getMedia_id() {
		return media_id.get();
	}

	public void setMedia_id(long media_id) {
		this.media_id.set(media_id);
		this.image_url.set("https://www.shutterstock.com/pic-" + media_id);
	}
	
	
	public int getDownloads() {
		return downloads.get();
	}

	public void setDownloads(int total_downloads) {
		this.downloads.set(total_downloads);
	}
	
	public double getEarnings() {
		return earnings.get();
	}

	public void setEarnings(double total_earnings) {
		this.earnings.set(total_earnings);
	}
	
	
	public String getImage_url() {
		return image_url.get();
	}

	public void setImage_url(String image_url) {
		this.image_url.set(image_url);
	}
	
	

	public long getUpload_id() {
		return upload_id.get();
	}

	public void setUpload_id(long upload_id) {
		this.upload_id.set(upload_id);
	}
	
	/*
	public String getUploaded_filename() {
		return uploaded_filename.get();
	}

	public void setUploaded_filename(String uploaded_filename) {
		this.uploaded_filename.set(uploaded_filename);
		try {
			this.extension = uploaded_filename.substring(uploaded_filename.lastIndexOf(".") + 1);
		} catch (IndexOutOfBoundsException e) {
			this.extension = "";
		}
		
		this.original_filename_backup = uploaded_filename;
	}
	
	*/
	
	public boolean isVector() {
		return original_filename.get().endsWith("eps");
		//return uploaded_filename.get().endsWith("eps");
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
	
	
	public String getUploaded_date() {
		return this.uploaded_date.get();
	}

	public void setUploaded_date(String date) {
		this.uploaded_date.set(date);
//		DateTimeFormatter formatterTimeOutput = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	//	DateTimeFormatter formatterTimeInput = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	//	this.uploadDate = LocalDateTime.parse(date, formatterTimeInput);
	//	this.uploaded_date.set(this.uploadDate.format(formatterTimeOutput));
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		this.uploadDate = LocalDate.parse(date, formatter);
	}
	
	
	public String getId() {
		return this.id.get();
	}

	public void setId(String id) {
		this.id.set(id);
	}


	
	public String getOriginal_filename() {
		return original_filename.get();
	}

	public void setOriginal_filename(String original_filename) {
		this.original_filename.set(original_filename);
		this.original_filename_backup = original_filename;
	 if (original_filename.endsWith("eps"))
			 setType("Vector");
	 else 
		 setType("Raster");
	}

	public boolean getIs_illustration() {
		return is_illustration.get();
	}

	public void setIs_illustration(boolean is_illustration) {
		this.is_illustration.set(is_illustration);
	}
	

	public String getType() {
		return type.get();
	}

	public void setType(String type) {
		this.type.set(type);
	}
	
	public String getPreviewPath() {
		return previewPath.get();
	}

	public void setPreviewPath(String previewPath) {
		this.previewPath.set(previewPath);
		this.setImage(new ImageView(new Image(previewPath, true)));
	}
	
	public byte[] getPreviewBytes() {
		return previewBytes;
	}
	public void setPreviewBytes(byte[] previewBytes) {
		this.previewBytes = previewBytes;
	}
	
	 public void setImage() {
		    if (this.getPreviewBytes() == null) return;
	        image = new ImageView(new Image(new ByteArrayInputStream(this.getPreviewBytes())));
	        image.setPreserveRatio(true);
	    }

	public void correctName() {
		int pos = this.original_filename_backup.indexOf('_');
		if (pos > -1) {
			String prefix = this.original_filename_backup.split("_")[0];
			if (prefix.length()==8 && !hasLowerCase(prefix))
			  this.original_filename.set(this.original_filename_backup.substring(pos + 1));
		}
	}
	
	private boolean hasLowerCase(String s) {
		 char[] ch = s.toCharArray();
		for (char c:ch) {
			if (Character.isLowerCase(c))
				return true;
		}
		return false;
	}
	
	
	public void restoreName() {
		this.original_filename.set(this.original_filename_backup);
	}
	
	

	@Override
	public String toString() {
		return "ShutterImage [id=" + id + ", filename=" + original_filename.get() + "]";
	}
	
	
}
