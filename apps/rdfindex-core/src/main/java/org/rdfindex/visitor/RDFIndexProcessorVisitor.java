package org.rdfindex.visitor;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.rdfindex.dao.MetadataDAOImpl;
import org.rdfindex.dao.RDFIndexMetadataDAO;
import org.rdfindex.processor.Processor;
import org.rdfindex.to.ComponentTO;
import org.rdfindex.to.IndexTO;
import org.rdfindex.to.IndicatorTO;
import org.rdfindex.to.ObservationTO;
import org.rdfindex.utils.RDFIndexUtils;

import com.hp.hpl.jena.rdf.model.Model;

public class RDFIndexProcessorVisitor extends RDFIndexVisitor implements Processor{

	protected Logger logger = Logger.getLogger(RDFIndexProcessorVisitor.class);
	private RDFIndexMetadataDAO metadata;
	
	public RDFIndexProcessorVisitor(){
		
	}
	
	public RDFIndexProcessorVisitor(RDFIndexMetadataDAO metadata){
		this.metadata = metadata;
		
	}
	
	@Override
	public List<ObservationTO> run(RDFIndexMetadataDAO metadata) {
		List<ObservationTO> observations = new LinkedList<ObservationTO>();
		this.metadata = metadata;
		List<IndexTO> indexes = metadata.getIndexMetadata();
		for(IndexTO index:indexes){
			List<ObservationTO> indexObservations = (List<ObservationTO>) this.visit(index);
			observations.addAll(indexObservations);
		}
	
		return observations;
	}
	
	//FIXME: Refactor? an interface ObservableTO could be extracted and ComponentTO, IndexTO, etc. could implement it, just one method! so far I am going to keep as it is
	//This kind be perfectly implemented with map/reduce, each index, component and indicator in any node.
	public Object visit(IndexTO index) {
		logger.debug("Processing index "+index.getUri());
		List<ObservationTO> observationsFromComponents = new LinkedList<ObservationTO>();
		List<ComponentTO> components = index.getComponents();
		for(ComponentTO component:components){
			List<ObservationTO> componentObservations = (List<ObservationTO>) this.visit(component);
			observationsFromComponents.addAll(componentObservations);
		}
		//Since we have generated a new set of observations for the components of this index we can aggregate
		//PrettyPrinter.prettyPrint(SPARQLQueriesHelper.observationsAsRDF(observationsFromComponents));
		logger.debug("...the index "+index.getUri()+" is going to compute "+observationsFromComponents.size()+" observations from components.");
		List<ObservationTO> observations = RDFIndexUtils.execute(observationsFromComponents, index.getMetadata(), index.getAggregated());
		logger.debug("...the index "+index.getUri()+" has generated "+observations.size()+" new observations.");
		return observations;
	}
	
	
	
	public Object visit(ComponentTO component) {
		logger.debug("Processing component "+component.getUri());
		List<ObservationTO> observationsFromIndicators = new LinkedList<ObservationTO>();
		List<IndicatorTO> indicators = component.getIndicators();
		for(IndicatorTO indicator:indicators){
			List<ObservationTO> indicatorObservations = (List<ObservationTO>) this.visit(indicator);
			observationsFromIndicators.addAll(indicatorObservations);
		}
		//Since we have generated a new set of observations for the indicators of this component we can aggregate
		//PrettyPrinter.prettyPrint(SPARQLQueriesHelper.observationsAsRDF(observationsFromIndicators));
		logger.debug("...the component "+component.getUri()+" is going to compute "+observationsFromIndicators.size()+" observations from indicators.");
		List<ObservationTO> observations = RDFIndexUtils.execute(observationsFromIndicators, component.getMetadata(), component.getAggregated());
		logger.debug("...the component "+component.getUri()+" has generated "+observations.size()+" new observations.");
		return observations;
	}
	
	
	
	public Object visit(IndicatorTO indicator) {
		logger.debug("Processing indicator "+indicator.getUri());
		List<ObservationTO> observations = new LinkedList<ObservationTO>();
		observations.addAll(RDFIndexUtils.execute(this.metadata.getAbox(), indicator.getMetadata(), indicator.getAggregated()));
		logger.debug("...the indicator "+indicator.getUri()+" has generated "+observations.size()+" new observations.");
		return observations;
	}
	
	
}
