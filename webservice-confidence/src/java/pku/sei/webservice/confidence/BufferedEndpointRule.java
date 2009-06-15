package pku.sei.webservice.confidence;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class BufferedEndpointRule implements StatisticMap.Rule {
	
	public Map<String, String> status;
	
	public Map<String, String> newStatus = new HashMap<String, String>();//for 301,302
	
	public ArrayList<String> validList = new ArrayList<String>();
	
	public void loadBufferedInfo(File file) throws Exception {
		List<List<String>> infos = FileUtil.readFile(file.getCanonicalPath());
		for (List<String> i : infos) {
			if (i.size() != 2)
				return;
			status.put(i.get(0), i.get(1));
		}
	}
	
	public void saveInfo() throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter("data/endpoint.buffer.file.txt"));
		for (Map.Entry<String, String> item : status.entrySet()) {
			writer.write(item.getKey() + "\t" + item.getValue() + "\n");
		}
		writer.close();
	}
	
	public BufferedEndpointRule(){
		try{
			status = new HashMap<String, String>();
			File bufferFile = new File("data/endpoint.buffer.file.txt");
			if (bufferFile.exists())
				loadBufferedInfo(bufferFile);
			
			init301301();//for 301,302
			
			initValidList();//valid net status code
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	public void initValidList() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader("data/validCode.txt"));
		String line = null;
		while((line=br.readLine())!=null){
			validList.add(line.trim());
		}
		br.close();
	}
	public void init301301() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader("data/301302.txt"));
		String line = null;
		while((line=br.readLine())!=null){
			String[] ss = line.split("\t");
			if(ss.length==2){
				newStatus.put(ss[0], ss[1]);
			}
		}
		br.close();
	}
	
	public boolean isValid(String code){
		return validList.contains(code);
	}
	
	 public boolean accept(String s) {
		 if (status.containsKey(s)){
			 String code = status.get(s);
			 if(!("301".equals(code)||"302".equals(code))){
				 return isValid(code);
			 }else{
				code = newStatus.get(s);
				if(code==null){
					return false;//新文件中没有说明不可用
				}else{
					return isValid(code);
				}
			 }
		 }
		 
		 String sta = WsdlFile.getConnetedStatus(s);
		 status.put(s, sta);
		 if (status.size() % 5 == 0) {
			 try {
				saveInfo();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		 }
		 return isValid(sta);
	 }

}
