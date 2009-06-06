package pku.sei.webservice;

import java.util.Map;

public class Operation implements java.io.Serializable {
	public String name;
	public Message input;
	public Message output;
	public String document;
	public String fileName;
	
	public Map<String, String> realInput;
	public Map<String, String> realOutput;
	
	public Operation (String name, Message input, Message output, String document, String fileName) {
		this.name = name;
		this.input = input;
		this.output = output;
		this.document = document;
		this.fileName = fileName;
	}
	
	public void setRealParameter(Map<String, String> realInput, Map<String, String> realOutput) {
		this.realInput = realInput;
		this.realOutput = realOutput;
	}
	
	public String toString() {
		String ret = "[name:" + this.name + "]\t[Input:" + this.input.name + "]\t[Output:" + this.output.name + "]";
		return ret;
	}
	
}


