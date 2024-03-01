package am;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Earning {

	private LongProperty media_id = new SimpleLongProperty();
	private StringProperty earn_date = new SimpleStringProperty();
	public LocalDate uploadDate;
	private StringProperty category = new SimpleStringProperty();
	private IntegerProperty count = new SimpleIntegerProperty();
	private DoubleProperty total = new SimpleDoubleProperty();
	
	public static final String[] CATEGORIES = new String[]{"25_a_day", "single_image_and_other", "enhanced",
															"on_demand", "cart_sales", "clip_packs", "footage_enhanced"};
	
	public Earning(long media_id, String earn_date, String category, int count, double total) {
		super();
		setMedia_id(media_id);
		setEarn_date(earn_date);
		setCategory(category);
		setCount(count);
		setTotal(total);
	}

	public long getMedia_id() {
		return media_id.get();
	}

	public void setMedia_id(long media_id) {
		this.media_id.set(media_id);
	}

	public String getEarn_date() {
		return earn_date.get();
	}

	public void setEarn_date(String earn_date) {
		this.earn_date.set(earn_date);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		this.uploadDate = LocalDate.parse(earn_date, formatter);
	}

	public String getCategory() {
		return category.get();
	}

	public void setCategory(String category) {
		this.category.set(category);
	}

	public int getCount() {
		return count.get();
	}

	public void setCount(int count) {
		this.count.set(count);
	}

	public double getTotal() {
		return total.get();
	}

	public void setTotal(double total) {
		this.total.set(total);
	}

	@Override
	public String toString() {
		return "Earning [media_id=" + media_id + ", earn_date=" + earn_date + ", category=" + category + ", count="
				+ count + ", total=" + total + "]";
	}
	
	
	
	
}
