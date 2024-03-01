package am.ui;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;


public class ReadObjectsHelper {

	// Read a ListProperty from ObjectInputStream (and return it)
	public static ListProperty readListProp(ObjectInputStream s) throws IOException, ClassNotFoundException {
	    ListProperty lst= new SimpleListProperty(FXCollections.observableArrayList());
	    int loop=s.readInt();
	    for(int i = 0;i<loop;i++) {
	        lst.add(s.readObject());
	    }

	    return lst;
	}

	// automatic fill a set of properties with values contained in ObjectInputStream
	public static void readAllProp(ObjectInputStream s, Property... properties) throws IOException, ClassNotFoundException {
	    for(Property prop:properties) {
	        if(prop instanceof IntegerProperty) ((IntegerProperty)prop).setValue(s.readInt());
	        else if(prop instanceof LongProperty) ((LongProperty)prop).setValue(s.readLong());
	        else if(prop instanceof DoubleProperty) ((DoubleProperty)prop).setValue(s.readDouble());
	        else if(prop instanceof StringProperty) ((StringProperty)prop).setValue(s.readUTF());
	        else if(prop instanceof BooleanProperty) ((BooleanProperty)prop).setValue(s.readBoolean());
	        else if(prop instanceof ListProperty) ((ListProperty)prop).setValue(readListProp(s));
	        else if(prop instanceof ObjectProperty) ((ObjectProperty)prop).setValue(s.readObject());
	        else throw new RuntimeException("Unsupported object type : " + prop==null?null:prop.toString());
	    }
	}
}
