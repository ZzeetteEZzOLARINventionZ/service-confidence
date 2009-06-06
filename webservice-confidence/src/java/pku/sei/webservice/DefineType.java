package pku.sei.webservice;

import java.util.Map;
import java.util.HashMap;

public class DefineType {
	public String name;
	public Map<String, String> elements;
	
	public DefineType (String name) {
		this.name = name;
		this.elements = new HashMap<String, String>();
	}
	
	public void addType(String name, String type) {
		this.elements.put(name, type);
	}
}