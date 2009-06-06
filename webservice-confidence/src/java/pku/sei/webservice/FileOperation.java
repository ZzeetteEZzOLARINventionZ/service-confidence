package pku.sei.webservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
public class FileOperation {
	
	
	public static String GetCurrentDir(){
		 String currentPath = System.getProperty("user.dir") ;
		 if( currentPath.charAt(currentPath.length() - 1 ) != '\\')
			 currentPath += '\\' ;
		 return currentPath ;
	}
	public static void GetAllFilesOneDir_Version(String DirPath,Vector vec)
	{
		File m_file = new File(DirPath);
		if(!m_file.isDirectory())
			return;
		//System.out.println(m_file.isDirectory());
		//System.out.println(m_file.list().length);
		File [] files = m_file.listFiles();
		for(int i=0;i<files.length;i++)
		{
			if(files[i].isFile())
			{
				//System.out.println(files[i].getAbsolutePath());
				if(files[i].getName().indexOf("view=log")!=-1)
			    {
					vec.add(files[i].getAbsolutePath());
					System.out.println(files[i].getAbsolutePath());
				}
			}
			else
			{
				GetAllFilesOneDir_Version(files[i].getAbsolutePath(), vec);
			}
		}
		return;
	}
	public static void GetAllDirsOneDir(String DirPath,Vector vec)
	{
		File m_file = new File(DirPath);
		if(!m_file.isDirectory())
			return;
		//System.out.println(m_file.isDirectory());
		//System.out.println(m_file.list().length);
		File [] files = m_file.listFiles();
		for(int i=0;i<files.length;i++)
		{
			if(files[i].isFile())
			{
			}
			else
			{
				vec.add(files[i].getAbsolutePath());
				System.out.println(files[i].getAbsolutePath());
				GetAllDirsOneDir(files[i].getAbsolutePath(), vec);
			}
		}
		return;
	}
	public static void WriteToFile( String path,
									String fileName, 
									String content, 
									boolean append)
		throws java.io.IOException,SecurityException{
		
		String  pathOld = FileOperation.createDir( path );		
		fileName = pathOld + fileName;
		
		FileWriter fw = new FileWriter(fileName ,append);
       fw.write(content);
       fw.close();
	}
	
	public static void WriteToFileNew(String fileName, 
			String content, 
			boolean append)
throws java.io.IOException,SecurityException{

		

FileWriter fw = new FileWriter(fileName ,append);
fw.write(content);
fw.close();
}
	
	public static boolean ReadFileToVector(String fileName,Vector vector)
	    throws java.io.FileNotFoundException,java.io.IOException{
		 if(vector == null)
			 return false;

		 FileReader fr = new FileReader(fileName);
		 BufferedReader br = new BufferedReader(fr);
		 String record=new String();
	     while((record = br.readLine()) != null){
	    	 if(!IsExit(vector,record.trim()))
	           vector.add(record.trim());
		 }
		 br.close();
		 fr.close();
		 return   true  ;
	}
	public static boolean ReadFileToVector1(String fileName,Vector vector)
	{
	 if(vector == null)
		 return false;

	 FileReader fr = null;
	try {
		fr = new FileReader(fileName);
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}
	 BufferedReader br = new BufferedReader(fr);
	 String record=new String();
     try {
		while((record = br.readLine()) != null){
		       vector.add(record.trim());
		 }
	} catch (IOException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		return false;
	}
	 try {
		br.close();
		fr.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
	}
	 return   true  ;
}
	public static String ReadFileToVector(String fileName)
    throws java.io.FileNotFoundException,java.io.IOException
    {
		String body = "";

		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		String record=new String();
		while((record = br.readLine()) != null)
		{
			body += record.trim();
		}
		br.close();
		fr.close();
		return   body  ;
}
	//add by zhlj
	public static boolean IsExit(Vector vec,String str){
		for(int i=0;i<vec.size();i++){
			String temp = (String)vec.get(i);
			if(temp.compareTo(str)==0)
				return true;
		}
		return false;
		
	}
	public static int ReadFileToHashMap( HashMap<Integer,String> hashmap ,
										 String filePath ,
										 String fileName ,
										 int startIndex)
	
       throws java.io.FileNotFoundException,java.io.IOException{
	    //VectorManager  vector = new VectorManager();	
		if(hashmap == null)
			return startIndex;
       
		String  pathOld = FileOperation.createDir( filePath );		
		fileName = pathOld + fileName;
		
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
	    
		String record=new String();
		while((record = br.readLine()) != null){
			if(!hashmap.containsValue(record)){
				//System.out.println(record);
				hashmap.put(startIndex++,record);
			}
			//else{
				//System.out.println(" repeat : " + record);
			//}
		}
		br.close();
		fr.close();
		return   startIndex ;
	}
	
	public static int  WriteHashMapToFile(String fileName,int startIndex,HashMap hash,boolean append)
   throws java.io.IOException{
		FileWriter fw = new FileWriter(fileName ,append);
		int        length = hash.size();
		for(; startIndex <= length ;startIndex++){
			String     content = (String)hash.get(Integer.toString(startIndex))+"\r\n";
			if(content != null)
				fw.write(content);
       }
		
       fw.close();
       return  startIndex ;
	}
	public static void WriteVectorToFile(String fileName,int startIndex,Vector vector,boolean append)
   throws java.io.IOException{
		FileWriter fw = new FileWriter(fileName ,append);
		int        length = vector.size();
		for(; startIndex <= length ;startIndex++){
			String     content = (String)vector.get(startIndex) + "\r\n";
			fw.write(content);
       }
       fw.close();
	}
	
	 /**
	   * Create new path
	   * 
	   * @param path String
	   */
	  public static String createDir(String path) {
	    
		  String  pathOld = FileOperation.GetCurrentDir() + path ;
	      //if (! (new File(pathOld).isDirectory())) 
	      new File(pathOld).mkdir();
	     
	      if( pathOld.charAt(pathOld.length() - 1 ) != '\\')
				pathOld += '\\' ;
	      
	      return pathOld;
	  }
	  
	  

	
	/**
	 * 
	 * @param fileName
	 * @param startIndex
	 * @param vector
	 * @param append
	 * @throws java.io.IOException
	 */
	/*public static void WriteVectorToFile(String fileName,int startIndex,Vector vector,boolean append)
    throws java.io.IOException{
		FileWriter fw = new FileWriter(fileName ,append);
		int        length = vector.size();
		for(; startIndex <length-1 ;startIndex++){
			String     content = (String)vector.get(startIndex) + "\r\n";
			fw.write(content);
        }
		fw.write((String)vector.get(startIndex));
        fw.close();
	}*/

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}



