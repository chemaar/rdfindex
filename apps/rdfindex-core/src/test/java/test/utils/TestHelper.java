package test.utils;

import java.io.FileNotFoundException;
import java.io.InputStream;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class TestHelper {

	//Helper Methods
	public static Model createModel(String filename) {
		Model model =ModelFactory.createDefaultModel();
		try {
			model.read(TestHelper.createInputStream(filename), "","TURTLE");
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
	
	public static final String DUMMY_OBSERVATIONS_FILE="dummyobservations.ttl";
	public static final String DUMMY_INDEX_METADATA_MODEL_FILE="dummyindex.ttl";
	public static final String INDEX_MODEL_FILE="rdfindex.ttl";
	
	public static Model INDEX_MODEL = TestHelper.createModel(INDEX_MODEL_FILE);
	public static Model DUMMY_INDEX_METADATA_MODEL = TestHelper.createModel(DUMMY_INDEX_METADATA_MODEL_FILE);
	public static Model DUMMY_OBSERVATIONS_MODEL = TestHelper.createModel(DUMMY_OBSERVATIONS_FILE);

}
