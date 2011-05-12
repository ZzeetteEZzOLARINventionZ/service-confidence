package sei.pku.wsdl_analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/**
 * 解析WSDL文件，从中提取Web Service的各项基本信息
 * 
 * @author zhanglj, liufei, lijiejacy
 * 
 */
public class WSDLFileAnalyzer {

	public static final String WSDL_URI = "http://schemas.xmlsoap.org/wsdl/";
	public static final String XSD_URI = "http://www.w3.org/2001/XMLSchema";
	public HashMap<String, String> WSDL_IdHash = new HashMap<String, String>();
	private File wsdlFile;
	
	public boolean checkWSDLDoc(String wsdlFilePath)
	{
		File file = new File(wsdlFilePath);
		if(file.exists())
		{
			String page = "";
			try {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader reader = new BufferedReader(read);
				String line;
				while((line = reader.readLine()) != null)
				{
					page += line + "\r\n";
				}
				reader.close();
				read.close();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			if(page != null && (page.indexOf("<wsdl:definitions") != -1 || page.indexOf("<definitions") != -1))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	/**
	 * 解析一个WSDL文件
	 * 
	 * @param
	 * @return
	 */
	public WSDLFile analyzeWSDL(String wsdlFilePath) throws Exception {
		WSDLFile ret = new WSDLFile();
		wsdlFile = new File(wsdlFilePath);

		ret.fileName = wsdlFile.getAbsolutePath();

		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(wsdlFile);// //XML
		Element root = doc.getRootElement();

		// extract wsdl file name
		/*
		 * <wsdl:service name="QueryService"> <wsdl:documentation
		 * xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">Microsoft Office
		 * SharePoint Server 2007 Search Query Web Service</wsdl:documentation>
		 * <wsdl:port name="QueryServiceSoap" binding="tns:QueryServiceSoap">
		 * <soap:address location="http://www.innocite.eu/_vti_bin/search.asmx"
		 * /> </wsdl:port> <wsdl:port name="QueryServiceSoap12"
		 * binding="tns:QueryServiceSoap12"> <soap12:address
		 * location="http://www.innocite.eu/_vti_bin/search.asmx" />
		 * </wsdl:port> </wsdl:service>
		 */
		Element wsdlServiceNode = root.getChild("service", Namespace
				.getNamespace(WSDL_URI));

		if (wsdlServiceNode != null) {
			// Get service name
			ret.name = wsdlServiceNode.getAttributeValue("name");
			// Get documentation of service
			Element ServicedocumentNode = wsdlServiceNode.getChild(
					"documentation", Namespace.getNamespace(WSDL_URI));
			ret.document = (ServicedocumentNode == null) ? ""
					: ServicedocumentNode.getValue().replaceAll("<[^>]*>", "");// why?
		}

		// extract wsdl file document which is embeded in the direct child
		// of root element
		/*
		 * <wsdl:documentation
		 * xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">Microsoft Office
		 * SharePoint Server 2007 Search Query Web Service</wsdl:documentation>
		 */
		Element wsdlDocumentNode = root.getChild("documentation", Namespace
				.getNamespace(WSDL_URI));
		String wsdlDocumentValue = "";
		if (wsdlDocumentNode != null)
			wsdlDocumentValue = (wsdlDocumentNode.getText() == null ? ""
					: wsdlDocumentNode.getText());

		// append this documentation to the original documentation if they
		// are different
		if (ret.document != null) {
			if (!ret.document.equals(wsdlDocumentValue)) {
				ret.document += "\r\n" + wsdlDocumentValue;
			}
		} else {
			ret.document = wsdlDocumentValue;
		}

		// extract defineTypes
		/*
		 * <wsdl:types> <s:schema elementFormDefault="qualified"
		 * targetNamespace="urn:Microsoft.Search"> <s:element name="Query">
		 * <s:complexType> <s:sequence> <s:element minOccurs="0" maxOccurs="1"
		 * name="queryXml" type="s:string" /> </s:sequence> </s:complexType>
		 * </s:element> </s:schema> </wsdl:types>
		 */
		List typeList = root.getChildren("types", Namespace
				.getNamespace(WSDL_URI));
		if (typeList.size() > 0) {
			List schemaList = ((Element) typeList.get(0)).getChildren();

			for (int i = 0; i < schemaList.size(); i++) {
				Element schemaNode = (Element) schemaList.get(i);
				if (schemaNode.getName().equals("schema")) {
					List llist = schemaNode.getChildren();
					for (int j = 0; j < llist.size(); j++) {
						Element elementNode = (Element) llist.get(j);
						if (elementNode.getName().equals("element")) {
							if (elementNode.getAttribute("type") != null) {
								DefineType dType = new DefineType(elementNode
										.getAttributeValue("name"));
								dType.addType(PureName.getName(elementNode
										.getAttributeValue("name")), PureName
										.getName(elementNode
												.getAttributeValue("type")));
								ret.addDefineType(dType);
							} else {
								List lllist = elementNode.getChildren();
								if (lllist.size() != 0)
									addDefineType(ret, (Element) lllist.get(0),
											elementNode
													.getAttributeValue("name"));
							}
						} else if (elementNode.getName().equals("complexType")) {
							addDefineType(ret, elementNode, elementNode
									.getAttributeValue("name"));
						} else if (elementNode.getName().equals("simpleType")) {
							List lllist = elementNode.getChildren();
							DefineType dType = new DefineType(PureName
									.getName(elementNode
											.getAttributeValue("name")));
							if (lllist.size() != 0) {
								if (((Element) lllist.get(0))
										.getAttributeValue("base") != null) {
									dType
											.addType(
													PureName
															.getName(((Element) lllist
																	.get(0))
																	.getAttributeValue("base")),
													"");
								} else {
									System.out.println("restriction name null");
								}
							}
						}
					}
				}
			}
		}

		// extract message
		/*
		 * <wsdl:message name="QuerySoapIn"> <wsdl:part name="parameters"
		 * element="s0:Query" /> </wsdl:message> <wsdl:message
		 * name="QuerySoapOut"> <wsdl:part name="parameters"
		 * element="s0:QueryResponse" /> </wsdl:message>
		 */
		List messageList = root.getChildren("message", Namespace
				.getNamespace(WSDL_URI));
		for (int i = 0; i < messageList.size(); i++) {
			Element messageNode = (Element) messageList.get(i);
			Message message = new Message(messageNode.getAttributeValue("name"));
			List llist = messageNode.getChildren();
			for (int j = 0; j < llist.size(); j++) {
				Element partNode = (Element) llist.get(j);
				if (partNode.getName().equals("part")) {
					if (partNode.getAttributeValue("type") != null)
						message.addPart(PureName.getName(partNode
								.getAttributeValue("name")), PureName
								.getName(partNode.getAttributeValue("type")));
					else if (partNode.getAttributeValue("element") != null)
						message
								.addPart(PureName.getName(partNode
										.getAttributeValue("name")), PureName
										.getName(partNode
												.getAttributeValue("element")));
				}
			}
			ret.addMessage(message);
		}

		// extract operations
		/*
		 * <wsdl:operation name="Query"> <wsdl:input message="tns:QuerySoapIn"
		 * /> <wsdl:output message="tns:QuerySoapOut" /> </wsdl:operation>
		 */
		List portTypeList = root.getChildren("portType", Namespace
				.getNamespace(WSDL_URI));
		for (int i = 0; i < portTypeList.size(); i++) {
			Element portType = (Element) portTypeList.get(i);
			List operationlist = portType.getChildren("operation", Namespace
					.getNamespace(WSDL_URI));
			for (int j = 0; j < operationlist.size(); j++) {
				Element operationNode = (Element) operationlist.get(j); // operation
				// node
				Element inputNode = operationNode.getChild("input", Namespace
						.getNamespace(WSDL_URI));
				Element outputNode = operationNode.getChild("output", Namespace
						.getNamespace(WSDL_URI));
				String messageInput = (inputNode == null ? null : inputNode
						.getAttributeValue("message"));
				String messageOutput = (outputNode == null ? null : outputNode
						.getAttributeValue("message"));
				Element documentNode = operationNode.getChild("documentation",
						Namespace.getNamespace(WSDL_URI));

				String documentValue = (documentNode == null) ? null
						: documentNode.getValue().replaceAll("<[^>]*>", "");

				Operation operation = new Operation(operationNode
						.getAttributeValue("name"), ret.getMessage(PureName
						.getName(messageInput)), ret.getMessage(PureName
						.getName(messageOutput)), documentValue, wsdlFile
						.getAbsolutePath());
				ret.addOperation(operation);
			}
		}
		for (int i = 0; i < ret.operations.size(); i++) {
			Operation operation = ret.operations.get(i);
			Map<String, String> realInput = new HashMap<String, String>();
			for (int j = 0; j < ret.messages.size(); j++) {
				Message message = ret.messages.get(j);
				if (operation.input.name.equals(message.name)) {
					Iterator<Map.Entry<String, String>> iter = message.parts
							.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry<String, String> entry = iter.next();
						int k;
						for (k = 0; k < ret.types.size(); k++) {
							if (ret.types.get(k).name.equals(entry.getValue())) {
								realInput.putAll(ret.types.get(k).elements);
								break;
							}
						}
						if (k == ret.types.size())
							realInput.put(entry.getKey(), entry.getValue());
					}
					break;
				}
			}

			Map<String, String> realOutput = new HashMap<String, String>();
			for (int j = 0; j < ret.messages.size(); j++) {
				Message message = ret.messages.get(j);
				if (message == null)
					System.out.println("}}}}}}}}}}}");
				if (operation.output == null)
					System.out.println("{{{{{{{{{{{{{{");
				if (operation.output.name.equals(message.name)) {
					Iterator<Map.Entry<String, String>> iter = message.parts
							.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry<String, String> entry = iter.next();
						int k;

						for (k = 0; k < ret.types.size(); k++) {
							if (ret.types.get(k).name.equals(entry.getValue())) {
								realOutput.putAll(ret.types.get(k).elements);
								break;
							}
						}
						if (k == ret.types.size())
							realOutput.put(entry.getKey(), entry.getValue());
					}
					break;
				}

			}
			operation.setRealParameter(realInput, realOutput);
		}

		// extract binding template
		// i.e. endpoint
		/*
		 * <wsdl:service name="QueryService"> <wsdl:documentation
		 * xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">Microsoft Office
		 * SharePoint Server 2007 Search Query Web Service</wsdl:documentation>
		 * <wsdl:port name="QueryServiceSoap" binding="tns:QueryServiceSoap">
		 * <soap:address location="http://www.innocite.eu/_vti_bin/search.asmx"
		 * /> </wsdl:port> <wsdl:port name="QueryServiceSoap12"
		 * binding="tns:QueryServiceSoap12"> <soap12:address
		 * location="http://www.innocite.eu/_vti_bin/search.asmx" />
		 * </wsdl:port> </wsdl:service>
		 */
		if (wsdlServiceNode != null) {
			List endpointList = wsdlServiceNode.getChildren("port", Namespace
					.getNamespace(WSDL_URI));
			if (endpointList != null) {
				for (int i = 0; i < endpointList.size(); i++) {
					Element endpoint = (Element) endpointList.get(i);
					Attribute nameAttr = endpoint.getAttribute("name");
					if (nameAttr != null) {
						String portName = endpoint.getAttribute("name")
								.getValue();
						Element documentationEle = endpoint
								.getChild("documentation");
						String documentation = "";
						if (documentationEle != null) {
							documentation = documentationEle.getValue();
						}
						List epChildren = endpoint.getChildren();
						for (int j = 0; j < epChildren.size(); j++) {
							Element ele = (Element) epChildren.get(j);
							if (ele.getName().equals("address")) {
								List attrList = ele.getAttributes();
								for (int k = 0; k < attrList.size(); k++) {
									Attribute attrEle = (Attribute) attrList
											.get(k);
									if (attrEle.getName().equals("location")) {
										String location = attrEle.getValue();
										Endpoint ep = new Endpoint();
										ep.setLocation(location);
										ep.setPortName(portName);
										ep.setDocumentation(documentation);
										ret.endPoints.add(ep);
										break;
									}
								}
							}
						}
					}

				}
			}
		}

		return ret;
	}

	private void addDefineType(WSDLFile ret, Element complexNode, String name) {
		DefineType defineType = new DefineType(PureName.getName(name));
		List list = complexNode.getChildren();
		for (int i = 0; i < list.size(); i++) {
			Element node = (Element) list.get(i);
			List llist = node.getChildren();
			for (int j = 0; j < llist.size(); j++) {
				Element nnode = (Element) llist.get(j);
				if (nnode.getName().equals("element")
						&& nnode.getAttributeValue("type") != null) {
					defineType.addType(PureName.getName(nnode
							.getAttributeValue("name")), PureName.getName(nnode
							.getAttributeValue("type")));
				}
			}
		}
		ret.addDefineType(defineType);
	}

	public static void main(String[] args) throws IOException, SQLException {
		// TODO Auto-generated method stub
	}

}

/**
 * handle situation like s:string
 * 
 * @author zhanglj
 * 
 */
class PureName {
	public static final String getName(String s) {
		if (s == null)
			return "";
		return s.substring(s.indexOf(':') + 1);
	}
}
