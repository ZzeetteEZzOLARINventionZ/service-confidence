import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import pku.sei.webservice.confidence.WsdlFile;

public class Test1 {
	ArrayList<String> invalidEps = new ArrayList<String>();
	HashMap<String, String> ep_status = new HashMap<String, String>();
	public Test1() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader("data/invalidEndPoint.txt"));
		
		String line = null;
		while((line=br.readLine())!=null){
			if(line.trim().length()>0){
				invalidEps.add(line.trim());
			}
		}
		br.close();	
		
		br = new BufferedReader(new FileReader("data2/netStatus.txt"));
		
		line = null;
		while((line=br.readLine())!=null){
			if(line.trim().length()>0){
				String[] ss = line.split("\t");
				if(ss.length==2){
					if(ss[1].trim().length()>0){
						ep_status.put(ss[0].trim(), ss[1].trim());
					}
				}
			}
		}
		br.close();	
	}
	public boolean validEp(String ep){
		boolean flag = true;
		String site = WsdlFile.getDomain(ep);
		for (String s : invalidEps) {
			if(site.contains(s)){
				flag = false;
				break;
			}
		}
		
		ArrayList<String> validCodes = new ArrayList<String>();
		validCodes.add("100");
		validCodes.add("200");
		validCodes.add("301");
		validCodes.add("401");
		validCodes.add("403");
		validCodes.add("500");
		if(flag){
			String code = ep_status.get(ep);
			if(code==null){
				flag = false;
			}else{
				if(!validCodes.contains(code)){
					flag = false;
				}
			}
		}
		return flag;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {	
		
		
		try {
			
			Test1 t = new Test1();
			
			HashMap<String,ArrayList<String>> wsdl_ep = new HashMap<String, ArrayList<String>>();
			
			//BufferedReader br = new BufferedReader(new FileReader("data/CrawledWSUrlFileNew_ËùÓÐµÄwsdl url.txt"));
			BufferedReader br = new BufferedReader(new FileReader("data2/allWsdlFile.txt"));
			HashMap<String, String> url_id = new HashMap<String, String>();
			
			String line = null;
			while((line=br.readLine())!=null){
				if(line.trim().length()>0){
					String[] ss = line.split("\t");
					if(ss.length==2){
						url_id.put(ss[1], ss[0]);
					}
				}
			}
			br.close();			
			
			//br = new BufferedReader(new FileReader("data/endpoints-buffer.txt"));
			br = new BufferedReader(new FileReader("data2/WSDLid_endPointList.txt"));
			line = null;
			while((line=br.readLine())!=null){
				Pattern p = Pattern.compile(".*/(\\d*).wsdl");
				
				if(line.trim().length()>0){
					String[] ss = line.split("\t");
					if(wsdl_ep.get(ss[0])==null){
						ArrayList<String> list = new ArrayList<String>();
						for(int i = 1;i<ss.length;i++){
							if(ss[i].trim().length()>0)
								list.add(ss[i]);
						}
						wsdl_ep.put(ss[0], list);
						
					}
				}
			}
			br.close();
			
			br = new BufferedReader(new FileReader("data2/rankOnlyUseNegative.txt"));
			//br = new BufferedReader(new FileReader("data2/rankWithoutPropagation.txt"));
			line = null;
			while((line=br.readLine())!=null){
				if(url_id.get(line)==null){
					System.out.println(line+"\t"+0);
				}else{
					ArrayList<String> eps = wsdl_ep.get(url_id.get(line));
					if(eps==null||eps.size()==0){
						System.out.println(line+"\t"+0);
					}else{
						if(t.validEp(eps.get(0))){
							System.out.println(line+"\t"+1);
						}else{
							System.out.println(line+"\t"+0);
						}
					}
				}
				
				
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
		
	}
}
