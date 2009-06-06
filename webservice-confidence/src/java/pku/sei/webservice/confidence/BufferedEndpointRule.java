package pku.sei.webservice.confidence;

import java.util.*;
import java.io.*;

public class BufferedEndpointRule implements StatisticMap.Rule {
	
	public Map<String, String> status;
	
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
	
	public BufferedEndpointRule() throws Exception{
		status = new HashMap<String, String>();
		File bufferFile = new File("data/endpoint.buffer.file.txt");
		if (bufferFile.exists())
			loadBufferedInfo(bufferFile);
	}

	 public boolean accept(String s) {
		 if (status.containsKey(s))
			 return "200".equals(status.get(s));
		 
		 String sta = WsdlFile.getConnetedStatus(s);
		 status.put(s, sta);
		 if (status.size() % 5 == 0) {
			 try {
				saveInfo();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		 }
		 return "200".equals(sta);
	 }

}
