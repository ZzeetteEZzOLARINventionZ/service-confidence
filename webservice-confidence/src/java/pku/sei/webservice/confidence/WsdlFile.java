package pku.sei.webservice.confidence;

import java.util.ArrayList;

import pku.sei.webservice.DataAnalysis;

public class WsdlFile {
	public static ArrayList<String> getWSDLEndpoints(String file) throws Exception {
		return DataAnalysis.GetEndPoint(file);
	}
	
	public static boolean isEndpointValid(String endpoint) {
		return DataAnalysis.IsValid(endpoint);
	}
	
	public static boolean getConneted (String endpoint) {
		String message = DataAnalysis.GetConnect(endpoint);
		if (message == null || "".equals(message))
			return false;
		return true;
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
