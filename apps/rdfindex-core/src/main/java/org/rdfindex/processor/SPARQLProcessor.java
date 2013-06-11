package org.rdfindex.processor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.rdfindex.to.AggregationMetadataTO;
import org.rdfindex.to.ObservationTO;
import org.rdfindex.utils.SPARQLFetcherUtils;
import org.rdfindex.utils.SPARQLUtils;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class SPARQLProcessor implements Processor {

	private static final String DIMENSION_VAR_SPARQL = "dimension";
	private static final String PART_VAR_SPARQL = "part";
	private static final String MEASURE_VAR_SPARQL = "measure";
	private static final String OPERATOR_VAR_SPARQL = "operator";
	private static final String INDEX_VAR_SPARQL = "index";
	private static final String COMPONENT_VAR_SPARQL = "component";
	private static final String ELEMENT_VAR_SPARQL = "element";
	private static final String NEW_VALUE_VAR_SPARQL = "newvalue";
	private static final String DATE_VALUE_VAR_SPARQL = "date";
	private static final String AGENT_VALUE_VAR_SPARQL = "date";
	
	protected Logger logger = Logger.getLogger(SPARQLProcessor.class);
	@Override
	public Model run(Model tbox, Model abox) {
		String indexQuery = createIndexQuery(abox);
		//Ask the index to be generated
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(abox, indexQuery);
		for (int i = 0; i < results.length; i++){
			String index = SPARQLFetcherUtils.resourceValue(results[i], INDEX_VAR_SPARQL);
			logger.info("Processing index "+index);
			processComponentsOf(index, tbox, abox);
		}
		//i_aggregated value
	
		return ModelFactory.createDefaultModel();
	}

	private List<ObservationTO> processComponentsOf(String index, Model tbox, Model abox) {
		List<ObservationTO> observations = new LinkedList<ObservationTO>();
		//Extract the set of components C
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(abox, createComponentsFromIndexQuery(index, abox));
		//For each ci in C
		for (int i = 0; i < results.length; i++){
			String component = SPARQLFetcherUtils.resourceValue(results[i], COMPONENT_VAR_SPARQL);
			logger.info("Processing component "+component);
			processIndicatorsOf(component, tbox, abox);
		}
		//  ci_aggregated_value = 0
		//	extract the set of indicators I
		//		For each indi in I
		//			ci_aggregated_value += indi
		//i_aggregated value += ci_aggregated_value
		return observations;	
	}

	private List<ObservationTO> processIndicatorsOf(String component, Model tbox, Model abox) {
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(abox, createComponentsFromIndexQuery(component, abox));
		List<ObservationTO> observations = new LinkedList<ObservationTO>();
		for (int i = 0; i < results.length; i++){
			String indicator = SPARQLFetcherUtils.resourceValue(results[i], COMPONENT_VAR_SPARQL);
			logger.info("Processing indicator "+indicator);
			AggregationMetadataTO metadata = getMetadataTO(indicator, abox);
			String sparqlQuery = createSPARQLQuery(metadata);
			observations.addAll(fetchNewObservations(metadata,SPARQLUtils.executeSimpleSparql(abox, sparqlQuery)));
		}
		//Convert observations in RDF and build a model to be query for the ancestor
		//The communication between layers is a MODEL JENA not JAVA Objects
		return observations;
	}

	private List<ObservationTO> fetchNewObservations(AggregationMetadataTO metadata, QuerySolution[] results) {
		List<ObservationTO> newObservations = new LinkedList<ObservationTO>();
		//A new observation will be created with the next metadata
		for(int i = 0; i<results.length;i++){
			ObservationTO observation = new ObservationTO();
			observation.setUri(createObservationUniqueID());
			observation.setUriDataset(metadata.getElement());
			observation.setMeasure(metadata.getMeasure());
			observation.setValue(SPARQLFetcherUtils.fetchStringValue(results[i], NEW_VALUE_VAR_SPARQL));
			observation.setDate(SPARQLFetcherUtils.fetchStringValue(results[i], DATE_VALUE_VAR_SPARQL));
			newObservations.add(observation);
		}
		return newObservations;
	}

	private String createObservationUniqueID() {
		return "o"+System.nanoTime(); //FIXME: in a distributed environment this does not ensure an unique id, a common repo should be used
	}

	private String createSPARQLQuery(AggregationMetadataTO metadata) {
		// TODO Auto-generated method stub
		return null;
	}

	protected String createIndexQuery(Model abox) {		
		String indexQuery = SPARQLUtils.NS+" "+
			"SELECT ?index WHERE{ "+
				"?index rdf:type rdfindex:Index. "+
			"} ";
		return indexQuery;
	}

	protected String createComponentsFromIndexQuery(String indexURI, Model abox) {
		String componentFromIndexQuery = SPARQLUtils.NS+" "+ 
			"SELECT ?component WHERE{ "+
				"?index rdf:type rdfindex:Index.  "+
				SPARQLFetcherUtils.createFilterResource(indexURI, INDEX_VAR_SPARQL)+
				"?index rdfindex:aggregates ?components.  "+
				"?components rdfindex:part-of ?component.  "+
			"}";
		return componentFromIndexQuery;
	}
	

	protected String createIndicatorsFromComponentQuery(String componentURI, Model abox) {
		String componentFromIndexQuery = SPARQLUtils.NS+" "+ 
			"SELECT ?indicator WHERE{ "+
				"?component rdf:type rdfindex:Component.  "+
				SPARQLFetcherUtils.createFilterResource(componentURI, COMPONENT_VAR_SPARQL)+
				"?component rdfindex:aggregates ?indicators.  "+
				"?indicators rdfindex:part-of ?indicator.  "+
			"}";
		return componentFromIndexQuery;
	}
	

	protected AggregationMetadataTO getMetadataTO(String uri, Model abox) {
		AggregationMetadataTO aggregation = new AggregationMetadataTO();
			String description = SPARQLUtils.NS+					
			"SELECT * WHERE{ "+
				"?element rdf:type rdfindex:Indicator.  "+
				SPARQLFetcherUtils.createFilterResource(uri, ELEMENT_VAR_SPARQL)+
				"?element rdfindex:aggregates ?aggregation. "+
				"?aggregation rdfindex:aggregation-operator ?operator. "+
				"?aggregation rdfindex:part-of ?part. "+
				"?aggregation qb:measure ?measure. "+
				"?aggregation rdfindex:group-by ?dimension "+
			"} ";
		//Validation rules and assumptions:
		//Check domain and ranges before 
		//1 and only one element can be aggregated			
		//1 and only one measure can be aggregated. 
		//1 and only one operator can be used. 
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(abox, description);
		for (int i = 0; i < results.length; i++){
			if (i == 0){
				aggregation.setElement(SPARQLFetcherUtils.resourceValue(results[i], ELEMENT_VAR_SPARQL));
				aggregation.setOperator(SPARQLFetcherUtils.resourceValue(results[i], OPERATOR_VAR_SPARQL));
				aggregation.setMeasure( SPARQLFetcherUtils.resourceValue(results[i], MEASURE_VAR_SPARQL));
			}
			aggregation.getPartsOf().add(SPARQLFetcherUtils.resourceValue(results[i], PART_VAR_SPARQL));
			aggregation.getDimensions().add(SPARQLFetcherUtils.resourceValue(results[i], DIMENSION_VAR_SPARQL));		
		}
		return aggregation;
	}
}
