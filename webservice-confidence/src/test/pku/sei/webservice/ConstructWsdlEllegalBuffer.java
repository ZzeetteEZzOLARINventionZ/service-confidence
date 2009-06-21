package pku.sei.webservice;

import java.io.*;

import pku.sei.webservice.confidence.CheckWsdlRule;

public class ConstructWsdlEllegalBuffer {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		File folder = new File("data2/AllFile");
		CheckWsdlRule rule = new CheckWsdlRule();
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i ++) {
			System.out.println(files[i].getAbsolutePath());
			rule.accept(files[i].getAbsolutePath());
			
		}
	}

}
