package pku.sei.webservice.confidence;

import java.io.*;
import java.util.*;

public class FileUtil {
	public static List<List<String>> readFile(String file) throws IOException {
		List<List<String>> result = new ArrayList<List<String>> ();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = reader.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);	//, "\t"
			List<String> array = new ArrayList<String> ();
			while (st.hasMoreTokens()) {
				array.add(st.nextToken());
			}
			result.add(array);
		}
		reader.close();
		return result;
	}
}
