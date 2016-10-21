package cn.standardai.api.core.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ConfigUtil {

	public static String getConfigString(String key) {

	    ResourceBundle rb = ResourceBundle.getBundle("parkos");
	    Enumeration<String> keys = rb.getKeys();
	    Map<String, String> props = new HashMap<String, String>();
	    while (keys.hasMoreElements()) {
	        String tmpKey = keys.nextElement();
	        props.put(tmpKey, rb.getString(tmpKey));
	    }
		return props.get(key);
	}

	public static Integer getConfigInt(String key, Integer defaultValue) {
		String value = getConfigString(key);
		if (value == null) {
			return defaultValue;
		}
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}