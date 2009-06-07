package pku.sei.webservice.confidence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CheckWsdlRule implements StatisticMap.Rule {

	// TODO 验证wsdl的是否合法，

	/**
	 * @param file
	 *            待验证的wsdl的文件路径
	 * @return true 如果wsdl是不合法的 false 如果wsdl是合法的
	 */
	public boolean accept(String file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			boolean tag = false;
			int count = 0;
			while ((line = br.readLine()) != null) {
				if (count++ > 100)
					break;
				if (line.trim().length() > 0) {// get the first none-empty line
					if (line.startsWith("<?xml version")
							|| line.startsWith("<wsdl:definitions")
							|| line.startsWith("<definitions")
							|| line.startsWith("<description xmlns")) {
						tag = true;
						break;
					}
				}
			}
			br.close();

			if (!tag)
				System.out.println("invalid wsdl file:" + file + "\r\n    "
						+ line);
			return tag;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}
