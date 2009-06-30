package pku.sei.webservice;

import java.util.ArrayList;

public class WSDLFile {

	public String Url;
	public String endPoint;
	public String fileName;
	public String document;
	public String name;
	public ArrayList<Operation> operations;
	public ArrayList<Message> messages;
	public ArrayList<DefineType> types;
	
	public void addOperation(Operation operation) {
		this.operations.add(operation);
	}
	
	public void addMessage(Message message) {
		this.messages.add(message);
	}
	
	public void addDefineType(DefineType type) {
		this.types.add(type);
	}
	
	public WSDLFile() {
		operations = new ArrayList<Operation> ();
		messages = new ArrayList<Message> ();
		types = new ArrayList<DefineType> ();
		name = "";
		document = "";
		fileName = "";
		Url = "";
	}
	
	public Message getMessage(String name) {
		for (int i = 0; i < messages.size(); i ++) {
			// System.out.println("hello\t" + messages.get(i).name + "\t" + name);
			if (messages.get(i).name.equals(name)) 
				return messages.get(i);
		}
		return new Message("");
	}
	
}

