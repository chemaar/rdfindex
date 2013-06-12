package org.rdfindex.utils;

import java.util.Set;


import com.hp.hpl.jena.query.QuerySolution;

public class SPARQLFetcherUtils {

	public static String formatVar(String var){
		return "?"+var;
	}
	public static String formatResource(String uri){
		return "<"+uri+">";
	}
	//	public static IndexTO fetchPPNTO (QuerySolution soln){
	//		String uri = resourceValue(soln,  "index"); //FIXME: Extract constans
	//		return ppnTO;
	//	}
	//
	//	public static ScoredPPNTO fetchScoredPPNTO(QuerySolution soln) {	
	//		ScoredPPNTO result = new ScoredPPNTO();
	//		result.setPpnTO(fetchPPNTO(soln));
	//		result.setScore(1);//FIXME
	//		return result;
	//	}
	//
	//	public static NUTSTO extractedNUTSTO(QuerySolution soln, String field) {
	//		String nutsCode = resourceValue(soln,  field); ;
	//		NUTSTO nutsTO = new NUTSTO(nutsCode);
	//		return nutsTO;
	//	}
	//
	//	public static PSCTO extractedPSCTO(QuerySolution soln, String field) {
	//		String cpvCode = resourceValue(soln, field); 
	//		PSCTO pscTO = new PSCTO(cpvCode);
	//		return pscTO;
	//	}
	//
	
	public static String fetchStringOrResource (QuerySolution soln, String field){
		return (soln==null  || soln.get(field)==null)?"": 
			soln.get(field).isLiteral() ?  soln.getLiteral(field).getString():
											soln.get(field).isURIResource()?
													soln.getResource(field).getURI():
													soln.toString();
	}
	
	
	public static String fetchStringValue (QuerySolution soln, String field){
		return (soln==null  || soln.get(field)==null)?"": 
			soln.get(field).isLiteral() ?  soln.getLiteral(field).getString():soln.toString();
	}

	public static String fetchResourceValue (QuerySolution soln, String field){
		return (soln==null || soln.get(field)==null)?"": 
			soln.get(field).isURIResource() ?  soln.getResource(field).getURI():soln.toString();
	}
	//
	//
	//
	
	public static String createGroupByResource(Set<String> vars) {
		StringBuffer sb = new StringBuffer();
		sb.append("GROUP BY ");
		for(String var:vars){
			sb.append(formatVar(var)+" ");
		}
		return sb.toString();
	}
	
	
	public static String createFilterResource(String resource, String onVar) {
		StringBuffer sb = new StringBuffer();
		sb.append("FILTER ( ");
		sb.append(" (?"+onVar+" = <"+resource+"> ) ");
		sb.append("). ");
		return sb.toString();
	}

		public static String createFilterPartsOf(Set<String> set) {
			return createFilterResources(set, RDFIndexUtils.PART_VAR_SPARQL);
		}
		
		
		public static String createFilterResources(Set<String> resources, String onVar) {
			StringBuffer sb = new StringBuffer();
			if(resources.size() > 0 ){
				sb.append("FILTER ( ");
				int i = 0;
				for(String resource:resources){
					sb.append(" (?"+onVar+" = <"+resource+"> ) ");
					i++;
					if(i< resources.size()){
						sb.append (" || ");
					}
				}
				sb.append("). ");
			}	
			return sb.toString();
		}
		
	//
	//
	//	public static String createFilter(Set<PSCTO> set) {
	//		StringBuffer sb = new StringBuffer();
	//		if(set.size() > 0 ){
	//			sb.append("FILTER ( ");
	//			int i = 0;
	//			for(PSCTO pscTO:set){
	//				sb.append(" (?cpvCode = <"+pscTO.getUri()+"> ) ");
	//				i++;
	//				if(i< set.size()){
	//					sb.append (" || ");
	//				}
	//			}
	//			sb.append("). ");
	//		}	
	//		return sb.toString();
	//	}
	//
	//
	//	public static String createFilterScoredCodes(Set<ScoredPSCTO> scoredPSCCodes) {
	//		StringBuffer sb = new StringBuffer();
	//		if(scoredPSCCodes.size() > 0 ){
	//			sb.append("FILTER ( ");
	//			int i = 0;
	//			for(ScoredPSCTO scoredPscTO:scoredPSCCodes){
	//				sb.append(" (?cpvCode = <"+scoredPscTO.getPscTO().getUri()+"> ) ");
	//				i++;
	//				if(i< scoredPSCCodes.size()){
	//					sb.append (" || ");
	//				}
	//			}
	//			sb.append("). ");
	//		}	
	//		return sb.toString();
	//	}
	//
	//
	//
	//	public static String createFilterYears(YearsTO years) {
	//		StringBuffer sb = new StringBuffer();
	//		if(years != null){
	//			sb.append("FILTER ( ");
	//			sb.append(" (xsd:long(?date) >=  xsd:long("+years.getMin()+")) && (xsd:long(?date) <=  xsd:long("+years.getMax()+"))");
	//			sb.append("). ");
	//		}
	//		return sb.toString();
	//	}
	//
	//	public static String createRewritingQuery(String filterCPV, String filterNuts, int max, String filterYears){
	//		return DAOSPARQLService.NS+" " +  
	//		"SELECT ?ppn ?id  ?date ?cpvCode ?nutsCode  WHERE{ " +
	//		"?ppn rdf:type moldeas-onto:Notice. " +
	//		"?ppn moldeas-onto:located-in ?nutsCode. "+
	//		filterNuts+
	//		"?ppn dcterms:date ?date. " +
	//		filterYears+
	//		"?ppn moldeas-onto:topic ?cpvCode. " +
	//		filterCPV+
	//		"?ppn dcterms:identifier ?id. " +
	//		"} "+"LIMIT "+max ;
	//
	//	}
	//
	//	public static String createRewritingQuery(String filterCPV, String filterNuts, int max){
	//		return DAOSPARQLService.NS+" " +  
	//		"SELECT ?ppn ?id  ?date ?cpvCode ?nutsCode  WHERE{ " +
	//		"?ppn rdf:type moldeas-onto:Notice. " +
	//		"?ppn moldeas-onto:located-in ?nutsCode. "+
	//		filterNuts+
	//		"?ppn moldeas-onto:topic ?cpvCode. " +
	//		filterCPV+
	//		"?ppn dcterms:identifier ?id. " +
	//		"?ppn dcterms:date ?date. " +
	//		"} "+"LIMIT "+max ;
	//
	//	}
	//
	//
	//	public static String createRawQuery(String filterCPV, String filterNuts, int max, String filterYears){
	//		return DAOSPARQLService.NS+" " +  
	//		"SELECT ?ppn ?id  ?date ?cpvCode ?nutsCode  WHERE{ " +
	//		"?ppn rdf:type moldeas-onto:Notice. " +
	//		"?ppn dcterms:identifier ?id. " +	
	//		"?ppn moldeas-onto:topic ?cpvCode. " +
	//		"?ppn moldeas-onto:located-in ?nutsCode. "+
	//		"?ppn dcterms:date ?date. " +
	//		filterCPV+
	//		filterNuts+
	//		filterYears+
	//		"} "+((max == -1)?"":"LIMIT "+max) ;
	//
	//	}


}