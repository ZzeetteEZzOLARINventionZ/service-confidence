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
		 * ͳ�ƣ�
		 * 1. ���ж��endpoint��wsdl�ĸ���
		 * 2. û��endpoint��wsdl�ĸ���
		 * 3. ����һ��endpoint��wsdl�ĸ���
		 * 4. �����endpoint������ͬһ��site�ĸ���
		 * 5. �����endpoint����ͬһ��site�ĸ���
		 * 
		 * 6. wsdl��site������wsdl��endpoint��site�ĸ���
		 * 7. wsdl��site����wsdl��endpoint��site�ĸ���
		 * 8. wsdl��endpointֻ��1���Һ�wsdl����ͬһ��site�ĸ���
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
		 * 6. ����endpoint����wsdl��site������wsdl��endpoint��site�ĸ���
		 * 7. wsdl��site����wsdl��endpoint��site�ĸ���
		 * 8. wsdl��endpointֻ��1���Һ�wsdl����ͬһ��site�ĸ���
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
		System.out.println("����endpoint����wsdl��site������wsdl��endpoint��site�ĸ���:\t" + differentSiteCount);
		System.out.println("wsdl��site����wsdl��endpoint��site�ĸ���:\t" + inSiteCount);
		System.out.println("wsdl��endpointֻ��1���Һ�wsdl����ͬһ��site�ĸ���:\t" + sameSiteCount);
		System.out.println("-------------------------------------------");
		
		
	}

	public static void endpointNumberCount(Map<String, Set<String>> wsdlEndpoints){
		/**
		 * 0. ����
		 * 1. ���ж��endpoint��wsdl�ĸ���
		 * 2. û��endpoint��wsdl�ĸ���
		 * 3. ����һ��endpoint��wsdl�ĸ���
		 * 4. �����endpoint������ͬһ��site�ĸ���
		 * 5. �����endpoint����ͬһ��site�ĸ���
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
		System.out.println("endpointFile������Ϊ:\t" + countAll);
		System.out.println("���ж��endpoint��wsdl�ĸ���:\t" + countMultiEndpoint);
		System.out.println("û��endpoint��wsdl�ĸ���:\t" + countNullEndpoint);
		System.out.println("����һ��endpoint��wsdl�ĸ���:\t" + countSingleEndpoint);
		System.out.println("-------------------------------------------");
		System.out.println("�����endpoint������ͬһ��site�ĸ���:\t" + countMultiEndponitSite);
		System.out.println("�����endpoint����ͬһ��site�ĸ���:\t" + countSingleEndponitSite);
	}

}
