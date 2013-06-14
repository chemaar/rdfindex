package org.rdfindex.to;

import java.util.HashSet;
import java.util.Set;

public class AggregatedTO {

	private String aggregator;
	private String operator;
	private String operatorNotation;
	private Set<PartTO> partsOf;
	
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	public Set<String> getPartsOfAsDatasetURIs() {
		//FIXME: Caché?
		Set <String> partsDatasets = new HashSet<String>();
		for(PartTO part:this.partsOf){
			partsDatasets.add(part.getDataset());
		}
		return partsDatasets;
	}
	public Set<PartTO> getPartsOf() {
		if(this.partsOf==null){
			this.partsOf = new HashSet<PartTO>();
		}
		return partsOf;
	}
	public void setPartsOf(Set<PartTO> partsOf) {
		this.partsOf = partsOf;
	}
	public String getOperatorNotation() {
		return operatorNotation;
	}
	public void setOperatorNotation(String operatorNotation) {
		this.operatorNotation = operatorNotation;
	}
	public String getAggregator() {
		return aggregator;
	}
	public void setAggregator(String aggregator) {
		this.aggregator = aggregator;
	}
	@Override
	public String toString() {
		return "AggregatedTO [aggregator=" + aggregator + ", operator="
				+ operator + ", operatorNotation=" + operatorNotation
				+ ", partsOf=" + partsOf + "]";
	}


	
	
}
