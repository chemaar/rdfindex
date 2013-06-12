package org.rdfindex.to;

import java.util.List;

public class IndicatorTO {
	private String uri;
	private DatasetStructureTO metadata;
	private AggregatedTO aggregated;
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public DatasetStructureTO getMetadata() {
		return metadata;
	}
	public void setMetadata(DatasetStructureTO metadata) {
		this.metadata = metadata;
	}
	//FIXME: these labels are from the structure not the indicator
	public String getLabel() {
		return this.metadata.getLabel();
	}
	public void setLabel(String label) {
		this.metadata.setLabel(label);
	}
	public AggregatedTO getAggregated() {
		return aggregated;
	}
	public void setAggregated(AggregatedTO aggregated) {
		this.aggregated = aggregated;
	}
	@Override
	public String toString() {
		return "IndicatorTO [uri=" + uri + ", metadata=" + metadata
				+ ", aggregated=" + aggregated + "]";
	}
	
	
}
