package org.rdfindex.to;

public class ObservationTO {

	/**The URI of the target dataset.**/
	String uriDataset; 
	/**The URI of the measure that is being aggregated.**/
	String measure;
	/**The value of the measure that is being generated.**/
	String value; 
	/**The date, year, etc. for that value.**/
	String date; 
	/**The new values are measured in an agent: place, person, organization, product, service, etc.**/
	String agent; 
	/**The URI of the observation.**/
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
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public ObservationTO(String uriDataset, String measure, String value,
			String date, String agent, String uri) {
		super();
		this.uriDataset = uriDataset;
		this.measure = measure;
		this.value = value;
		this.date = date;
		this.agent = agent;
		this.uri = uri;
	}
	public ObservationTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "ObservationTO [uriDataset=" + uriDataset + ", measure="
				+ measure + ", value=" + value + ", date=" + date + ", agent="
				+ agent + ", uri=" + uri + "]";
	}
	
	
	
	
}
