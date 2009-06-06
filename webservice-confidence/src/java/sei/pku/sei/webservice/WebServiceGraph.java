package sei.pku.sei.webservice;

import java.util.*;
import java.io.*;

public class WebServiceGraph {
	
	public Map<Integer, String> idUrl;
	public Map<String, Integer> urlId;
	
	public ArrayList<ArrayList<Integer> > graph;
	
	public void saveGraph(String file) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for (int i = 0; i < graph.size(); i ++) {
			String line = "" + i + ":\t";
			ArrayList<Integer> edges = new ArrayList<Integer>();
			for (int j = 0; j < edges.size(); j ++)
				line = line + edges.get(i) + "\t";
			writer.write(line);
			writer.newLine();
			
		}
		writer.close();
	}
	
	
	public WebServiceGraph() {
		idUrl = new HashMap<Integer, String>();
		urlId = new HashMap<String, Integer>();
		graph = new ArrayList<ArrayList<Integer>>();
	}
	
	public void addNode(String node) {
		if (urlId.containsKey(node))
			return;
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
		couldBacklink.loadHasIdFile("data/能下载下来文件的wsdlURL的backlink.txt");
		
		BacklinkMap couldNotBacklink = new BacklinkMap(allServiceId);
		couldNotBacklink.loadNotHasIdFile("data/不能下载下文件的wsdlURL的backlinkFile.txt");
		
		WebServiceGraph graph = new WebServiceGraph();
		
		int error1Count = 0;
		for (Map.Entry<String, String> pair : allServiceId.idUrl.entrySet()) {
			String id = pair.getKey();
			String wsdlFile = "data/AllFile/" + id + ".wsdl";
			ArrayList<String> endpoints = WsdlFile.getWSDLEndpoints(wsdlFile);
			String url = pair.getValue();
			String domain = WsdlFile.getDomain(url);
			ArrayList<String> backlinks = new ArrayList<String>();
			System.out.println(id);
			System.out.println(url);
			if (!notAvailServiceId.idUrl.containsKey(id)) {
				backlinks = couldBacklink.urlBacklink.get(url);
				if (backlinks == null) {
					// System.err.println("error:\t" + url);
					backlinks = new ArrayList<String> ();
					error1Count ++;
				}
			}
			graph.addNode("D_" + domain);
			for (String endpoint : endpoints) {
				graph.addNode("P_" + endpoint);
				graph.addEdge("P_" + endpoint, "D_" + domain);
			}
			for (String backlink : backlinks) {
				graph.addNode("B_" + backlink);
				graph.addEdge("D_" + domain, "B_" + backlink);
			}
			
			graph.saveGraph("data/graph.txt");
			
		}
		// System.out.println("在有backlink里和没有backlink里都找不到的url的个数：" + error1Count);
	}

}
