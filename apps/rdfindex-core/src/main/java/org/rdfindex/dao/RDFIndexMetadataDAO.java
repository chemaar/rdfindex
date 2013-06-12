package org.rdfindex.dao;

import java.util.List;

import org.rdfindex.to.ComponentTO;
import org.rdfindex.to.IndexTO;
import org.rdfindex.to.IndicatorTO;

public interface RDFIndexMetadataDAO {
	public List<IndexTO> getIndexMetadata();
	public List<ComponentTO> getComponentMetadata(String indexUri);
	public List<IndicatorTO> getIndicatorMetadata(String componentUri);
	
}