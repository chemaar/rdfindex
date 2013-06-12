package org.rdfindex.utils;

import java.util.List;

import org.rdfindex.to.ObservationTO;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class SPARQLQueriesHelper {

	
	public static Model observationsAsRDF(List<ObservationTO> newObservations) {
		Model model = ModelFactory.createDefaultModel();
		for(ObservationTO observation:newObservations){
			model.add(RDFIndexUtils.observationAsRDF(observation));
		}
		return model;
	}
	
	
	public static String createQueryAggregatesFromElement(String uri) {
		String createQueryAggregatesFromElement = SPARQLUtils.NS+" "+ 
			"SELECT ?element ?type ?ref ?operator WHERE{ "+
				"?element rdfindex:aggregates ?aggregated. " +
				SPARQLFetcherUtils.createFilterResource(uri, RDFIndexUtils.ELEMENT_VAR_SPARQL)+
				"?aggregated rdfindex:part-of ?part.  "+
				"?aggregated rdfindex:aggregation-operator ?operator.  "+
				"?aggregated ?type ?ref.  "+
				//"FILTER(?type=qb:dimension || ?type=qb:measure || ?type=rdfindex:part-of)" +
				"FILTER(?type=rdfindex:part-of)" +
				//This is information is already available in the Dataset Structure
			"}";
		return createQueryAggregatesFromElement;
	}

	public static String createQueryDASFromElement(String uri) {
		String dsdElementQuery = SPARQLUtils.NS+" "+ 
			"SELECT ?element ?label ?type ?ref WHERE{ "+
				"?element qb:structure ?dsd. " +
				SPARQLFetcherUtils.createFilterResource(uri, RDFIndexUtils.ELEMENT_VAR_SPARQL)+
				"?element rdfs:label ?label. " +
				"FILTER (lang(?label)=\""+SPARQLQueriesHelper.DEFAULT_LANG+"\"). "+
				"?dsd qb:component ?qbcomponent. " +
				"?qbcomponent ?type ?ref. " +
				"FILTER(?type=qb:dimension || ?type=qb:measure )" +
			"}";
		return dsdElementQuery;
	}

	public static String createIndicatorsFromComponent(String componentURI) {
		String componentFromIndexQuery = SPARQLUtils.NS+" "+ 
			"SELECT ?indicator ?label ?type ?ref WHERE{ "+
				"?component  rdf:type rdfindex:Component.  "+
				SPARQLFetcherUtils.createFilterResource(componentURI, RDFIndexUtils.COMPONENT_VAR_SPARQL)+
				"?component  rdfindex:aggregates ?indicators.  "+
				"?indicators rdfindex:part-of ?indicator.  "+
				"?indicator  rdf:type rdfindex:Indicator.  "+
			"}";
		return componentFromIndexQuery;
	}

	public static String createComponentsFromIndex(String indexURI) {
		String componentFromIndexQuery = SPARQLUtils.NS+" "+ 
			"SELECT ?component WHERE{ "+
				"?index rdf:type rdfindex:Index.  "+
				SPARQLFetcherUtils.createFilterResource(indexURI, RDFIndexUtils.INDEX_VAR_SPARQL)+
				"?index rdfindex:aggregates ?components.  "+
				"?components rdfindex:part-of ?component.  "+
				"?component  rdf:type rdfindex:Component.  "+
			"}";
		return componentFromIndexQuery;
	}

	public static String createIndexQuery() {		
		String indexQuery = SPARQLUtils.NS+" "+
			"SELECT ?index WHERE{ "+
				"?index rdf:type rdfindex:Index. "+
			"} ";
		return indexQuery;
	}

	/**
	 * SPARQL queries to be extracted to a Helper Class
	 * @return
	 */
	
	public static String createSPARQLExtractDSD(){
		return SPARQLUtils.NS+" "+
		"SELECT ?dataset ?label ?type ?ref WHERE{ " +
			"?dataset qb:structure ?dsd. " +
			"?dataset rdfs:label ?label. " +
			"FILTER (lang(?label)=\""+SPARQLQueriesHelper.DEFAULT_LANG+"\"). "+
			"?dsd qb:component ?component. " +
			"?component ?type ?ref. " +
			"FILTER(?type=qb:dimension || ?type=qb:measure )" +
		"}";
	}

	public static String DEFAULT_LANG="en";

}
