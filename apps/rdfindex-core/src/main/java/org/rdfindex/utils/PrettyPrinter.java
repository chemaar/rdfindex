package org.rdfindex.utils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.ResourceBundle;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class PrettyPrinter {

	public static void serializeModel(Model result, ResourceBundle mapPrefixesURIs, String file, String syntax)
			throws FileNotFoundException, IOException {
		addPrefixes(result, mapPrefixesURIs);
		RDFWriter w = result.getWriter(syntax);	
		OutputStream os= new FileOutputStream(new File(file));
		w.write(result, os,"");	
		os.close();
	}
	
	public static void addPrefixes(Model result, ResourceBundle mapPrefixesURIs){
		Enumeration<String> prefixes = mapPrefixesURIs.getKeys();
		while(prefixes.hasMoreElements()){
			String prefix = prefixes.nextElement();
			result.setNsPrefix(prefix, mapPrefixesURIs.getString(prefix));
		}
		//Building (not extract)
		result.setNsPrefix("dcterms", DCTerms.getURI());
		result.setNsPrefix("xsd", XSD.getURI());
		result.setNsPrefix("owl", OWL.getURI());
		result.setNsPrefix("rdf", RDF.getURI());
		result.setNsPrefix("rdfs", RDFS.getURI());
	}
	
}