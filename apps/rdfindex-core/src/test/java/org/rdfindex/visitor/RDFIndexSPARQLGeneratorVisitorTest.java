package org.rdfindex.visitor;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.rdfindex.dao.MetadataDAOImpl;
import org.rdfindex.dao.RDFIndexMetadataDAO;
import org.rdfindex.to.IndexTO;
import org.rdfindex.to.ObservationTO;

import test.utils.TestHelper;

public class RDFIndexSPARQLGeneratorVisitorTest {
	
	//@Test
	public void testGeneratedQueryIndex() throws Exception {
		RDFIndexMetadataDAO metadata = new MetadataDAOImpl(TestHelper.INDEX_MODEL, TestHelper.DUMMY_INDEX_METADATA_MODEL, TestHelper.DUMMY_OBSERVATIONS_MODEL);	
		RDFIndexVisitor rdfIndexProcessor = new RDFIndexSPARQLGeneratorVisitor(metadata);
		//Test as visitor
		List<IndexTO> indexes = metadata.getIndexMetadata();
		
		for(IndexTO index:indexes){
			String sparqlQuery = (String) rdfIndexProcessor.visit(index);			
			Assert.assertEquals(3816 , sparqlQuery.length());
			//PrettyPrinter.prettyPrint(SPARQLQueriesHelper.observationsAsRDF(indexObservations));
		
			
		}
	}
	
	@Test
	public void testWeightedQueryIndex() throws Exception {
		RDFIndexMetadataDAO metadata = new MetadataDAOImpl(
				TestHelper.INDEX_MODEL, 
				TestHelper.createModel("weight/dummyindex-weight.ttl"),
				TestHelper.createModel("weight/dummyobservations-weight.ttl"));	
		RDFIndexVisitor rdfIndexProcessor = new RDFIndexSPARQLGeneratorVisitor(metadata);
		//Test as visitor
		List<IndexTO> indexes = metadata.getIndexMetadata();
		
		for(IndexTO index:indexes){
			String sparqlQuery = (String) rdfIndexProcessor.visit(index);			
			Assert.assertEquals(2336 , sparqlQuery.length());
			//PrettyPrinter.prettyPrint(SPARQLQueriesHelper.observationsAsRDF(indexObservations));
		
			
		}
	}

}
