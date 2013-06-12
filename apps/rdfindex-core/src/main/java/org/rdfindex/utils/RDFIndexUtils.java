package org.rdfindex.utils;

import org.rdfindex.to.ObservationTO;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public class RDFIndexUtils {

	public static Model observationAsRDF(ObservationTO observation){
		Model model = ModelFactory.createDefaultModel();
		Resource observationResource = ResourceFactory.createResource(observation.getUri());
		model.add(observationResource, RDF.type, RDFIndexVocabulary.OBSERVATION_TYPE );
		model.add(observationResource,RDFIndexVocabulary.QB_DATASET, ResourceFactory.createResource(observation.getUriDataset()));
		model.add(observationResource,RDFIndexVocabulary.REF_DATE,observation.getDate());
		model.add(observationResource,RDFIndexVocabulary.REF_AGENT, ResourceFactory.createResource(observation.getAgent()));
		model.add(observationResource,ResourceFactory.createProperty(observation.getMeasure()),observation.getValue());
		return model;
	}

	public static final String INDEX_VAR_SPARQL = "index";
	private static final String VALUE_VAR_SPARQL = "value";
	public static final String COMPONENT_VAR_SPARQL = "component";
	public static final String ELEMENT_VAR_SPARQL = "element";
	public static final String INDICATOR_VAR_SPARQL = "indicator";
	public static final String OBSERVATION_VAR_SPARQL = "observation";
	public static final String MEASURE_VAR_SPARQL = "measure";
	public static final String DIMENSION_VAR_SPARQL = "dimension";
	public static final String PART_VAR_SPARQL = "part";
	public static final String OPERATOR_VAR_SPARQL = "operator";
	public static final String NEW_VALUE_VAR_SPARQL = "newvalue";
	public static final String DATE_VALUE_VAR_SPARQL = "date";
	public static final String AGENT_VALUE_VAR_SPARQL = "agent";
}
