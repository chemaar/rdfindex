package org.rdfindex.to;

import java.util.HashSet;
import java.util.Set;

public class AggregatedTO {

	private String operator;
	private String operatorNotation;
	private Set<String> partsOf;	
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
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
	public String getOperatorNotation() {
		return operatorNotation;
	}
	public void setOperatorNotation(String operatorNotation) {
		this.operatorNotation = operatorNotation;
	}
	@Override
	public String toString() {
		return "AggregatedTO [operator=" + operator + ", operatorNotation="
				+ operatorNotation + ", partsOf=" + partsOf + "]";
	}


	
	
}
