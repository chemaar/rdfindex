package org.rdfindex.processor;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.rdfindex.dao.MetadataDAOImpl;
import org.rdfindex.dao.RDFIndexMetadataDAO;
import org.rdfindex.to.AggregatedTO;
import org.rdfindex.to.ComponentTO;
import org.rdfindex.to.DatasetStructureTO;
import org.rdfindex.to.IndexTO;
import org.rdfindex.to.IndicatorTO;
import org.rdfindex.to.ObservationTO;
import org.rdfindex.utils.PrettyPrinter;
import org.rdfindex.utils.RDFIndexUtils;
import org.rdfindex.utils.RDFIndexVocabulary;
import org.rdfindex.utils.SPARQLFetcherUtils;
import org.rdfindex.utils.SPARQLQueriesHelper;
import org.rdfindex.utils.SPARQLUtils;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class JavaSPARQLProcessorImpl  implements Processor{

	protected Logger logger = Logger.getLogger(JavaSPARQLProcessorImpl.class);
	protected RDFIndexMetadataDAO metadata;
	
	public JavaSPARQLProcessorImpl(){
		
	}
	
	@Override
	public List<ObservationTO> run(RDFIndexMetadataDAO metadata) {

		List<ObservationTO> observations = new LinkedList<ObservationTO>();
		
		this.metadata = metadata;
		List<IndexTO> indexes = this.metadata.getIndexMetadata();
		for(IndexTO index:indexes){
			List<ObservationTO> indexObservations = processIndex(index);
			observations.addAll(indexObservations);
		}
	
		return observations;
	}
	//FIXME: Refactor? an interface ObservableTO could be extracted and ComponentTO, IndexTO, etc. could implement it, just one method! so far I am goint to keep as it is
	//This kind be perfectly implemented with map/reduce, each index, component and indicator in any node.
	private List<ObservationTO> processIndex(IndexTO index) {
		logger.debug("Processing index "+index.getUri());
		List<ObservationTO> observationsFromComponents = new LinkedList<ObservationTO>();
		List<ComponentTO> components = index.getComponents();
		for(ComponentTO component:components){
			List<ObservationTO> componentObservations = processComponent(component);
			observationsFromComponents.addAll(componentObservations);
		}
		//Since we have generated a new set of observations for the components of this index we can aggregate
		//PrettyPrinter.prettyPrint(SPARQLQueriesHelper.observationsAsRDF(observationsFromComponents));
		logger.debug("...the index "+index.getUri()+" is going to compute "+observationsFromComponents.size()+" observations from components.");
		List<ObservationTO> observations = execute(observationsFromComponents, index.getMetadata(), index.getAggregated());
		logger.debug("...the index "+index.getUri()+" has generated "+observations.size()+" new observations.");
		return observations;
	}
	
	
	
	private List<ObservationTO> processComponent(ComponentTO component) {
		logger.debug("Processing component "+component.getUri());
		List<ObservationTO> observationsFromIndicators = new LinkedList<ObservationTO>();
		List<IndicatorTO> indicators = component.getIndicators();
		for(IndicatorTO indicator:indicators){
			List<ObservationTO> indicatorObservations = processIndicator(indicator);
			observationsFromIndicators.addAll(indicatorObservations);
		}
		//Since we have generated a new set of observations for the indicators of this component we can aggregate
		//PrettyPrinter.prettyPrint(SPARQLQueriesHelper.observationsAsRDF(observationsFromIndicators));
		logger.debug("...the component "+component.getUri()+" is going to compute "+observationsFromIndicators.size()+" observations from indicators.");
		List<ObservationTO> observations = execute(observationsFromIndicators, component.getMetadata(), component.getAggregated());
		logger.debug("...the component "+component.getUri()+" has generated "+observations.size()+" new observations.");
		return observations;
	}
	
	
	
	private List<ObservationTO> processIndicator(IndicatorTO indicator) {
		logger.debug("Processing indicator "+indicator.getUri());
		List<ObservationTO> observations = new LinkedList<ObservationTO>();
		observations.addAll(execute(this.metadata.getAbox(), indicator.getMetadata(), indicator.getAggregated()));
		logger.debug("...the indicator "+indicator.getUri()+" has generated "+observations.size()+" new observations.");
		return observations;
	}
	
	protected static List<ObservationTO> execute(List<ObservationTO> observations, DatasetStructureTO metadata, AggregatedTO aggregated){
		Model model = ModelFactory.createDefaultModel();
		model.add(SPARQLQueriesHelper.observationsAsRDF(observations));
		return execute(model, metadata, aggregated);
	}
	
	protected static List<ObservationTO> execute(Model model, DatasetStructureTO metadata, AggregatedTO aggregated){
		String sparqlQuery = SPARQLUtils.NS+" "+createSPARQLQuery(metadata,aggregated);		
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(model, sparqlQuery);
		return fetchNewObservations(metadata,results);
	}
	
	protected static List<ObservationTO> fetchNewObservations(DatasetStructureTO metadata, QuerySolution[] results) {
		List<ObservationTO> newObservations = new LinkedList<ObservationTO>();
		//A new observation will be created with the next metadata
		for(int i = 0; i<results.length;i++){
			ObservationTO observation = new ObservationTO();
			observation.setUri(createObservationUniqueID());
			observation.setUriDataset(metadata.getElement());
			observation.setMeasure(metadata.getMeasure());
			observation.setValue(SPARQLFetcherUtils.fetchStringValue(results[i], RDFIndexUtils.NEW_VALUE_VAR_SPARQL));//depends on metadata
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
	
	
	
	protected static String createSPARQLQuery(DatasetStructureTO metadata, AggregatedTO aggregated) {
		Set<String> dimensions = metadata.getDimensions();
		Set<String> partsOf = aggregated.getPartsOf();
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
					SPARQLFetcherUtils.formatVar(RDFIndexUtils.OBSERVATION_VAR_SPARQL)+" "+
					SPARQLFetcherUtils.formatResource(dim)+" "+
					" "+dimVar+". "
			);
		}
		if(operator == null && partsOf.size()==0){
			//There is no aggregation so we keep as ?part the whole element
			partsOf.add(metadata.getElement());
		}
		sparqlQuery = "SELECT "+dimensionVars+" "+formatFormula(operator)+" "+
				"WHERE{ " +
					SPARQLFetcherUtils.formatVar(RDFIndexUtils.OBSERVATION_VAR_SPARQL)+" "+				
						SPARQLFetcherUtils.formatResource(RDFIndexVocabulary.QB_DATASET.getURI())+" "+
								SPARQLFetcherUtils.formatVar(RDFIndexUtils.PART_VAR_SPARQL)+" . "+
					SPARQLFetcherUtils.createFilterPartsOf(partsOf)+
					SPARQLFetcherUtils.formatVar(RDFIndexUtils.OBSERVATION_VAR_SPARQL)+" "+
							SPARQLFetcherUtils.formatResource(measure)+" "+
								SPARQLFetcherUtils.formatVar(RDFIndexUtils.MEASURE_VAR_SPARQL)+" . "+
					createDimensionsBGPs+		
				"} GROUP BY"+dimensionVars;
		
		return sparqlQuery;
	}



	//FIXME: extract mapping, what happen with the operation aggregator
	protected static String formatFormula(String operator) {
		if (operator == null){
			return "( min("+ SPARQLFetcherUtils.formatVar(RDFIndexUtils.MEASURE_VAR_SPARQL)+") as "+SPARQLFetcherUtils.formatVar(RDFIndexUtils.NEW_VALUE_VAR_SPARQL)+")";	
		}else if (operator.equalsIgnoreCase("http://purl.org/rdfindex/ontology/Mean")){
			String function = "avg"+"("+SPARQLFetcherUtils.formatVar(RDFIndexUtils.MEASURE_VAR_SPARQL)+")";
			return "("+ function+" as "+SPARQLFetcherUtils.formatVar(RDFIndexUtils.NEW_VALUE_VAR_SPARQL)+")";
		}else{
			return "( min("+ SPARQLFetcherUtils.formatVar(RDFIndexUtils.MEASURE_VAR_SPARQL)+") as "+SPARQLFetcherUtils.formatVar(RDFIndexUtils.NEW_VALUE_VAR_SPARQL)+")";
		}

		
	}
	
	//FIXME: in a distributed environment this does not ensure an unique id, a common repo should be used
	protected static String createObservationUniqueID() {
		return RDFIndexVocabulary.RDFINDEX_COMPUTATION_RESOURCE_OBS_BASE+System.nanoTime(); 
	}
	
	
}
