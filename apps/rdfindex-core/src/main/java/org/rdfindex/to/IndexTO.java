package org.rdfindex.to;

import java.util.List;

public class IndexTO {
	private String uri;

	private List<ComponentTO> components;
	private AggregationMetadataTO metadata;
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
	public AggregationMetadataTO getMetadata() {
		return metadata;
	}
	public void setMetadata(AggregationMetadataTO metadata) {
		this.metadata = metadata;
	}
	@Override
	public String toString() {
		return "IndexTO [uri=" + uri + ", components=" + components
				+ ", metadata=" + metadata + "]";
	}
	
	
}
