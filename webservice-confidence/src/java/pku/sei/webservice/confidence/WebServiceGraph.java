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
			System.out.println(s);
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
		return d;
	}
	
	public int[][] matrix = null;
	public int[][] makeMatrix() {
		int[][] matrix = new int[graph.size()][];
		for (int i = 0; i < matrix.length; i ++)
			matrix[i] = new int[graph.size()];
		
		for (int i = 0; i < graph.size(); i ++) {
			ArrayList<Integer> edges = graph.get(i);
			for (int j = 0; j < edges.size(); j ++) {
				matrix[i][edges.get(j)] += 1;
			}
			break;
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
		
		dHost = new StatisticMap(new CheckWsdlRule());
		
		dBacklink = new StatisticMap(new StatisticMap.Rule() {
			 public boolean accept(String s) {
				 if ("notDownload".equals(s))
					 return true;
				 return false;
			 }
		 });
		
		init();
	}
	
	public void init() throws Exception {
		ServiceIdMap allServiceId = new ServiceIdMap("data/CrawledWSUrlFileNew_所有的wsdl url.txt");
		ServiceIdMap notAvailServiceId = new ServiceIdMap("data/不能下载文件的wsdl_URL.txt");
		BacklinkMap couldBacklink = new BacklinkMap(allServiceId);
		couldBacklink.loadHasIdFile("data/能下载下来文件的wsdlURL的backlink.txt");
		
		BacklinkMap couldNotBacklink = new BacklinkMap(allServiceId);
		couldNotBacklink.loadNotHasIdFile("data/不能下载下文件的wsdlURL的backlinkFile.txt");
		
		int error1Count = 0;
		System.out.println(allServiceId.idUrl.size());
		for (Map.Entry<String, String> pair : allServiceId.idUrl.entrySet()) {
			String id = pair.getKey();
			String wsdlFile = "data/AllFile/" + id + ".wsdl";
			if (!new File(wsdlFile).exists())
				continue;
			System.out.println("wsdlFile:\t" + wsdlFile);
			ArrayList<String> endpoints = WsdlFile.getWSDLEndpoints(wsdlFile);
			System.out.println("\tgeted endpoint");
			String url = pair.getValue();
			String domain = WsdlFile.getDomain(url);
			ArrayList<String> backlinks = new ArrayList<String>();
			// System.out.println(id);
			// System.out.println(url);
			if (!notAvailServiceId.idUrl.containsKey(id)) {
				backlinks = couldBacklink.urlBacklink.get(url);
				if (backlinks == null) {
					// System.err.println("error:\t" + url);
					backlinks = new ArrayList<String> ();
					error1Count ++;
				}
			}
			this.addNode("D_" + domain);
			this.dHost.add(domain, wsdlFile);
			// System.out.println("\tsfdsadfasfasf");
			
			for (String endpoint : endpoints) {
				if (WsdlFile.isEndpointValid(endpoint)) {
					this.addNode("E_" + WsdlFile.getDomain(endpoint));
					this.addEdge("E_" + WsdlFile.getDomain(endpoint), "D_" + domain);
					this.dEndpoint.add(WsdlFile.getDomain(endpoint), endpoint);
				}
			}
			for (String backlink : backlinks) {
				this.addNode("B_" + WsdlFile.getDomain(backlink));
				this.addEdge("D_" + domain, "B_" + WsdlFile.getDomain(backlink));
				this.dBacklink.add(WsdlFile.getDomain(backlink), "download");
			}
			
		}
		
		System.out.println("add other backlink");
		//将没有下载下来wsdl的backlink加进去
		for (Map.Entry<String, ArrayList<String>> pair : couldNotBacklink.urlBacklink.entrySet()) {
			ArrayList<String> backlinks = pair.getValue();
			for (String backlink : backlinks) {
				this.dBacklink.add(WsdlFile.getDomain(backlink), "notDownload");
			}
		}
		
		// graph.saveGraph("data/graph.txt");
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
	
	public ArrayList<String> getOutput(String src) {
		int id = this.urlId.get(src);
		if (this.matrix == null)
			this.matrix = makeMatrix();
		ArrayList<String> result = new ArrayList<String>();
		for (int j = 0; j < this.matrix[id].length; j ++) {
			if (this.matrix[id][j] != 0) {
				result.add(this.idUrl.get(j));
			}
		}
		return result;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		WebServiceGraph graph = new WebServiceGraph();
		
		System.out.println("make matrix");
		
		int[][] matrix = graph.makeMatrix();
		double[] d = graph.calculateD();
		
		String src = graph.idUrl.get(0);
		ArrayList<String> dsts = graph.getOutput(src);
		for (String dst : dsts) {
			System.out.println("src: " + src + " -> dst: " + dst);
		}
		
		graph.saveGraph("data/abc.txt");
	}

}
