package org.rdfindex.to;

import java.util.List;

public class IndexTO {
	private String uri;

	private List<ComponentTO> components;
	private DatasetStructureTO metadata;
	private AggregatedTO aggregated;
	
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public List<ComponentTO> getComponents() {
		return components;
	}
	public void setComponents(List<ComponentTO> components) {
		this.components = components;
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
		return "IndexTO [uri=" + uri + ", components=" + components
				+ ", metadata=" + metadata + ", aggregated=" + aggregated + "]";
	}

	
	
}
