package org.rdfindex.processor;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.rdfindex.to.ObservationTO;
import org.rdfindex.utils.PrettyPrinter;
import org.rdfindex.utils.SPARQLQueriesHelper;

import test.utils.TestHelper;

import com.hp.hpl.jena.rdf.model.Model;

public class JavaSPARQLProcessorImplTest {

	@Test
	public void testProcessing(){
		Processor rdfIndexProcessor = new JavaSPARQLProcessorImpl();
		Model tbox = TestHelper.createModel("rdfindex.ttl");
		Model abox = TestHelper.createModel("dummyindex.ttl");
		List<ObservationTO> result = rdfIndexProcessor.run(tbox,abox);
		//PrettyPrinter.prettyPrint(SPARQLQueriesHelper.observationsAsRDF(result));
		Assert.assertEquals(4, result.size());
	}
}
