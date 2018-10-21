package te.util;

import javafx.scene.control.Tab;

public class DataException extends Exception {
    public Tab errorTab;
    
	public DataException(Tab tab) {
		this.errorTab = tab;
	}

	
	
}
