package org.rdfindex.processor;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;

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
