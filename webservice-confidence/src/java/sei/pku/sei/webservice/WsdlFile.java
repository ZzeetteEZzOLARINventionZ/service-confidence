package sei.pku.sei.webservice;

import java.util.ArrayList;

public class WsdlFile {
	//TODO implement it
	public static ArrayList<String> getWSDLEndpoints(String file) throws Exception {
		ArrayList<String> endpoints = new ArrayList<String>();
		endpoints.add("google.com");
		return endpoints;
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
