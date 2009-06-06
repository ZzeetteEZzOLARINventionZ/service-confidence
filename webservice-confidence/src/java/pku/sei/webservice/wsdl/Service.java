package pku.sei.webservice.wsdl;

import java.util.ArrayList;

public class Service {
	private String name = null;
	private String documentation = null;
	private ArrayList ports = null;//‘› ±≤ª”√

	public void setName(String _name){
		this.name = _name;
	}
	public void setDocumentation(String _documentation){
		this.documentation = _documentation;
	}
	
	public String getName(){
		return this.name;
	}
	public String getDocumentation(){
		return this.documentation;
	}

	
	
}
