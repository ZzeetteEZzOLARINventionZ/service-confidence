package sei.pku.netstatus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 发送SOAP消息到输入的地址，测试URL访问状态，并记录返回消息 得到一个URL的网络访问状态代码
 * 程序在访问时会进行一些自适应调整：首先用POST方法访问，如果遇到访问方法不支持的话则自动切换为GET方法。
 * 如果遇到URL重定向则自动转向重定向后的URL进行访问测试。
 * 
 * @author lijiejacy
 * 
 */
public class GetWSNetStatus {
	/**
	 * 超时时间
	 */
	static int timeout = 20000;

	/**
	 * 利用POST或GET方法发送SOAP消息到输入的地址，测试URL访问状态，并记录返回消息
	 * 
	 * @param strUrl
	 * @param type
	 *            : POST or GET
	 * @return 消息内容
	 */
	private static String GetConnect(String strUrl, String type) {
		Socket client;
		BufferedOutputStream sender;
		BufferedInputStream receiver;
		String message = new String();

		// 构造封装的SOAP消息
		String postMessage;
		postMessage = "<?xml version='1.0' encoding='UTF-8'?>\r\n"
				+ "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">\r\n"
				+ "<SOAP-ENV:Header>"
				+ "<t:Transaction SOAP-ENV:mustUnderstand=\"1\">"
				+ "</t:Transaction>" + "</SOAP-ENV:Header>"
				+ "<SOAP-ENV:Body></SOAP-ENV:Body>\r\n"
				+ "</SOAP-ENV:Envelope>";

		try {
			URL url = new URL(strUrl);
			String path = url.getPath();
			if (path.equals("")) {
				path = "/";
			}

			String content;
			if (type.equals("POST")) {
				content = "POST " + path + " HTTP/1.1\r\nHost:" + url.getHost()
						+ "\r\nUser-Agent:socket/1.1\r\n"
						+ "Content-Type: text/xml; charset=utf-8\r\n"
						+ "Content-length:" + postMessage.length()
						+ "\r\nConnection:Close\r\n\r\n" + postMessage;
			} else if (type.equals("GET")) {
				content = "GET " + path + " HTTP/1.1\r\nHost:" + url.getHost()
						+ "\r\nUser-Agent:socket/1.1\r\n"
						+ "Content-Type: text/xml; charset=utf-8\r\n"
						+ "Content-length:" + postMessage.length()
						+ "\r\nConnection:Close\r\n\r\n" + postMessage;
			} else {
				return "error type";
			}

			InetAddress address = InetAddress.getByName(url.getHost());
			if (url.getPort() == -1)
				client = new Socket(address, 80);
			else
				client = new Socket(address, url.getPort());
			client.setSoTimeout(timeout);

			sender = new BufferedOutputStream(client.getOutputStream());
			receiver = new BufferedInputStream(client.getInputStream());
			sender.write(content.getBytes(), 0, content.length());
			sender.flush();
			int ret = 1;

			byte buffer[] = new byte[4096];

			while ((ret = receiver.read(buffer, 0, 4096)) != -1) {

				message += new String(buffer, 0, ret);

			}
		} catch (UnknownHostException e) {
			return "UnknownHostException";
		} catch (IOException e) {
			return e.getMessage();
		}
		String[] array = message.split("\r\n");
		int length = 11;
		if (array.length < length)
			length = array.length;
		String str = "";
		for (int i = 0; i < length; i++)
			str += array[i] + "\r\n";
		return str;
	}

	/**
	 * 从消息中提取HTTP CODE
	 * 
	 * @param msg
	 * @return code
	 */
	private static String getCode(String msg) {
		// return msg;
		// System.out.println(msg);
		Pattern p = Pattern.compile("http/1.[1|0]\\s(\\d*)\\s.*");
		Matcher m = p.matcher(msg.toLowerCase());
		if (m.find()) {
			return m.group(1);
		} else {
			return null;
		}
	}

	private static String getStatus(String line, int type) {
		String msg;
		if (type == 1) {
			msg = GetConnect(line, "POST");
		} else {
			msg = GetConnect(line, "GET");
		}
		String msg_code = getCode(msg);

		if (msg_code == null)
			return null;
		if (msg_code.equals("301") || msg_code.equals("302")) {
			// 如果是重定向，则转向新URL访问
			Pattern p = Pattern.compile("<a href=\"(.*)\">");
			Matcher m = p.matcher(msg.toLowerCase());
			String new_url = null;
			if (m.find()) {
				new_url = m.group(1);
			}
			if (new_url == null) {
				Pattern np = Pattern.compile("location:[\\s](.*)\\s");
				Matcher nm = np.matcher(msg.toLowerCase());
				if (nm.find()) {
					new_url = nm.group(1);
				}
			}
			if (new_url != null) {
				String result;
				if (type == 1) {
					result = GetConnect(new_url, "POST");
				} else {
					result = GetConnect(new_url, "GET");
				}
				if (result != null && result.length() > 0) {
					msg_code = new GetWSNetStatus().getCode(result);
					return msg_code;
				} else {
					return msg_code;
				}
			} else {
				return msg_code;
			}
		} else {
			return msg_code;
		}
	}

	/**
	 * 发送SOAP消息到输入的地址，测试URL访问状态，并记录返回消息 得到一个URL的网络访问状态代码
	 * 程序在访问时会进行一些自适应调整：首先用POST方法访问，如果遇到访问方法不支持的话则自动切换为GET方法。
	 * 如果遇到URL重定向则自动转向重定向后的URL进行访问测试。
	 * 
	 * @param url
	 * @param timeoutLimitation
	 *            超时时间，以毫秒为单位
	 * @return code or null if error happens
	 */
	public static String getSOAPStatus(String url, int timeoutLimitation) {
		timeout = timeoutLimitation;
		String code;
		String code1 = getStatus(url, 2);
		// 首先用GET方法尝试，如果失败则用POST方法
		if (code1 == null) {
			String code2 = getStatus(url, 1);
			code = code2;
		} else {
			if (code1.trim().equals("400") || code1.trim().equals("405")
					|| code1.trim().equals("500")) {
				// 如果失败换另外一种调用方式尝试
				String code2 = getStatus(url, 1);
				code = code2;
			} else {
				code = code1;
			}
		}
		return code;
	}

	/**
	 * 检验URL合法性
	 * 
	 * @param url
	 * @return
	 */
	public static boolean checkURL(String url) {
		if (!url.startsWith("http")) {
			// 不以http开头
			return false;
		} else {
			String newURL = url.toLowerCase();
			if (newURL.contains("//localhost")) {
				// e.g: http://localhost:8080/muse/services/host
				return false;
			}
			if (newURL.contains("//127.0.0.1/")) {
				// e.g: http://127.0.0.1/testPort
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		try {
			BufferedReader br;
			PrintWriter pw;
			if (args.length == 0) {

				br = new BufferedReader(new FileReader("G:\\endpoints.txt"));

				pw = new PrintWriter(new FileWriter("G:\\result.txt"));
			} else {
				br = new BufferedReader(new FileReader(args[0]));
				pw = new PrintWriter(new FileWriter(args[1]));
			}

			String line = null;
			String code = null;
			int count = 0;
			while ((line = br.readLine()) != null) {

				if (line.trim().length() == 0)
					continue;
				if (checkURL(line)) {
					code = getSOAPStatus(line, 20000);
				} else {
					code = "null";
				}
				System.out.println(line + "\t" + code);
				pw.println(line + "\t" + code);
				if (count++ % 10 == 0)
					pw.flush();
			}
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
