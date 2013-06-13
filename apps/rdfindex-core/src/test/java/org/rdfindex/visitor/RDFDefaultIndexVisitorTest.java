package org.rdfindex.visitor;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.rdfindex.dao.MetadataDAOImpl;
import org.rdfindex.dao.RDFIndexMetadataDAO;
import org.rdfindex.to.IndexTO;
import org.rdfindex.to.ObservationTO;

import test.utils.TestHelper;

import com.hp.hpl.jena.rdf.model.Model;

public class RDFDefaultIndexVisitorTest {

	@Test
	public void test() throws Exception {
		RDFIndexVisitor rdfIndexProcessor = new RDFDefaultIndexVisitor();
		RDFIndexMetadataDAO metadata = new MetadataDAOImpl(TestHelper.INDEX_MODEL, TestHelper.DUMMY_INDEX_METADATA_MODEL, TestHelper.DUMMY_OBSERVATIONS_MODEL);	
		List<IndexTO> indexes = metadata.getIndexMetadata();

		for(IndexTO index:indexes){
			rdfIndexProcessor.visit(index);
		}

	}

}
