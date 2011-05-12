package sei.pku.wsdl_analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * 记录WSDL文件中声明的message信息的数据结构
 * @author lijiejacy
 *
 */
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

