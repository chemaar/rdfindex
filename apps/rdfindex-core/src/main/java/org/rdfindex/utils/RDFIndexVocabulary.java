package org.rdfindex.utils;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class RDFIndexVocabulary {

	public static final Resource OBSERVATION_TYPE = ResourceFactory.createProperty(PrefixManager.getURIPrefix("qb"), "dataset" );
	public static final Property QB_DATASET = ResourceFactory.createProperty(PrefixManager.getURIPrefix("qb"), "dataset" );
	public static final Property REF_DATE = ResourceFactory.createProperty(PrefixManager.getURIPrefix("rdfindex"), "ref-year" );
	public static final Property REF_AGENT = ResourceFactory.createProperty(PrefixManager.getURIPrefix("rdfindex"), "ref-area" );;
	
	


}
