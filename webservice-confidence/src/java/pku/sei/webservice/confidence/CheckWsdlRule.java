package pku.sei.webservice.confidence;



public class CheckWsdlRule implements StatisticMap.Rule {
	
	//TODO 验证wsdl的是否合法，
	
	/**
	 * @param file 待验证的wsdl的文件路径
	 * @return true 如果wsdl是不合法的
	 *         false 如果wsdl是合法的
	 */
	public boolean accept(String file) {
		return false;
	}

}
