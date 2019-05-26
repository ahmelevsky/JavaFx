package te.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "keys")
public class ImportWrapper {

	private List<Variable> keyvariables = new ArrayList<Variable>();
	private List<Variable> descriptionvariables = new ArrayList<Variable>();
	private List<Target> targets = new ArrayList<Target>();
	private KeysEditorWrapper keysPage;
	private DescriptionEditorWrapper descriptionPage;
	private TitleEditorWrapper titlePage;
	private FolderVariablesWrapper folderWrapper;
	
    @XmlElement(name = "keyvariable")
    public List<Variable> getKeyVariables() {
        return keyvariables;
    }
    public void setKeyVariables(List<Variable> variables) {
        this.keyvariables = variables;
    }
	
    
    @XmlElement(name = "descriptionvariable")
    public List<Variable> getDescriptionVariables() {
        return descriptionvariables;
    }
    public void setDescriptionVariables(List<Variable> variables) {
        this.descriptionvariables = variables;
    }
	
    @XmlElement(name = "target")
    public List<Target> getTarget() {
        return targets;
    }
    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }
    
    @XmlElement(name = "description")
	public DescriptionEditorWrapper getDescriptionPage() {
		return descriptionPage;
	}
	public void setDescriptionPage(DescriptionEditorWrapper descriptionPage) {
		this.descriptionPage = descriptionPage;
	}
	  @XmlElement(name = "keys")
	public KeysEditorWrapper getKeysPage() {
		return keysPage;
	}
	public void setKeysPage(KeysEditorWrapper keysPage) {
		this.keysPage = keysPage;
	}
	@XmlElement(name = "title")
	public TitleEditorWrapper getTitlePage() {
		return titlePage;
	}
	public void setTitlePage(TitleEditorWrapper titlePage) {
		this.titlePage = titlePage;
	}
	@XmlElement(name = "folder")
	public FolderVariablesWrapper getFolderWrapper() {
		return folderWrapper;
	}
	public void setFolderWrapper(FolderVariablesWrapper folderWrapper) {
		this.folderWrapper = folderWrapper;
	}
    
}
