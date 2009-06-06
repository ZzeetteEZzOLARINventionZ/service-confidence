package sei.pku.sei.webservice;

import java.util.*;

public class ServiceIdMap {
	protected Map<String, String> idUrl = new HashMap<String, String> ();	//<id, url>
	protected Map<String, String> urlId = new HashMap<String, String> ();	//<url, id>
	public ServiceIdMap (String file) throws Exception {
		List<List<String>> lines = FileUtil.readFile(file);
		for (List<String> list : lines) {
			idUrl.put(list.get(0), list.get(1));
			idUrl.put(list.get(1), list.get(0));
		}
	}
}
