package am.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javafx.util.StringConverter;

public class NumberDateToStringConverter<T> extends StringConverter<T> {

	@Override
	public String toString(T object) {
		if (object instanceof Long) {
			Date date = new Date((Long)object);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
			return dateFormat.format(date);  
		}
		else
		{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T fromString(String string) {
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(string);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return (T)(Long)date.getTime();
		
	}

}
