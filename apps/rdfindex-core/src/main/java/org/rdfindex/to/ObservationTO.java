package org.rdfindex.to;

import java.util.HashMap;
import java.util.Map;

public class ObservationTO {

	/**The URI of the target dataset.**/
	String uriDataset; 
	/**The URI of the measure that is being aggregated.**/
	String measure;
	/**The value of the measure that is being generated.**/
	String value;
	
	Map dimensions = new HashMap<String, String>();
	
	String uri;
	public String getUriDataset() {
		return uriDataset;
	}
	public void setUriDataset(String uriDataset) {
		this.uriDataset = uriDataset;
	}
	public String getMeasure() {
		return measure;
	}
	public void setMeasure(String measure) {
		this.measure = measure;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public ObservationTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public Map getDimensions() {
		if (this.dimensions == null){
			this.dimensions = new HashMap<String, String>();
		}
		return dimensions;
	}
	public void setDimensions(Map dimensions) {
		this.dimensions = dimensions;
	}
	@Override
	public String toString() {
		return "ObservationTO [uriDataset=" + uriDataset + ", measure="
				+ measure + ", value=" + value + ", dimensions=" + dimensions
				+ ", uri=" + uri + "]";
	}

	
	
}
