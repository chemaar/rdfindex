import test.utils.TestHelper;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class JenaSPARQL {

	public static void main(String []args){
		String query="prefix rdfindex-res:  <http://purl.org/rdfindex/resource/> prefix rdfindex:  <http://purl.org/rdfindex/ontology/> SELECT  ?dim0 ?dim1 (sum(?w*?measure) as ?newvalue) WHERE{ ?element rdfindex:aggregates ?parts. FILTER (  (?element = <http://purl.org/rdfindex/ontology/Health> ) ). ?parts rdfindex:part-of ?partof.?partof rdfindex:dataset ?part .?part rdfindex:weight ?defaultw. OPTIONAL {?partof rdfindex:weight ?aggregationw.} .BIND (if( BOUND(?aggregationw), ?aggregationw, ?defaultw) AS ?w).?observation <http://purl.org/linked-data/cube#dataSet> ?part . FILTER (  (?part = <http://purl.org/rdfindex/ontology/A> )  ||  (?part = <http://purl.org/rdfindex/ontology/B> ) ). ?observation <http://purl.org/rdfindex/ontology/value> ?measure . ?observation <http://purl.org/rdfindex/ontology/ref-year>  ?dim0. ?observation <http://purl.org/rdfindex/ontology/ref-area>  ?dim1. } GROUP BY ?dim0 ?dim1";
		QueryExecution qExec = QueryExecutionFactory.sparqlService(
				"http://localhost:3030/rdfindexweights/query", query, "");
		ResultSet results1 = qExec.execSelect();
		
		Model model =  TestHelper.createModel("full-weight.ttl");
		qExec = QueryExecutionFactory.create(query, model);
		ResultSet results2 = qExec.execSelect();
		
		while(results1.hasNext()){
			System.out.println(results1.next());
		}
		System.out.println("RESULTS 2");
		while(results2.hasNext()){
			System.out.println(results2.next());
		}
		
	}
	
	
}
