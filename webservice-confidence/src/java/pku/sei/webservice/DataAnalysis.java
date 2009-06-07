package pku.sei.webservice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.net.*;
import java.nio.Buffer;
public class DataAnalysis {

	public static void Process() throws SecurityException, IOException {
		String fileName = "E:\\Code program\\WSCrawler\\WSInfoData\\CrawledWSUrlFileNew.txt";
		HashMap<String, String> Hash = new HashMap<String, String>();
		Vector vec = new Vector();
		FileOperation.ReadFileToVector1(fileName, vec);
		for (int i = 0; i < vec.size(); i++) {
			String line = (String) vec.get(i);
			String[] array = line.split("\t");
			String id = array[0].toString();
			String url = array[1].toString();
			Hash.put(id, url);
		}

		String file1 = "E:\\Code program\\WSCrawler\\WSInfoData\\AllFinalWS.txt";
		vec = new Vector();
		FileOperation.ReadFileToVector1(file1, vec);
		for (int i = 0; i < vec.size(); i++) {
			String line = (String) vec.get(i);
			int idx1 = line.lastIndexOf("\\");
			int idx2 = line.lastIndexOf(".wsdl");
			String id = line.substring(idx1 + 1, idx2);
			//System.out.println(id);
			String url = Hash.get(id);
			FileOperation
					.WriteToFileNew(
							"E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\CrawledFile.txt",
							id + "\t" + url + "\r\n", true);
		}
	}

	public static void EndPoint() {
		String File2 = "D:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\EndPoint.txt";
		HashMap<String, String> Hash2 = new HashMap<String, String>();
		GetHash_Domain(File2, Hash2);

		String file1 = "D:\\Code program\\WSCrawler\\WSInfoData\\AllFinalWS.txt";
		Vector vec = new Vector();
		FileOperation.ReadFileToVector1(file1, vec);
		for (int i = 0; i < vec.size(); i++) {
			String line = (String) vec.get(i);
			int idx1 = line.lastIndexOf("\\");
			int idx2 = line.lastIndexOf(".wsdl");
			String id = line.substring(idx1 + 1, idx2);
			//System.out.println(id);
			String EndPoint = Hash2.get(id);
			if (EndPoint == null || IsValid(EndPoint) == false)
				continue;
			try {
				FileOperation
						.WriteToFileNew(
								"D:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\ValidUrl.txt",
								id + "\r\n", true);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void Expe() {
		ArrayList<String> list = new ArrayList<String>();
		// String file1 = "D:\\Code program\\WSCrawler\\WSInfoData\\For
		// 2946\\0415_NoDocument_Static_0.3.txt";
		String file1 = "D:\\Code program\\WSCrawler\\WSInfoData\\For0303\\Static_0421.txt";

		Vector vec = new Vector();
		FileOperation.ReadFileToVector1(file1, vec);
		for (int i = 0; i < vec.size(); i++) {
			String line = (String) vec.get(i);
			String[] array = line.split("\t");
			String id = array[0].trim();
			//System.out.println(id);
			list.add(id);
		}
		String file2 = "D:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\ValidUrl.txt";
		vec = new Vector();
		FileOperation.ReadFileToVector1(file2, vec);
		int count = 0;
		for (int i = 0; i < vec.size(); i++) {
			String line = (String) vec.get(i);
			if (list.contains(line.trim())) {
				count++;
			}
		}
		//System.out.println(count);
	}

	public static void Process1() throws SecurityException, IOException {
		String file1 = "D:\\WSFile_ForICWS\\WS_UpdateYear_Final.txt";
		Vector vec = new Vector();
		FileOperation.ReadFileToVector1(file1, vec);
		for (int i = 0; i < vec.size(); i++) {
			String line1 = (String) vec.get(i);
			String[] array = line1.split("\t");
			String line = array[0];
			int idx1 = line.lastIndexOf("\\");
			int idx2 = line.lastIndexOf(".txt");
			// int idx2 = line.lastIndexOf(".wsdl");
			String id = line.substring(idx1 + 1, idx2);
			//System.out.println(id);
			FileOperation
					.WriteToFileNew(
							"E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\UpdateYear.txt",
							id + "\t" + array[1] + "\r\n", true);
		}
	}

	public static void GetHash(String fileName, HashMap<String, String> Hash) {
		Vector vec = new Vector();
		FileOperation.ReadFileToVector1(fileName, vec);
		for (int i = 0; i < vec.size(); i++) {
			String line1 = (String) vec.get(i);
			String[] array = line1.split("\t");
			Hash.put(array[0], array[1]);
		}
	}

	public static void WriteValidWSUrl() {
		String WSFile = "D:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\CrawledFile.txt";
		HashMap<String, String> IdHash = new HashMap<String, String>();
		Vector vec = new Vector();
		FileOperation.ReadFileToVector1(WSFile, vec);
		for (int i = 0; i < vec.size(); i++) {
			String line = (String) vec.get(i);
			String[] array = line.split("\t");

			String url = array[1].toString();
			IdHash.put(array[0], url);
		}

		String file2 = "D:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\ValidUrl.txt";
		vec = new Vector();
		FileOperation.ReadFileToVector1(file2, vec);
		for (int i = 0; i < vec.size(); i++) {
			String line = (String) vec.get(i);
			String Url = IdHash.get(line.trim());
			try {
				FileOperation
						.WriteToFileNew(
								"D:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\ValidUrl0425.txt",
								line + "\t" + Url + "\r\n", true);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void GetHash_Domain(String fileName,
			HashMap<String, String> Hash) {
		Vector vec = new Vector();
		FileOperation.ReadFileToVector1(fileName, vec);
		for (int i = 0; i < vec.size(); i++) {
			String line = (String) vec.get(i);
			String[] array = line.split("\t");

			String url = array[1].toString();
			int idx1 = url.indexOf("//");
			url = url.substring(idx1 + 2);
			int idx2 = url.indexOf("/");
			String domain = "";
			if (idx2 != -1)
				domain = url.substring(0, idx2);
			else
				domain = url;
			Hash.put(array[0], domain);
		}
	}

	public static void GetHash_Domain1(String fileName,
			HashMap<String, String> Hash, ArrayList<String> list) {
		Vector vec = new Vector();
		FileOperation.ReadFileToVector1(fileName, vec);
		for (int i = 0; i < vec.size(); i++) {
			String line = (String) vec.get(i);
			String[] array = line.split("\t");

			String url = array[1].toString();
			int idx1 = url.indexOf("//");
			url = url.substring(idx1 + 2);
			int idx2 = url.indexOf("/");
			String domain = "";
			if (idx2 != -1)
				domain = url.substring(0, idx2);
			else
				domain = url;

			if (!list.contains(domain)) {
				list.add(domain);
			}
			Hash.put(array[0], domain);
		}
	}

	public static void Statics() {
		String WSFile = "E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\CrawledFile.txt";
		HashMap<String, String> Hash1 = new HashMap<String, String>();
		GetHash_Domain(WSFile, Hash1);

		String File2 = "E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\EndPoint.txt";
		HashMap<String, String> Hash2 = new HashMap<String, String>();
		GetHash_Domain(File2, Hash2);

		// Description
		String File3 = "E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\DescriptionCount.txt";
		HashMap<String, String> Hash3 = new HashMap<String, String>();
		GetHash(File3, Hash3);

		// Operation Number
		String File4 = "E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\OperationCount.txt";
		HashMap<String, String> Hash4 = new HashMap<String, String>();
		GetHash(File4, Hash4);

		// File Size
		String File5 = "E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\FileSizeCount.txt";
		HashMap<String, String> Hash5 = new HashMap<String, String>();
		GetHash(File5, Hash5);

		// net Status
		String File6 = "E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\ActiveNetStatus.txt";
		HashMap<String, String> Hash6 = new HashMap<String, String>();
		GetHash(File6, Hash6);

		// Modify time
		String File7 = "E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\UpdateYear.txt";
		HashMap<String, String> Hash7 = new HashMap<String, String>();
		GetHash(File7, Hash7);

		Set<String> keys = Hash1.keySet();
		Iterator ite = keys.iterator();
		while (ite.hasNext()) {
			String id = (String) ite.next();
			String UrlDomain = Hash1.get(id);
			String EndPointDomain = Hash2.get(id);
			if (EndPointDomain == null)
				EndPointDomain = "";
			String Description = Hash3.get(id);
			if (Description == null)
				Description = "0";
			String OpeCount = Hash4.get(id);
			if (OpeCount == null)
				OpeCount = "0";
			String FileSize = Hash5.get(id);
			if (FileSize == null)
				FileSize = "0";
			String NetStatus = Hash6.get(id);
			if (NetStatus == null)
				NetStatus = "0";
			String ModifyYear = Hash7.get(id);
			if (ModifyYear == null)
				ModifyYear = "Unknown";
			try {
				FileOperation
						.WriteToFileNew(
								"E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\0318Static.txt",
								id + "\t" + UrlDomain + "\t" + EndPointDomain
										+ "\t" + Description + "\t" + OpeCount
										+ "\t" + FileSize + "\t" + NetStatus
										+ "\t" + ModifyYear + "\r\n", true);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public static void Statics0331() {
		ArrayList<String> list = new ArrayList();
		String WSFile = "E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\CrawledFile.txt";
		HashMap<String, String> Hash1 = new HashMap<String, String>();
		// GetHash_Domain(WSFile,Hash1,list);
		GetHash_Domain1(WSFile, Hash1, list);

		String File2 = "E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\EndPoint.txt";
		HashMap<String, String> Hash2 = new HashMap<String, String>();
		GetHash_Domain(File2, Hash2);

		// Description
		/*
		 * String File3 = "E:\\Code
		 * program\\WSCrawler\\WSInfoData\\0316Expe\\DescriptionCount.txt";
		 * HashMap<String,String> Hash3 = new HashMap<String,String>();
		 * GetHash(File3,Hash3);
		 * 
		 *  // Operation Number String File4 = "E:\\Code
		 * program\\WSCrawler\\WSInfoData\\0316Expe\\OperationCount.txt";
		 * HashMap<String,String> Hash4 = new HashMap<String,String>();
		 * GetHash(File4,Hash4);
		 *  // File Size String File5 = "E:\\Code
		 * program\\WSCrawler\\WSInfoData\\0316Expe\\FileSizeCount.txt"; HashMap<String,String>
		 * Hash5 = new HashMap<String,String>(); GetHash(File5,Hash5);
		 *  // net Status String File6 = "E:\\Code
		 * program\\WSCrawler\\WSInfoData\\0316Expe\\ActiveNetStatus.txt";
		 * HashMap<String,String> Hash6 = new HashMap<String,String>();
		 * GetHash(File6,Hash6);
		 *  // Modify time String File7 = "E:\\Code
		 * program\\WSCrawler\\WSInfoData\\0316Expe\\UpdateYear.txt"; HashMap<String,String>
		 * Hash7 = new HashMap<String,String>(); GetHash(File7,Hash7);
		 */

		HashMap<String, HashMap<String, String>> mHash = new HashMap<String, HashMap<String, String>>();

		Set<String> keys = Hash1.keySet();
		Iterator ite = keys.iterator();
		ArrayList<String> list1 = new ArrayList<String>();
		while (ite.hasNext()) {
			String id = (String) ite.next();
			String UrlDomain = Hash1.get(id);
			String EndPointDomain = Hash2.get(id);
			if (EndPointDomain == null)
				EndPointDomain = "";
			else {
				/*
				 * if(!UrlDomain.equals(EndPointDomain)&&
				 * EndPointDomain.indexOf("localhost")==-1)
				 * if(list.contains(EndPointDomain)&&
				 * !list1.contains(EndPointDomain)) list1.add(EndPointDomain);
				 */
				/*
				 * try { if(list.contains(EndPointDomain)&&
				 * !list1.contains(EndPointDomain)) list1.add(EndPointDomain);
				 * //FileOperation.WriteToFileNew("E:\\Code
				 * program\\WSCrawler\\WSInfoData\\0316Expe\\0331Static.txt", //
				 * UrlDomain+"\t"+EndPointDomain+"\r\n",true); } catch
				 * (SecurityException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); } catch (IOException e) { // TODO
				 * Auto-generated catch block e.printStackTrace(); }
				 */
			}

			if (!mHash.containsKey(UrlDomain)) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(EndPointDomain, "1");
				mHash.put(UrlDomain, map);
			} else {
				HashMap<String, String> map = mHash.get(UrlDomain);
				if (!map.containsKey(EndPointDomain))
					map.put(EndPointDomain, "1");
				else {
					String temp = map.get(EndPointDomain);
					int iValue = Integer.parseInt(temp);
					iValue++;
					map.remove(EndPointDomain);
					map.put(EndPointDomain, "" + iValue);
				}
				mHash.remove(UrlDomain);
				mHash.put(UrlDomain, map);
			}
			/*
			 * String Description = Hash3.get(id); if(Description == null)
			 * Description = "0"; String OpeCount = Hash4.get(id); if(OpeCount ==
			 * null) OpeCount = "0"; String FileSize = Hash5.get(id);
			 * if(FileSize == null) FileSize = "0"; String NetStatus =
			 * Hash6.get(id); if(NetStatus == null) NetStatus = "0"; String
			 * ModifyYear = Hash7.get(id); if(ModifyYear == null) ModifyYear =
			 * "Unknown"; try { FileOperation.WriteToFileNew( "E:\\Code
			 * program\\WSCrawler\\WSInfoData\\0316Expe\\0318Static.txt",
			 * id+"\t"+UrlDomain+"\t"+EndPointDomain +"\t"+Description+"\t"+
			 * OpeCount+"\t"+FileSize+ "\t"+NetStatus+"\t"+ModifyYear+"\r\n",
			 * true); } catch (SecurityException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); } catch (IOException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 */

		}
		// System.out.println(list1.size());
		//System.out.println(mHash.size());

		ArrayList<String> validList = new ArrayList<String>();
		int number = 0;
		Set<String> mkeys = mHash.keySet();
		ite = mkeys.iterator();
		while (ite.hasNext()) {
			String UrlDomain = (String) ite.next();
			HashMap<String, String> map = mHash.get(UrlDomain);
			// System.out.println(map.size());
			if (IsValid(map)) {
				/*
				 * try { FileOperation.WriteToFileNew("E:\\Code
				 * program\\WSCrawler\\WSInfoData\\0316Expe\\0407_AllValid.txt",
				 * UrlDomain+"\r\n", true); } catch (SecurityException e) { //
				 * TODO Auto-generated catch block e.printStackTrace(); } catch
				 * (IOException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); }
				 */
				// WriteMapToFile(map,"E:\\Code
				// program\\WSCrawler\\WSInfoData\\0316Expe\\0407_AllValid.txt");
				validList.add(UrlDomain);
				number++;
			} else {
				/*
				 * try { FileOperation.WriteToFileNew("E:\\Code
				 * program\\WSCrawler\\WSInfoData\\0316Expe\\0407_AllNoValid.txt",
				 * UrlDomain+"\r\n", true); } catch (SecurityException e) { //
				 * TODO Auto-generated catch block e.printStackTrace(); } catch
				 * (IOException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); }
				 */
				// WriteMapToFile(map,"E:\\Code
				// program\\WSCrawler\\WSInfoData\\0316Expe\\0407_AllNoValid.txt");
				number++;
			}
			// Add localhost judgement
		}
		//System.out.println(number);

		Set<String> keys2 = Hash1.keySet();
		Iterator ite2 = keys2.iterator();
		ArrayList<String> list2 = new ArrayList<String>();
		while (ite2.hasNext()) {
			String id = (String) ite2.next();
			String UrlDomain = Hash1.get(id);
			String EndPointDomain = Hash2.get(id);
			if (EndPointDomain == null)
				EndPointDomain = "";
			else {
				if (!UrlDomain.equals(EndPointDomain)
						&& EndPointDomain.indexOf("localhost") == -1)
					// if(list.contains(EndPointDomain)&&
					// !list1.contains(EndPointDomain)&&
					// !validList.contains(EndPointDomain))
					// list1.add(EndPointDomain);
					try {
						if (list.contains(EndPointDomain)
								&& !list1.contains(EndPointDomain)
								&& !validList.contains(EndPointDomain)) {
							list1.add(EndPointDomain);
							FileOperation
									.WriteToFileNew(
											"E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\0407Static.txt",
											UrlDomain + "\t" + EndPointDomain
													+ "\r\n", true);
						}
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
	}

	// 判断endPoint是否能够连接
	// 输入：WSDL文档中对应的 endpoint地址
	// 返回：true false
	public boolean IsConnect(String endPoint, String ResultFile)
			throws IOException {

		String url = endPoint;
		if (url == null)
			return false;
		int pos = url.indexOf("http://");
		String temp = url;
		if (pos != -1) {

			temp = temp.substring(pos);
			//System.out.println(temp);
			String str = GetConnect(temp);
			//System.out.println(str);
			if (str.length() != 0) {
				FileOperation.WriteToFileNew(ResultFile, str + "\r\n", true);
				return true;
			} else {
				return false;

			}
		} else {
			return false;
		}
	}

	// 判断是否能够连接
	// 输入：WSDL文档中对应的 endpoint地址
	// 返回：true false
	public boolean TestStatus(String url, String ResultFile) throws IOException {

		// String url = GetEndPoint(fileName);

		if (url == null)
			return false;
		int pos = url.indexOf("http://");
		String temp = url;
		if (pos != -1) {

			temp = temp.substring(pos);
			System.out.println(temp);
			String str = GetConnect(temp);
			System.out.println(str);
			if (str.length() != 0) {
				FileOperation.WriteToFileNew(ResultFile, str + "\r\n", true);
				return true;
			} else {
				return false;

			}
		} else {
			return false;
		}
	}

	// 发送SOAP消息到输入的地址，并记录返回消息
	public static String GetConnect(String strUrl) {
		// TODO Auto-generated method stub
		Socket client;
		BufferedOutputStream sender;
		BufferedInputStream receiver;
		/*
		 * String postMessage= "<?xml version='1.0' encoding='UTF-8'?>\r\n" + "<SOAP-ENV:Envelope
		 * xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"
		 * xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\"
		 * xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">\r\n" + "<SOAP-ENV:Body>\r\n" + "<ns1:doGoogleSearch
		 * xmlns:ns1=\"urn:GoogleSearch\"
		 * SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n" + "<key
		 * xsi:type=\"xsd:string\">N3dRZHVQFHJNUgQUfFLSQ3QliZNacow+</key>\r\n" + "<q xsi:type=\"xsd:string\">miscrosoft</q>\r\n" + "<start
		 * xsi:type=\"xsd:int\">0</start>\r\n" + "<maxResults
		 * xsi:type=\"xsd:int\">10</maxResults>\r\n" + "<filter
		 * xsi:type=\"xsd:boolean\">true</filter>\r\n" + "<restrict
		 * xsi:type=\"xsd:string\"></restrict>\r\n" + "<safeSearch
		 * xsi:type=\"xsd:boolean\">false</safeSearch>\r\n" + "<lr
		 * xsi:type=\"xsd:string\"></lr>\r\n" + "<ie xsi:type=\"xsd:string\"></ie>\r\n" + "<oe
		 * xsi:type=\"xsd:string\"></oe>\r\n" + "</ns1:doGoogleSearch>\r\n" + "</SOAP-ENV:Body>\r\n" + "</SOAP-ENV:Envelope>";
		 */

		String postMessage = "<?xml version='1.0' encoding='UTF-8'?>\r\n"
				+ "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">\r\n"
				+ "<SOAP-ENV:Body>\r\n" + "</SOAP-ENV:Body>\r\n"
				+ "</SOAP-ENV:Envelope>";

		String message = new String();

		// String strUrl = "http://www.oakleaf.ws/alpharpc/alpharpc.asmx?wsdl" ;
		// String strUrl = "http://api.google.com/search/beta2" ;
		// String strUrl = "http://www.google.com" ;
		try {

			URL url = new URL(strUrl);
			String path = url.getPath();
			if (path == "") {
				path = "/";
			}

			
			 String content = "POST " + path +" HTTP/1.1\r\nHost:" +
			 url.getHost() + "\r\nUser-Agent:socket/1.1\r\nContent-Type: text/xml; charset=utf-8\r\n" + "Content-length:" +
			 postMessage.length() + "\r\nConnection:Close\r\n\r\n" +
			 postMessage ;
			
//			String content = "GET "
//					+ path
//					+ " HTTP/1.1\r\nHost:"
//					+ url.getHost()
//					+ "\r\nUser-Agent:socket/1.1\r\nContent-Type: text/xml; charset=utf-8\r\n"
//					+ "Content-length:" + postMessage.length()
//					+ "\r\nConnection:Close\r\n\r\n" + postMessage;

			// System.out.println(content);
			// System.out.println("===================");
			InetAddress address = InetAddress.getByName(url.getHost());
			if (url.getPort() == -1)
				client = new Socket(address, 80);
			else
				client = new Socket(address, url.getPort());
			client.setSoTimeout(120000);

			sender = new BufferedOutputStream(client.getOutputStream());
			receiver = new BufferedInputStream(client.getInputStream());
			sender.write(content.getBytes(), 0, content.length());
			sender.flush();
			int ret = 1;

			byte buffer[] = new byte[4096];

			while ((ret = receiver.read(buffer, 0, 4096)) != -1) {

				message += new String(buffer, 0, ret);

			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("**************************");

		}
		// System.out.println(message);
		String[] array = message.split("\r\n");
		int length = 11;
		if (array.length < length)
			length = array.length;
		String str = "";
		for (int i = 0; i < length; i++)
			str += array[i] + "\r\n";
		// return message;
		return str;
	}

	public static boolean IsValid(HashMap<String, String> map) {
		boolean flag = true;
		Set<String> mkeys = map.keySet();
		Iterator ite = mkeys.iterator();
		while (ite.hasNext()) {
			String EndPointDomain = (String) ite.next();
			if (EndPointDomain.trim().length() == 0) {
				flag = false;
				break;
			}
			if (EndPointDomain.toLowerCase().indexOf("localhost") != -1) {
				flag = false;
				break;
			}
			if (EndPointDomain.toLowerCase().indexOf("192.168") != -1) {
				flag = false;
				break;
			}
			if (EndPointDomain.toLowerCase().indexOf("xxx") != -1) {
				flag = false;
				break;
			}
			if (EndPointDomain.toLowerCase().indexOf("127.0.0.1") != -1) {
				flag = false;
				break;
			}
			if (EndPointDomain.toLowerCase().indexOf(".") == -1) {
				flag = false;
				break;
			}
		}
		return flag;
	}
	/**
	 * 判断endPoint是否合法
	 * @param EndPointDomain
	 * @return
	 */
	public static boolean IsValid(String EndPointDomain) {
		//System.out.println("+++check:"+EndPointDomain);
		if (EndPointDomain.trim().length() < 7) {
			return false;
		}
		if(EndPointDomain.trim().indexOf('.')==-1){
			return false;
		}
		ArrayList<String> invalidEndpoint = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("data/invalidEndPoint.txt"));
			String line = null;
			while((line=br.readLine())!=null){
				invalidEndpoint.add(line);
			}	
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (String end : invalidEndpoint) {
			if(EndPointDomain.toLowerCase().trim().indexOf(end)!=-1){
				//System.out.println(end);
				//System.out.println("invalid");
				return false;
			}
		}
		return true;
	}

	public static void WriteMapToFile(HashMap<String, String> map,
			String fileName) {
		Set<String> mkeys = map.keySet();
		Iterator ite = mkeys.iterator();
		while (ite.hasNext()) {
			String EndPointDomain = (String) ite.next();
			String times = map.get(EndPointDomain);
			try {
				FileOperation.WriteToFileNew(fileName, EndPointDomain + ":"
						+ times + "\t", true);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			FileOperation.WriteToFileNew(fileName, "\r\n", true);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void Compute() throws SecurityException, IOException {
		String fileName = "E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\Domain_Static_Crawled.txt";
		HashMap<String, String> Hash = new HashMap<String, String>();
		Vector vec = new Vector();
		FileOperation.ReadFileToVector1(fileName, vec);
		for (int i = 0; i < vec.size(); i++) {
			String line = (String) vec.get(i);
			String[] array = line.split("\t");
			String domain = array[0].toString();
			String count = array[1].toString();
			Hash.put(domain, count);
		}

		String fileName1 = "E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\Domain_Static.txt";
		HashMap<String, String> HashAll = new HashMap<String, String>();
		vec = new Vector();
		FileOperation.ReadFileToVector1(fileName1, vec);
		for (int i = 0; i < vec.size(); i++) {
			String line = (String) vec.get(i);
			String[] array = line.split("\t");
			String domain = array[0].toString();
			String count = array[1].toString();
			HashAll.put(domain, count);
		}

		Set<String> keys = HashAll.keySet();
		Iterator ite = keys.iterator();
		while (ite.hasNext()) {
			String My_domain = (String) ite.next();
			if (!Hash.containsKey(My_domain)) {
				FileOperation
						.WriteToFileNew(
								"E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\Domain_Static_Coverage.txt",
								My_domain + "\t0\r\n", true);
				continue;
			}
			String count_1 = Hash.get(My_domain);
			String count_2 = HashAll.get(My_domain);
			double c1 = Double.parseDouble(count_1);
			double c2 = Double.parseDouble(count_2);
			double coverage = c1 / c2;
			FileOperation
					.WriteToFileNew(
							"E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\Domain_Static_Coverage.txt",
							My_domain + "\t" + coverage + "\r\n", true);

		}
	}

	public static void DomainAnalysis(String fileName)
			throws SecurityException, IOException {
		HashMap<String, String> Hash = new HashMap<String, String>();

		Vector vec = new Vector();
		FileOperation.ReadFileToVector1(fileName, vec);
		for (int i = 0; i < vec.size(); i++) {
			String line = (String) vec.get(i);
			String[] array = line.split("\t");
			String url = array[1].toString();
			int idx1 = url.indexOf("//");
			url = url.substring(idx1 + 2);
			int idx2 = url.indexOf("/");
			String domain = "";
			if (idx2 != -1)
				domain = url.substring(0, idx2);
			else
				domain = url;
			// System.out.println(domain);

			if (!Hash.containsKey(domain)) {
				String num = "1";
				Hash.put(domain, num);
			} else {
				int count = Integer.parseInt(Hash.get(domain));
				count++;
				Hash.remove(domain);
				Hash.put(domain, "" + count);
			}
		}

		//System.out.println(Hash.size());
		Set<String> keys = Hash.keySet();

		Iterator ite = keys.iterator();
		while (ite.hasNext()) {
			String My_domain = (String) ite.next();
			String count_1 = Hash.get(My_domain);
			FileOperation
					.WriteToFileNew(
							"E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\DomainEndPoint_Static_Crawled.txt",
							My_domain + "\t" + count_1 + "\r\n", true);

		}
	}

	public static ArrayList<String> GetEndPoint(String fileName) throws IOException {
		FileInputStream fstream = new FileInputStream(fileName);
		DataInputStream in = new DataInputStream(fstream);
		String text = "";

		while (in.available() != 0) {
			text += in.readLine();
		}
		
		ArrayList<String> resultList = new ArrayList<String>();
		int start = 0;
		while(true){
			//get endpoint iteratively
			int pos_soap = text.toLowerCase().indexOf("<soap:address",start);
			int pos_http = text.toLowerCase().indexOf("<http:address",start);
			int pos_wsdlsoap = text.toLowerCase().indexOf("<wsdlsoap:address",start);
			int pos_soap12 = text.toLowerCase().indexOf("<soap12:address",start);
			int pos_soapenv = text.toLowerCase().indexOf("<soapenv:address",start);
			int pos = Integer.MAX_VALUE;
			if(pos_soap!=-1&&pos_soap<pos)
				pos = pos_soap;
			if(pos_http!=-1&&pos_http<pos)
				pos = pos_http;
			if(pos_wsdlsoap!=-1&&pos_wsdlsoap<pos)
				pos = pos_wsdlsoap;
			if(pos_soap12!=-1&&pos_soap12<pos)
				pos = pos_soap12;
			if(pos_soapenv!=-1&&pos_soapenv<pos)
				pos = pos_soapenv;
			if(pos==Integer.MAX_VALUE)
				pos = -1;
			if(pos == -1)
				break;

			start = pos+1;//for next loop
			String temp = text.substring(pos);
			pos = temp.indexOf("location");
			if (pos == -1)
				continue;
			temp = temp.substring(pos);
			int pos_https = temp.indexOf("https://");
			int pos_http2 = temp.indexOf("http://");
			
			pos = Integer.MAX_VALUE;
			if(pos_https!=-1&&pos_https<pos)
				pos = pos_https;
			if(pos_http2!=-1&&pos_http2<pos)
				pos = pos_http2;
			if(pos==Integer.MAX_VALUE)
				pos = -1;
			
			if (pos == -1) {
					continue;
			}
			temp = temp.substring(pos);
			pos = temp.indexOf("\"");
			int pos_double = temp.indexOf("\"");
			int pos_single = temp.indexOf("'");
			pos = Integer.MAX_VALUE;
			if(pos_double!=-1&&pos_double<pos)
				pos = pos_double;
			if(pos_single!=-1&&pos_single<pos)
				pos = pos_single;
			if(pos == Integer.MAX_VALUE)
				pos = -1;
			
			if (pos == -1) {
					continue;
			}
			temp = temp.substring(0, pos);
			
			resultList.add(temp);//add endpoint to list
		}//end of while
		
		return resultList;
	}

	public static void ComputeEndPoint() {
		// File file = new File("E:\\Code
		// program\\WSCrawler\\WSInfoData\\AllFile");
		// File []files = file.listFiles();

		Vector vec = new Vector();
		FileOperation
				.ReadFileToVector1(
						"E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\AllWSFileLeft.txt",
						vec);
		// for(int i=0;i<files.length;i++)
		for (int i = 0; i < vec.size(); i++) {
			/*
			 * File mFile = files[i];
			 * //System.out.println(mFile.getAbsolutePath()); String WSFile =
			 * mFile.getName(); int idx = WSFile.indexOf(".wsdl"); String id =
			 * WSFile.substring(0,idx); System.out.println(id);
			 * //System.out.println(mFile.getName());
			 * 
			 * 
			 * try { FileOperation.WriteToFileNew("E:\\Code
			 * program\\WSCrawler\\WSInfoData\\0316Expe\\AllWSFile.txt",
			 * mFile.getAbsolutePath()+"\r\n", true); } catch (SecurityException
			 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
			 * catch (IOException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 */
			String WSFile = (String) vec.get(i);
			int idx1 = WSFile.lastIndexOf("\\");
			int idx2 = WSFile.lastIndexOf(".wsdl");
			String id = WSFile.substring(idx1 + 1, idx2);
			System.out.println(id);
			try {
				ArrayList<String> endPoints = GetEndPoint(WSFile);
				//System.out.println(endPoint);
				for (String ep : endPoints) {
					if (ep != null)
						FileOperation
								.WriteToFileNew(
										"E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\EndPoint.txt",
										id + "\t" + ep + "\r\n", true);
					}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public static void Process3() {
		String file = "E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\EndPoint.txt";
		HashMap<String, String> Hash = new HashMap<String, String>();
		GetHash(file, Hash);

		Vector vec = new Vector();
		FileOperation
				.ReadFileToVector1(
						"E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\AllWSFile.txt",
						vec);
		for (int i = 0; i < vec.size(); i++) {
			String line = (String) vec.get(i);
			int idx1 = line.lastIndexOf("\\");
			int idx2 = line.lastIndexOf(".wsdl");
			String id = line.substring(idx1 + 1, idx2);
			if (!Hash.containsKey(id)) {
				try {
					FileOperation
							.WriteToFileNew(
									"E:\\Code program\\WSCrawler\\WSInfoData\\0316Expe\\AllWSFileLeft.txt",
									line + "\r\n", true);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void ComputeIntersection(String inFile1, String inFile2,
			String outFile) {
		Vector vec = new Vector();
		try {
			FileOperation.ReadFileToVector(inFile1, vec);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < vec.size(); i++) {
			String line = (String) vec.get(i);
			int idx1 = line.lastIndexOf("\\");
			int idx2 = line.lastIndexOf(".");
			String id = line.substring(idx1 + 1, idx2);
			System.out.println(id);
			list.add(id);
		}
		vec = new Vector();
		try {
			FileOperation.ReadFileToVector(inFile2, vec);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < vec.size(); i++) {
			String line = (String) vec.get(i);
			String[] array = line.split("\t");
			String id = array[0];
			if (!list.contains(id))
				try {
					FileOperation.WriteToFileNew(outFile, line + "\r\n", true);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}

	public static void main(String[] args) throws SecurityException,
			IOException {

		// DataAnalysis.DomainAnalysis("E:\\Code
		// program\\WSCrawler\\WSInfoData\\CrawledWSUrlFileNew.txt");
		// DataAnalysis.DomainAnalysis("E:\\Code
		// program\\WSCrawler\\WSInfoData\\0316Expe\\CrawledFile.txt");
		// DataAnalysis.DomainAnalysis("E:\\Code
		// program\\WSCrawler\\WSInfoData\\0316Expe\\EndPoint.txt");
		// DataAnalysis.Process();
		// DataAnalysis.Process1();
		// DataAnalysis.Process3();
		// DataAnalysis.Statics();
		// DataAnalysis.Statics0331();
		// DataAnalysis.EndPoint();
		// DataAnalysis.Expe();
		// DataAnalysis.WriteValidWSUrl();
		// DataAnalysis.ComputeIntersection("D:\\Code
		// program\\WSCrawler\\WSInfoData\\AllFinalWS.txt",
		// "D:\\Code program\\WSCrawler\\WSInfoData\\CrawledWSUrlFileNew.txt",
		// "D:\\Code
		// program\\WSCrawler\\WSInfoData\\CrawledWSUrlFileNew0601.txt");
		/*
		 * HashMap<String,String> map = new HashMap<String,String>();
		 * map.put("a", "1"); map.put("b", "2");
		 * 
		 * String str = map.get("c"); System.out.println(str); if(str==null)
		 * System.out.println("asdf");
		 */

		// DataAnalysis.Compute();
		// DataAnalysis.ComputeEndPoint();
		/*
		 * System.out.println( DataAnalysis.GetEndPoint("E:\\Code
		 * program\\WSCrawler\\WSInfoData\\AllFile\\558.wsdl"));
		 */
		BufferedReader br = new BufferedReader(new FileReader("data/301302.txt"));
		String line = null;
		while((line=br.readLine())!=null){
			String msg = DataAnalysis.GetConnect(line);
			Pattern p = Pattern.compile("<a href=\"(.*)\">");
			Matcher m = p.matcher(msg.toLowerCase());
			String new_url = null;
			if(m.find()){
				new_url = m.group(1);				
			}
			if(new_url==null){
				//String msg = "Location: https://adwords.google.com/api/adwords/v11/CriterionService\r\nx";
				Pattern np = Pattern.compile("location:[\\s](.*)\\s");
				Matcher nm = np.matcher(msg.toLowerCase());
				if(nm.find()){
					new_url = nm.group(1);	
					//System.out.println(new_url);
				}
			}
			if(new_url!=null){
				//System.out.println("----URL:"+line+"\r\n----New URL:"+new_url);
				String result = DataAnalysis.GetConnect(new_url);
				if(result!=null&&result.length()>0){
					//System.out.println(result);
					Pattern cp = Pattern.compile("http/1.1\\s(\\d*)\\s.*");
					Matcher cm = cp.matcher(result.toLowerCase());
					if(cm.find()){
						System.out.println(line+"\t"+cm.group(1));
					}
				}
			}
		}
		
//		System.out.println(IsValid("http://www.ncbi.nlm.nih.gov/entrez/eutils/soap/soap_adapter.cgi"));
	}
}
