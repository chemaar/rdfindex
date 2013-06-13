package org.rdfindex.visitor;

import org.apache.log4j.Logger;
import org.rdfindex.to.ComponentTO;
import org.rdfindex.to.IndexTO;
import org.rdfindex.to.IndicatorTO;

public class RDFDefaultIndexVisitor extends RDFIndexVisitor {
	protected static Logger logger = Logger.getLogger(RDFDefaultIndexVisitor.class);


	public Object visit(IndexTO index) {
		logger.debug("Visiting index"+index.getUri());
		for(ComponentTO component:index.getComponents()){
			this.visit(component);			
		}
		return null;
	}


	public Object visit(ComponentTO component) {
		logger.debug("Visiting component"+component.getUri());
		for(IndicatorTO indicator:component.getIndicators()){
			this.visit(indicator);			
		}
		return null;
	}


	public Object visit(IndicatorTO indicator) {
		logger.debug("Visiting indicator"+indicator.getUri());
		return null;
	}

}
