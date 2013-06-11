package org.rdfindex.processor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.rdfindex.to.AggregationMetadataTO;
import org.rdfindex.to.ObservationTO;
import org.rdfindex.utils.PrettyPrinter;
import org.rdfindex.utils.RDFIndexUtils;
import org.rdfindex.utils.RDFIndexVocabulary;
import org.rdfindex.utils.SPARQLFetcherUtils;
import org.rdfindex.utils.SPARQLUtils;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class SPARQLProcessor implements Processor {

	public static final String INDEX_VAR_SPARQL = "index";
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
	private static final String VALUE_VAR_SPARQL = "value";
	
	protected Logger logger = Logger.getLogger(SPARQLProcessor.class);
	@Override
	public List<ObservationTO> run(Model tbox, Model abox) {
		List<ObservationTO> observations = new LinkedList<ObservationTO>();
		String indexQuery = createIndexQuery(abox);
		//Ask the index to be generated
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(abox, indexQuery);
		System.out.println("Found  "+results.length+" indexes.");
		for (int i = 0; i < results.length; i++){
			String index = SPARQLFetcherUtils.fetchResourceValue(results[i], INDEX_VAR_SPARQL);
			System.out.println("Processing index "+index);
			observations.addAll(processComponentsOf(index, tbox, abox));
		}
		//i_aggregated value
	
		return observations;
	}

	private List<ObservationTO> processComponentsOf(String index, Model tbox, Model abox) {
		List<ObservationTO> observations = new LinkedList<ObservationTO>();
		//Extract the set of components C
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(abox, createComponentsFromIndexQuery(index, abox));
		//For each ci in C
		for (int i = 0; i < results.length; i++){
			String component = SPARQLFetcherUtils.fetchResourceValue(results[i], COMPONENT_VAR_SPARQL);
			System.out.println("Processing component "+component);			
			List<ObservationTO> newObservations = processIndicatorsOf(component, tbox, abox);
			Model componentModel = observationsAsRDF(newObservations);
			AggregationMetadataTO metadata = getMetadataTO(component, abox);
			
			String sparqlQuery = createSPARQLQuery(metadata);
			System.out.println(sparqlQuery);
			observations.addAll(
					fetchNewObservations(metadata,
							SPARQLUtils.executeSimpleSparql(componentModel, sparqlQuery)));//IMPORTANT: the model
			PrettyPrinter.prettyPrint(observationsAsRDF(observations));
			//observations.addAll(newObservations);
		}
		//  ci_aggregated_value = 0
		//	extract the set of indicators I
		//		For each indi in I
		//			ci_aggregated_value += indi
		//i_aggregated value += ci_aggregated_value
		return observations;	
	}

	private Model observationsAsRDF(List<ObservationTO> newObservations) {
		Model model = ModelFactory.createDefaultModel();
		for(ObservationTO observation:newObservations){
			model.add(RDFIndexUtils.observationAsRDF(observation));
		}
		return model;
	}

	private List<ObservationTO> processIndicatorsOf(String component, Model tbox, Model abox) {
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(abox, 
				createIndicatorsFromComponentQuery(component, abox));
		List<ObservationTO> observations = new LinkedList<ObservationTO>();
		for (int i = 0; i < results.length; i++){
			String indicator = SPARQLFetcherUtils.fetchResourceValue(results[i], 
					INDICATOR_VAR_SPARQL);
			System.out.println("Processing indicator "+indicator);
			AggregationMetadataTO metadata = getMetadataTO(indicator, abox);
			String sparqlQuery = createSPARQLQuery(metadata);
			observations.addAll(fetchNewObservations(metadata,SPARQLUtils.executeSimpleSparql(abox, sparqlQuery)));
		}
		//Convert observations in RDF and build a model to be query for the ancestor
		//The communication between layers is a MODEL JENA not JAVA Objects
		return observations;
	}

	protected static List<ObservationTO> fetchNewObservations(AggregationMetadataTO metadata, QuerySolution[] results) {
		List<ObservationTO> newObservations = new LinkedList<ObservationTO>();
		//A new observation will be created with the next metadata
		for(int i = 0; i<results.length;i++){
			ObservationTO observation = new ObservationTO();
			observation.setUri(createObservationUniqueID());
			observation.setUriDataset(metadata.getElement());
			observation.setMeasure(metadata.getMeasure());
			observation.setValue(SPARQLFetcherUtils.fetchStringValue(results[i], NEW_VALUE_VAR_SPARQL));
			observation.setDate(SPARQLFetcherUtils.fetchStringValue(results[i],  DATE_VALUE_VAR_SPARQL));
			observation.setAgent(SPARQLFetcherUtils.fetchResourceValue(results[i], AGENT_VALUE_VAR_SPARQL));
			newObservations.add(observation);
		}
		return newObservations;
	}

	protected static String createSPARQLQuery(AggregationMetadataTO metadata) {
		//FIXME: distinguish between slice or others to create the first match pattern
		String sparqlQuery = SPARQLUtils.NS+" "+
			"SELECT " +SPARQLFetcherUtils.formatVar(DATE_VALUE_VAR_SPARQL)+ " "+SPARQLFetcherUtils.formatVar(AGENT_VALUE_VAR_SPARQL)+" "+formatFormula(metadata.getOperator())+" "+ 
			"WHERE{ " +
				SPARQLFetcherUtils.formatVar(PART_VAR_SPARQL)+" "+
					SPARQLFetcherUtils.formatResource(RDFIndexVocabulary.QB_OBSERVATION.getURI())+" "+SPARQLFetcherUtils.formatVar(OBSERVATION_VAR_SPARQL)+" . "+
				createFilterParts(metadata.getPartsOf())+
				SPARQLFetcherUtils.formatVar(OBSERVATION_VAR_SPARQL)+" "+
					SPARQLFetcherUtils.formatResource(metadata.getMeasure())+" "+SPARQLFetcherUtils.formatVar(VALUE_VAR_SPARQL)+" . "+
				createDimensionsBGPs(metadata.getDimensions())+				
			"} "+ createGroupByDimensions(metadata.getDimensions());
		return sparqlQuery;
	}

	

	private static String createDimensionsBGPs(Set<String> dimensions) {
		return SPARQLFetcherUtils.formatVar(OBSERVATION_VAR_SPARQL)+" "+
				SPARQLFetcherUtils.formatResource(RDFIndexVocabulary.REF_DATE.getURI())+" "+SPARQLFetcherUtils.formatVar(DATE_VALUE_VAR_SPARQL)+" . "+
			
				SPARQLFetcherUtils.formatVar(OBSERVATION_VAR_SPARQL)+" "+
					SPARQLFetcherUtils.formatResource(RDFIndexVocabulary.REF_AGENT.getURI())+" "+SPARQLFetcherUtils.formatVar(AGENT_VALUE_VAR_SPARQL)+" . ";
	}

	protected static String createObservationUniqueID() {
		return "http://purl.org/rdfindex/computation/o"+System.nanoTime(); //FIXME: in a distributed environment this does not ensure an unique id, a common repo should be used
	}

	
	protected static String createGroupByDimensions(Set<String> partsOf) {
		Set s = new HashSet<String>(); //FIXME: extract from ontology
		s.add(DATE_VALUE_VAR_SPARQL);
		s.add(AGENT_VALUE_VAR_SPARQL);
		return SPARQLFetcherUtils.createGroupByResource(s);
	}

	protected static String formatFormula(String operator) {
		//FIXME: extract mapping
		String function = "avg"+"("+SPARQLFetcherUtils.formatVar(VALUE_VAR_SPARQL)+")";
		return "("+ function+" as "+SPARQLFetcherUtils.formatVar(NEW_VALUE_VAR_SPARQL)+")";
	}

	protected static String createFilterParts(Set<String> partsOf) {
		return SPARQLFetcherUtils.createFilterPartsOf(partsOf);
	}

	protected static String createIndexQuery(Model abox) {		
		String indexQuery = SPARQLUtils.NS+" "+
			"SELECT ?index WHERE{ "+
				"?index rdf:type rdfindex:Index. "+
			"} ";
		return indexQuery;
	}

	protected static String createComponentsFromIndexQuery(String indexURI, Model abox) {
		String componentFromIndexQuery = SPARQLUtils.NS+" "+ 
			"SELECT ?component WHERE{ "+
				"?index rdf:type rdfindex:Index.  "+
				SPARQLFetcherUtils.createFilterResource(indexURI, INDEX_VAR_SPARQL)+
				"?index rdfindex:aggregates ?components.  "+
				"?components rdfindex:part-of ?component.  "+
			"}";
		return componentFromIndexQuery;
	}
	

	protected static String createIndicatorsFromComponentQuery(String componentURI, Model abox) {
		String componentFromIndexQuery = SPARQLUtils.NS+" "+ 
			"SELECT ?indicator WHERE{ "+
				"?component  rdf:type rdfindex:Component.  "+
				SPARQLFetcherUtils.createFilterResource(componentURI, COMPONENT_VAR_SPARQL)+
				"?component  rdfindex:aggregates ?indicators.  "+
				"?indicators rdfindex:part-of ?indicator.  "+
			"}";
		return componentFromIndexQuery;
	}
	

	protected static AggregationMetadataTO getMetadataTO(String uri, Model abox) {
		AggregationMetadataTO aggregation = new AggregationMetadataTO();
			String description = SPARQLUtils.NS+					
			"SELECT * WHERE{ "+
				"?element rdf:type ?type  "+ //FIXME: how to select the type, it should be the same for index, component and indicator!
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
				aggregation.setElement(SPARQLFetcherUtils.fetchResourceValue(results[i], ELEMENT_VAR_SPARQL));
				aggregation.setOperator(SPARQLFetcherUtils.fetchResourceValue(results[i], OPERATOR_VAR_SPARQL));
				aggregation.setMeasure( SPARQLFetcherUtils.fetchResourceValue(results[i], MEASURE_VAR_SPARQL));
			}
			aggregation.getPartsOf().add(SPARQLFetcherUtils.fetchResourceValue(results[i], PART_VAR_SPARQL));
			aggregation.getDimensions().add(SPARQLFetcherUtils.fetchResourceValue(results[i], DIMENSION_VAR_SPARQL));		
		}
		return aggregation;
	}
}
