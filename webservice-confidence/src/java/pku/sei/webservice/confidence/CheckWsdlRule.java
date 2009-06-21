package pku.sei.webservice.confidence;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import pku.sei.webservice.DataAnalysis;
import sun.font.EAttribute;

public class CheckWsdlRule implements StatisticMap.Rule {
	
	private HashMap<String, ArrayList<String>> wsdl_end = new HashMap<String, ArrayList<String>>();
	//private ArrayList<String> endlist = new ArrayList<String>();
	BufferedEndpointRule ber = new BufferedEndpointRule();//算法2
	public CheckWsdlRule(){
		try {
			BufferedReader br = new BufferedReader(new FileReader("data/endpoints-buffer.txt"));
			String line = null;
			while((line=br.readLine())!=null){
				if(line.trim().length()>0){
					String[] ss = line.split("\t");
					ArrayList<String> list = new ArrayList<String>();
					for(int i = 1;i<ss.length;i++){
						if(ss[i].trim().length()>0){
							if(!list.contains(ss[i].trim()))
								list.add(ss[i]);
//							if(WsdlFile.isEndpointValid(WsdlFile.getDomain(ss[i].trim()))){
//								//if(!endlist.contains(ss[i].trim())){
//									endlist.add(ss[i]);
//								//}
//							}
						}
					}
					wsdl_end.put(ss[0], list);
				}
			}
			br.close();
//			PrintWriter pw = new PrintWriter(new FileWriter("data/newlist_redundent.txt"));
//			for (String e : endlist) {
//				pw.println(e);
//			}
//			pw.flush();
//			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	// TODO 验证wsdl的是否合法，

	/**
	 * @param file
	 *            待验证的wsdl的文件路径
	 * @return false如果wsdl是不合法的  true 如果wsdl是合法的
	 */
	public boolean accept(String file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			boolean tag = false;
			int count = 0;
			while ((line = br.readLine()) != null) {
				if (count++ > 100)
					break;
				if (line.trim().length() > 0) {// get the first none-empty line
					if (line.startsWith("<?xml version=")
							|| line.startsWith("<wsdl:definitions")
							|| line.startsWith("<definitions")
							|| line.startsWith("<description xmlns")) {
						tag = true;
						break;
					}
				}
			}
			br.close();
			if(tag){
				ArrayList<String> endPoints = wsdl_end.get(file);
				if(endPoints==null){
					endPoints = DataAnalysis.GetEndPoint(file);
				}
				if(endPoints.size()>0){
					int i = 0;
					for (i = 0;i<endPoints.size();i++) {
						if(WsdlFile.isEndpointValid(WsdlFile.getDomain(endPoints.get(i)))){
							if(ber.accept(endPoints.get(i))){//算法2
								tag = true;//合法
								break;
							}//算法2
						}
					}
					if(i == endPoints.size()){
						tag = false;
					}					
				}else{
					tag = false;
				}
			}
			//if (!tag);
				//System.out.println("invalid wsdl file:" + file + "\r\n    "+ line);
			return tag;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public static void main(String[] args){
		CheckWsdlRule cr = new CheckWsdlRule();
	}
}
