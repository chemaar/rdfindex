package org.rdfindex.processor;

import java.util.List;

import org.rdfindex.dao.RDFIndexMetadataDAO;
import org.rdfindex.to.ObservationTO;

import com.hp.hpl.jena.rdf.model.Model;

public interface Processor {

	public List<ObservationTO> run(RDFIndexMetadataDAO metadata);
	
}
