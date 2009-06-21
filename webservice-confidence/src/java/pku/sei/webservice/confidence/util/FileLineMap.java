package pku.sei.webservice.confidence.util;

import java.util.*;
import java.io.*;
import java.net.*;

public class FileLineMap {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6815434923486567619L;

	
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
		int start = s.indexOf("://");
		int end = s.indexOf(':', start + 3);
		if (end > start + 3)
			return s.substring(0, end);
		end = s.indexOf('/', start + 3);
		if (end < 0)
			return s;
		return s.substring(0, end);
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
//				System.out.println("wsdl:\t" + wsdl);
//				for (String s : item.getValue()) {
//					System.out.println("endpoint:\t" + s);
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
