package org.rdfindex.dao;

import java.util.List;

import org.rdfindex.to.AggregatedTO;
import org.rdfindex.to.ComponentTO;
import org.rdfindex.to.DatasetStructureTO;
import org.rdfindex.to.IndexTO;
import org.rdfindex.to.IndicatorTO;

import com.hp.hpl.jena.rdf.model.Model;

public interface RDFIndexMetadataDAO {
	public List<IndexTO> getIndexMetadata();
	public List<ComponentTO> getComponentMetadata(String indexUri);
	public List<IndicatorTO> getIndicatorMetadata(String componentUri);
	public DatasetStructureTO getDatasetStructure(String uri);
	public abstract AggregatedTO getAggregatedTO(String elementUri);
	public Model getAbox();
	public Model getTbox();
	
}
