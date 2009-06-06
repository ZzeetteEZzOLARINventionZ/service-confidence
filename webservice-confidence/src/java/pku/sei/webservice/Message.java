package pku.sei.webservice;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class Message implements java.io.Serializable {
	public String name;
	
	public Map<String, String> parts;
	
	public ArrayList<String> discreptions;
	
	public Message(String name) {
		this.name = name;
		this.parts = new HashMap<String, String> ();
		this.discreptions = new ArrayList<String> ();
	}
	
	public void addPart(String name, String typeName) {
		this.parts.put(name, typeName);
	}
	
}

