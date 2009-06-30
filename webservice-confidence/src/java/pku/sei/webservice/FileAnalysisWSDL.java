package pku.sei.webservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

public class FileAnalysisWSDL implements AnalysisWSDL {

	public static final String WSDL_URI = "http://schemas.xmlsoap.org/wsdl/";
	public static final String XSD_URI = "http://www.w3.org/2001/XMLSchema";
	public HashMap<String,String> WSDL_IdHash = new HashMap<String,String>();
	private File wsdlFile;
	
	public FileAnalysisWSDL(File wsdlFile) {
		this.wsdlFile = wsdlFile;
	}
	public FileAnalysisWSDL() {
	}

	public WSDLFile getWSDL(String filePath) {
		WSDLFile ret = new WSDLFile();
		try {
			wsdlFile = new File(filePath);
			
			int idx1 = filePath.lastIndexOf("/");
			int idx2 = filePath.indexOf(".wsdl");
			if(idx1!=-1&&idx2!=-1&&idx1<idx2)
			{
				String id = filePath.substring(idx1+1,idx2);
				//System.out.println(id);
				ret.Url = (String)this.WSDL_IdHash.get(id);
				ret.name = "Unknown_"+id;
			}
			

			ret.fileName = wsdlFile.getAbsolutePath();
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(wsdlFile);////XML
			Element root = doc.getRootElement();
			
			//extract wsdl file name
			Element wsdlServiceNode = root.getChild("service", Namespace.getNamespace(WSDL_URI));
			if(wsdlServiceNode!=null)
				ret.name = wsdlServiceNode.getAttributeValue("name");
			
			if(wsdlServiceNode!=null)
			{
				Element ServicedocumentNode = wsdlServiceNode.getChild("documentation", Namespace.getNamespace(WSDL_URI));
				ret.document  = (ServicedocumentNode == null) ? "" : ServicedocumentNode.getValue().replaceAll("<[^>]*>", "");
			}
			
			
			
			
			//extract wsdl file document
			Element wsdlDocumentNode = root.getChild("documentation", Namespace.getNamespace(WSDL_URI));
			String wsdlDocumentValue = "";
			if (wsdlDocumentNode != null)
				wsdlDocumentValue = (wsdlDocumentNode.getText() == null ? "" : wsdlDocumentNode.getText());
			if(ret.document!=null&&ret.document.length()!=0)
				ret.document += "\r\n"+wsdlDocumentValue;
			else
				ret.document = wsdlDocumentValue; 
			
			// extract defineTypes
			List typeList = root.getChildren("types", Namespace.getNamespace(WSDL_URI));
			if(typeList.size() != 0)
			{
				List schemaList = ((Element)typeList.get(0)).getChildren(); 
				
				for (int i = 0; i < schemaList.size(); i ++) {
					Element schemaNode = (Element)schemaList.get(i);
					// RecurVisit(node, "\t");
					if (schemaNode.getName().equals("schema")) {
						List llist = schemaNode.getChildren();
						for (int j = 0; j < llist.size(); j ++) {
							Element elementNode = (Element)llist.get(j);
							if (elementNode.getName().equals("element")) {
								if (elementNode.getAttribute("type") != null) {
									DefineType dType = new DefineType(elementNode.getAttributeValue("name"));
									dType.addType(PureName.getName(elementNode.getAttributeValue("name")),
											PureName.getName(elementNode.getAttributeValue("type")));
									ret.addDefineType(dType);
								}
								else {
									List lllist = elementNode.getChildren();
									if (lllist.size() != 0)
										addDefineType(ret, (Element)lllist.get(0), elementNode.getAttributeValue("name"));
								}
							}
							else if (elementNode.getName().equals("complexType")) {
								addDefineType(ret, elementNode, elementNode.getAttributeValue("name"));
							}
							else if (elementNode.getName().equals("simpleType")) {
								List lllist = elementNode.getChildren();
								DefineType dType = new DefineType(PureName.getName(elementNode.getAttributeValue("name")));
								if (lllist.size() != 0) {
									if (((Element)lllist.get(0)).getAttributeValue("base") != null) {
										dType.addType(PureName.getName(((Element)lllist.get(0)).getAttributeValue("base")),
												"");
									}
									else {
										System.out.println("restriction name null");
									}
								}
							}
						}
					}
				}
			}
			
			
			// extract message
			List messageList = root.getChildren("message", Namespace.getNamespace(WSDL_URI));
			for (int i = 0; i < messageList.size(); i ++) {
				Element messageNode = (Element) messageList.get(i);
				Message message = new Message(messageNode.getAttributeValue("name"));
				// System.out.println(message.name);
				List llist = messageNode.getChildren();
				for (int j = 0; j < llist.size(); j ++) {
					Element partNode = (Element) llist.get(j);
					// System.out.println(nnode.getName() + nnode.getNamespaceURI());
					if (partNode.getName().equals("part")) {
						if (partNode.getAttributeValue("type") != null)
							message.addPart(PureName.getName(partNode.getAttributeValue("name")),
								PureName.getName(partNode.getAttributeValue("type")));
						else if (partNode.getAttributeValue("element") != null)
							message.addPart(PureName.getName(partNode.getAttributeValue("name")),
									PureName.getName(partNode.getAttributeValue("element")));
					}
				}
				ret.addMessage(message);
			}
		
			// extract operations
			List portTypeList = root.getChildren("portType", Namespace.getNamespace(WSDL_URI));
			for (int i = 0; i < portTypeList.size(); i ++) {
				Element portType = (Element)portTypeList.get(i);
				List operationlist = portType.getChildren("operation", Namespace.getNamespace(WSDL_URI));
				for (int j = 0; j < operationlist.size(); j ++) {
					Element operationNode = (Element)operationlist.get(j);	// operation node
					Element inputNode = operationNode.getChild("input", Namespace.getNamespace(WSDL_URI));
					Element outputNode = operationNode.getChild("output", Namespace.getNamespace(WSDL_URI));
					String messageInput = (inputNode == null ? null : inputNode.getAttributeValue("message"));
					String messageOutput = (outputNode == null ? null : outputNode.getAttributeValue("message"));
					Element documentNode = operationNode.getChild("documentation", Namespace.getNamespace(WSDL_URI));
					
					String documentValue = (documentNode == null) ? null : documentNode.getValue().replaceAll("<[^>]*>", "");
					/*Operation operation = new Operation(operationNode.getAttributeValue("name"),
							ret.getMessage(PureName.getName(messageInput)), 
							ret.getMessage(PureName.getName(messageOutput)), 
							wsdlDocumentValue + " " + documentValue,
							wsdlFile.getAbsolutePath());*/
					
					Operation operation = new Operation(operationNode.getAttributeValue("name"),
							ret.getMessage(PureName.getName(messageInput)), 
							ret.getMessage(PureName.getName(messageOutput)), 
							documentValue,
							wsdlFile.getAbsolutePath());
					ret.addOperation(operation);
				}
			}
			for (int i = 0; i < ret.operations.size(); i ++) {
				Operation operation = ret.operations.get(i);
				Map<String, String> realInput = new HashMap<String, String> ();
				for (int j = 0; j < ret.messages.size(); j ++) {
					Message message = ret.messages.get(j);
					if (operation.input.name.equals(message.name)) {
						Iterator<Map.Entry<String, String> > iter = message.parts.entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry<String, String> entry = iter.next();
							int k;
							for (k = 0; k < ret.types.size(); k ++) {
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
				Map<String, String> realOutput = new HashMap<String, String> ();
				for (int j = 0; j < ret.messages.size(); j ++) {
					Message message = ret.messages.get(j);
					if (message == null)	System.out.println("}}}}}}}}}}}");
					if (operation.output == null)	System.out.println("{{{{{{{{{{{{{{");
					if (operation.output.name.equals(message.name)) {
						Iterator<Map.Entry<String, String> > iter = message.parts.entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry<String, String> entry = iter.next();
							int k;
							
							for (k = 0; k < ret.types.size(); k ++) {
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
			return ret;
		} catch(JDOMException e) {
			//System.out.println(e);
			//e.printStackTrace();
			//return null;
			return ret;
		} catch(IOException e) {
			//System.out.println(e);
			//e.printStackTrace();
			//return null;
			return ret;
		}	
		catch(Exception e)
		{
			//return null;
			return ret;
		}
	}	
	
	private void addDefineType(WSDLFile ret, Element complexNode, String name) {
		DefineType defineType = new DefineType(PureName.getName(name));
		List list = complexNode.getChildren();
		for (int i = 0; i < list.size(); i ++) {
			Element node = (Element) list.get(i);
			List llist = node.getChildren();
			for (int j = 0; j < llist.size(); j ++) {
				Element nnode = (Element) llist.get(j);
				if (nnode.getName().equals("element") && nnode.getAttributeValue("type")!= null) {
					defineType.addType(PureName.getName(nnode.getAttributeValue("name")),
							PureName.getName(nnode.getAttributeValue("type")));
				}
			}
		}
		ret.addDefineType(defineType);
	}
	
	
	public void TestDocu(String fileName)
	{
		
		FileReader fr= null;
		try {
			fr = new FileReader(fileName);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		String record=new String();
		int count = 0;
		int NotWSDLCount = 0;
		int NULLCount = 0;
		int NoDocCount = 0;
		int opeDocCount = 0;
	    try {
			while((record = br.readLine()) != null){
				if(!WSDLChecker(record.trim()))
				{
					System.out.println("NOT WSDL:   "+record.trim());
					FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_NotWSDL_AllFinal.txt", record.trim()+"\r\n", true);
					NotWSDLCount++;
					continue;
				}
				WSDLFile wFile = getWSDL(record.trim());
				if(wFile==null)
				{
					System.out.println("NULL:   "+record.trim());
					FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_NULL_AllFinal.txt", record.trim()+"\r\n", true);
					
					NULLCount++;
					continue;
				}
				if(wFile.document!=null&&wFile.document.trim().length()>0)
				{
					//FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_Document_in_1006.txt", record.trim()+"\r\n"+
					//		wFile.document+"\r\n", true);
					FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_Document_in_AllFinal.txt", record.trim()+"\r\n", true);
					count++;
				}
				else
				{
					if(wFile.operations.size()>0)
					{
						ArrayList<Operation> opelist = wFile.operations;
						boolean flag = false;
						String temp = "";
						for(int i=0;i<opelist.size();i++)
						{
							Operation ope = (Operation)opelist.get(i);
							if(ope.document!=null&&ope.document.trim().length()>0)
							{
								flag = true;
								temp = ope.document;
								break;
							}
							System.out.println(ope.document);//operation 描述信息
						}
						if(flag)
						{
							System.out.println("NO Service Doc But Ope doc: "+record.trim());
							//FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_opeDocument_1006.txt", 
							//		record.trim()+"\r\n"+temp+"\r\n", true);
							FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_opeDocument_AllFinal.txt", 
									record.trim()+"\r\n", true);
							opeDocCount++;
						}
						else
						{
							System.out.println("NO Doc: "+record.trim());
							FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_NoDocument_AllFinal.txt", 
									record.trim()+"\r\n", true);
							NoDocCount++;
						}
							
					}
					else
					{
					System.out.println("NO Doc: "+record.trim());
					FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_NoDocument_AllFinal.txt", 
							record.trim()+"\r\n", true);
					NoDocCount++;
					}
				}
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Docu Doc: "+count);
		System.out.println("ope Doc: "+opeDocCount);
		System.out.println("No Doc: "+NoDocCount);
		System.out.println("Null : "+NULLCount);
		System.out.println("NotWSDL : "+NotWSDLCount);
		try {
			br.close();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int GetLength(String text)
	{
		int length = 0;
		String []array = text.split("\\.|\\?|!| |\t|:");
		for(int i=0;i<array.length;i++)
		{
		System.out.println(array[i]);
		if(array[i].trim().length()>0)
			length++;
		}
		return length;
	}
	public void TestDescription(String fileName)
	{
		FileReader fr= null;
		try
		{
			fr = new FileReader(fileName);			
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedReader br = new BufferedReader(fr);
		String record=new String();
		int count = 0;
		int NotWSDLCount = 0;
		int NULLCount = 0;
		int NoDocCount = 0;
		int opeDocCount = 0;
	    try {
			while((record = br.readLine()) != null){
				String []array = record.split("\t");
				record = "D:\\Code program\\WSCrawler\\WSInfoData\\AllFile\\"+array[0].trim()+".wsdl";
				if(!WSDLChecker(record.trim()))
				{
					System.out.println("NOT WSDL:   "+record.trim());
					NotWSDLCount++;
					continue;
				}
				WSDLFile wFile = getWSDL(record.trim());
				if(wFile==null)
				{
					System.out.println("NULL:   "+record.trim());
					NULLCount++;
					continue;
				}
				if(wFile.document!=null&&wFile.document.trim().length()>0)
				{
					//FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_Document_in_1006.txt", record.trim()+"\r\n"+
					//		wFile.document+"\r\n", true);
					//FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_Document_in_AllFinal.txt", record.trim()+"\r\n", true);
					int length = GetLength(wFile.document.trim());
					FileOperation.WriteToFileNew("D:\\WSFile_ForICWS\\WS_Description_All_Final.txt", 
							record.trim()+"\t"+length+"\r\n", true);
					FileOperation.WriteToFileNew("D:\\WSFile_ForICWS\\WS_DescriptionDetail_All_Final.txt", 
							record.trim()+"\t"+length+"\r\n", true);
					FileOperation.WriteToFileNew("D:\\WSFile_ForICWS\\WS_DescriptionDetail_All_Final.txt", 
							"\t\t\t"+wFile.document.trim()+"\r\n", true);
					
					count++;
				}
				/*else
				{
					if(wFile.operations.size()>0)
					{
						ArrayList<Operation> opelist = wFile.operations;
						boolean flag = false;
						String temp = "";
						for(int i=0;i<opelist.size();i++)
						{
							Operation ope = (Operation)opelist.get(i);
							if(ope.document!=null&&ope.document.trim().length()>0)
							{
								flag = true;
								temp = ope.document;
								break;
							}
							System.out.println(ope.document);//operation 描述信息
						}
						if(flag)
						{
							System.out.println("NO Service Doc But Ope doc: "+record.trim());
							//FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_opeDocument_1006.txt", 
							//		record.trim()+"\r\n"+temp+"\r\n", true);
							FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_opeDocument_AllFinal.txt", 
									record.trim()+"\r\n", true);
							opeDocCount++;
						}
						else
						{
							System.out.println("NO Doc: "+record.trim());
							FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_NoDocument_AllFinal.txt", 
									record.trim()+"\r\n", true);
							NoDocCount++;
						}
							
					}
					else
					{
					System.out.println("NO Doc: "+record.trim());
					FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_NoDocument_AllFinal.txt", 
							record.trim()+"\r\n", true);
					NoDocCount++;
					}
				}*/
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Docu Doc: "+count);
		System.out.println("ope Doc: "+opeDocCount);
		System.out.println("No Doc: "+NoDocCount);
		System.out.println("Null : "+NULLCount);
		System.out.println("NotWSDL : "+NotWSDLCount);
		 try {
			br.close();
			 fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void TestOpeDescription(String fileName)
	{
		FileReader fr= null;
		try
		{
			fr = new FileReader(fileName);			
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedReader br = new BufferedReader(fr);
		String record=new String();
		int count = 0;
		int NotWSDLCount = 0;
		int NULLCount = 0;
		int NoDocCount = 0;
		int opeDocCount = 0;
	    try {
			while((record = br.readLine()) != null){
				String []array = record.split("\t");
				record = "D:\\Code program\\WSCrawler\\WSInfoData\\AllFile\\"+array[0].trim()+".wsdl";
				if(!WSDLChecker(record.trim()))
				{
					System.out.println("NOT WSDL:   "+record.trim());
					NotWSDLCount++;
					continue;
				}
				WSDLFile wFile = getWSDL(record.trim());
				if(wFile==null)
				{
					System.out.println("NULL:   "+record.trim());
					NULLCount++;
					continue;
				}
				/*if(wFile.document!=null&&wFile.document.trim().length()>0)
				{
					//FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_Document_in_1006.txt", record.trim()+"\r\n"+
					//		wFile.document+"\r\n", true);
					//FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_Document_in_AllFinal.txt", record.trim()+"\r\n", true);
					int length = GetLength(wFile.document.trim());
					FileOperation.WriteToFileNew("D:\\WSFile_ForICWS\\WS_Description_All_Final.txt", 
							record.trim()+"\t"+length+"\r\n", true);
					FileOperation.WriteToFileNew("D:\\WSFile_ForICWS\\WS_DescriptionDetail_All_Final.txt", 
							record.trim()+"\t"+length+"\r\n", true);
					FileOperation.WriteToFileNew("D:\\WSFile_ForICWS\\WS_DescriptionDetail_All_Final.txt", 
							"\t\t\t"+wFile.document.trim()+"\r\n", true);
					
					count++;
				}*/
				
				if(wFile.operations.size()>0)
				{
					ArrayList<Operation> opelist = wFile.operations;
					boolean flag = false;
					int totalLength = 0;
					for(int i=0;i<opelist.size();i++)
					{
						Operation ope = (Operation)opelist.get(i);
						if(ope.document!=null&&ope.document.trim().length()>0)
						{
							flag = true;
							totalLength += GetLength(ope.document);
						}
						//System.out.println(ope.document);//operation 描述信息
					}
					if(opelist.size()>0)
					{
						double ave = (double)totalLength/opelist.size();
						
						FileOperation.WriteToFileNew("D:\\WSFile_ForICWS\\WS_opeDescription_AllFinal.txt", 
							record.trim()+"\t"+totalLength+"\t"+ave+"\r\n", true);
					}
					else
						FileOperation.WriteToFileNew("D:\\WSFile_ForICWS\\WS_opeDescription_AllFinal.txt", 
								record.trim()+"\t"+totalLength+"\t0"+
								"\r\n", true);
				/*	if(flag)
					{
						//System.out.println("NO Service Doc But Ope doc: "+record.trim());
						//FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_opeDocument_1006.txt", 
						//		record.trim()+"\r\n"+temp+"\r\n", true);
						//FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_opeDocument_AllFinal.txt", 
						//		record.trim()+"\r\n", true);
						FileOperation.WriteToFileNew("D:\\WSFile_ForICWS\\WS_opeDescription_AllFinal.txt", 
										record.trim()+"\t"+totalLength+"\t"+
										(totalLength/opelist.size())+"\r\n", true);
						opeDocCount++;
					}
					else
					{
						System.out.println("NO Doc: "+record.trim());
						FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_NoDocument_AllFinal.txt", 
								record.trim()+"\r\n", true);
						NoDocCount++;
					}*/
						
				}
				/*else
				{
					if(wFile.operations.size()>0)
					{
						ArrayList<Operation> opelist = wFile.operations;
						boolean flag = false;
						String temp = "";
						for(int i=0;i<opelist.size();i++)
						{
							Operation ope = (Operation)opelist.get(i);
							if(ope.document!=null&&ope.document.trim().length()>0)
							{
								flag = true;
								temp = ope.document;
								break;
							}
							System.out.println(ope.document);//operation 描述信息
						}
						if(flag)
						{
							System.out.println("NO Service Doc But Ope doc: "+record.trim());
							//FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_opeDocument_1006.txt", 
							//		record.trim()+"\r\n"+temp+"\r\n", true);
							FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_opeDocument_AllFinal.txt", 
									record.trim()+"\r\n", true);
							opeDocCount++;
						}
						else
						{
							System.out.println("NO Doc: "+record.trim());
							FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_NoDocument_AllFinal.txt", 
									record.trim()+"\r\n", true);
							NoDocCount++;
						}
							
					}
					else
					{
					System.out.println("NO Doc: "+record.trim());
					FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_NoDocument_AllFinal.txt", 
							record.trim()+"\r\n", true);
					NoDocCount++;
					}
				}*/
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Docu Doc: "+count);
		System.out.println("ope Doc: "+opeDocCount);
		System.out.println("No Doc: "+NoDocCount);
		System.out.println("Null : "+NULLCount);
		System.out.println("NotWSDL : "+NotWSDLCount);
		 try {
			br.close();
			 fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void TestRelatedPage(String fileName)
	{
		FileReader fr= null;
		try
		{
			fr = new FileReader(fileName);			
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedReader br = new BufferedReader(fr);
		String record=new String();

	    try {
			while((record = br.readLine()) != null){		
				String []array = record.split("\t");
						FileOperation.WriteToFileNew("D:\\WSFile_ForICWS\\WS_RelatedPage_AllFinal.txt", 
							array[0].trim()+"\t"+(array.length-1)+"\r\n", true);
				
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 try {
			br.close();
			 fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//add 2009.01.10
	public void TestOperation(String fileName)
	{
		 FileReader fr= null;
		try {
			fr = new FileReader(fileName);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		int opeCount[] = new int[1000];
		for(int i=0;i<1000;i++)
		{
		   opeCount[i] = 0;
		}
		
		String record=new String();
		int count = 0;
		int NotWSDLCount = 0;
		int NULLCount = 0;
		int NoDocCount = 0;
		int opeDocCount = 0;
	     try {
			while((record = br.readLine()) != null){
				if(!WSDLChecker(record.trim()))
				{
					System.out.println("NOT WSDL:   "+record.trim());
					//FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_NotWSDL_AllFinal.txt", record.trim()+"\r\n", true);
					NotWSDLCount++;
					continue;
				}
				WSDLFile wFile = getWSDL(record.trim());
				if(wFile==null)
				{
					System.out.println("NULL:   "+record.trim());
					//FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_NULL_AllFinal.txt", record.trim()+"\r\n", true);
					NULLCount++;
					continue;
				}
				ArrayList<Operation> opelist = wFile.operations;
				int number = opelist.size();
				if(number>=0)
				{
					FileOperation.WriteToFileNew("D:\\WSFile_ForICWS\\WS_OperationDetail_AllFinal.txt", 
							record.trim()+"\t"+number+"\r\n", true);
					
					opeCount[number]++;
				}
			/*	if(wFile.document!=null&&wFile.document.trim().length()>0)
				{
					//FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_Document_in_1006.txt", record.trim()+"\r\n"+
					//		wFile.document+"\r\n", true);
					FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_Document_in_AllFinal.txt", record.trim()+"\r\n", true);
					count++;
				}
				else
				{
					if(wFile.operations.size()>0)
					{
						ArrayList<Operation> opelist = wFile.operations;
						boolean flag = false;
						String temp = "";
						for(int i=0;i<opelist.size();i++)
						{
							Operation ope = (Operation)opelist.get(i);
							if(ope.document!=null&&ope.document.trim().length()>0)
							{
								flag = true;
								temp = ope.document;
								break;
							}
							System.out.println(ope.document);//operation 描述信息
						}
						if(flag)
						{
							System.out.println("NO Service Doc But Ope doc: "+record.trim());
							//FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_opeDocument_1006.txt", 
							//		record.trim()+"\r\n"+temp+"\r\n", true);
							FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_opeDocument_AllFinal.txt", 
									record.trim()+"\r\n", true);
							opeDocCount++;
						}
						else
						{
							System.out.println("NO Doc: "+record.trim());
							FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_NoDocument_AllFinal.txt", 
									record.trim()+"\r\n", true);
							NoDocCount++;
						}
							
					}
					else
					{
					System.out.println("NO Doc: "+record.trim());
					FileOperation.WriteToFileNew("D:\\WSFile_1209\\WS_NoDocument_AllFinal.txt", 
							record.trim()+"\r\n", true);
					NoDocCount++;
					}
				}*/
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 try {
			br.close();
			 fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			FileOperation.WriteToFileNew("D:\\WSFile_ForICWS\\WS_Operation_AllFinal.txt", "NotWSDLCount\t"+NotWSDLCount+"\r\n", true);
			FileOperation.WriteToFileNew("D:\\WSFile_ForICWS\\WS_Operation_AllFinal.txt", "NULLCount\t"+NULLCount+"\r\n", true);
			for(int i=0;i<1000;i++)
			{
				if(opeCount[i]>0)
				{
					FileOperation.WriteToFileNew("D:\\WSFile_ForICWS\\WS_Operation_AllFinal.txt", ""+i+"\t"+opeCount[i]+"\r\n", true);
					
				}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
//	add 2009.01.10
	public void TestFileSize(String fileName)
	{
		 FileReader fr= null;
		try {
			fr = new FileReader(fileName);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		
		int opeCount[] = new int[1000];
		for(int i=0;i<1000;i++)
		{
		   opeCount[i] = 0;
		}
		
		String record=new String();
	     try {
			while((record = br.readLine()) != null){
				File mFile = new File(record.trim());
				System.out.println(mFile.length());
				try {
					FileOperation.WriteToFileNew("D:\\WSFile_ForICWS\\WS_FileSize_AllFinal.txt", 
							record.trim()+"\t"+mFile.length()+"\r\n", true);
				
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		 try {
			br.close();
			 fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
	public void TestDocu1(String fileName)
	{
		 FileReader fr= null;
		try {
			fr = new FileReader(fileName);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		String record=new String();
		int count = 0;
		int NotWSDLCount = 0;
		int NULLCount = 0;
		int NoDocCount = 0;
		int opeDocCount = 0;
	     try {
			while((record = br.readLine()) != null){
				if(!WSDLChecker1(record.trim()))
				{
					System.out.println("NOT WSDL:   "+record.trim());
					FileOperation.WriteToFileNew("E:\\WSFile0906\\WS_NotWSDL_1006_TrueDoc.txt", record.trim()+"\r\n", true);
					NotWSDLCount++;
					continue;
				}
				
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("NotWSDL_True : "+NotWSDLCount);
		 try {
			br.close();
			 fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/*public boolean WSDLChecker(String fileName)
	{
		boolean flag = true;
		try {
			String body = FileOperation.ReadFileToVector(fileName);
			if(body.toLowerCase().indexOf("</head>")!=-1
					&& body.toLowerCase().indexOf("</html>")!=-1)
				flag = false;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}*/
	//检查是否为HTML文件
	public boolean WSDLChecker(String fileName) throws IOException
	{
		boolean flag = true;
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		String record=new String();
		while((record = br.readLine()) != null)
		{
			if(record.toLowerCase().indexOf("<head>")!=-1)
			{
				flag = false;
				break;
			}		
		}
		br.close();
		fr.close();
		return flag;
	}
	//检查是否为XML文件
	public boolean WSDLChecker2(String fileName) throws IOException
	{
		boolean flag = false;
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		String record=new String();
		while((record = br.readLine()) != null)
		{
			if(record.toLowerCase().indexOf("&lt;?xml")!=-1)
			{
				flag = true;
				break;
			}		
		}
		br.close();
		fr.close();
		return flag;
	}
	public boolean WSDLChecker1(String fileName) throws IOException
	{
		boolean flag = true;
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		String record=new String();
		while((record = br.readLine()) != null)
		{
			if(record.toLowerCase().indexOf("documentation")!=-1)
			{
				flag = false;
				break;
			}		
		}
		br.close();
		fr.close();
		return flag;
	}
	public HashMap<String,String> InitialHash(String inFile)
	{
		//HashMap<String,String> WSDL_IdHash = new HashMap<String,String>();
		Vector vec = new Vector();
		try {
			FileOperation.ReadFileToVector(inFile, vec);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<vec.size();i++)
		{
			String str = (String)vec.get(i);
			String strs[] = str.split("\t");
			WSDL_IdHash.put(strs[0],strs[1]);
		}		
		return WSDL_IdHash;	
	}
	public void InfoAddLength()
	{
		Vector vec = new Vector();
		try {
			FileOperation.ReadFileToVector("E:\\Code program\\WSCrawler\\WSInfoData\\For 2946\\Document_Static.txt", vec);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i =0;i<vec.size();i++)
		{
			String line = (String)vec.get(i);
			String []array = line.split("\t");
			String id = array[0];
			String fileName = "E:\\Code program\\WSCrawler\\WSInfoData\\For 2946\\DesInfo\\"+id+"_Info.txt";
			Vector vec1 = new Vector();
			try {
				FileOperation.ReadFileToVector(fileName, vec1);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int count = 0;
			String content = "";
			for(int j=1;j<vec1.size();j++)
			{
				String info = (String)vec1.get(j);
				content+=info;
			}
			count = GetLength(content);
			try {
				FileOperation.WriteToFileNew("E:\\Code program\\WSCrawler\\WSInfoData\\For 2946\\Document_info_Number.txt",
						id+"\t"+count+"\r\n", true);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void ConverseFile(String fileName) throws SecurityException, IOException
	{
		Vector vec = new Vector();
		try {
			FileOperation.ReadFileToVector(fileName, vec);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<vec.size();i++)
		{
			String line  = (String)vec.get(i);
			String []array = line.split("\t");
			String Name = "D:/Code program/WSCrawler/WSInfoData/AllFile/"+array[0].trim()+".wsdl";
			FileOperation.WriteToFileNew("E:/1766ValidUrlNew.txt", Name+"\r\n", true);
		}
	}
	
	public void ConverseFile0508() throws SecurityException, IOException
	{
		ArrayList<String> list = new ArrayList<String>();
		Vector vec = new Vector();
		try {
			FileOperation.ReadFileToVector("D:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\ValidUrl0425.txt", vec);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<vec.size();i++)
		{
			String line  = (String)vec.get(i);
			String []array = line.split("\t");
			list.add(array[1].trim());
		}
		
		vec = new Vector();
		try {
			FileOperation.ReadFileToVector("D:\\Code program\\WSCrawler\\WSInfoData\\For0303\\TrueBackLinkFile.txt", vec);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<vec.size();i++)
		{
			String line  = (String)vec.get(i);
			String []array = line.split("\t");
			if(list.contains(array[0].trim()))
				FileOperation.WriteToFileNew("D:\\RelatedPageQ12.txt", array[0].trim()+"\t"+(array.length-1)+"\r\n", true);
					
		}
		
		
	}
	
	public void CombineFile0508() throws SecurityException, IOException
	{
		HashMap<String,String> map = new HashMap<String,String>();
		Vector vec = new Vector();
		try {
			FileOperation.ReadFileToVector("D:\\RelatedPageQ34.txt", vec);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<vec.size();i++)
		{
			String line  = (String)vec.get(i);
			String []array = line.split("\t");
			map.put(array[0],array[1]);
		}
		
		vec = new Vector();
		try {
			FileOperation.ReadFileToVector("D:\\RelatedPageQ12.txt", vec);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<vec.size();i++)
		{
			String line  = (String)vec.get(i);
			String []array = line.split("\t");
			int count = Integer.parseInt(array[1].trim());
			if(map.containsKey(array[0].trim()))
			{
				String m = map.get(array[0].trim());
				count+=Integer.parseInt(m);
				map.remove(array[0].trim());
				map.put(array[0].trim(), ""+count);
				//FileOperation.WriteToFileNew("D:\\RelatedPageQ12.txt", array[0].trim()+"\t"+(array.length-1)+"\r\n", true);
			}
			else
				map.put(array[0],array[1]);
					
		}
		
		 Iterator ite = map.keySet().iterator();
		 while(ite.hasNext())
		 {
			 String url = (String)ite.next();
			 String count = map.get(url);
			 FileOperation.WriteToFileNew("D:\\RelatedPageQ1234.txt", url+"\t"+count+"\r\n", true);
		 }
		
		
		
	}
	
	
	public static void main(String[] args) throws IOException, SQLException {
		// TODO Auto-generated method stub
		
		FileAnalysisWSDL  wsdl = new FileAnalysisWSDL();
		//wsdl.InfoAddLength();
	//	wsdl.TestDocu("E:/1766ValidUrlNew.txt");
		//wsdl.TestDocu("E:/Code program/WSCrawler/WSInfoData/AllFinalWS.txt");
		//wsdl.TestDocu("E:/Code program/WSCrawler/WSInfoData/AllFinalWS.txt");
		//wsdl.TestOperation("E:/Code program/WSCrawler/WSInfoData/AllFinalWS.txt");
	//	wsdl.TestDescription("E:/Code program/WSCrawler/WSInfoData/AllFinalWS.txt");
	//	wsdl.TestDescription("D:/Code program/WSCrawler/WSInfoData/0316Expe/ValidUrl0425.txt");
	//wsdl.TestOpeDescription("E:/Code program/WSCrawler/WSInfoData/AllFinalWS.txt");
	wsdl.TestOpeDescription("D:/Code program/WSCrawler/WSInfoData/0316Expe/ValidUrl0425.txt");
		//wsdl.TestRelatedPage("E:\\Code program\\WSCrawler\\WSInfoData\\For 2946\\DocumentTrueBackLinkFile.txt");
		//wsdl.TestFileSize("E:/Code program/WSCrawler/WSInfoData/AllFinalWS.txt");
		//wsdl.TestDocu1("E:\\WSFile0906\\WS_NotWSDL_1006_True1.txt");
		//wsdl.TestDocu("E:\\WSFile0906\\WS_Path_0906.txt");
		//wsdl.TestDocu("E:\\WSFile0906\\WS_Path_Old.txt");
		
		//wsdl.ConverseFile("E:/ValidUrl0425.txt");
		//wsdl.ConverseFile0508();
	//	wsdl.CombineFile0508();
	//	String str = "asdf ert dfg.asds sdf "+"\r\n"+"dfg sdf  ert.";
	//	System.out.println(str);
	//	System.out.println(wsdl.GetLength(str));
		
		//Statics
	/*	int count = 0;
		Vector vec = new Vector();
		FileOperation.ReadFileToVector("E:/Code program/WSCrawler/TranWSDLFile_1212.txt", vec);
		for(int i=0;i<vec.size();i++)
		{
			String filePath = (String)vec.get(i);
		//for(int i=0;i<4865;i++)
		//{
		//	String filePath = "D:/WSFiles/"+i+".wsdl";
			if(!wsdl.WSDLChecker(filePath))
			{
				//System.out.println("NOT WSDL:   "+record.trim());
				//FileOperation.WriteToFileNew("E:\\WSFile0906\\WS_NotWSDL_1006.txt", record.trim()+"\r\n", true);
				count++;
				FileOperation.WriteToFileNew("D:/NotWSDLTran_1212.txt", filePath+"\r\n", true);
				continue;
			}
			else
			{
				//FileOperation.WriteToFileNew("D:/WSDL_1209.txt", filePath+"\r\n", true);
				
			}
		}
		System.out.println("NOT WSDL:   "+count);*/
		
		/*int count = 0;
		Vector vec = new Vector();
		FileOperation.ReadFileToVector("D:/NotWSDL_1209.txt", vec);
		for(int i=0;i<vec.size();i++)
		{
			String path = (String)vec.get(i);
			if(wsdl.WSDLChecker2(path))
			{
				//System.out.println("NOT WSDL:   "+record.trim());
				//FileOperation.WriteToFileNew("E:\\WSFile0906\\WS_NotWSDL_1006.txt", record.trim()+"\r\n", true);
				count++;
				FileOperation.WriteToFileNew("D:/IncludeWSDL_1209.txt", path+"\r\n", true);
				continue;
			}
		}
		System.out.println(count);*/
		
		
		//Url 与 id 对应的文件
	/*	String UrlFile = "E:/Code program/WSCrawler/WSInfoData/CrawledWSUrlFileNew.txt";
		wsdl.InitialHash(UrlFile);
		
		//WS 文件的路径
		WSDLFile wFile = wsdl.getWSDL("E:/Code program/WSCrawler/WSInfoData/AllFile/0.wsdl");
		
		System.out.println(wFile.name); //WS 名称
		System.out.println(wFile.fileName); //WS 名称
		System.out.println(wFile.Url); //WS Url
		System.out.println(wFile.document); //WS 描述信息
		
		//operation列表
		ArrayList<Operation> opelist = wFile.operations;
		for(int i=0;i<opelist.size();i++)
		{
			Operation ope = (Operation)opelist.get(i);
			System.out.println("====================================");
			System.out.println(ope.name);//operation 名称
			System.out.println(ope.document);//operation 描述信息
			System.out.println(ope.input.name+"\t"+ope.output.name);//输入输出
			//System.out.println((String)ope.input.parts.get(ope.input.name));//输入参数具体类型
		}
		
        //message 列表
		ArrayList<Message> messagelist = wFile.messages;
		for(int i=0;i<opelist.size();i++)
		{
			Message mes = (Message)messagelist.get(i);
			System.out.println("******************************");
			System.out.println(mes.name); //message 名称
		}*/
	}
		
	
	
}
