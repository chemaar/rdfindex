package org.rdfindex.visitor;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.rdfindex.dao.MetadataDAOImpl;
import org.rdfindex.dao.RDFIndexMetadataDAO;
import org.rdfindex.to.IndexTO;
import org.rdfindex.to.ObservationTO;
import org.rdfindex.utils.PrettyPrinter;
import org.rdfindex.utils.SPARQLQueriesHelper;

import test.utils.TestHelper;

public class NaiveWorldBankTest {

	@Test
	public void testSPARQLVisitor() throws Exception {
		RDFIndexMetadataDAO metadata = new MetadataDAOImpl(
				TestHelper.INDEX_MODEL, 
				TestHelper.createModel("wb/naive-worldbank.ttl"),
				TestHelper.createModel("wb/naive-worldbank-observations.ttl"));	
		RDFIndexVisitor rdfIndexProcessor = new RDFIndexSPARQLGeneratorVisitor(metadata);
		//Test as visitor
		List<IndexTO> indexes = metadata.getIndexMetadata();
		
		for(IndexTO index:indexes){
			String sparqlQuery = (String) rdfIndexProcessor.visit(index);			
			Assert.assertEquals(3544 , sparqlQuery.length());
			//PrettyPrinter.prettyPrint(SPARQLQueriesHelper.observationsAsRDF(indexObservations));
			
		}
	}
	
	@Test
	public void testProces() throws Exception {
		RDFIndexMetadataDAO metadata = new MetadataDAOImpl(
				TestHelper.INDEX_MODEL, 
				TestHelper.createModel("wb/naive-worldbank.ttl"),
				TestHelper.createModel("wb/naive-worldbank-observations.ttl"));	
		
		RDFIndexVisitor rdfIndexProcessor = new RDFIndexProcessorVisitor(metadata);
		//Test as visitor
		List<IndexTO> indexes = metadata.getIndexMetadata();
		
		for(IndexTO index:indexes){
			List<ObservationTO> indexObservations = (List<ObservationTO>) rdfIndexProcessor.visit(index);
			PrettyPrinter.prettyPrint(SPARQLQueriesHelper.observationsAsRDF(indexObservations));
			Assert.assertEquals(4, indexObservations.size());
			
		}

	}

}
