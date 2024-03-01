package am;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;

import am.ui.ReadObjectsHelper;
import am.ui.WriteObjectsHelper;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SetData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient StringProperty name = new SimpleStringProperty();
	private transient StringProperty startUpload = new SimpleStringProperty();
	private transient StringProperty lastUpload = new SimpleStringProperty();
	private transient IntegerProperty imagesCount = new SimpleIntegerProperty();
	private transient IntegerProperty downloads = new SimpleIntegerProperty();
	private transient IntegerProperty soldImagesCount = new SimpleIntegerProperty();
	private transient DoubleProperty soldPercent = new SimpleDoubleProperty();
	private transient DoubleProperty sum = new SimpleDoubleProperty();
	private transient DoubleProperty rpi = new SimpleDoubleProperty();
	private transient DoubleProperty dpi = new SimpleDoubleProperty();
	private transient DoubleProperty rpd = new SimpleDoubleProperty();
	private transient DoubleProperty uploadMonths = new SimpleDoubleProperty();
	private transient IntegerProperty imagesMonths = new SimpleIntegerProperty();
	
	public SetData (String name) {
		this.name.set(name);
	}
	
	private void initInstance() {
		name = new SimpleStringProperty();
		startUpload = new SimpleStringProperty();
		lastUpload = new SimpleStringProperty();
		imagesCount = new SimpleIntegerProperty();
		downloads = new SimpleIntegerProperty();
		soldImagesCount = new SimpleIntegerProperty();
		soldPercent = new SimpleDoubleProperty();
		sum = new SimpleDoubleProperty();
		rpi = new SimpleDoubleProperty();
		dpi = new SimpleDoubleProperty();
		rpd = new SimpleDoubleProperty();
		uploadMonths = new SimpleDoubleProperty();
		imagesMonths = new SimpleIntegerProperty();
	}
	private void writeObject(ObjectOutputStream s) throws IOException {
	    WriteObjectsHelper.writeAllProp(s, name, startUpload, lastUpload, imagesCount, downloads, soldImagesCount, soldPercent, sum, rpi, dpi, rpd, uploadMonths, imagesMonths);
	}

	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
	    initInstance();
	    ReadObjectsHelper.readAllProp(s, name, startUpload, lastUpload, imagesCount, downloads, soldImagesCount, soldPercent, sum, rpi, dpi, rpd, uploadMonths, imagesMonths);
	}


	public String getName() {
		return name.get();
	}




	public void setName(String name) {
		this.name.set(name);
	}




	public String getStartUpload() {
		return startUpload.get();
	}




	public void setStartUpload(String startUpload) {
		this.startUpload.set(startUpload);
	}




	public String getLastUpload() {
		return lastUpload.get();
	}




	public void setLastUpload(String lastUpload) {
		this.lastUpload.set(lastUpload);
	}




	public int getImagesCount() {
		return imagesCount.get();
	}




	public void setImagesCount(int imagesCount) {
		this.imagesCount.set(imagesCount);
	}

	public int getDownloads() {
		return downloads.get();
	}




	public void setDownloads(int downloads) {
		this.downloads.set(downloads);
	}



	public int getSoldImagesCount() {
		return soldImagesCount.get();
	}




	public void setSoldImagesCount(int soldImagesCount) {
		this.soldImagesCount.set(soldImagesCount);
	}




	public double getSoldPercent() {
		return soldPercent.get();
	}




	public void setSoldPercent(double soldPercent) {
		this.soldPercent.set(soldPercent);
	}




	public double getSum() {
		return sum.get();
	}




	public void setSum(double sum) {
		this.sum.set(sum);
	}
	
	
	public double getRpi() {
		return rpi.get();
	}


	public void setRpi(double rpi) {
		this.rpi.set(rpi);
	}
	
	
	public double getDpi() {
		return dpi.get();
	}


	public void setDpi(double dpi) {
		this.dpi.set(dpi);
	}
	
	
	public double getRpd() {
		return rpd.get();
	}


	public void setRpd(double rpd) {
		this.rpd.set(rpd);
	}




	public double getUploadMonths() {
		return uploadMonths.get();
	}




	public void setUploadMonths(double uploadMonths) {
		this.uploadMonths.set(uploadMonths);
	}




	public int getImagesMonths() {
		return imagesMonths.get();
	}




	public void setImagesMonths(int imagesMonths) {
		this.imagesMonths.set(imagesMonths);
	}
	
	public static Comparator<SetData> getComparator() {
		return new SetDataComparator();
	}
	
	public static class SetDataComparator implements Comparator<SetData> {
		  @Override
		  public int compare(SetData data1, SetData data2) {
		      return data1.getName().toLowerCase()
		               .compareTo(data2.getName().toLowerCase());
		  }
		  //Override other methods...
		}
}
