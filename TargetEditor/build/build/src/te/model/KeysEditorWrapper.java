package te.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class KeysEditorWrapper {

	public String keysField = "";
	public boolean isTarget;
	public boolean isFolderVariable;
	
	
	public KeysEditorWrapper() {
	}



	public KeysEditorWrapper(String keysField, boolean isTarget, boolean isFolderVariable){
		this.keysField = keysField;
		this.isTarget = isTarget;
		this.isFolderVariable = isFolderVariable;
	}
	
	
}
