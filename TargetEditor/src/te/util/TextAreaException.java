package te.util;

import javafx.scene.control.TextArea;

public class TextAreaException extends TextException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TextArea textArea;
	 
	
	public TextAreaException(TextArea ta, String string) {
		super(string);
		textArea = ta;
		// TODO Auto-generated constructor stub
	}

}
