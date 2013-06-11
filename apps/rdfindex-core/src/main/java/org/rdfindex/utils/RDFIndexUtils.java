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
}
