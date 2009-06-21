package pku.sei.webservice.confidence;

import java.util.*;

public class AnotherRank {
	public double[] d;
	int[][] matrix;
	WebServiceGraph graph;
	
	public AnotherRank () throws Exception {
		graph = new WebServiceGraph(
				"data2/allWsdlFile.txt", 
				"data2/notDownloadUrl.txt", 
				"data2/notDownloadUrlBacklinks.txt", 
				"data2/downloadWsdlBacklinks.txt");
		 
		System.out.println("after graph constructor");
		
		int[][] matrix = graph.makeMatrix();
		
		System.out.println("after make matrix");
		this.d = graph.calculateD();
		
		System.out.println("after calculate init d vector");
		this.matrix = WebServiceGraph.reverseMatrix(matrix);
		
		System.out.println("after reverse vector");
		
		// 计算rank
		step1();
		step2();
	}

	/**
	 * 将backlink的值向外发散给domain
	 */
	public void step1() {
		for (Map.Entry<String, Integer> urlId : graph.urlId.entrySet()) {
			if (urlId.getKey().startsWith("B_")) {
				int i = urlId.getValue();
				int width = matrix[0].length;
				int sumWeighted = 0;
				for (int j = 0; j < width; j ++) {
					sumWeighted += matrix[i][j];
				}
				if (sumWeighted != 0) {
					for (int j = 0; j < width; j ++) {
						d[j] += d[i] * matrix[i][j] / sumWeighted;
					}
				}
			}
		}
	}
	
	/**
	 * 将domain的值向外发散给endpoint
	 */
	public void step2() {
		for (Map.Entry<String, Integer> urlId : graph.urlId.entrySet()) {
			if (urlId.getKey().startsWith("D_")) {
				int i = urlId.getValue();
				int width = matrix[0].length;
				int sumWeighted = 0;
				for (int j = 0; j < width; j ++) {
					sumWeighted += matrix[i][j];
				}
				if (sumWeighted != 0) {
					for (int j = 0; j < width; j ++) {
						d[j] += d[i] * matrix[i][j] / sumWeighted;
					}
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		AnotherRank ar= new AnotherRank();
		double d[] = ar.d;
		System.out.println("----------------R A N K -------------------------");
		for (Map.Entry<String, Integer> item : ar.graph.urlId.entrySet()) {
			System.out.println(item.getKey() + "\t" + d[item.getValue()]);
		}
	}
}
