package pku.sei.webservice;

import java.io.*;
import java.util.*;

import pku.sei.webservice.confidence.*;

public class TestDataAnalysis {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		File folder = new File("data/AllFile");
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i ++) {
			//List<String> endpoints = DataAnalysis.GetEndPoint(files[i].getAbsolutePath());
			String path = files[i].getAbsolutePath();
			String id = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
			List<String> endpoints = WsdlFile.getWSDLEndpoints(files[i].getAbsolutePath(), id);
			System.out.println(files[i]);
			for (int j = 0; j < endpoints.size(); j ++)
				System.out.println("\t" + endpoints.get(j));
		}
	}

}
