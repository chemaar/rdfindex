package org.rdfindex.to;

import java.util.List;

public class ComponentTO {
	private String uri;
	private List<IndicatorTO> indicators;
	private DatasetStructureTO metadata;
	private AggregatedTO aggregated;
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
	public DatasetStructureTO getMetadata() {
		return metadata;
	}
	public void setMetadata(DatasetStructureTO metadata) {
		this.metadata = metadata;
	}
	public AggregatedTO getAggregated() {
		return aggregated;
	}
	public void setAggregated(AggregatedTO aggregated) {
		this.aggregated = aggregated;
	}
	@Override
	public String toString() {
		return "ComponentTO [uri=" + uri + ", indicators=" + indicators
				+ ", metadata=" + metadata + ", aggregated=" + aggregated + "]";
	}
	
	
}
