package org.rdfindex.processor;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.rdfindex.to.DatasetStructureTO;
import org.rdfindex.to.ObservationTO;
import org.rdfindex.utils.SPARQLUtils;

import test.utils.TestHelper;

import com.hp.hpl.jena.rdf.model.Model;

public class SPARQLProcessorTest {

////	@Test
//	public void test() {
//		Processor rdfIndexProcessor = new SPARQLProcessor();
//		Model tbox = TestHelper.createModel("rdfindex.ttl");
//		Model abox = TestHelper.createModel("dummyindex.ttl");
//		List<ObservationTO> result = rdfIndexProcessor.run(tbox,abox);
//		Assert.assertNotNull(result);
//
//	}
//	//@Test
//	public void testCreateQuery(){
//		SPARQLProcessor rdfIndexProcessor = new SPARQLProcessor();
//		Model abox = TestHelper.createModel("dummyindex.ttl");
//		String query = rdfIndexProcessor.createIndexQuery(abox);
//	}
//
//	
//	//@Test
//	public void testGetMetadataTO(){
//		SPARQLProcessor rdfIndexProcessor = new SPARQLProcessor();
//		Model abox = TestHelper.createModel("dummyindex.ttl");
//		String uri = "http://purl.org/rdfindex/ontology/AggregatedLifeExpectancy";
//		String measure = "http://purl.org/rdfindex/ontology/life-expectancy";
//		String operator = "http://purl.org/rdfindex/ontology/Mean";
//		DatasetStructureTO metadata = rdfIndexProcessor.getMetadataTO(uri , abox);
//		Assert.assertEquals(uri, metadata.getElement());
//		Assert.assertEquals(measure, metadata.getMeasure());
//		Assert.assertEquals(operator, metadata.getOperator());
//		Assert.assertEquals(2, metadata.getPartsOf().size());
//		Assert.assertEquals(2, metadata.getDimensions().size());
//	}
//	
//	//@Test
//	public void testGetCreateQuery(){
//		Model abox = TestHelper.createModel("dummyindex.ttl");
//		String uri = "http://purl.org/rdfindex/ontology/AggregatedLifeExpectancy";
//		DatasetStructureTO metadata = SPARQLProcessor.getMetadataTO(uri , abox);
//		String expected = SPARQLUtils.NS+" "+ "SELECT ?date ?agent (avg(?value) as ?newvalue) WHERE{ ?part <http://purl.org/linked-data/cube#observation> ?observation . FILTER (  (?part = <http://purl.org/rdfindex/ontology/slice2> )  ||  (?part = <http://purl.org/rdfindex/ontology/slice1> ) ). ?observation <http://purl.org/rdfindex/ontology/life-expectancy> ?value . ?observation <http://purl.org/rdfindex/ontology/ref-year> ?date . ?observation <http://purl.org/rdfindex/ontology/ref-area> ?agent . } GROUP BY ?date ?agent ";
//		Assert.assertEquals(expected,SPARQLProcessor.createSPARQLQuery(metadata));
//	}
//	
//	
//	@Test
//	public void testProcessing(){
//		Processor rdfIndexProcessor = new SPARQLProcessor();
//		Model tbox = TestHelper.createModel("rdfindex.ttl");
//		Model abox = TestHelper.createModel("dummyindex.ttl");
//		List<ObservationTO> result = rdfIndexProcessor.run(tbox,abox);
//		Assert.assertEquals(1, result.size());
//	}

}
