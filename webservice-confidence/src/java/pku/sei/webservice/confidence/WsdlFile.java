package pku.sei.webservice.confidence;

import java.util.ArrayList;

import pku.sei.webservice.DataAnalysis;

public class WsdlFile {
	//TODO implement it
	public static ArrayList<String> getWSDLEndpoints(String file) throws Exception {
		return DataAnalysis.GetEndPoint(file);
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
