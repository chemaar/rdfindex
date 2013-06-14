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
import org.rdfindex.utils.PrettyPrinter;
import org.rdfindex.utils.SPARQLQueriesHelper;

import test.utils.TestHelper;

import com.hp.hpl.jena.rdf.model.Model;

public class RDFIndexProcessorVisitorTest {

	@Test
	public void testProcessing(){
		//Test as processor
		Processor rdfIndexProcessor = new RDFIndexProcessorVisitor();
		RDFIndexMetadataDAO metadata = new MetadataDAOImpl(TestHelper.INDEX_MODEL, TestHelper.DUMMY_INDEX_METADATA_MODEL, TestHelper.DUMMY_OBSERVATIONS_MODEL);	
		List<ObservationTO> result = rdfIndexProcessor.run(metadata);
		Assert.assertEquals(4, result.size());
	}


	@Test
	public void testAsVisitor() throws Exception{
		//Test as visitor
		RDFIndexMetadataDAO metadata = new MetadataDAOImpl(TestHelper.INDEX_MODEL, TestHelper.DUMMY_INDEX_METADATA_MODEL, TestHelper.DUMMY_OBSERVATIONS_MODEL);	
		RDFIndexVisitor rdfIndexProcessor = new RDFIndexProcessorVisitor(metadata);
		
		List<IndexTO> indexes = metadata.getIndexMetadata();
		
		for(IndexTO index:indexes){
			List<ObservationTO> indexObservations = (List<ObservationTO>) rdfIndexProcessor.visit(index);
			//PrettyPrinter.prettyPrint(SPARQLQueriesHelper.observationsAsRDF(indexObservations));
			Assert.assertEquals(4, indexObservations.size());
			
		}
	}

	
}
