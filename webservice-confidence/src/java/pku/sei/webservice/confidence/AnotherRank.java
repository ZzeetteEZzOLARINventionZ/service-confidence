package pku.sei.webservice.confidence;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;

public class AnotherRank {
	public double[] d;
	public double[] old_d;
	int[][] matrix;
	WebServiceGraph graph;
	
	public AnotherRank () throws Exception {
		graph = new WebServiceGraph(
				"data2/allWsdlFile.txt", 
				"data2/notDownloadUrl.txt",
				"data2/downloadWsdlBacklinks.txt",
				"data2/notDownloadUrlBacklinks.txt");
		 
		System.out.println("after graph constructor");
		
		int[][] temp_matrix = graph.makeMatrix();
		
		System.out.println("after make matrix");
		this.d = graph.calculateD();
		old_d = d.clone();
		
		System.out.println("after calculate init d vector");
		this.matrix = WebServiceGraph.reverseMatrix(temp_matrix);
		//this.matrix = temp_matrix;
		
		System.out.println("after reverse vector");
		
		// 计算rank
		step1();
		step2();
	}
	
	public double alpha = 0.8;
	public double belta = 0.2;

	/**
	 * 将backlink的值向外发散给domain
	 */
	public void step1() {
		double[] addedValue = new double[d.length];
		boolean[] hasInlink = new boolean[d.length];
		
		System.out.println("Another Rank: step 1");
		for (Map.Entry<String, Integer> urlId : graph.urlId.entrySet()) {
			if (urlId.getKey().startsWith("B_")) {
				System.out.println(urlId.getKey());
				int i = urlId.getValue();
				int width = matrix[0].length;
				int sumWeighted = 0;
				for (int j = 0; j < width; j ++) {
					sumWeighted += matrix[i][j];
					if (matrix[i][j] > 0)
						hasInlink[j] = true;
				}
				if (sumWeighted != 0) {
					for (int j = 0; j < width; j ++) {
						addedValue[j] = d[i] * matrix[i][j] / sumWeighted;
					}
				}
			}
		}
		
		// 将新传入的值加进来
		for (int i = 0; i < d.length; i ++) {
			if (hasInlink[i]) {
				d[i] = d[i] * alpha + addedValue[i] * belta;
			}
		}
	}
	
	/**
	 * 将domain的值向外发散给endpoint
	 */
	public void step2() {
		double[] addedValue = new double[d.length];
		boolean[] hasInlink = new boolean[d.length];
		
		for (Map.Entry<String, Integer> urlId : graph.urlId.entrySet()) {
			if (urlId.getKey().startsWith("D_")) {
				int i = urlId.getValue();
				int width = matrix[0].length;
				int sumWeighted = 0;
				for (int j = 0; j < width; j ++) {
					sumWeighted += matrix[i][j];
					if (matrix[i][j] > 0)
						hasInlink[j] = true;
				}
				if (sumWeighted != 0) {
					for (int j = 0; j < width; j ++) {
						addedValue[j] = d[i] * matrix[i][j] / sumWeighted;
					}
				}
			}
		}
		
		// 将新传入的值加进来
		for (int i = 0; i < d.length; i ++) {
			if (hasInlink[i]) {
				d[i] = d[i] * alpha + addedValue[i] * belta;
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		AnotherRank ar= new AnotherRank();
		double d[] = ar.d;
		System.out.println("----------------R A N K -------------------------");
		PrintWriter writer = new PrintWriter(new FileWriter("data2/anotherRankResult.txt"));
		PrintWriter writer2 = new PrintWriter(new FileWriter("data2/withoutPropagation.txt"));
		for (Map.Entry<String, Integer> item : ar.graph.urlId.entrySet()) {
			writer.println(item.getKey() + "\t" + d[item.getValue()]);
			writer2.println(item.getKey() + "\t" + ar.old_d[item.getValue()]);
			System.out.println(item.getKey() + "\t" + d[item.getValue()]);
		}
		writer.close();
		writer2.close();
		System.out.println("ok");
	}
}
