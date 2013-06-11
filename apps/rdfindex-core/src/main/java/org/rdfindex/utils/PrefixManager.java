package org.rdfindex.utils;

import java.util.Enumeration;
import java.util.ResourceBundle;

public class PrefixManager {
	static ResourceBundle prefixes = null; 

	static{
		prefixes = ResourceBundle.getBundle(PrefixManager.class.getName().toString());
	}

	public static String getURIPrefix(String prefix){
		return prefixes.getString(prefix);
	}
	
	public static String getPrefixURI(String uri){
		Enumeration<String> keys = prefixes.getKeys();
		String uriValue = "";
		boolean found = Boolean.FALSE;
		while (keys.hasMoreElements() && !found){
			uriValue = prefixes.getString(keys.nextElement()); 
			if(uriValue.equalsIgnoreCase(uri)){
				found = Boolean.TRUE; 
			}
		}
		return uriValue;
	}
	public static ResourceBundle getResourceBundle(){
		return prefixes ;
	}
}