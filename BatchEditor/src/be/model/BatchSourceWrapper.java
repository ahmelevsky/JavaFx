package be.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "sources")
public class BatchSourceWrapper {

	
    private List<BatchSource> sources;
    public GlobalSettings gs;
    
	@XmlElement(name = "source")
    public List<BatchSource> getBatchSources() {
        return sources;
    }

    public void setBatchSources(List<BatchSource> sources) {
        this.sources = sources;
    }
    
    
}
