package te.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class TitleEditorWrapper {

	public boolean isTakeFromDescription;
	public String comboValue;
	public String inputValue;
	
	
	public TitleEditorWrapper() {
	}

	public TitleEditorWrapper(boolean isTakeFromDescription, String comboValue,
			String inputValue) {
		this.isTakeFromDescription = isTakeFromDescription;
		this.comboValue = comboValue;
		this.inputValue = inputValue;
	}
	
	
	
	
}
