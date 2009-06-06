package pku.sei.webservice;

public class PureName {
	public static final String getName(String s) {
		if (s == null)
			return "";
		return s.substring(s.indexOf(':')+1);
	}
}

