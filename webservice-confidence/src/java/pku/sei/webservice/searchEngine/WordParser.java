package pku.sei.webservice.searchEngine;

public class WordParser {
	public static String[] wordParser(String query){//将AB,A.B,A_B 形式的查询词组合拆开
		StringBuffer sb = new StringBuffer();
		String str=query.replace('.', ' ').replace('_', ' ');
		if(str != null&&!str.equals(" ")){
			//简单切词
			int start=0;
			int end=0;
			char pre=' ';
			for(int i=0;i<str.length();i++)
			{
				char c=str.charAt(i);
				if(!java.lang.Character.isLowerCase(c)&&java.lang.Character.isLetter(c))//遇到大写字母
				{
						end=i;
						if(i<str.length()-1){
							if(end!=0&&pre!=' '&&java.lang.Character.isLowerCase(str.charAt(i+1)))
							{
								sb.append(str.substring(start, end));
								sb.append(" ");
								start=end;
							}
						}
				}
				pre=c;
			}
			sb.append(str.substring(start));
		}
			System.out.println(sb.toString());
			String[] queryArray = sb.toString().split(" ");
			return queryArray;
			
	}
}
