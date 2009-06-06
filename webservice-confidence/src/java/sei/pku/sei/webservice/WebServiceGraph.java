package sei.pku.sei.webservice;

import java.util.*;

public class WebServiceGraph {
	
	public Map<Integer, String> idUrl;
	public Map<String, Integer> urlId;
	
	public ArrayList<ArrayList<Integer> > graph;
	
	
	public WebServiceGraph() {
		idUrl = new HashMap<Integer, String>();
		urlId = new HashMap<String, Integer>();
		graph = new ArrayList<ArrayList<Integer>>();
	}
	
	public void addNode(String node) {
		idUrl.put(graph.size(), node);
		urlId.put(node, graph.size());
		graph.add(new ArrayList<Integer>());
	}
	
	public void addEdge(String src, String dst) {
		int srcId = urlId.get(src);
		int dstId = urlId.get(dst);
		graph.get(srcId).add(dstId);
	}
	
	
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		ServiceIdMap allServiceId = new ServiceIdMap("data/CrawledWSUrlFileNew_所有的wsdl url.txt");
		ServiceIdMap notAvailServiceId = new ServiceIdMap("data/不能下载文件的wsdl_URL.txt");
		BacklinkMap couldBacklink = new BacklinkMap(allServiceId);
		couldBacklink.loadHasIdFile("能下载下来文件的wsdlURL的backlink.txt");
		
		BacklinkMap couldNotBacklink = new BacklinkMap(allServiceId);
		couldNotBacklink.loadNotHasIdFile("不能下载下文件的wsdlURL的backlinkFile.txt");
		
		
	}

}
