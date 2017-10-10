package be.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.xml.bind.annotation.XmlTransient;

public class BatchSource {

    private final StringProperty caption;
    private final StringProperty path;
    private final IntegerProperty filesCount;
    private final BooleanProperty isJpgOnly;
    @XmlTransient
    public final StringProperty status;    
    
	public BatchSource() {
		this(null, null, 0, false);
	}

	public BatchSource(String caption, String path, int filesCount, boolean isJpgOnly) {
		this.caption = new SimpleStringProperty(caption);
		this.path = new SimpleStringProperty(path);
		this.filesCount = new SimpleIntegerProperty(filesCount);
		this.isJpgOnly = new SimpleBooleanProperty(isJpgOnly);
		this.status = new SimpleStringProperty("underfined");
	}

	public final StringProperty captionProperty() {
		return this.caption;
	}

	public final java.lang.String getCaption() {
		return this.captionProperty().get();
	}

	public final void setCaption(final java.lang.String caption) {
		this.captionProperty().set(caption);
	}

	public final StringProperty pathProperty() {
		return this.path;
	}

	public final java.lang.String getPath() {
		return this.pathProperty().get();
	}

	public final void setPath(final java.lang.String path) {
		this.pathProperty().set(path);
	}

	public final IntegerProperty filesCountProperty() {
		return this.filesCount;
	}

	public final int getFilesCount() {
		return this.filesCountProperty().get();
	}

	public final void setFilesCount(final int filesCount) {
		this.filesCountProperty().set(filesCount);
	}

	@Override
	public String toString() {
		return "BatchSource [caption=" + caption + ", path=" + path
				+ ", filesCount=" + filesCount + "]";
	}

	public final BooleanProperty isJpgOnlyProperty() {
		return this.isJpgOnly;
	}

	public final boolean isIsJpgOnly() {
		return this.isJpgOnlyProperty().get();
	}

	public final void setIsJpgOnly(final boolean isJpgOnly) {
		this.isJpgOnlyProperty().set(isJpgOnly);
	}
	public final StringProperty statusProperty() {
		return this.status;
	}
	
	public final java.lang.String getStatus() {
		return this.statusProperty().get();
	}
	
	public final void setStatus(final java.lang.String status) {
		this.statusProperty().set(status);
	}
	
    
}
