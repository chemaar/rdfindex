package org.rdfindex.utils;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class RDFIndexVocabulary {

	public static final Resource OBSERVATION_TYPE = ResourceFactory.createProperty(PrefixManager.getURIPrefix("qb"), "observation" );
	public static final Property QB_DATASET = ResourceFactory.createProperty(PrefixManager.getURIPrefix("qb"), "dataSet" );
	public static final Property QB_DIMENSION = ResourceFactory.createProperty(PrefixManager.getURIPrefix("qb"), "dimension" );
	public static final Property QB_MEASURE = ResourceFactory.createProperty(PrefixManager.getURIPrefix("qb"), "measure" );
	public static final Property QB_OBSERVATION = ResourceFactory.createProperty(PrefixManager.getURIPrefix("qb"), "observation" );
	
	public static final Property REF_DATE = ResourceFactory.createProperty(PrefixManager.getURIPrefix("rdfindex"), "ref-year" );

	public static final Property REF_AGENT = ResourceFactory.createProperty(PrefixManager.getURIPrefix("rdfindex"), "ref-area" );
	
	public static final Property PART_OF = ResourceFactory.createProperty(PrefixManager.getURIPrefix("rdfindex"), "part-of" );
	public static final Property DATASET = ResourceFactory.createProperty(PrefixManager.getURIPrefix("rdfindex"), "dataset" );
	public static final Property WEIGHT = ResourceFactory.createProperty(PrefixManager.getURIPrefix("rdfindex"), "weight" );
	public static final Property NORMALIZES = ResourceFactory.createProperty(PrefixManager.getURIPrefix("rdfindex"), "normalizes" );
	public static final String RDFINDEX_COMPUTATION_RESOURCE_OBS_BASE = PrefixManager.getURIPrefix("rdfindex-res")+"o";
	
	public static final Resource OWA = ResourceFactory.createResource(PrefixManager.getURIPrefix("rdfindex")+"OWA" );
	
	


}
