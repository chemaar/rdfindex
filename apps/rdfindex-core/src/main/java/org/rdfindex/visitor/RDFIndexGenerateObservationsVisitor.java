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
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class RDFIndexGenerateObservationsVisitor extends RDFIndexVisitor implements Processor{

	protected Logger logger = Logger.getLogger(RDFIndexGenerateObservationsVisitor.class);
	private RDFIndexMetadataDAO metadata;
	
	public RDFIndexGenerateObservationsVisitor(){
		
	}
	
	public RDFIndexGenerateObservationsVisitor(RDFIndexMetadataDAO metadata){
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
	
	public Object visit(IndexTO index) {
		List<ObservationTO> observationsFromComponents = new LinkedList<ObservationTO>();
		List<ComponentTO> components = index.getComponents();
		for(ComponentTO component:components){
			List<ObservationTO> componentObservations = (List<ObservationTO>) this.visit(component);
			observationsFromComponents.addAll(componentObservations);
		}
		List<ObservationTO> observations = RDFIndexUtils.execute(observationsFromComponents, index.getMetadata(), index.getAggregated());
		logger.debug("The index "+index.getUri()+" has generated "+observations.size());
		observations.addAll(observationsFromComponents);	
		logger.debug("Total collected "+observations.size());
		return observations;
	}
	
	
	
	public Object visit(ComponentTO component) {
		List<ObservationTO> observationsFromIndicators = new LinkedList<ObservationTO>();
		List<IndicatorTO> indicators = component.getIndicators();
		
		for(IndicatorTO indicator:indicators){
			List<ObservationTO> indicatorObservations = (List<ObservationTO>) this.visit(indicator);
			observationsFromIndicators.addAll(indicatorObservations);
		}
		List<ObservationTO> observations = RDFIndexUtils.execute(observationsFromIndicators, component.getMetadata(), component.getAggregated());
		logger.debug("The component "+component.getUri()+" has generated "+observations.size());
		observations.addAll(observationsFromIndicators);
		logger.debug("Total collected "+observations.size());
		return observations;
	}
	
	
	
	public Object visit(IndicatorTO indicator) {
		List<ObservationTO> observations = new LinkedList<ObservationTO>();
		observations.addAll(RDFIndexUtils.execute(this.metadata.getObservationsModel(), indicator.getMetadata(), indicator.getAggregated()));
		logger.debug("The indicator "+indicator.getUri()+" has generated "+observations.size());
		return observations;
	}
	
	
}
