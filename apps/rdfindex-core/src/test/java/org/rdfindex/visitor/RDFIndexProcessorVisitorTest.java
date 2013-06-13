package org.rdfindex.visitor;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.rdfindex.dao.MetadataDAOImpl;
import org.rdfindex.dao.RDFIndexMetadataDAO;
import org.rdfindex.processor.JavaSPARQLProcessorImpl;
import org.rdfindex.processor.Processor;
import org.rdfindex.to.IndexTO;
import org.rdfindex.to.ObservationTO;

import test.utils.TestHelper;

import com.hp.hpl.jena.rdf.model.Model;

public class RDFIndexProcessorVisitorTest {

	@Test
	public void testProcessing(){
		//Test as processor
		Processor rdfIndexProcessor = new RDFIndexProcessorVisitor();
		Model tbox = TestHelper.createModel("rdfindex.ttl");
		Model abox = TestHelper.createModel("dummyindex.ttl");
		RDFIndexMetadataDAO metadata = new MetadataDAOImpl(tbox, abox);
		List<ObservationTO> result = rdfIndexProcessor.run(metadata);
		//PrettyPrinter.prettyPrint(SPARQLQueriesHelper.observationsAsRDF(result));
		Assert.assertEquals(4, result.size());
	}


	@Test
	public void testAsVisitor() throws Exception{
		//Test as visitor
		Model tbox = TestHelper.createModel("rdfindex.ttl");
		Model abox = TestHelper.createModel("dummyindex.ttl");
		RDFIndexMetadataDAO metadata = new MetadataDAOImpl(tbox, abox);
		RDFIndexVisitor rdfIndexProcessor = new RDFIndexProcessorVisitor(metadata);
		
		List<IndexTO> indexes = metadata.getIndexMetadata();
		
		for(IndexTO index:indexes){
			List<ObservationTO> indexObservations = (List<ObservationTO>) rdfIndexProcessor.visit(index);
			Assert.assertEquals(4, indexObservations.size());
		}
	}

	
}
