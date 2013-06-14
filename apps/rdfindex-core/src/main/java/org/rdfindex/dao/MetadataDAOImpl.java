package org.rdfindex.dao;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.rdfindex.to.AggregatedTO;
import org.rdfindex.to.ComponentTO;
import org.rdfindex.to.DatasetStructureTO;
import org.rdfindex.to.IndexTO;
import org.rdfindex.to.IndicatorTO;
import org.rdfindex.to.PartTO;
import org.rdfindex.utils.RDFIndexUtils;
import org.rdfindex.utils.RDFIndexVocabulary;
import org.rdfindex.utils.SPARQLFetcherUtils;
import org.rdfindex.utils.SPARQLQueriesHelper;
import org.rdfindex.utils.SPARQLUtils;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class MetadataDAOImpl implements RDFIndexMetadataDAO {
	
	//FIXME: some information is repeated, aggregates is used in two different modes!
	
	private Model indexMetadataModel;
	private Model rdfIndexModel;
	private Model observationsModel;
	private Model datasource;
	
	//FIXME: better just one datasource
	public MetadataDAOImpl(Model rdfIndexModel, Model indexMetadataModel, Model observations) {
		this.indexMetadataModel = indexMetadataModel;
		this.rdfIndexModel = rdfIndexModel;
		this.observationsModel = observations;
		this.datasource = ModelFactory.createDefaultModel();
		this.datasource.add(this.indexMetadataModel);
		this.datasource.add(this.rdfIndexModel);
		this.datasource.add(this.observationsModel);
	}


	public Model getIndexMetadataModel() {
		return indexMetadataModel;
	}


	public void setIndexMetadataModel(Model abox) {
		this.indexMetadataModel = abox;
	}


	public Model getRDFIndexModel() {
		return rdfIndexModel;
	}


	public void setRDFIndexModel(Model tbox) {
		this.rdfIndexModel = tbox;
	}

	
	
	
	public Model getObservationsModel() {
		return observationsModel;
	}


	public void setObservationsModel(Model observations) {
		this.observationsModel = observations;
	}


	public List<IndexTO> getIndexMetadata(){
		List<IndexTO> indexes = new LinkedList<IndexTO>();
		String indexQuery = SPARQLQueriesHelper.createIndexQuery();
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(datasource, indexQuery);
		for (int i = 0; i < results.length; i++){
			String indexUri = SPARQLFetcherUtils.fetchResourceValue(results[i], RDFIndexUtils.INDEX_VAR_SPARQL);
			IndexTO indexTO = new IndexTO();
			indexTO.setUri(indexUri);
			indexTO.setComponents(getComponentMetadata(indexUri));
			indexTO.setMetadata(getDatasetStructure(indexUri));
			indexTO.setAggregated(getAggregatedTO(indexUri));
			indexes.add(indexTO);			
		}
		return indexes;
	}
	
	
	public List<ComponentTO> getComponentMetadata(String indexUri){
		List<ComponentTO> components = new LinkedList<ComponentTO>();
		String componentsQuery = SPARQLQueriesHelper.createComponentsFromIndex(indexUri);
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(datasource, componentsQuery);
		for (int i = 0; i < results.length; i++){
			String componentUri = SPARQLFetcherUtils.fetchResourceValue(results[i], RDFIndexUtils.COMPONENT_VAR_SPARQL);
			ComponentTO componentTO = new ComponentTO();
			componentTO.setUri(componentUri);
			componentTO.setIndicators(getIndicatorMetadata(componentUri));
			componentTO.setMetadata(getDatasetStructure(componentUri));
			componentTO.setAggregated(getAggregatedTO(componentUri));
			components.add(componentTO);
		}
		return components;
	}
	
	
	
	public List<IndicatorTO> getIndicatorMetadata(String componentUri){
		List<IndicatorTO> indicators = new LinkedList<IndicatorTO>();
		String indicatorsQuery = SPARQLQueriesHelper.createIndicatorsFromComponent(componentUri);
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(datasource, indicatorsQuery);
		for (int i = 0; i < results.length; i++){
			String indicatorUri = SPARQLFetcherUtils.fetchResourceValue(results[i], RDFIndexUtils.INDICATOR_VAR_SPARQL);
			IndicatorTO indicatorTO = new IndicatorTO();
			indicatorTO.setUri(indicatorUri);
			indicatorTO.setMetadata(getDatasetStructure(indicatorUri));
			indicatorTO.setAggregated(getAggregatedTO(indicatorUri));
			indicators.add(indicatorTO);			
		}
		return indicators;
	}
	
	public DatasetStructureTO getDatasetStructure(String elementUri){
		DatasetStructureTO structure = new DatasetStructureTO();
		structure.setElement(elementUri);
		String dsdQuery = SPARQLQueriesHelper.createQueryDASFromElement(elementUri);
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(datasource, dsdQuery);
		for (int i = 0; i < results.length; i++){
			if (i == 0){
				String label = SPARQLFetcherUtils.fetchStringValue(results[i], "label");
				structure.setLabel(label);
			}
			String type = SPARQLFetcherUtils.fetchResourceValue(results[i], "type");
			String ref = SPARQLFetcherUtils.fetchResourceValue(results[i], "ref");
			if (type.equals(RDFIndexVocabulary.QB_DIMENSION.getURI())){
				structure.getDimensions().add(ref);
			}else if (type.equals(RDFIndexVocabulary.QB_MEASURE.getURI())){
				structure.setMeasure(ref);
			}
		}
		return structure;
	}
	

	public AggregatedTO getAggregatedTO(String elementUri){
		AggregatedTO aggregated = new AggregatedTO();
		String aggregatedQuery = SPARQLQueriesHelper.createQueryAggregatesFromElement(elementUri);
		Map<String, PartTO> partsOf = new HashMap<String, PartTO>();
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(datasource, aggregatedQuery);
		PartTO partTO;
		for (int i = 0; i < results.length; i++){
			if (i == 0){
				String operator = SPARQLFetcherUtils.fetchResourceValue(results[i], "operator");
				String notation = SPARQLFetcherUtils.fetchStringValue(results[i], "notation");
				aggregated.setOperator(operator);
				aggregated.setOperatorNotation(notation);
			}
			//WARNING: this is a blank node
			String part = results[i].get("part")!=null?results[i].get("part").toString():"";
			partTO = partsOf.get(part);
			if(partTO==null){
				partTO = new PartTO();
			}
			String weight = SPARQLFetcherUtils.fetchStringValue(results[i], "weight");
			//Take default weight
			if(!weight.equalsIgnoreCase("")){
				partTO.setWeight(weight);
			}
			String type = SPARQLFetcherUtils.fetchResourceValue(results[i], "type");
			String ref = SPARQLFetcherUtils.fetchStringOrResource(results[i], "ref");
			//FIMXE: use a table?
			if(type.equals(RDFIndexVocabulary.DATASET.getURI())){
				partTO.setDataset(ref);	
			}else if (type.equals(RDFIndexVocabulary.WEIGHT.getURI())){
				partTO.setWeight(ref);
			}else if (type.equals(RDFIndexVocabulary.NORMALIZES.getURI())){
				partTO.setNormalization(ref);
			}else{
				//SKIP
			}
			partsOf.put(part, partTO);
		}
		aggregated.getPartsOf().addAll(partsOf.values());
		System.out.println(aggregated);
		return aggregated;
	}
	
	

}
