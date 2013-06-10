package org.rdfindex.processor;

import com.hp.hpl.jena.rdf.model.Model;

public interface Processor {

	public Model run(Model tbox, Model abox);
	
}
