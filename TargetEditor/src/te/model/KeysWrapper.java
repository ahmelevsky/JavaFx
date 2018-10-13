package te.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import te.model.Variable;

@XmlRootElement(name = "keys")
public class KeysWrapper {

	
	private List<Variable> keyvariables;
	private List<Variable> descriptionvariables;

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
}
