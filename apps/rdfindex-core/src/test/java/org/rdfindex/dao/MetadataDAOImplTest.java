package org.rdfindex.dao;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;
import org.rdfindex.processor.SPARQLProcessorTest;

import com.hp.hpl.jena.rdf.model.Model;

public class MetadataDAOImplTest {

	@Test
	public void testGetIndexes() {
		Model tbox = SPARQLProcessorTest.createModel("rdfindex.ttl");
		Model abox = SPARQLProcessorTest.createModel("dummyindex.ttl");
		RDFIndexMetadataDAO metadata = new MetadataDAOImpl(tbox, abox);
		Assert.assertEquals(1,metadata.getIndexMetadata().size());
	}

	@Test
	public void testGetComponents() {
		Model tbox = SPARQLProcessorTest.createModel("rdfindex.ttl");
		Model abox = SPARQLProcessorTest.createModel("dummyindex.ttl");
		String indexUri = "http://purl.org/rdfindex/ontology/TheLongestLifeCountry";
		RDFIndexMetadataDAO metadata = new MetadataDAOImpl(tbox, abox);
		Assert.assertEquals(1,metadata.getComponentMetadata(indexUri).size());
	}
	
	@Test
	public void testGetIndicators() {
		Model tbox = SPARQLProcessorTest.createModel("rdfindex.ttl");
		Model abox = SPARQLProcessorTest.createModel("dummyindex.ttl");
		String componentUri = "http://purl.org/rdfindex/ontology/HealthValue";
		RDFIndexMetadataDAO metadata = new MetadataDAOImpl(tbox, abox);
		Assert.assertEquals(1,metadata.getIndicatorMetadata(componentUri).size());
	}
	
}
