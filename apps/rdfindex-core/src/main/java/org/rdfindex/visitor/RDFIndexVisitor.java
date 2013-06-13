package org.rdfindex.visitor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public abstract class RDFIndexVisitor {

	private Class nodesType;

	public RDFIndexVisitor() {
		nodesType = null;
	}

	public RDFIndexVisitor(String nodesTypeName) throws ClassNotFoundException {
		this.nodesType = Class.forName(nodesTypeName);
	}

	public RDFIndexVisitor(Class nodesType) {
		this.nodesType = nodesType;
	}

	// ----------------------------------

	public Object visit(Object node) throws Exception{
		return visit("visit", node);
	}

	public Object visit(ArrayList list) throws Exception{
		return visit("visit", list);
	}

	public Object visit(String methodName, Object node) throws Exception{
		try {
			Method method = getClass().getMethod(methodName, new Class[] { node.getClass() });                      
			return method.invoke(this, new Object[] { node });
		} catch (Exception e) { 
			e.printStackTrace();
			if (nodesType != null && nodesType.isInstance(node))
				visitChildren(methodName, node);
			else
				throw new Exception("Method not found "+methodName+" in "+node.getClass().getName()+" ");
			return null;
		}
	}

	public Object visitChildren(String methodName, Object node) throws Exception{
		Field[] fields = node.getClass().getDeclaredFields();
		for (int i =0; i < fields.length; i++) {
			try {
				if (Modifier.isStatic(fields[i].getModifiers()))
					continue;
				fields[i].setAccessible(true);
				Object child = fields[i].get(node);
				if (nodesType.isInstance(child) || List.class.isInstance(child))
					return visit(methodName, child);
			} catch(IllegalAccessException e) {                             
				throw e;
			}
		}
		return null;
	}


}