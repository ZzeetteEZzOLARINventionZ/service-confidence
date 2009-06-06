package pku.sei.webservice.wsdl;

public class Operation {
	private String name = null;
	private String input = null;
	private String output = null;
	
	public void setName(String _name){
		this.name = _name;
	}
	public void setInput(String _input){
		this.input = _input;
	}
	public void setOutput(String _output){
		this.output = _output;
	}
	public String getName(){
		return this.name;
	}
	public String getInput(){
		return this.input;
	}
	public String getOutput(){
		return this.output;
	}
}
