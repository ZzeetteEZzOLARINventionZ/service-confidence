package sei.pku.netstatus;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Get the last update time of web services via reading information from http header.
 * @author lijiejacy
 *
 */
public class LastUpdateTime {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line = null;
			int i = 0;
			while((line=br.readLine())!=null){
				URL u;
				try {
					u = new URL(line.trim());
					HttpURLConnection con = (HttpURLConnection) u.openConnection();
					con.connect();
					long a = con.getLastModified();
					System.out.println(line.trim()+"\t"+new Date(a));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					System.out.println(line.trim()+"\t error");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println(line.trim()+"\t error");
				}
				
			}			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}