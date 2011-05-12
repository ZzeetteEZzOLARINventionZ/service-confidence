/** 
 * File-Name:Demo20101128.java
 *
 * Created on 2010-11-28 上午11:50:08
 * 
 * @author: Neo (neolimeng@gmail.com)
 * Software Engineering Institute, Peking University, China
 * 
 * Copyright (c) 2009, Peking University
 * 
 *
 */
package sei.pku.wsdl_analyzer;

import java.io.File;
import java.util.ArrayList;

/**
 * Description:
 * 
 * @author: Neo (neolimeng@gmail.com) Software Engineering Institute, Peking
 *          University, China
 * @version 1.0 2010-11-28 上午11:50:08
 */
public class Demo20101128 {

	/**
	 * Description:
	 * 
	 * @param args
	 *            void
	 */
	public static void main(String[] args) {
		try {
			String filePath = "D:\\WsdlJob\\WSDL_FILE\\";
			File file = new File(filePath);
			File[] files = file.listFiles();
			WSDLFileAnalyzer wsdlFileAnalyzer = new WSDLFileAnalyzer();
			for (File f : files) {
				try {
					System.out.println(f.getName());
					if(wsdlFileAnalyzer.checkWSDLDoc(filePath + f.getName()) == true)
					{
						WSDLFile wFile = wsdlFileAnalyzer.analyzeWSDL(filePath
								+ f.getName());
						System.out.println("Document:" + wFile.document);
						// System.out.println("File Name:" + wFile.fileName);
						// System.out.println("Name: " + wFile.name);
						// System.out.println("Url: " + wFile.Url);
						System.out.println();
	
						ArrayList<Operation> opelist = wFile.operations;
						for (int i = 0; i < opelist.size(); i++) {
							Operation ope = (Operation) opelist.get(i);
	
							String operationName = ope.operationName;
							String inputName = ope.input.name;
							String outputName = ope.output.name;
							String op_documentation = ope.documentation;
	
							System.out.println("Operation Name:\t" + operationName);
							System.out.println("\t Input: " + inputName);
							System.out.println("\t Output: " + outputName);
							System.out.println("\t Doc: " + op_documentation);
						}
	
						ArrayList<Message> messagelist = wFile.messages;
						for (int i = 0; i < messagelist.size(); i++) {
							Message mes = (Message) messagelist.get(i);
							String messageName = mes.name;
							/**
							 * insert msg
							 */
							System.out.println("Add msg:\t" + messageName);
	
						}
	
						for (Endpoint ep : wFile.endPoints) {
							String portName = ep.getPortName();
							String portLocation = ep.getLocation();
							String port_documentation = ep.getDocumentation();
							System.out.println("Add ep:\t" + portLocation + "\t");
							/**
							 * insert endpoint
							 */
						}
					}
				} catch (Exception e) {
					// e.printStackTrace();
					System.err.println("Error!");
					continue;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
