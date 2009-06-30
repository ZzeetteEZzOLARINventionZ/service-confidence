import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;


public class DoRank {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		InputStream is = new FileInputStream("C://Book1.xls");			
		System.out.println("in ExcelReader"+is);
		Workbook wb = Workbook.getWorkbook(is);
		Sheet st = wb.getSheet("Sheet6");
		Service[] ss = new Service[2803];
		int i = 1;
		for(i=1;i<2804;i++){
			Cell[] cc = st.getRow(i);
			ss[i-1] = new Service(cc[0].getContents(),new Double(cc[1].getContents()).doubleValue(),new Double(cc[2].getContents()).doubleValue());
		}
		Arrays.sort(ss);
		for(int j = ss.length-1;j>=0;j--){
			System.out.println(ss[j].url+"\t"+ss[j].p+"\t"+ss[j].n);
		}

	}

}
class Service implements Comparable{
	public String url;
	public double p;
	public double n;
	public Service(String url, double p, double n){
		this.url = url;
		this.p = p;
		this.n = n;
	}
	public int compareTo(Object o) {
		Service s = (Service)o;
		int t1 = 1, t2 = 1;
		if((p-0)<0.00001||(n-0)<0.00001)
			t1 = 0;
		if((s.p-0)<0.00001||(s.n-0)<0.00001)
			t2 = 0;
		if(t1==0&&t2==0){
			return 0;
		}else{
			if(t1==0)
				return -1;
			if(t2==0)
				return 1;
			if(p>s.p&&n>s.n)
				return 1;
			else if(p<s.p&&n<s.n)
				return -1;
			else if(p==s.p&&n<s.n)
				return 0;
			else{
				if(p<s.p){
					if(((s.p-p)/s.p)<((n-s.n)/n))
						return 1;
					else
						return -1;
						
				}else{
					if(((p-s.p)/p)<((s.n-n)/s.n))
						return -1;
					else
						return 1;
				}
			}
		}
	}
}
