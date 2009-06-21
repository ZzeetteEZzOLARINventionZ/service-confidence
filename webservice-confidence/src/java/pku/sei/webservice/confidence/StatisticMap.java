package pku.sei.webservice.confidence;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;


public class StatisticMap {
	private PrintWriter pw = null;
	public interface Rule {
		public boolean accept(String s);
	}
	
	public Map<String, ArrayList<String>> map;
	public Rule rule;
	public StatisticMap(Rule rule) throws Exception {
		this.map = new HashMap<String, ArrayList<String>>();
		this.rule = rule;
		//pw = new PrintWriter(new FileWriter("data/d.txt"));
	}
	
	public void add(String key, String value) {
		if (!map.containsKey(key))
			map.put(key, new ArrayList<String> ());
		map.get(key).add(value);
	}
	
	public double statistic(String key) {
//		if(key.equals("weather.gov")){
//			//System.out.println("weather.gov");
//		}
		ArrayList<String> values = map.get(key);
		// if (values == null)
			////System.out.println(key);
		if (values.size() == 0)
			return 0.0;
		double num = 0.0;
		for (int i = 0; i < values.size(); i ++)
			if (rule.accept(values.get(i)))
				num += 1.0;
		if(rule instanceof CheckWsdlRule){
			System.out.println("D_"+key+"\t"+num+"\t"+values.size());
		}else if(rule instanceof BufferedEndpointRule){
			System.out.println("E_"+key+"\t"+num+"\t"+values.size());
		}else if(rule instanceof BackLinkRule){
			System.out.println("B_"+key+"\t"+num+"\t"+values.size());
		}
//		pw.flush();
		//return num / values.size();
		return (1- num / values.size());//reliability
	}
}
