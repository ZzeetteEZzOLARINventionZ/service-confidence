package sei.pku.wsdl_analyzer;

/**
 * 使用WSDLFileAnanlyzer类的示例代码
 * @author lijiejacy
 *
 */
public class Demo_WSDLFileAnalyzer {

	/**
	 * @param args
	 * void
	 */
	public static void main(String[] args) {
//		try {
//			String filePath = "G:\\wsdldoc\\wsdldoc\\";
//			MysqlDAO mydao = new MysqlDAO();
//			Connection jinCon = new GetConnection().getConnection("jdbc:mysql://192.168.2.254:3306/ow2", "root", "root");
//			PreparedStatement ps_jin = jinCon.prepareStatement("select * from wsinfo");
//			ResultSet rsJin = ps_jin.executeQuery();
//			while(rsJin.next()){
//				int id = rsJin.getInt("id");
//				String servicename = rsJin.getString("name");
//				String wsdlurl = rsJin.getString("wsdladdr");
//				String wsdlcheck = rsJin.getString("wsdlcheck");
//				if(wsdlcheck.equals("F")||wsdlcheck.equals("N")){
//					mydao.insertWS(id, wsdlurl, servicename, wsdlcheck, "");
//				}else if(wsdlcheck.equals("Y")){
//					FileWSDLAnalyzer wsdl = new FileWSDLAnalyzer();
//
//
//					WSDLFile wFile = wsdl.getWSDL("G:\\wsdldoc\\wsdldoc\\"+id+".txt");
//
//					System.out.println(wFile.name);
//					System.out.println(wFile.fileName);
//					System.out.println(wFile.Url); // WS Url
//					System.out.println(wFile.document);
//					String documentation = wFile.document;
//					
//					/**
//					 * insert ws
//					 */
//					System.out.println("Add ws:\t"+id+"\t"+wsdlurl+"\t"+servicename);
//					if(documentation.length()>9000){
//						documentation = documentation.substring(0, 8000);
//					}
//					try{
//						mydao.insertWS(id, wsdlurl, servicename, wsdlcheck, documentation);
//					} catch (SQLException e) {
//						// TODO Auto-generated catch block
//						System.out.println(e.getMessage());
//					}
//					
//					
//					 ArrayList<Operation> opelist = wFile.operations;
//					 for(int i=0;i<opelist.size();i++)
//					 {
//						 Operation ope = (Operation)opelist.get(i);
//						 
//						 String operationName = ope.operationName;
//						 String inputName = ope.input.name;
//						 String outputName = ope.output.name;
//						 String op_documentation = ope.documentation;
//						 /**
//						  * insert operation
//						  */
//						 System.out.println("Add operation:\t"+id+"\t"+operationName);
//						 if(op_documentation!=null&&op_documentation.length()>5000){
//							 op_documentation = op_documentation.substring(0, 5000);
//						 }
//						 mydao.insertOperation(id, operationName, inputName, outputName, op_documentation);
//						 
//					 }
//							
//					 ArrayList<Message> messagelist = wFile.messages;
//					 for(int i=0;i<messagelist.size();i++)
//					 {
//						 Message mes = (Message)messagelist.get(i);
//						 String messageName = mes.name;
//						 /**
//						  * insert msg
//						  */
//						 System.out.println("Add msg:\t"+id+"\t"+messageName);
//						 mydao.insertMessage(id, messageName);
//						 
//					 }
//					 
//
//					for (Endpoint ep : wFile.endPoints) {
//						String portName = ep.getPortName();
//						String portLocation = ep.getLocation();
//						String port_documentation = ep.getDocumentation();
//						System.out.println("Add ep:\t"+id+"\t"+portLocation+"\t");
//						/**
//						 * insert endpoint
//						 */
//						
//						try{
//							int epID = mydao.insertEndpoint(portName, portLocation);
//							System.out.println("Add ws-ep:\t"+id+"\t"+epID+"\t");
//							/**
//							 * insert ws-endpoint
//							 */
//							mydao.insertWSandEP(id, epID, port_documentation);
//						} catch (SQLException e) {
//							// TODO Auto-generated catch block
//							System.out.println(e.getMessage());
//						}
//						
//						
//					}
//					/////////////////////////////
//				}
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
