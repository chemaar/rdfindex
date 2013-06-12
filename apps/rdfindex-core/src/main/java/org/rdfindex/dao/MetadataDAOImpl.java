package org.rdfindex.dao;

import java.util.LinkedList;
import java.util.List;

import org.rdfindex.to.ComponentTO;
import org.rdfindex.to.IndexTO;
import org.rdfindex.to.IndicatorTO;
import org.rdfindex.utils.RDFIndexUtils;
import org.rdfindex.utils.SPARQLFetcherUtils;
import org.rdfindex.utils.SPARQLUtils;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;

public class MetadataDAOImpl implements RDFIndexMetadataDAO {
	
	public static String DEFAULT_LANG="en";
	private Model abox;
	private Model tbox;
	
	public MetadataDAOImpl(Model tbox, Model abox) {
		this.abox = abox;
		this.tbox = tbox;
	}


	public List<IndexTO> getIndexMetadata(){
		List<IndexTO> indexes = new LinkedList<IndexTO>();
		String indexQuery = createIndexQuery();
		//Ask the index to be generated
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(abox, indexQuery);
		for (int i = 0; i < results.length; i++){
			String indexUri = SPARQLFetcherUtils.fetchResourceValue(results[i], RDFIndexUtils.INDEX_VAR_SPARQL);
			IndexTO indexTO = new IndexTO();
			indexTO.setUri(indexUri);
			indexTO.setComponents(getComponentMetadata(indexUri));
			indexes.add(indexTO);
		}
		return indexes;
	}
	
	
	public List<ComponentTO> getComponentMetadata(String indexUri){
		List<ComponentTO> components = new LinkedList<ComponentTO>();
		String componentsQuery = createComponentsFromIndex(indexUri);
		//Ask the index to be generated
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(abox, componentsQuery);
		for (int i = 0; i < results.length; i++){
			String componentUri = SPARQLFetcherUtils.fetchResourceValue(results[i], RDFIndexUtils.COMPONENT_VAR_SPARQL);
			ComponentTO componentTO = new ComponentTO();
			componentTO.setUri(componentUri);
			componentTO.setIndicators(getIndicatorMetadata(indexUri));
			components.add(componentTO);
		}
		return components;
	}
	
	
	
	public List<IndicatorTO> getIndicatorMetadata(String componentUri){
		List<IndicatorTO> indicators = new LinkedList<IndicatorTO>();
		String indicatorsQuery = createIndicatorsFromComponent(componentUri);
		System.out.println(indicatorsQuery);
		//Ask the index to be generated
		QuerySolution[] results = SPARQLUtils.executeSimpleSparql(abox, indicatorsQuery);
		for (int i = 0; i < results.length; i++){
			String indicatorUri = SPARQLFetcherUtils.fetchResourceValue(results[i], RDFIndexUtils.INDICATOR_VAR_SPARQL);
			IndicatorTO componentTO = new IndicatorTO();
			componentTO.setUri(indicatorUri);
		}
		return indicators;
	}
	
	
	public static String createSPARQLExtractDSD(){
		return SPARQLUtils.NS+" "+
		"SELECT ?dataset ?label ?type ?ref WHERE{ " +
			"?dataset qb:structure ?dsd. " +
			"?dataset rdfs:label ?label. " +
			"FILTER (lang(?label)=\""+DEFAULT_LANG+"\"). "+
			"?dsd qb:component ?component. " +
			"?component ?type ?ref. " +
			"FILTER(?type=qb:dimension || ?type=qb:measure )" +
		"}";
	}
	
	protected static String createIndexQuery() {		
		String indexQuery = SPARQLUtils.NS+" "+
			"SELECT ?index WHERE{ "+
				"?index rdf:type rdfindex:Index. "+
			"} ";
		return indexQuery;
	}

	protected static String createComponentsFromIndex(String indexURI) {
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
	
	
	protected static String createIndicatorsFromComponent(String componentURI) {
		String componentFromIndexQuery = SPARQLUtils.NS+" "+ 
			"SELECT ?indicator WHERE{ "+
				"?component  rdf:type rdfindex:Component.  "+
				SPARQLFetcherUtils.createFilterResource(componentURI, RDFIndexUtils.COMPONENT_VAR_SPARQL)+
				"?component  rdfindex:aggregates ?indicators.  "+
				"?indicators rdfindex:part-of ?indicator.  "+
				"?indicator  rdf:type rdfindex:Indicator.  "+
			"}";
		return componentFromIndexQuery;
	}

}
