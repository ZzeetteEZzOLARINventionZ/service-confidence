package sei.pku.wsdl_analyzer;

import java.util.Map;
/**
 * web service一个operation的数据结构
 * @author zhanglj,lijiejacy
 *
 */
public class Operation implements java.io.Serializable {
	public String operationName;
	public Message input;
	public Message output;
	public String documentation;
	public String fileName;
	/**
	 * 将input message替换为具体的信息后的真正的输入参数的信息
	 */
	public Map<String, String> realInput;
	/**
	 * 将output message替换为具体的信息后的真正的返回参数的信息
	 */
	public Map<String, String> realOutput;
	
	public Operation (String name, Message input, Message output, String document, String fileName) {
		this.operationName = name;
		this.input = input;
		this.output = output;
		this.documentation = document;
		this.fileName = fileName;
	}
	
	public void setRealParameter(Map<String, String> realInput, Map<String, String> realOutput) {
		this.realInput = realInput;
		this.realOutput = realOutput;
	}
	
	public void setDocumentation(String documentation) 
	{
		this.documentation = documentation;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getOperationName() {
		return operationName;
	}
	
	
	public String toString() {
		String ret = "[name:" + this.operationName + "]\t[Input:" + this.input.name + "]\t[Output:" + this.output.name + "]";
		return ret;
	}
	
	
	
}


