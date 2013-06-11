package org.rdfindex.processor;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.rdfindex.to.AggregationMetadataTO;
import org.rdfindex.to.ObservationTO;
import org.rdfindex.utils.SPARQLUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class SPARQLProcessorTest {

//	@Test
	public void test() {
		Processor rdfIndexProcessor = new SPARQLProcessor();
		Model tbox = createModel("rdfindex.ttl");
		Model abox = createModel("dummyindex.ttl");
		List<ObservationTO> result = rdfIndexProcessor.run(tbox,abox);
		Assert.assertNotNull(result);

	}
	//@Test
	public void testCreateQuery(){
		SPARQLProcessor rdfIndexProcessor = new SPARQLProcessor();
		Model abox = createModel("dummyindex.ttl");
		String query = rdfIndexProcessor.createIndexQuery(abox);
	}

	
	//@Test
	public void testGetMetadataTO(){
		SPARQLProcessor rdfIndexProcessor = new SPARQLProcessor();
		Model abox = createModel("dummyindex.ttl");
		String uri = "http://purl.org/rdfindex/ontology/AggregatedLifeExpectancy";
		String measure = "http://purl.org/rdfindex/ontology/life-expectancy";
		String operator = "http://purl.org/rdfindex/ontology/Mean";
		AggregationMetadataTO metadata = rdfIndexProcessor.getMetadataTO(uri , abox);
		Assert.assertEquals(uri, metadata.getElement());
		Assert.assertEquals(measure, metadata.getMeasure());
		Assert.assertEquals(operator, metadata.getOperator());
		Assert.assertEquals(2, metadata.getPartsOf().size());
		Assert.assertEquals(2, metadata.getDimensions().size());
	}
	
	//@Test
	public void testGetCreateQuery(){
		Model abox = createModel("dummyindex.ttl");
		String uri = "http://purl.org/rdfindex/ontology/AggregatedLifeExpectancy";
		AggregationMetadataTO metadata = SPARQLProcessor.getMetadataTO(uri , abox);
		String expected = SPARQLUtils.NS+" "+ "SELECT ?date ?agent (avg(?value) as ?newvalue) WHERE{ ?part <http://purl.org/linked-data/cube#observation> ?observation . FILTER (  (?part = <http://purl.org/rdfindex/ontology/slice2> )  ||  (?part = <http://purl.org/rdfindex/ontology/slice1> ) ). ?observation <http://purl.org/rdfindex/ontology/life-expectancy> ?value . ?observation <http://purl.org/rdfindex/ontology/ref-year> ?date . ?observation <http://purl.org/rdfindex/ontology/ref-area> ?agent . } GROUP BY ?date ?agent ";
		Assert.assertEquals(expected,SPARQLProcessor.createSPARQLQuery(metadata));
	}
	
	
	@Test
	public void testProcessing(){
		Processor rdfIndexProcessor = new SPARQLProcessor();
		Model tbox = createModel("rdfindex.ttl");
		Model abox = createModel("dummyindex.ttl");
		List<ObservationTO> result = rdfIndexProcessor.run(tbox,abox);
		Assert.assertEquals(1, result.size());
	}
	
	
	//Helper Methods
	public static Model createModel(String filename) {
		Model model =ModelFactory.createDefaultModel();
		try {
			model.read(createInputStream(filename), "","TURTLE");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			//throw new RuntimeErrorException(e);
		}
		return model; 
	}

	public static InputStream createInputStream (String filename) throws FileNotFoundException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream in = classLoader.getResourceAsStream(filename);
		if (in == null) {
			throw new FileNotFoundException(filename);
		} else {
			return in;
		}
	}

}
