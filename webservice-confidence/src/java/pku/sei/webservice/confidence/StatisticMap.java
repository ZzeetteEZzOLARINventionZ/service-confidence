package pku.sei.webservice.confidence;

import java.util.*;

public class StatisticMap {
	
	public interface Rule {
		public boolean accept(String s);
	}
	
	public Map<String, ArrayList<String>> map;
	public Rule rule;
	public StatisticMap(Rule rule) {
		this.map = new HashMap<String, ArrayList<String>>();
		this.rule = rule;
	}
	
	public void add(String key, String value) {
		if (!map.containsKey(key))
			map.put(key, new ArrayList<String> ());
		map.get(key).add(value);
	}
	
	public double statistic(String key) {
		ArrayList<String> values = map.get(key);
		if (values.size() == 0)
			return 0.0;
		double num = 0.0;
		for (int i = 0; i < values.size(); i ++)
			if (rule.accept(values.get(i)))
				num += 1.0;
		return num / values.size();
	}
}
