package sei.pku.wsdl_analyzer;

/**
 * 记录Web Service的一个Endpoint的基本信息的数据结构
 * 
 * @author lijiejacy
 * 
 */
public class Endpoint {
	/**
	 * endpoint的名字（例如QueryServiceSoap，QueryServiceSoap12），
	 * 从名字也可以判断出对应的endpoint支持的访问协议的类型
	 */
	private String portName = "";
	/**
	 * endpoint的URL
	 */
	private String location = "";
	/**
	 * endpoint在该web服务的WSDL文件中对应的说明信息，是从所对应的port元素的documentation子节点中抽取出的
	 */
	private String documentation = "";

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
