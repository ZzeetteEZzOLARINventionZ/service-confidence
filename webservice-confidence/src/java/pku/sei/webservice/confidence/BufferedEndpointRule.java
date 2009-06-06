package pku.sei.webservice.confidence;

import java.util.*;
import java.io.*;

public class BufferedEndpointRule implements StatisticMap.Rule {
	
	public Map<String, Boolean> status;
	
	public void loadBufferedInfo(File file) throws Exception {
		List<List<String>> infos = FileUtil.readFile(file.getCanonicalPath());
		for (List<String> i : infos) {
			if (i.size() != 2)
				return;
			if (i.get(1).equals("true"))
				status.put(i.get(0), true);
			else
				status.put(i.get(0), false);
		}
	}
	
	public void saveInfo() throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter("data/endpoint.buffer.file.txt"));
		for (Map.Entry<String, Boolean> item : status.entrySet()) {
			if (item.getValue().equals(true))
				writer.write(item.getKey() + "\ttrue\n");
			else
				writer.write(item.getKey() + "\tfalse\n");
		}
		writer.close();
	}
	
	public BufferedEndpointRule() throws Exception{
		status = new HashMap<String, Boolean>();
		File bufferFile = new File("data/endpoint.buffer.file.txt");
		if (bufferFile.exists())
			loadBufferedInfo(bufferFile);
	}

	 public boolean accept(String s) {
		 if (status.containsKey(s))
			 return status.get(s);
		 
		 boolean connected = WsdlFile.getConneted(s);
		 status.put(s, connected);
		 if (status.size() % 5 == 0) {
			 try {
				saveInfo();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		 }
		 return connected;
	 }

}
