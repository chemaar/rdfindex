package org.rdfindex.to;

import java.util.HashSet;
import java.util.Set;

public class AggregationMetadataTO {

	private String element;
	private String operator;
	private String measure;
	private Set<String> partsOf;	
	private Set<String> dimensions;
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getMeasure() {
		return measure;
	}
	public void setMeasure(String measure) {
		this.measure = measure;
	}
	public Set<String> getPartsOf() {
		if (this.partsOf == null){
			this.partsOf = new HashSet<String>();
		}
		return partsOf;
	}
	public void setPartsOf(Set<String> partsOf) {
		this.partsOf = partsOf;
	}
	public Set<String> getDimensions() {
		if (this.dimensions == null){
			this.dimensions = new HashSet<String>();
		}
		return dimensions;
	}
	public void setDimensions(Set<String> dimensions) {
		this.dimensions = dimensions;
	}
	public String getElement() {
		return element;
	}
	public void setElement(String element) {
		this.element = element;
	}
	@Override
	public String toString() {
		return "AggregationMetadataTO [element=" + element + ", operator="
				+ operator + ", measure=" + measure + ", partsOf=" + partsOf
				+ ", dimensions=" + dimensions + "]";
	}

	
	
}
