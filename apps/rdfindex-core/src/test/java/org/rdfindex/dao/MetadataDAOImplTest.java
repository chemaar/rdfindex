package org.rdfindex.dao;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;
import org.rdfindex.processor.SPARQLProcessorTest;

import com.hp.hpl.jena.rdf.model.Model;

public class MetadataDAOImplTest {
	Model tbox = SPARQLProcessorTest.createModel("rdfindex.ttl");
	Model abox = SPARQLProcessorTest.createModel("dummyindex.ttl");
	@Test
	public void testGetIndexes() {	
		RDFIndexMetadataDAO metadata = new MetadataDAOImpl(tbox, abox);	
		Assert.assertEquals(1,metadata.getIndexMetadata().size());
	}

	@Test
	public void testGetComponents() {
		String indexUri = "http://purl.org/rdfindex/ontology/TheLongestLifeCountry";
		RDFIndexMetadataDAO metadata = new MetadataDAOImpl(tbox, abox);
		Assert.assertEquals(1,metadata.getComponentMetadata(indexUri).size());
	}
	
	@Test
	public void testGetIndicators() {
		String componentUri = "http://purl.org/rdfindex/ontology/HealthValue";
		RDFIndexMetadataDAO metadata = new MetadataDAOImpl(tbox, abox);		
		Assert.assertEquals(1,metadata.getIndicatorMetadata(componentUri).size());
	}
	
	@Test
	public void testGetIndicatorDSD() {
		String indicatorUri = "http://purl.org/rdfindex/ontology/LifeExpectancy";
		RDFIndexMetadataDAO metadata = new MetadataDAOImpl(tbox, abox);	
		Assert.assertEquals(3,metadata.getDatasetStructure(indicatorUri).getDimensions().size());
	}
	
	@Test
	public void testGetComponentAggregated() {
		String indexUri = "http://purl.org/rdfindex/ontology/TheLongestLifeCountry";
		String componentUri = "http://purl.org/rdfindex/ontology/HealthValue";
		String indicatorUri = "http://purl.org/rdfindex/ontology/AggregatedLifeExpectancy";
		RDFIndexMetadataDAO metadata = new MetadataDAOImpl(tbox, abox);
		Assert.assertEquals(1,metadata.getAggregatedTO(indexUri).getPartsOf().size());
		Assert.assertEquals(1,metadata.getAggregatedTO(componentUri).getPartsOf().size());
		Assert.assertEquals(1,metadata.getAggregatedTO(indicatorUri).getPartsOf().size());
	}
	
}
