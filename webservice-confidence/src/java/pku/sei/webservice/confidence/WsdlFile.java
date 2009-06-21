package pku.sei.webservice.confidence;

import java.io.*;
import java.util.*;

import pku.sei.webservice.DataAnalysis;

public class WsdlFile {
	
	public static Map<String, ArrayList<String>> endpoints = new HashMap<String, ArrayList<String>> ();
	
	static {
		try {
			// if (new File("data/endpoints-buffer.txt").exists())
			if (new File("data2/WSDLid_endPointList.txt").exists())
				loadEndpoints();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void loadEndpoints() throws Exception {
		List<List<String>> lines = FileUtil.readFile("data2/WSDLid_endPointList.txt");
		for (List<String> line : lines) {
			if (line.size() <= 0)
				return;
			ArrayList<String> list = new ArrayList<String> ();
			for (int i = 1; i < line.size(); i++)
				list.add(line.get(i));
			endpoints.put(line.get(0), list);
		}
	}
	
	public static void saveEndpoints() throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter("data2/WSDLid_endPointList.txt"));
		for (Map.Entry<String, ArrayList<String>> item : endpoints.entrySet()) {
			String line = item.getKey() + "\t";
			for (int i = 0; i < item.getValue().size(); i ++) {
				line += item.getValue().get(i);
				line += "\t";
			}
			bw.write(line + "\n");
		}
		bw.close();
	}
	
	public static ArrayList<String> getWSDLEndpoints(String file, String id) throws Exception {
		if (endpoints.containsKey(id))
			return endpoints.get(id);
		// System.out.println(file);
		ArrayList<String> e =  DataAnalysis.GetEndPoint(file);	// 那个里面好像有bug
		endpoints.put(id, e);
		if (endpoints.size() % 20 == 0)
			saveEndpoints();
		return e;
	}
	
	public static boolean isEndpointValid(String endpoint) {
		return DataAnalysis.IsValid(endpoint);
	}
	
	public static String getConnetedStatus (String endpoint) {
		String message = DataAnalysis.GetConnect(endpoint);
		String[] lines = message.split("\n");
		if (lines.length <= 0)
			return "error";
		StringTokenizer st = new StringTokenizer (lines[0]);
		if (st.countTokens() < 2)
			return "error";
		st.nextToken();
		return st.nextToken();
	}
	
	public static String getDomain(String url) {
		int pos = url.indexOf("://") + 3;
		int end = url.indexOf("/", pos);
		if (end == -1)
			return url.substring(pos);
		else
			return url.substring(pos, end);
	}
}


//public static String[] wsdlEndpoint = {
//	"<soap:address.+?>", "<http:address.+?>", "<wsdlsoap:address.+?>", "<soap12:address.+?>"
//};
//
//public static Pattern[] patterns = {
//	Pattern.compile(wsdlEndpoint[0]),
//	Pattern.compile(wsdlEndpoint[1]),
//	Pattern.compile(wsdlEndpoint[2]),
//	Pattern.compile(wsdlEndpoint[3])
//};
//
//BufferedReader reader = new BufferedReader(new FileReader(file));
//String text = "";
//String line = null;
//while ((line = reader.readLine()) != null) {
//	text += line.trim();
//	text += "\n";
//}
//reader.close();
//// System.out.println(text);
//for (Pattern p : patterns) {
//	Matcher matcher = p.matcher(text);
//	matcher.find();
//	System.out.println(matcher.matches());
//	
//	/*for (int i = 0; i < n; i ++) {
//		matcher.group(i);
//		System.out.println(text.substring(matcher.start(i), matcher.end(i)));
//	}*/
//}
//return new ArrayList<String> ();
