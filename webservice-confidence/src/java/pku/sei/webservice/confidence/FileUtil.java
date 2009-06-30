package pku.sei.webservice.confidence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
