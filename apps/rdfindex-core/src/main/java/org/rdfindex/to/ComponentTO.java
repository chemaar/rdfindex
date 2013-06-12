package org.rdfindex.to;

import java.util.List;

public class ComponentTO {
	private String uri;
	private List<IndicatorTO> indicators;
	private AggregationMetadataTO metadata;
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public List<IndicatorTO> getIndicators() {
		return indicators;
	}
	public void setIndicators(List<IndicatorTO> indicators) {
		this.indicators = indicators;
	}
	public AggregationMetadataTO getMetadata() {
		return metadata;
	}
	public void setMetadata(AggregationMetadataTO metadata) {
		this.metadata = metadata;
	}
	
}
