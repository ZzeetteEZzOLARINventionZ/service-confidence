package pku.sei.webservice.wsdl;

import java.util.ArrayList;
import java.util.List;

public class WsdlInfo {
	private String id = null;
	private String name = null;
	private String description = null;//从其他网页得到的描述信息
	private String url = null;
	private String state = null;//暂时不用
	
	private Service service = null;//如果service对象的名字就是wsdl的名字，那么我们可以只用一个就好
	List<Operation> operations = null;
	//private String operations = null;//所有operation的名字拼接在一起
	
	public void serId(String _id){
		this.id = _id;
	}
	
	public void setName(String _name){
		this.name = _name;
	}
	public void setDescription(String _description){
		this.description = _description;
	}
	public void setUrl(String _url){
		this.url = _url;
	}
	public void setOperations(ArrayList _operations){
		this.operations = _operations;
	}
	public void setSerive(Service _service){
		this.service = _service;
	}
	public String getId(){
		return this.id;
	}
	public String getName(){
		return this.name;
	}
	public String getDescription(){
		return this.description;
	}
	public String getUrl(){
		return this.url;
	} 
	public List<Operation> getOperations(){
		return this.operations;
	}
	public Service getService(){
		return this.service;
	}
	public String getAllOperationName(){
		StringBuffer sb= new StringBuffer();
		if(this.operations!=null){
			//得到所有的operation的名字
			for(int i=0;i<this.operations.size();i++){
				Operation op = this.operations.get(i);
				sb.append(op.getName());
				sb.append(";");
				
			}	
		}
		
		return sb.toString();
	}
	
}
