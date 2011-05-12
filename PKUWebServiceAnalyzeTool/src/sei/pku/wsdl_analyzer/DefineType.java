package sei.pku.wsdl_analyzer;

import java.util.HashMap;
import java.util.Map;

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