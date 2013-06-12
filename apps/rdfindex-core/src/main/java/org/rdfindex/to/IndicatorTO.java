package org.rdfindex.to;

import java.util.List;

public class IndicatorTO {
	private String uri;
	private AggregationMetadataTO metadata;
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public AggregationMetadataTO getMetadata() {
		return metadata;
	}
	public void setMetadata(AggregationMetadataTO metadata) {
		this.metadata = metadata;
	}
	
}
