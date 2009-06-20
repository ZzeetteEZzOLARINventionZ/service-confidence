package pku.sei.webservice;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pku.sei.webservice.confidence.WsdlFile;
import sun.java2d.pipe.SpanClipRenderer;

public class GetWSEDB {
	/**
	 * @param args
	 */
	public static void main(String[] args) {	

		
		try {
			HashMap<String,ArrayList<String>> wsdl_backlink = new HashMap<String, ArrayList<String>>();
			HashMap<String,ArrayList<String>> wsdl_ep = new HashMap<String, ArrayList<String>>();
			HashMap<String, String> wsdl_domain = new HashMap<String, String>();
			
			BufferedReader br = new BufferedReader(new FileReader("data/CrawledWSUrlFileNew_���е�wsdl url.txt"));
			HashMap<String, String> id_url = new HashMap<String, String>();
			String line = null;
			while((line=br.readLine())!=null){
				if(line.trim().length()>0){
					String[] ss = line.split("\t");
					if(ss.length==2){
						id_url.put(ss[0], ss[1]);
						wsdl_domain.put(ss[1], WsdlFile.getDomain(ss[1]));					
					}
				}
			}
			br.close();			
			
			br = new BufferedReader(new FileReader("data/endpoints-buffer.txt"));
			line = null;
			while((line=br.readLine())!=null){
				Pattern p = Pattern.compile(".*/(\\d*).wsdl");
				
				if(line.trim().length()>0){
					String[] ss = line.split("\t");
					if(wsdl_ep.get(ss[0])==null){
						ArrayList<String> list = new ArrayList<String>();
						for(int i = 1;i<ss.length;i++){
							list.add(WsdlFile.getDomain(ss[i]));
						}
						Matcher m = p.matcher(ss[0]);
						if(m.find()){
							wsdl_ep.put(id_url.get(m.group(1)), list);
						}else{
							System.out.println("");
						}
						
					}
				}
			}
			br.close();
			
			br = new BufferedReader(new FileReader("data/�����������ļ���wsdlURL��backlink.txt"));
			line = null;
			while((line=br.readLine())!=null){
				if(line.trim().length()>0){
					String[] ss = line.split("\t");
					if(wsdl_backlink.get(ss[0])==null){
						ArrayList<String> list = new ArrayList<String>();
						for(int i = 1;i<ss.length;i++){
							list.add(WsdlFile.getDomain(ss[i]));
						}
						wsdl_backlink.put(id_url.get(ss[0]), list);
					}
				}
			}
			br.close();
			
			br = new BufferedReader(new FileReader("data/�����������ļ���wsdlURL��backlinkFile.txt"));
			line = null;
			while((line=br.readLine())!=null){
				if(line.trim().length()>0){
					String[] ss = line.split("\t");
					if(wsdl_backlink.get(ss[0])==null){
						ArrayList<String> list = new ArrayList<String>();
						for(int i = 1;i<ss.length;i++){
							list.add(WsdlFile.getDomain(ss[i]));
						}
						wsdl_backlink.put(ss[0], list);
					}
				}
			}
			br.close();
			
			HashMap<String, Double> map = new HashMap<String, Double>();
			br = new BufferedReader(new FileReader("data/ReputationRank.txt"));
			line = null;
			while((line=br.readLine())!=null){
				if(line.trim().length()>0){
					String[] ss = line.split("\t");
					//map.put(ss[0], new Double(ss[1]).doubleValue()/new Double(ss[2]).doubleValue());
					map.put(ss[1], new Double(ss[2]).doubleValue());
				}
			}
			br.close();
			
			int total = 0;
			int withep_total = 0;
			int c = 0;
			for (Map.Entry<String, String> pair :wsdl_domain.entrySet()) {
				
				String wsdl = pair.getKey();
				String domain = pair.getValue();
				
				ArrayList<String> eps = wsdl_ep.get(wsdl);
				ArrayList<String> bls = wsdl_backlink.get(wsdl);
				
				String endpoint = null;
				total ++;
				if(eps!=null){
					if(eps.size()>0){
						boolean flag = false;
						for(String ep:eps){
							if(WsdlFile.isEndpointValid(ep)){
								endpoint = ep;
								flag = true;
								break;
							}
						}
						if(flag){
							withep_total++;
							if(compare(endpoint, domain)){
								c++;
							}
						}
					}
				}
				double epscore = 0.0;
				double blscore = 0.0;
				//String dmsocre = map.get("D_"+domain)==null?("D_"+domain+":NaN"):(domain+":"+map.get("D_"+domain));
				double dmscore = 0.0;
				if(map.get("D_"+domain)!=null){
					dmscore = map.get("D_"+domain);
				}else
					dmscore = -1;
				
				if(bls==null||bls.size()==0){
					blscore = -1;
				}else{
					for (int i = 0;i<bls.size();i++) {
						String bl = bls.get(i);
						if(map.get("B_"+bl)!=null){
							//blscore += "B_"+bl+":"+map.get("B_"+bl)+",";
							blscore += map.get("B_"+bl);
						}else{
							//blscore += "B_"+bl+":NaN,";
							blscore += 0.0;
						}
					}
					blscore = blscore/bls.size()/Math.sqrt(bls.size());
				}
				
				if(eps==null||eps.size()==0){
					epscore = -1;
				}else{
					for (int i = 0;i<eps.size();i++) {
						String ep = eps.get(i);
						if(map.get("E_"+ep)!=null){
							//epscore += "E_"+ep+":"+map.get("E_"+ep)+",";
							epscore += map.get("E_"+ep);
						}else{
							//epscore += "E_"+ep+":NaN,";
							epscore += 0.0;
						}
					}
					epscore = epscore/eps.size()/Math.sqrt(eps.size());
					
				}
				
				System.out.println(wsdl+"\t"+epscore+"\t"+dmscore+"\t"+blscore);
			}
			System.out.println(total+"\t"+withep_total+"\t"+c);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
		
	}
	public static boolean compare(String s1, String s2){
		String s3 = s1.toLowerCase();
		String s4 = s2.toLowerCase();
		String s5 = s3.substring(s3.indexOf('.'));
		String s6 = s4.substring(s4.indexOf('.'));
		return s5.equals(s6);
	}
}