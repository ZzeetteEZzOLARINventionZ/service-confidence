package pku.sei.webservice.confidence.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class FileLineMap {
	public static Map<String, String> loadFileMap(String file) throws Exception {
		Map<String, String> results = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = reader.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);
			String key = st.nextToken();
			String value = st.nextToken();
			results.put(key, value);
		}
		reader.close();
		return results;
		
	}
	
	public static Set<String> loadFileSet(String file) throws Exception {
		Set<String> results = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = reader.readLine()) != null) {
			results.add(line);
		}
		reader.close();
		return results;
		
	}
	
	public static Map<String, Set<String>> loadFileMapSet(String file) throws Exception {
		Map<String, Set<String>> results = new HashMap<String, Set<String>>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = reader.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);
			String key = st.nextToken();
			Set<String> values = new HashSet<String> ();
			while (st.hasMoreTokens()) {
				values.add(st.nextToken());
			}
			results.put(key, values);
		}
		reader.close();
		return results;
		
	}
	
	public static String getSiteFromUrl(String s){
		// System.out.println(s);
		int start = s.indexOf("://");
		int end = s.indexOf(':', start + 3);
		String site = null;
		if (end > start + 3)
			site = s.substring(0, end);
		if (end < 0)
			end = s.indexOf('/', start + 3);
		if (end < 0)
			site = s;
		else
			site = s.substring(0, end);
		int c = 2;
		for (int i = site.length() - 1; i >= 0; i --) {
			if (site.charAt(i) == '.') {
				c --;
				if (c == 0)
					return site.substring(i + 1);
			}
		}
		return site;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		/**
		 * 统计：
		 * 1. 含有多个endpoint的wsdl的个数
		 * 2. 没有endpoint的wsdl的个数
		 * 3. 含有一个endpoint的wsdl的个数
		 * 4. 自身的endpoint不属于同一个site的个数
		 * 5. 自身的endpoint属于同一个site的个数
		 * 
		 * 6. wsdl的site不属于wsdl的endpoint的site的个数
		 * 7. wsdl的site属于wsdl的endpoint的site的个数
		 * 8. wsdl的endpoint只有1个且和wsdl都是同一个site的个数
		 * 
		 */
		Map<String, String> wsdls = loadFileMap("data2/allWsdlFile.txt");
		Map<String, Set<String>> wsdlEndpoints = loadFileMapSet("data2/WSDLid_endPointList.txt");
		endpointNumberCount(wsdlEndpoints);
		accrossCount(wsdls, wsdlEndpoints);
		

		Set<String> invalidEndpoints = loadFileSet("data2/invalidEndPointList.txt");
		
		//统计合法的endpoint的情况
		accrossValidCount(wsdls, wsdlEndpoints, invalidEndpoints);
		// printSuffix("data2/allWsdlFile.txt");

	}

	public static void accrossCount(Map<String, String> wsdls,
			Map<String, Set<String>> wsdlEndpoints) {
		/** 
		 * 6. 含有endpoint，且wsdl的site不属于wsdl的endpoint的site的个数
		 * 7. wsdl的site属于wsdl的endpoint的site的个数
		 * 8. wsdl的endpoint只有1个且和wsdl都是同一个site的个数
		 */
		
		int differentSiteCount = 0;
		int inSiteCount = 0;
		int sameSiteCount = 0;
		
		Set<String> countSite = new HashSet<String> ();
		for (Map.Entry<String, Set<String>> item : wsdlEndpoints.entrySet()) {
			Iterator<String> iter = item.getValue().iterator();
			while (iter.hasNext()) {
				String s = null;
				String n = iter.next();
				s = getSiteFromUrl(n);
				countSite.add(s);
			}
			String wsdl = wsdls.get(item.getKey());
			String site = getSiteFromUrl(wsdl);
			if (countSite.contains(site))
				inSiteCount ++;
			else if (countSite.size() > 0){
				differentSiteCount ++;
				// XXX 去掉注释可以看不一样的wsdl的site和endpoint的site
//				System.out.println("wsdl:\t" + wsdl);
//				for (String s : item.getValue()) {
//					System.out.println("epoint:\t" + s);
//				}
			}
			
			if (countSite.contains(site) && countSite.size() == 1)
				sameSiteCount ++;
			
			countSite.clear();
		}
		
		System.out.println("-------------------------------------------");
		System.out.println("含有endpoint，且wsdl的site不属于wsdl的endpoint的site的个数:\t" + differentSiteCount);
		System.out.println("wsdl的site属于wsdl的endpoint的site的个数:\t" + inSiteCount);
		System.out.println("wsdl的endpoint只有1个且和wsdl都是同一个site的个数:\t" + sameSiteCount);
		System.out.println("-------------------------------------------");
	}
	
	public static void accrossValidCount(Map<String, String> wsdls,
			Map<String, Set<String>> wsdlEndpoints, Set<String> invalidEndpoints) {
		
		/** 
		 * 将不合法的endpoint去掉，不统计
		 */
		
		int differentSiteCount = 0;
		int inSiteCount = 0;
		int sameSiteCount = 0;
		
		Set<String> countSite = new HashSet<String> ();
		for (Map.Entry<String, Set<String>> item : wsdlEndpoints.entrySet()) {
			Iterator<String> iter = item.getValue().iterator();
			while (iter.hasNext()) {
				String s = null;
				String n = iter.next();
				if (invalidEndpoints.contains(n))
					continue;
				s = getSiteFromUrl(n);
				countSite.add(s);
			}
			String wsdl = wsdls.get(item.getKey());
			String site = getSiteFromUrl(wsdl);
			if (countSite.contains(site))
				inSiteCount ++;
			else if (countSite.size() > 0){
				differentSiteCount ++;
				// XXX 去掉注释可以看不一样的wsdl的site和endpoint的site
				System.out.println("wsdl:\t" + wsdl);
				for (String s : item.getValue()) {
					System.out.println("epoint:\t" + s); 
				}
			}
			
			if (countSite.contains(site) && countSite.size() == 1)
				sameSiteCount ++;
			
			countSite.clear();
		}
		
		System.out.println();
		System.out.println("-------------------------------------------");
		System.out.println("以下的结果中只计算了合法的wsdl");
		System.out.println("含有endpoint，且wsdl的site不属于wsdl的endpoint的site的个数:\t" + differentSiteCount);
		System.out.println("wsdl的site属于wsdl的endpoint的site的个数:\t" + inSiteCount);
		System.out.println("wsdl的endpoint只有1个且和wsdl都是同一个site的个数:\t" + sameSiteCount);
		System.out.println("-------------------------------------------");
	}

	public static void endpointNumberCount(Map<String, Set<String>> wsdlEndpoints){
		/**
		 * 0. 总数
		 * 1. 含有多个endpoint的wsdl的个数
		 * 2. 没有endpoint的wsdl的个数
		 * 3. 含有一个endpoint的wsdl的个数
		 * 4. 自身的endpoint不属于同一个site的个数
		 * 5. 自身的endpoint属于同一个site的个数
		 */
		int countAll = 0;
		int countMultiEndpoint = 0;
		int countNullEndpoint = 0;
		int countSingleEndpoint = 0;
		
		int countMultiEndponitSite = 0;
		int countSingleEndponitSite = 0;
		Set<String> countSite = new HashSet<String> ();
		for (Map.Entry<String, Set<String>> item : wsdlEndpoints.entrySet()) {
			if (item.getValue().size() == 0) 
				countNullEndpoint ++;
			else if (item.getValue().size() == 1) 
				countSingleEndpoint ++;
			else if (item.getValue().size() > 1) 
				countMultiEndpoint ++;
			else {
				System.out.println(item.getKey());
			}
			Iterator<String> iter = item.getValue().iterator();
			while (iter.hasNext()) {
				String s = null;
				String n = iter.next();
				s = getSiteFromUrl(n);
				countSite.add(s);
				
			}
			if (countSite.size() > 1)
				countMultiEndponitSite ++;
			if (countSite.size() == 1 && item.getValue().size() > 1)
				countSingleEndponitSite ++;
			
			countSite.clear();
			
			countAll ++;
		}
		System.out.println("-------------------------------------------");
		System.out.println("endpointFile中总数为:\t" + countAll);
		System.out.println("含有多个endpoint的wsdl的个数:\t" + countMultiEndpoint);
		System.out.println("没有endpoint的wsdl的个数:\t" + countNullEndpoint);
		System.out.println("含有一个endpoint的wsdl的个数:\t" + countSingleEndpoint);
		System.out.println("-------------------------------------------");
		System.out.println("自身的endpoint不属于同一个site的个数:\t" + countMultiEndponitSite);
		System.out.println("自身的endpoint属于同一个site的个数:\t" + countSingleEndponitSite);
	}

}
