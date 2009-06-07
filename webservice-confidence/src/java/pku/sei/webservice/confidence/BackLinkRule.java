package pku.sei.webservice.confidence;

public class BackLinkRule  implements StatisticMap.Rule  {
	public boolean accept(String s) {
		 if ("notDownload".equals(s))
			 return true;
		 return false;
	 }
}
