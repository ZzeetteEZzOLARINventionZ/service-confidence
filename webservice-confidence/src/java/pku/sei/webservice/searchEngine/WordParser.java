package pku.sei.webservice.searchEngine;

public class WordParser {
	public static String[] wordParser(String query){//��AB,A.B,A_B ��ʽ�Ĳ�ѯ����ϲ�
		StringBuffer sb = new StringBuffer();
		String str=query.replace('.', ' ').replace('_', ' ');
		if(str != null&&!str.equals(" ")){
			//���д�
			int start=0;
			int end=0;
			char pre=' ';
			for(int i=0;i<str.length();i++)
			{
				char c=str.charAt(i);
				if(!java.lang.Character.isLowerCase(c)&&java.lang.Character.isLetter(c))//������д��ĸ
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
