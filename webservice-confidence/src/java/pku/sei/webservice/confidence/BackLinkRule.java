package pku.sei.webservice.confidence;

public class BackLinkRule  implements StatisticMap.Rule  {
	CheckWsdlRule cwr = new CheckWsdlRule();//算法2
	public boolean accept(String s) {
		 if ("notDownload".equals(s)){
			 return false;
		 }else{//算法2
			 if(cwr.accept(s)){//算法2
				 return true;//算法2
			 }else{
				 return false;//算法2
			 }
		 }
	 }
}
