package pku.sei.webservice;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetNetStatus {
	public static String GetConnectGet(String strUrl){
		Socket client;
		BufferedOutputStream sender;
		BufferedInputStream receiver;

		String postMessage = "<?xml version='1.0' encoding='UTF-8'?>\r\n"
				+ "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">\r\n"
				+ "<SOAP-ENV:Body>\r\n" + "</SOAP-ENV:Body>\r\n"
				+ "</SOAP-ENV:Envelope>";

		String message = new String();

		try {

			URL url = new URL(strUrl);
			String path = url.getPath();
			if (path == "") {
				path = "/";
			}
			
			String content = "GET "
					+ path
					+ " HTTP/1.1\r\nHost:"
					+ url.getHost()
					+ "\r\nUser-Agent:socket/1.1\r\nContent-Type: text/xml; charset=utf-8\r\n"
					+ "Content-length:" + postMessage.length()
					+ "\r\nConnection:Close\r\n\r\n" + postMessage;

			InetAddress address = InetAddress.getByName(url.getHost());
			if (url.getPort() == -1)
				client = new Socket(address, 80);
			else
				client = new Socket(address, url.getPort());
			client.setSoTimeout(120000);

			sender = new BufferedOutputStream(client.getOutputStream());
			receiver = new BufferedInputStream(client.getInputStream());
			sender.write(content.getBytes(), 0, content.length());
			sender.flush();
			int ret = 1;

			byte buffer[] = new byte[4096];

			while ((ret = receiver.read(buffer, 0, 4096)) != -1) {

				message += new String(buffer, 0, ret);

			}
		} catch(UnknownHostException e){
			return "UnknownHostException";
		}catch (IOException e) {
			//e.printStackTrace();
			//System.out.println("**************************");

		}
		// System.out.println(message);
		String[] array = message.split("\r\n");
		int length = 11;
		if (array.length < length)
			length = array.length;
		String str = "";
		for (int i = 0; i < length; i++)
			str += array[i] + "\r\n";
		// return message;
		return str;
	}
	// 发送SOAP消息到输入的地址，并记录返回消息
	public static String GetConnect(String strUrl) {
		Socket client;
		BufferedOutputStream sender;
		BufferedInputStream receiver;

		String postMessage = "<?xml version='1.0' encoding='UTF-8'?>\r\n"
				+ "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">\r\n"
				+ "<SOAP-ENV:Body>\r\n" + "</SOAP-ENV:Body>\r\n"
				+ "</SOAP-ENV:Envelope>";

		String message = new String();

		try {

			URL url = new URL(strUrl);
			String path = url.getPath();
			if (path == "") {
				path = "/";
			}

			
			 String content = "POST " + path +" HTTP/1.1\r\nHost:" +
			 url.getHost() + "\r\nUser-Agent:socket/1.1\r\nContent-Type: text/xml; charset=utf-8\r\n" + "Content-length:" +
			 postMessage.length() + "\r\nConnection:Close\r\n\r\n" +
			 postMessage ;
			
//			String content = "GET "
//					+ path
//					+ " HTTP/1.1\r\nHost:"
//					+ url.getHost()
//					+ "\r\nUser-Agent:socket/1.1\r\nContent-Type: text/xml; charset=utf-8\r\n"
//					+ "Content-length:" + postMessage.length()
//					+ "\r\nConnection:Close\r\n\r\n" + postMessage;

			InetAddress address = InetAddress.getByName(url.getHost());
			if (url.getPort() == -1)
				client = new Socket(address, 80);
			else
				client = new Socket(address, url.getPort());
			client.setSoTimeout(120000);

			sender = new BufferedOutputStream(client.getOutputStream());
			receiver = new BufferedInputStream(client.getInputStream());
			sender.write(content.getBytes(), 0, content.length());
			sender.flush();
			int ret = 1;

			byte buffer[] = new byte[4096];

			while ((ret = receiver.read(buffer, 0, 4096)) != -1) {

				message += new String(buffer, 0, ret);

			}
		} catch(UnknownHostException e){
			return "UnknownHostException";
		}catch (IOException e) {
			//e.printStackTrace();
			//System.out.println("**************************");

		}
		// System.out.println(message);
		String[] array = message.split("\r\n");
		int length = 11;
		if (array.length < length)
			length = array.length;
		String str = "";
		for (int i = 0; i < length; i++)
			str += array[i] + "\r\n";
		// return message;
		return str;
	}
	
	public String getCode(String msg){
		Pattern p = Pattern.compile("http/1.[1|0]\\s(\\d*)\\s.*");
		Matcher m = p.matcher(msg.toLowerCase());
		if(m.find()){
			return m.group(1);
		}else{
			return null;
		}
	}
	
	public static String getStatus(String line, int type){
		if(type==1){
			String msg = GetNetStatus.GetConnect(line);
			//System.out.println(msg);
			String msg_code = new GetNetStatus().getCode(msg);
			if(msg_code == null)
				return null;
			if(msg_code.equals("301")||msg_code.equals("302")){
				Pattern p = Pattern.compile("<a href=\"(.*)\">");
				Matcher m = p.matcher(msg.toLowerCase());
				String new_url = null;
				if(m.find()){
					new_url = m.group(1);				
				}
				if(new_url==null){
					//String msg = "Location: https://adwords.google.com/api/adwords/v11/CriterionService\r\nx";
					Pattern np = Pattern.compile("location:[\\s](.*)\\s");
					Matcher nm = np.matcher(msg.toLowerCase());
					if(nm.find()){
						new_url = nm.group(1);	
					}
				}
				if(new_url!=null){
					//System.out.println("----URL:"+line+"\r\n----New URL:"+new_url);
					String result = GetNetStatus.GetConnect(new_url);
					if(result!=null&&result.length()>0){
						msg_code = new GetNetStatus().getCode(result);						
						return (msg_code==null?"error":msg_code);
					}else{
						return msg_code;
					}
				}else{
					return msg_code;
				}
			}else{
				return msg_code;
			}
		}else{
			String msg = GetNetStatus.GetConnectGet(line);
			String msg_code = new GetNetStatus().getCode(msg);
			if(msg_code == null)
				return null;
			if(msg_code.equals("301")||msg_code.equals("302")){
				Pattern p = Pattern.compile("<a href=\"(.*)\">");
				Matcher m = p.matcher(msg.toLowerCase());
				String new_url = null;
				if(m.find()){
					new_url = m.group(1);				
				}
				if(new_url==null){
					//String msg = "Location: https://adwords.google.com/api/adwords/v11/CriterionService\r\nx";
					Pattern np = Pattern.compile("location:[\\s](.*)\\s");
					Matcher nm = np.matcher(msg.toLowerCase());
					if(nm.find()){
						new_url = nm.group(1);	
					}
				}
				if(new_url!=null){
					//System.out.println("----URL:"+line+"\r\n----New URL:"+new_url);
					String result = GetNetStatus.GetConnectGet(new_url);
					if(result!=null&&result.length()>0){
						msg_code = new GetNetStatus().getCode(result);						
						return (msg_code==null?"error":msg_code);
					}else{
						return msg_code;
					}
				}else{
					return msg_code;
				}
			}else{
				return msg_code;
			}
		}
	}
	
	public static void main(String[] args) throws SecurityException,
			IOException {
//		BufferedReader br = new BufferedReader(new FileReader("C:/data/urlList.txt"));
//		PrintWriter pw = new PrintWriter(new FileWriter("C:/data/netStatus.txt"));
		String line = "http://www.webservicex.net/WeatherForecast.asmx";
		String code = null;
		int count = 0;
//		while((line=br.readLine())!=null){
//			if(line.trim().length()==0)
//				continue;
			String code1 = getStatus(line, 1);
			if(code1==null){
				String code2 = getStatus(line, 2);
				code = code2;
			}else{
				if(code1.trim().equals("400")||code1.trim().equals("405")){
					String code2 = getStatus(line, 2);
					code = code2;
				}else{
					code = code1;
				}
			}
			
//			pw.println(line+"\t"+(code==null?"error":code));
			System.out.println(line+"\t"+(code==null?"error":code));
//			if(count++%10==0)
//				pw.flush();
//		}
//		pw.flush();
//		pw.close();
	}
}
