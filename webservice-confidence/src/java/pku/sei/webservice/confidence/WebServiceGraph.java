package pku.sei.webservice.confidence;

import java.util.*;
import java.io.*;

public class WebServiceGraph {
	
	public Map<String, Integer> urlId;
	
	public ArrayList<ArrayList<Integer> > graph;
	public ArrayList<String> idUrl;
	
	public void saveGraph(String file) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for (int i = 0; i < graph.size(); i ++) {
			String line = "" + i + ":\t";
			ArrayList<Integer> edges = graph.get(i);
			for (int j = 0; j < edges.size(); j ++)
				line = line + edges.get(j) + "\t";
			writer.write(line);
			writer.newLine();
			
		}
		writer.close();
	}
	
	//TODO implement it
	public double[] calculateD() {
		double[] d = new double[graph.size()];
		for (int i = 0; i < graph.size(); i ++) {
			String s = idUrl.get(i);
			String key = s.substring(2);
			if (s.startsWith("E_")) {
				d[i] = dEndpoint.statistic(key);
			}
			else if (s.startsWith("D_")) {
				d[i] = dHost.statistic(key);
			}
			else if (s.startsWith("B_")) {
				d[i] = dBacklink.statistic(key);
			}
		}
		return null;
	}
	
	public int[][] makeMatrix() {
		int[][] matrix = new int[graph.size()][];
		for (int i = 0; i < matrix.length; i ++)
			matrix[i] = new int[graph.size()];
		
		for (int i = 0; i < graph.size(); i ++) {
			ArrayList<Integer> edges = graph.get(i);
			for (int j = 0; j < edges.size(); j ++)
				matrix[i][j] = 1;
		}
		
		return matrix;
	}
	
	public StatisticMap dEndpoint;
	public StatisticMap dHost;
	public StatisticMap dBacklink;
	
	public WebServiceGraph() throws Exception {
		urlId = new HashMap<String, Integer>();
		graph = new ArrayList<ArrayList<Integer>>();
		idUrl = new ArrayList<String>();
		
		dEndpoint = new StatisticMap(new BufferedEndpointRule());
		
		dHost = new StatisticMap(new StatisticMap.Rule() {
			 public boolean accept(String s) {
				 return false;
			 }
		 });
		
		dBacklink = new StatisticMap(new StatisticMap.Rule() {
			 public boolean accept(String s) {
				 if ("download".equals(s))
					 return true;
				 return false;
			 }
		 });
	}
	
	public void addNode(String node) {
		if (urlId.containsKey(node))
			return;
		urlId.put(node, graph.size());
		graph.add(new ArrayList<Integer>());
		idUrl.add(node);
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
		System.out.println(allServiceId.idUrl.size());
		for (Map.Entry<String, String> pair : allServiceId.idUrl.entrySet()) {
			String id = pair.getKey();
			String wsdlFile = "data/AllFile/" + id + ".wsdl";
			if (!new File(wsdlFile).exists())
				continue;
			ArrayList<String> endpoints = WsdlFile.getWSDLEndpoints(wsdlFile);
			String url = pair.getValue();
			String domain = WsdlFile.getDomain(url);
			ArrayList<String> backlinks = new ArrayList<String>();
			// System.out.println(id);
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
			graph.dHost.add(domain, wsdlFile);
			
			for (String endpoint : endpoints) {
				if (WsdlFile.isEndpointValid(endpoint)) {
					graph.addNode("E_" + endpoint);
					graph.addEdge("E_" + endpoint, "D_" + domain);
					graph.dEndpoint.add(WsdlFile.getDomain(endpoint), endpoint);
				}
			}
			for (String backlink : backlinks) {
				graph.addNode("B_" + backlink);
				graph.addEdge("D_" + domain, "B_" + backlink);
				graph.dBacklink.add(backlink, "download");
			}
			
		}
		
		System.out.println("add other backlink");
		//将没有下载下来wsdl的backlink加进去
		for (Map.Entry<String, ArrayList<String>> pair : couldNotBacklink.urlBacklink.entrySet()) {
			ArrayList<String> backlinks = pair.getValue();
			for (String backlink : backlinks) {
				graph.dBacklink.add(backlink, "notDownload");
			}
		}
		
		// graph.saveGraph("data/graph.txt");
		System.out.println("make matrix");
		int[][] matrix = graph.makeMatrix();
		double[] d = graph.calculateD();
		// System.out.println("在有backlink里和没有backlink里都找不到的url的个数：" + error1Count);
	}

}
