package org.rdfindex.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rdfindex.to.AggregatedTO;
import org.rdfindex.to.DatasetStructureTO;
import org.rdfindex.to.ObservationTO;

import com.hp.hpl.jena.query.QuerySolution;
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
		Map <String,String>dimensions = observation.getDimensions();
		for(String dimensionUri:dimensions.keySet()){
			model.add(observationResource,
					ResourceFactory.createProperty(dimensionUri),
					ResourceFactory.createResource(dimensions.get(dimensionUri))); 
					//FIXME:Ask to the abox which is the range of of the dimension to generate a resource or a literal
					//Now everyting is a resource
		}
		//Very important to indicate to the SPARQL processor that the value is a number!
		model.addLiteral(observationResource,ResourceFactory.createProperty(observation.getMeasure()),Double.valueOf(observation.getValue()));		
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
	//FIXME: extract mapping, what happen with the operation aggregator
	public static String formatFormula(AggregatedTO aggregated) {
		String formula = "( min("+ SPARQLFetcherUtils.formatVar(MEASURE_VAR_SPARQL)+") as "+SPARQLFetcherUtils.formatVar(NEW_VALUE_VAR_SPARQL)+")";
		if (aggregated.getOperator() != null){
			String operatorNotation = aggregated.getOperatorNotation();
			if (operatorNotation!= null && !operatorNotation.equalsIgnoreCase("")){
				String function = operatorNotation+"("+SPARQLFetcherUtils.formatVar(MEASURE_VAR_SPARQL)+")";
				formula = "("+ function+" as "+SPARQLFetcherUtils.formatVar(NEW_VALUE_VAR_SPARQL)+")";
			}
		}
		return formula;
//		if (operator == null){
//				
//		}else if (operator.equalsIgnoreCase("http://purl.org/rdfindex/ontology/Mean")){
//			String function = "avg"+"("+SPARQLFetcherUtils.formatVar(MEASURE_VAR_SPARQL)+")";
//			return "("+ function+" as "+SPARQLFetcherUtils.formatVar(NEW_VALUE_VAR_SPARQL)+")";
//		}else{
//			return "( min("+ SPARQLFetcherUtils.formatVar(MEASURE_VAR_SPARQL)+") as "+SPARQLFetcherUtils.formatVar(NEW_VALUE_VAR_SPARQL)+")";
//		}
	
		
	}
	//FIXME: in a distributed environment this does not ensure an unique id, a common repo should be used
	public static String createObservationUniqueID() {
		return RDFIndexVocabulary.RDFINDEX_COMPUTATION_RESOURCE_OBS_BASE+System.nanoTime(); 
	}
	public static String createSPARQLQuery(DatasetStructureTO metadata, AggregatedTO aggregated) {
		Set<String> dimensions = metadata.getDimensions();
		Set<String> partsOf = aggregated.getPartsOfAsDatasetURIs();
		String measure = metadata.getMeasure();
		String operator = aggregated.getOperator();
		StringBuffer createDimensionsBGPs = new StringBuffer();
		StringBuffer dimensionVars = new StringBuffer();
		String sparqlQuery = "";
		int i = 0; //Maybe a table?
		for(String dim:dimensions){
			String dimVar = "?dim"+(i++);
			dimensionVars.append(" "+dimVar);
			createDimensionsBGPs.append(
					SPARQLFetcherUtils.formatVar(OBSERVATION_VAR_SPARQL)+" "+
					SPARQLFetcherUtils.formatResource(dim)+" "+
					" "+dimVar+". "
			);
		}
		if(operator == null && partsOf.size()==0){
			//There is no aggregation so we keep as ?part the whole element
			partsOf.add(metadata.getElement());
		}
		sparqlQuery = "SELECT "+dimensionVars+" "+formatFormula(aggregated)+" "+
				"WHERE{ " +
					SPARQLFetcherUtils.formatVar(OBSERVATION_VAR_SPARQL)+" "+				
						SPARQLFetcherUtils.formatResource(RDFIndexVocabulary.QB_DATASET.getURI())+" "+
								SPARQLFetcherUtils.formatVar(PART_VAR_SPARQL)+" . "+
					SPARQLFetcherUtils.createFilterPartsOf(partsOf)+
					SPARQLFetcherUtils.formatVar(OBSERVATION_VAR_SPARQL)+" "+
							SPARQLFetcherUtils.formatResource(measure)+" "+
								SPARQLFetcherUtils.formatVar(MEASURE_VAR_SPARQL)+" . "+
					createDimensionsBGPs+		
				"} GROUP BY"+dimensionVars;
		
		return sparqlQuery;
	}
	public static List<ObservationTO> fetchNewObservations(DatasetStructureTO metadata, QuerySolution[] results) {
		List<ObservationTO> newObservations = new LinkedList<ObservationTO>();
		//A new observation will be created with the next metadata
		for(int i = 0; i<results.length;i++){
			ObservationTO observation = new ObservationTO();
			observation.setUri(createObservationUniqueID());
			observation.setUriDataset(metadata.getElement());
			observation.setMeasure(metadata.getMeasure());
			observation.setValue(SPARQLFetcherUtils.fetchStringValue(results[i], NEW_VALUE_VAR_SPARQL));//depends on metadata
			//Fetch dimensions
			Set<String> dimensions = metadata.getDimensions();
			int d = 0; //Maybe a table?
			for(String dimension:dimensions){
				String dimVar = "?dim"+(d++);
				String valueDimension = SPARQLFetcherUtils.fetchStringOrResource(results[i], dimVar);		
				//It is necessary to have the flag to know if it is a literal
				observation.getDimensions().put(dimension, valueDimension);
			}
			newObservations.add(observation);
		}
		return newObservations;
	}
	public static List<ObservationTO> execute(Model model, DatasetStructureTO metadata, AggregatedTO aggregated){
		String sparqlQuery = SPARQLUtils.NS+" "+createSPARQLQuery(metadata,aggregated);	
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(model, sparqlQuery);
		return fetchNewObservations(metadata,results);
	}
	public static List<ObservationTO> execute(List<ObservationTO> observations, DatasetStructureTO metadata, AggregatedTO aggregated){
		Model model = ModelFactory.createDefaultModel();
		model.add(SPARQLQueriesHelper.observationsAsRDF(observations));
		return execute(model, metadata, aggregated);
	}
}
