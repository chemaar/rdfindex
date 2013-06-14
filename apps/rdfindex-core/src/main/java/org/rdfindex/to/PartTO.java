package org.rdfindex.to;

public class PartTO {
	String dataset;
	String weight;
	String normalization;
	public String getDataset() {
		return dataset;
	}
	public void setDataset(String dataset) {
		this.dataset = dataset;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getNormalization() {
		return normalization;
	}
	public void setNormalization(String normalization) {
		this.normalization = normalization;
	}
	@Override
	public String toString() {
		return "PartTO [dataset=" + dataset + ", weight=" + weight
				+ ", normalization=" + normalization + "]";
	}
	public PartTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
