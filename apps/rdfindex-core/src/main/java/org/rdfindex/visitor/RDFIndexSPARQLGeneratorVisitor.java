package org.rdfindex.visitor;

import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.log4j.Logger;
import org.rdfindex.dao.RDFIndexMetadataDAO;
import org.rdfindex.processor.Processor;
import org.rdfindex.to.ComponentTO;
import org.rdfindex.to.IndexTO;
import org.rdfindex.to.IndicatorTO;
import org.rdfindex.to.ObservationTO;
import org.rdfindex.utils.RDFIndexUtils;

public class RDFIndexSPARQLGeneratorVisitor extends RDFIndexVisitor implements Processor{

	protected Logger logger = Logger.getLogger(RDFIndexSPARQLGeneratorVisitor.class);
	private RDFIndexMetadataDAO metadata;
	
	public RDFIndexSPARQLGeneratorVisitor(){
		
	}
	
	public RDFIndexSPARQLGeneratorVisitor(RDFIndexMetadataDAO metadata){
		this.metadata = metadata;
		
	}
	
	@Override
	public List<ObservationTO> run(RDFIndexMetadataDAO metadata) {		
		throw new RuntimeException("This visitor does not calculate the index, only generates a macro SPARQL query.");	
	}
	
	public Object visit(IndexTO index) {
		StringBuffer composedSPARQLQuery = new StringBuffer();
		List<ComponentTO> components = index.getComponents();
		for(ComponentTO component:components){
			composedSPARQLQuery.append((String) this.visit(component));
			composedSPARQLQuery.append("\n");
		}
		String sparqlQuery = RDFIndexUtils.createSPARQLQuery(index.getMetadata(), index.getAggregated());
		logger.debug("Index "+index.getUri()+" has generated :\n"+sparqlQuery);
		composedSPARQLQuery.append(sparqlQuery);
		return composedSPARQLQuery.toString();
	}
	
	
	
	public Object visit(ComponentTO component) {
		StringBuffer composedSPARQLQuery = new StringBuffer();
		List<IndicatorTO> indicators = component.getIndicators();
		for(IndicatorTO indicator:indicators){
			composedSPARQLQuery.append((String) this.visit(indicator));
			composedSPARQLQuery.append("\n");
			
		}		
		String sparqlQuery = RDFIndexUtils.createSPARQLQuery(component.getMetadata(), component.getAggregated());
		logger.debug("Component "+component.getUri()+" has generated :\n"+sparqlQuery);
		composedSPARQLQuery.append(sparqlQuery);		
		return composedSPARQLQuery.toString();
	}
	
	
	
	public Object visit(IndicatorTO indicator) {
		String sparqlQuery = RDFIndexUtils.createSPARQLQuery(indicator.getMetadata(), indicator.getAggregated());	
		logger.debug("Indicator "+indicator.getUri()+" has generated :\n"+sparqlQuery);
		return sparqlQuery;
	}
	
	
}
