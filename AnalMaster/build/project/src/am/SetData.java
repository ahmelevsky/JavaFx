package am;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SetData {
	
	private StringProperty name = new SimpleStringProperty();
	private StringProperty startUpload = new SimpleStringProperty();
	private StringProperty lastUpload = new SimpleStringProperty();
	private IntegerProperty imagesCount = new SimpleIntegerProperty();
	private IntegerProperty soldImagesCount = new SimpleIntegerProperty();
	private DoubleProperty soldPercent = new SimpleDoubleProperty();
	private DoubleProperty sum = new SimpleDoubleProperty();
	
	
	
	
	public SetData (String name) {
		this.name.set(name);
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
	
	
	
	
	
	
}
