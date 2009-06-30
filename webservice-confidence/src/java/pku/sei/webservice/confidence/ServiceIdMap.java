package pku.sei.webservice.confidence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceIdMap {
	protected Map<String, String> idUrl = new HashMap<String, String> ();	//<id, url>
	protected Map<String, String> urlId = new HashMap<String, String> ();	//<url, id>
	public ServiceIdMap (String file) throws Exception {
		List<List<String>> lines = FileUtil.readFile(file);
		for (List<String> list : lines) {
			idUrl.put(list.get(0), list.get(1));
			urlId.put(list.get(1), list.get(0));
		}
	}
}
