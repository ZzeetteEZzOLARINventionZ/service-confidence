package pku.sei.webservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import pku.sei.webservice.confidence.WsdlFile;

public class CheckResult {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			BufferedReader br = new BufferedReader(new FileReader("data/CrawledWSUrlFileNew_ËùÓÐµÄwsdl url.txt"));
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
			
			HashMap<String,ArrayList<String>> wsdl_ep = new HashMap<String, ArrayList<String>>();
			br = new BufferedReader(new FileReader("data/endpoints-buffer.txt"));
			line = null;
			while((line=br.readLine())!=null){
				if(line.trim().length()>0){
					String[] ss = line.split("\t");
					if(wsdl_ep.get(ss[0])==null){
						ArrayList<String> list = new ArrayList<String>();
						for(int i = 1;i<ss.length;i++){
							list.add(ss[i]);
						}
						wsdl_ep.put(ss[0], list);
					}
				}
			}
			br.close();
			
			ArrayList<String> validCode = new ArrayList<String>();
			br = new BufferedReader(new FileReader("data/validCode.txt"));
			line = null;
			while((line=br.readLine())!=null){
				if(line.trim().length()>0){
					validCode.add(line.trim());
				}
			}
			br.close();
			
			PrintWriter pw = new PrintWriter(new FileWriter("data/checkResult.txt"));
			int count = 0;
			br = new BufferedReader(new FileReader("data/rankResult.txt"));
			while((line=br.readLine())!=null){
				String code = null;
				if(line.trim().length()>0){
					String wsdlpath = "data/AllFile/"+url_id.get(line)+".wsdl";
					ArrayList<String> eps = wsdl_ep.get(wsdlpath);
					if(eps==null||eps.size()==0){
						code = "nonexist";
					}else{
						for(String ep:eps){
							String code1 = GetNetStatus.getStatus(ep, 1);
							if(code1==null){
								String code2 = GetNetStatus.getStatus(ep,  2);
								code = code2;
							}else{
								if(code1.trim().equals("400")||code1.trim().equals("405")){
									String code2 = GetNetStatus.getStatus(ep,  2);
									code = code2;
								}else{
									code = code1;
								}
							}
							pw.println(line+"\t"+(code==null?"error":code));
							System.out.println(line+"\t"+(code==null?"error":code));
							count ++;
							if(count%10==0){
								pw.flush();
							}
						}
					}
				}
			}
			br.close();
			pw.flush();
			pw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		

	}

}
