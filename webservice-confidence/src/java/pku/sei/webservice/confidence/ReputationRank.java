package pku.sei.webservice.confidence;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;


public class ReputationRank{
	public int matrix[][];
	
	public int outDeg[];
	
	public double[] d;
	
	public double alpha = 0.85;
	
	public double[] rank;
	
	public double[] old_rank;
	
	public double[][] T;
	
	public WebServiceGraph graph = new WebServiceGraph();
	
	public ReputationRank() throws Exception
	{
		//LinkGraphExtractor extractor=new LinkGraphExtractor();	
		
		this.matrix=graph.makeMatrix();
		
		this.d = graph.calculateD();
		
		this.T = new double[d.length][d.length];
		this.T = this.getTansitionMatrix(matrix, d.length);
//		for(int i = 0;i<T.length;i++){
//			for (int j = 0;j<T[i].length;j++)
//				System.out.print("\t"+T[i][j]);
//			System.out.println();
//		}
		
		this.rank = new double[d.length];
		this.rank = (double[])this.d.clone();
		
		this.old_rank = new double[d.length];
		this.old_rank = (double[])this.d.clone();
		
	}
	//×ªÒÆ¾ØÕó
	public double[][] getTansitionMatrix(int[][] m, int size){
		double[][] t = new double[size][size];
		
		int[] outDegree = new int[size];
		for(int i = 0;i<m.length;i++){
			int temp = 0;
			for(int j = 0;j<m[i].length;j++){
				temp += m[i][j];
			}
			outDegree[i] = temp;
		}
		
		for(int i = 0;i<size;i++){
			for(int j = 0;j<size;j++){
//				if(i==197&&j==198){
//					System.out.println(m[j][i]+" "+outDegree[j]);
//				}
				if(outDegree[j]-0<0.0000001);
					//t[i][j] = Math.sqrt(1.0/m.length);
					//t[i][j] = 1.0/m.length;
				else
					t[i][j] = Math.sqrt(m[j][i]/(double)outDegree[j]);
			}
		}
		
		return t;
	}
	//initialize the outdegree of every node
	public void iniOutDeg(){
		for(int i = 0;i<this.matrix.length;i++){
			int temp = 0;
			for(int j = 0;j<matrix[i].length;j++){
				temp += matrix[i][j];
			}
			this.outDeg[i] = temp;
		}
	}
	
	public void run()
	{
		int count = 0;
		this.normalize(rank);
		while(true){
			//System.out.println("----------------"+count++);
			//double[] tempArr = (double[])rank.clone();
			for(int i = 0;i<rank.length;i++){
				if(i==197){
					System.out.println("hello");
				}
				double temp = 0;
				for(int j = 0;j<rank.length;j++){
					if(i==197&&j==198){
						System.out.println(T[i][j]);
					}
					temp += T[i][j]*old_rank[j];
				}
				temp *= alpha;
				
				temp += (1-alpha)*d[i];
				
				rank[i] = temp;
				
//				if(Math.abs(rank[i]-old_rank[i])>0.0000000001){
//					System.out.println(i+"----"+rank[i]+"-----"+old_rank);
//				}
			}					
			
			this.normalize(rank);
//			
//			if(count>20)
//				break;
			if(canTerminate(old_rank,rank)){
				this.normalize(rank);
				break;		
			}
			old_rank = (double[])rank.clone();
		}
	}
	
	/*
	 * test if the terminate condition satisfied
	 * rankArr and oldRankArr are both available
	 */
	public boolean canTerminate(double[] newR ,double[] oldR)
	{
		double threshold=0.0000000000001;
		
		return this.dist2(newR, oldR) < threshold * threshold;
	}
	
	/*
	 * return the square of the distance between arr1 and arr2
	 */
	public double dist2(double arr1[], double arr2[])
	{
		double ret=0.0;
		int i;
		for(i=0; i<arr1.length; i++)
		{
			double diff=arr1[i]-arr2[i];
			ret+=diff*diff;
		}
		return ret;
	}
	
	public void normalize(double arr[])
	{
		double sum=this.sum(arr);
		int i;
		for(i=0; i<arr.length; i++)
		{
			arr[i]/=sum;
		}
	}
	
	public double sum(double arr[])
	{
		int i;
		double sum=0.0;
		for(i=0; i<arr.length; i++)
		{
			sum+=arr[i];
		}
		return sum;
	}
	public static void main(String[] args)
	{
		ReputationRank rr;
		try {
			rr = new ReputationRank();
			rr.run();
			System.out.println("end:::::");
			PrintWriter pw = new PrintWriter(new FileWriter("data/ReputationRank.txt"));
			for(int i = 0;i<rr.rank.length;i++){
				pw.println(i+"\t"+rr.graph.idUrl.get(i)+"\t"+rr.rank[i]+"\t"+rr.d[i]);
			}
			pw.flush();
			pw.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
