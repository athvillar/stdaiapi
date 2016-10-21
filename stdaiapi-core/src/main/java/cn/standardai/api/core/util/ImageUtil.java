package cn.standardai.api.core.util;

public class ImageUtil {

	public static String getFullImageName(String registry, String name, String version) {

	    if (name.indexOf("/") == -1) {
	    	return registry + "/official/" + name + ":" + version;
	    } else {
	    	return registry + "/" + name + ":" + version;
	    }
	}
}
