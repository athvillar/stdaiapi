package cn.standardai.api.ash.bean;

import java.util.HashMap;
import java.util.Map;

public class AshCommandParams {

	private int paramNumber;

	public Map<String, String> paramMap = new HashMap<String, String>();

	public AshCommandParams() {
		paramMap.put("_p", "");
	}

	public void set(String p) {
		paramMap.put("_p", paramMap.get("_p") + p);
	}

	public void set(int n, String p) {
		paramMap.put("_" + n, p);
	}

	public void set(String k, String v) {
		paramMap.put(k, v);
	}

	public boolean has(String p) {
		return paramMap.get("_p").contains(p);
	}

	public String get(String p) {
		return paramMap.get(p);
	}

	public String get(int n) {
		return paramMap.get("_" + n);
	}

	public int number() {
		return paramNumber;
	}
}
