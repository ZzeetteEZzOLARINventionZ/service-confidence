package pku.sei.webservice.confidence;

public class BackLinkRule  implements StatisticMap.Rule  {
	CheckWsdlRule cwr = new CheckWsdlRule();//�㷨2
	public boolean accept(String s) {
		 if ("notDownload".equals(s)){
			 return false;
		 }else{//�㷨2
			 if(cwr.accept(s)){//�㷨2
				 return true;//�㷨2
			 }else{
				 return false;//�㷨2
			 }
		 }
	 }
}
