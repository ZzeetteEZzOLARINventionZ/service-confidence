package sei.pku.wsdl_analyzer;

import java.util.ArrayList;

/**
 * 记录一个WSDL文档中的信息的数据结构
 * 
 * @author zhanglj, lijiejacy
 * 
 */
public class WSDLFile {
	/**
	 * WSDL文件的完整路径
	 */
	public String fileName;
	/**
	 * WSDL文件的URL
	 */
	public String Url;
	/**
	 * 所有的endpoints
	 */
	public ArrayList<Endpoint> endPoints = new ArrayList<Endpoint>();
	/**
	 * WSDL文件中的关于Web
	 * Service的说明信息，只包括根目录下的直接子节点<documentation>和<service>节点的<documentation>中的内容
	 */
	public String document;
	/**
	 * Web Service的名称
	 */
	public String name;
	/**
	 * Web Service提供的所有operation
	 */
	public ArrayList<Operation> operations;
	/**
	 * Web Service提供的所有message
	 */
	public ArrayList<Message> messages;
	/**
	 * WSDL文件中声明的所有数据类型
	 */
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
		operations = new ArrayList<Operation>();
		messages = new ArrayList<Message>();
		types = new ArrayList<DefineType>();
		name = "";
		document = "";
		fileName = "";
		Url = "";
	}

	public Message getMessage(String name) {
		for (int i = 0; i < messages.size(); i++) {
			if (messages.get(i).name.equals(name))
				return messages.get(i);
		}
		return new Message("");
	}

}
