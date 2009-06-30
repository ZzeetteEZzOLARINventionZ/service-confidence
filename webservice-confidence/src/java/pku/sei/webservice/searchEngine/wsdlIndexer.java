package pku.sei.webservice.searchEngine;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import pku.sei.webservice.wsdl.WsdlInfo;


public class wsdlIndexer {
	private String INDEX_STORE_PATH="D:\\WSSE\\IndexStore";
	private String[] STOPWORDS={"messge","wsdl","binding","operation","type","<",">","/","=","xmlns",":","xs"};
	//创建索引
	public void createIndex(String inputDir){
		try{
			//分词工具
			IndexWriter writer = new IndexWriter (INDEX_STORE_PATH,new StandardAnalyzer(), true);
			
			File filesDir = new File(inputDir);
			File[] files = filesDir.listFiles();
			for (int i = 0; i < files.length; i++){
				System.out.println("No "+i);
				String fileName = files[i].getName();
				//存入文件解析出wsdlInfo类所需要的值 ，张良杰提供接口
				WsdlInfo wsdlInfo = new WsdlInfo();
				Document doc = new Document();
				Field field = new Field("ws_id",wsdlInfo.getId(),Field.Store.YES, Field.Index.NO);
				doc.add(field);
				field = new Field("ws_name",wsdlInfo.getName(),Field.Store.YES, Field.Index.TOKENIZED);
				doc.add(field);
				field = new Field("ws_description",wsdlInfo.getDescription(),Field.Store.YES, Field.Index.TOKENIZED);
				doc.add(field);
				field = new Field("ws_url",wsdlInfo.getUrl(),Field.Store.YES, Field.Index.TOKENIZED);
				doc.add(field);
				if(wsdlInfo.getService().getDocumentation()!=null){
					field = new Field("ws_service_documentation",wsdlInfo.getService().getDocumentation(),Field.Store.YES, Field.Index.TOKENIZED);
					doc.add(field);
				}
				field = new Field("ws_operation",wsdlInfo.getAllOperationName(),Field.Store.YES, Field.Index.TOKENIZED);
				doc.add(field);
				field = new Field("ws_content",loadFileToString(files[i]),Field.Store.YES,Field.Index.TOKENIZED);//全文索引
				doc.add(field);
				writer.addDocument(doc);
					
				
			}
			writer.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public String loadFileToStringWithoutAnalyzer(File file){
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			StringBuffer sb = new StringBuffer();
			String line = br.readLine();
			while(line != null){
				//不需要切词
				sb.append(line);
				line = br.readLine();	
			}
			br.close();
			//System.out.println(sb.toString());
			return sb.toString().replace('_', ' ');
			
		}	catch (IOException e){
			e.printStackTrace();
			return null;
		}	
	}
	public String loadFileToString(File file){
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			StringBuffer sb = new StringBuffer();
			String line = br.readLine();
			while(line != null){
				//简单切词
				int start=0;
				int end=0;
				//System.out.println(line);
				//line=line.substring(0, line.indexOf(" "));
				for(int i=0;i<line.length();i++)
				{
					char c= ' ';
					char prech;
					if(i!=0)
						prech = c;
					else prech=' ';
					c=line.charAt(i);
					if(!java.lang.Character.isLowerCase(c))//遇到大写字母
					{
							end=i;
							if(i<line.length()-1){
								if(end!=0&&!java.lang.Character.isLetter(prech)&&java.lang.Character.isLowerCase(line.charAt(i+1)))
								{
									sb.append(line.substring(start, end));
									sb.append(" ");
									start=end;
								}
							}
					}
				}
				sb.append(line.substring(start));
				
				line = br.readLine();	
			}
			br.close();
			//System.out.println(sb.toString());
			String str=sb.toString().replace('_', ' ');
			return str;//.replace('.', '@');
			
		}	catch (IOException e){
			e.printStackTrace();
			return null;
		}	
	}	
	public static void main(String[] args) throws IOException{
		wsdlIndexer processor = new wsdlIndexer();
		processor.createIndex("E:\\work\\WebServiceSE\\DataStore");
		
		
	}
}
