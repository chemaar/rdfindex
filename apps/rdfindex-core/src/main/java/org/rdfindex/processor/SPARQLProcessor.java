package org.rdfindex.processor;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class SPARQLProcessor implements Processor {

	@Override
	public Model run(Model tbox, Model abox) {
	
		return ModelFactory.createDefaultModel();
	}

}
