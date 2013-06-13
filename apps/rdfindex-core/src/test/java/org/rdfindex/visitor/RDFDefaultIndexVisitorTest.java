package org.rdfindex.visitor;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.rdfindex.dao.MetadataDAOImpl;
import org.rdfindex.to.IndexTO;
import org.rdfindex.to.ObservationTO;

import test.utils.TestHelper;

import com.hp.hpl.jena.rdf.model.Model;

public class RDFDefaultIndexVisitorTest {

	@Test
	public void test() throws Exception {
		RDFIndexVisitor rdfIndexProcessor = new RDFDefaultIndexVisitor();
		Model tbox = TestHelper.createModel("rdfindex.ttl");
		Model abox = TestHelper.createModel("dummyindex.ttl");
		MetadataDAOImpl metadata = new MetadataDAOImpl(tbox, abox);
		List<IndexTO> indexes = metadata.getIndexMetadata();

		for(IndexTO index:indexes){
			rdfIndexProcessor.visit(index);
		}

	}

}
