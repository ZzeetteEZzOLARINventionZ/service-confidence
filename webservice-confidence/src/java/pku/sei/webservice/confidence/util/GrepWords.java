package pku.sei.webservice.confidence.util;

import java.io.*;
import java.util.*;

public class GrepWords {
	
	public static String readFile (File file) throws Exception {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = reader.readLine()) != null) {
			buffer.append(line).append("\n");
		}
		reader.close();
		return buffer.toString().toLowerCase();
	}
	
	public static String query = "location";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		File foder = new File("data2/AllFile");
		File[] files = foder.listFiles();
		int count = 0;
		for (File file : files) {
			String content = readFile(file);
			if (content.indexOf(query) > 0) {
				count ++;
				System.out.println(file.getName());
			}
		}
		System.out.println("含有该词的wsdl个数为：" + count);
		
		Map<String, String> wsdls = FileLineMap.loadFileMap("data2/allWsdlFile.txt"); // id, url
		int count2 = 0;
		for (Map.Entry<String, String> item : wsdls.entrySet()) {
			String url = item.getValue().toLowerCase();
			if (url.indexOf(query) > 0 
					&& new File("data2/AllFile/" + item.getKey() + ".wsdl").exists()) {
				String content = readFile(new File("data2/AllFile/" + item.getKey() + ".wsdl"));
				if (content.indexOf(query) > 0) {
					System.out.println(item.getKey() + "\t" + item.getValue());
					count2 ++;
				}
			}
		}
		System.out.println("含有该词的wsdl个数为：" + count2);
	}

}
