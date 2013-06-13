package org.rdfindex.visitor;

import java.util.List;

import org.apache.log4j.Logger;
import org.rdfindex.to.ComponentTO;
import org.rdfindex.to.IndexTO;
import org.rdfindex.to.IndicatorTO;
import org.rdfindex.to.ObservationTO;

public class RDFDefaultIndexVisitor extends RDFIndexVisitor {
	protected static Logger logger = Logger.getLogger(RDFDefaultIndexVisitor.class);


	public Object visit(IndexTO index) {
		logger.debug("The index "+index.getUri()+" is comprised of ");
		for(ComponentTO component:index.getComponents()){
			this.visit(component);			
		}
		return null;
	}


	public Object visit(ComponentTO component) {
		logger.debug("\t the component "+component.getUri()+" that is also comprised of: ");
		for(IndicatorTO indicator:component.getIndicators()){
			this.visit(indicator);			
		}
		return null;
	}


	public Object visit(IndicatorTO indicator) {
		logger.debug("\t\t indicator "+indicator.getUri()+".");
		return null;
	}

}
