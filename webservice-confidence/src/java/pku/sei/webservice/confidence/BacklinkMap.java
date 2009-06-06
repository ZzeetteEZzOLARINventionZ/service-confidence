package pku.sei.webservice.confidence;

import java.util.*;

public class BacklinkMap {
	public Map<String, ArrayList<String>> urlBacklink;
	public ServiceIdMap urlIdMap;
	public BacklinkMap(ServiceIdMap map) throws Exception {
		urlBacklink = new HashMap<String, ArrayList<String>>();
		urlIdMap = map;
	}
	public void loadHasIdFile(String file) throws Exception {
		List<List<String>> lines = FileUtil.readFile(file);
		for (List<String> list : lines) {
			String id = list.get(0);
			String url = urlIdMap.idUrl.get(id);
			ArrayList<String> backlinks = new ArrayList<String> ();
			for (int i = 1; i < list.size(); i ++)
				backlinks.add(list.get(i));
			urlBacklink.put(url, backlinks);
		}
	}
	
	public void loadNotHasIdFile(String file) throws Exception {
		List<List<String>> lines = FileUtil.readFile(file);
		for (List<String> list : lines) {
			String url = list.get(0);
			ArrayList<String> backlinks = new ArrayList<String> ();
			for (int i = 1; i < list.size(); i ++)
				backlinks.add(list.get(i));
			urlBacklink.put(url, backlinks);
		}
	}
}
