package rf;

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

public class ShutterImage {

	private IntegerProperty media_id = new SimpleIntegerProperty();
	private BooleanProperty selected = new SimpleBooleanProperty(false);
	//private boolean isSelected;
	private StringProperty media_type = new SimpleStringProperty();
	private List<String> reasonsList;
	private StringProperty reasonsString = new SimpleStringProperty();
	private StringProperty uploaded_date = new SimpleStringProperty();
	private StringProperty original_filename = new SimpleStringProperty();
	private String original_filename_backup;
	private StringProperty verdict_time = new SimpleStringProperty();
	public LocalDate uploadedDate;
	public LocalDateTime verdictTime;
	
	public ShutterImage(int media_id, String media_type, List<String> reasons, String uploaded_date,
			String original_filename, String verdict_time) {
		this.media_id.set(media_id);
		this.media_type.set(media_type);
		this.reasonsList = reasons;
		this.reasonsString.set(String.join(", ", this.reasonsList));
		this.uploaded_date.set(uploaded_date);
		this.original_filename.set(original_filename);
		this.original_filename_backup = original_filename;
		this.verdict_time.set(verdict_time);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		uploadedDate = LocalDate.parse(uploaded_date, formatter);
		Instant instant = Instant.parse(verdict_time);
		verdictTime = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));	
	}

	public BooleanProperty getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected.set(selected);
	}
	
	
	public int getMedia_id() {
		return media_id.get();
	}

	public void setMedia_id(int media_id) {
		this.media_id.set(media_id);
	}

	public String getMedia_type() {
		return media_type.get();
	}

	public void setMedia_type(String media_type) {
		this.media_type.set(media_type);
	}

	public String getReasonsString() {
		return reasonsString.get();
	}

	public void setReasonsString(String reasonsString) {
		this.reasonsString.set(reasonsString);
	}

	public String getUploaded_date() {
		return uploaded_date.get();
	}

	public void setUploaded_date(String uploaded_date) {
		this.uploaded_date.set(uploaded_date);
	}

	public String getOriginal_filename() {
		return original_filename.get();
	}

	public void setOriginal_filename(String original_filename) {
		this.original_filename.set(original_filename);
	}

	public String getVerdict_time() {
		return verdict_time.get();
	}

	public void setVerdict_time(StringProperty verdict_time) {
		this.verdict_time = verdict_time;
	}

	public void correctName() {
		int pos= this.original_filename_backup.indexOf('_');
		if (pos>-1)
			this.original_filename.set(this.original_filename_backup.substring(pos+1));
	}
	
	public void restoreName() {
		this.original_filename.set(this.original_filename_backup);
	}
}
