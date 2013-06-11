package org.rdfindex.processor;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.rdfindex.to.AggregationMetadataTO;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class SPARQLProcessorTest {

	@Test
	public void test() {
		Processor rdfIndexProcessor = new SPARQLProcessor();
		Model tbox = createModel("rdfindex.ttl");
		Model abox = createModel("dummyindex.ttl");
		Model result = rdfIndexProcessor.run(tbox,abox);
		Assert.assertNotNull(result);

	}

	@Test
	public void testCreateQuery(){
		SPARQLProcessor rdfIndexProcessor = new SPARQLProcessor();
		Model abox = createModel("dummyindex.ttl");
		String query = rdfIndexProcessor.createIndexQuery(abox);
	}

	
	@Test
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
