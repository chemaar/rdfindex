package org.rdfindex.utils;

import static org.junit.Assert.*;

import org.junit.Test;
import org.rdfindex.to.ObservationTO;

import com.hp.hpl.jena.rdf.model.Model;

public class RDFIndexUtilsTest {

	@Test
	public void test() {
		ObservationTO observation = new ObservationTO();
		observation.setUri("http://example.org/o1");
		observation.setAgent("http://dbpedia.org/resource/Spain");
		observation.setDate("2010");
		observation.setMeasure("http://example.org/ontology/property1");
		observation.setValue("5");
		observation.setUriDataset("http://example.org/resource/element");
		Model model = RDFIndexUtils.observationAsRDF(observation);
		model.write(System.out);
	}

}
