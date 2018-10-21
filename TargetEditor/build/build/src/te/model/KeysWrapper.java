package te.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import te.model.Variable;

@XmlRootElement(name = "keys")
public class KeysWrapper {

	
	private List<Variable> keyvariables = new ArrayList<Variable>();
	private List<Variable> descriptionvariables = new ArrayList<Variable>();
	private List<Target> targets = new ArrayList<Target>();
	

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
}
