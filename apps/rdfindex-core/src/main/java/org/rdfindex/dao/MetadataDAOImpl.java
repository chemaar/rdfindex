package org.rdfindex.dao;

import java.util.LinkedList;
import java.util.List;

import org.rdfindex.to.AggregatedTO;
import org.rdfindex.to.ComponentTO;
import org.rdfindex.to.DatasetStructureTO;
import org.rdfindex.to.IndexTO;
import org.rdfindex.to.IndicatorTO;
import org.rdfindex.utils.RDFIndexUtils;
import org.rdfindex.utils.RDFIndexVocabulary;
import org.rdfindex.utils.SPARQLFetcherUtils;
import org.rdfindex.utils.SPARQLQueriesHelper;
import org.rdfindex.utils.SPARQLUtils;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;

public class MetadataDAOImpl implements RDFIndexMetadataDAO {
	
	//FIXME: some information is repeated, aggregates is used in two different modes!
	
	private Model abox;
	private Model tbox;
	
	public MetadataDAOImpl(Model tbox, Model abox) {
		this.abox = abox;
		this.tbox = tbox;
	}


	public Model getAbox() {
		return abox;
	}


	public void setAbox(Model abox) {
		this.abox = abox;
	}


	public Model getTbox() {
		return tbox;
	}


	public void setTbox(Model tbox) {
		this.tbox = tbox;
	}


	public List<IndexTO> getIndexMetadata(){
		List<IndexTO> indexes = new LinkedList<IndexTO>();
		String indexQuery = SPARQLQueriesHelper.createIndexQuery();
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(abox, indexQuery);
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
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(abox, componentsQuery);
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
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(abox, indicatorsQuery);
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
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(abox, dsdQuery);
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
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(abox, aggregatedQuery);
		for (int i = 0; i < results.length; i++){
			if (i == 0){
				String operator = SPARQLFetcherUtils.fetchResourceValue(results[i], "operator");
				aggregated.setOperator(operator);
			}
			String type = SPARQLFetcherUtils.fetchResourceValue(results[i], "type");
			String ref = SPARQLFetcherUtils.fetchResourceValue(results[i], "ref");
			if (type.equals(RDFIndexVocabulary.PART_OF.getURI())){
				aggregated.getPartsOf().add(ref);//FIXME: to be removed it is part of the composition
			}
		}

		return aggregated;
	}
	
	

}
