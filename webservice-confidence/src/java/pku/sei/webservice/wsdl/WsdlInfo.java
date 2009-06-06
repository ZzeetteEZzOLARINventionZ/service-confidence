package pku.sei.webservice.wsdl;

import java.util.ArrayList;
import java.util.List;

public class WsdlInfo {
	private String id = null;
	private String name = null;
	private String description = null;//��������ҳ�õ���������Ϣ
	private String url = null;
	private String state = null;//��ʱ����
	
	private Service service = null;//���service��������־���wsdl�����֣���ô���ǿ���ֻ��һ���ͺ�
	List<Operation> operations = null;
	//private String operations = null;//����operation������ƴ����һ��
	
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
			//�õ����е�operation������
			for(int i=0;i<this.operations.size();i++){
				Operation op = this.operations.get(i);
				sb.append(op.getName());
				sb.append(";");
				
			}	
		}
		
		return sb.toString();
	}
	
}
