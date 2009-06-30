package pku.sei.webservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.omg.PortableInterceptor.INACTIVE;

import pku.sei.webservice.confidence.WsdlFile;

public class Util {
	/**
	 * 得到所有的endPoint，以便验证其网络连接状况
	 * @param root
	 * @throws IOException
	 */
	public static void getAllEndPointList(String root) throws IOException{
		File f = new File(root+"AllFile");
		String[] wsdls = f.list();
		
		PrintWriter pw =new PrintWriter(new FileWriter(root+"WSDLid_endPointList.txt"));
		for (String wsdl : wsdls) {
			ArrayList<String> eps = DataAnalysis.GetEndPoint(root+"AllFile\\"+wsdl);
			System.out.println(wsdl);
			pw.print(wsdl.substring(0,wsdl.indexOf('.')));
			for (String ep : eps) {
				//pw.println(ep);
				pw.print("\t"+ep);
			}
			pw.println();
		}
		pw.flush();
		pw.close();
	}
	public static void getEPs() throws IOException{
		ArrayList<String> invalidEp = new ArrayList<String>();
		BufferedReader br =new BufferedReader(new FileReader("F:\\资源库集成开发环境eclipse 3.4\\work space\\webservice-confidence\\data\\invalidEndPoint.txt"));
		String line = null;
		while((line=br.readLine())!=null){
			if(line.trim().length()>0)
				invalidEp.add(line.trim());
		}
		
		br =new BufferedReader(new FileReader("G:\\论文\\Reputation of Web Service\\crawled by Liufei\\data2\\data2\\endPointList.txt"));
		ArrayList<String> valid_eps = new ArrayList<String>();
		ArrayList<String> invalid_eps = new ArrayList<String>();
		line = null;
		while((line=br.readLine())!=null){
			if(line.trim().length()>0){
				String domain = WsdlFile.getDomain(line);
				boolean flag = true;
				for (String s : invalidEp) {
					if(domain.contains(s)){
						flag = false;
						break;
					}
				}
				if(flag){
					if(!valid_eps.contains(line)){
						valid_eps.add(line);
					}
				}else{
					if(!invalid_eps.contains(line)){
						invalid_eps.add(line);
					}
				}
			}
		}
		PrintWriter pw = new PrintWriter(new FileWriter("G:\\论文\\Reputation of Web Service\\crawled by Liufei\\data2\\data2\\validEndPointList.txt"));
		for (String s : valid_eps) {
			pw.println(s);
		}
		pw.flush();
		pw.close();
		
		pw = new PrintWriter(new FileWriter("G:\\论文\\Reputation of Web Service\\crawled by Liufei\\data2\\data2\\invalidEndPointList.txt"));
		for (String s : invalid_eps) {
			pw.println(s);
		}
		pw.flush();
		pw.close();
	}
	public static void getDifferentEP() throws IOException{
		ArrayList<String> list = new ArrayList<String>();
		BufferedReader br =new BufferedReader(new FileReader("G:\\论文\\Reputation of Web Service\\crawled by Liufei\\data2\\data2\\WSDLid_endPointList.txt"));
		String line = null;
		while((line=br.readLine())!=null){
			if(line.trim().length()>0){
				String[] ss = line.split("\t");
				if(ss.length>=3){
					ArrayList<String> epl = new ArrayList<String>();
					epl.add(WsdlFile.getDomain(ss[1]));
					for (int i = 2;i<ss.length;i++) {
						if(!epl.contains(WsdlFile.getDomain(ss[i]))){
							epl.add(WsdlFile.getDomain(ss[i]));
							System.out.println(ss[0]+"\t"+WsdlFile.getDomain(ss[1])+"\t"+WsdlFile.getDomain(ss[i]));
							break;
						}							
					}
				}
			}
				
		}
		br.close();
	}
	
	public static void getAvailability() throws IOException{
		
		ArrayList<String> validCodes = new ArrayList<String>();
		validCodes.add("100");
		validCodes.add("200");
		validCodes.add("301");
		validCodes.add("401");
		validCodes.add("403");
		validCodes.add("500");
		
		BufferedReader br =new BufferedReader(new FileReader("data2/serviceScore.txt"));
		String line = null;
		HashMap<String, String> map = new HashMap<String, String>();
		while((line=br.readLine())!=null){
			String[] ss = line.trim().split("\t");
			map.put(ss[0], ss[1]);
		}
		br.close();
		
		br =new BufferedReader(new FileReader("G:\\论文\\Reputation of Web Service\\crawled by Liufei\\调用实验结果\\调用实验结果\\toInvokeWsdl-count.txt"));
		
		line = null;
		ArrayList<String> list = new ArrayList<String>();
		while((line=br.readLine())!=null){
			String[] ss = line.trim().split("\t");
			if(list.contains(ss[1]))
				continue;
			else{
				list.add(ss[1]);
			}
			int count = 0;
			for(int i = 2;i<ss.length;i++){
				String[] ss2 = ss[i].split(":");
				if(validCodes.contains(ss2[0])){
					count+=new Integer(ss2[1]).intValue();
				}
			}
			System.out.println(ss[0]+"\t"+ss[1]+"\t"+count/100.0+"\t"+map.get(ss[0]));
			
		}
		br.close();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//getAllEndPointList("G:\\论文\\Reputation of Web Service\\crawled by Liufei\\data2\\data2\\");
			//getDifferentEP();
			getAvailability();
			//getEPs();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
