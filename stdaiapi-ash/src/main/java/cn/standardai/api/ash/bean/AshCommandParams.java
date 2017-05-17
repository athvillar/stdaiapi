package cn.standardai.api.ash.bean;

import java.util.HashMap;
import java.util.Map;

import cn.standardai.api.ash.exception.ParamException;

public class AshCommandParams {

	private int paramNumber = 0;

	public Map<String, String> paramMap = new HashMap<String, String>();

	public AshCommandParams() {
		paramMap.put("_p", "");
	}

	public void set(char p) {
		paramMap.put("_p", paramMap.get("_p") + p);
	}

	public void setInteger(Integer n) {
		paramMap.put("_n", n.toString());
	}

	public void set(int n, String p) {
		paramMap.put("_" + n, p);
	}

	public void set(char k, String v) {
		paramMap.put(String.valueOf(k), v);
	}

	public boolean has(char p) {
		return inChars(p, paramMap.get("_p"));
	}

	public String get(char k) {
		return paramMap.get(String.valueOf(k));
	}

	public Integer getInteger() {
		if (paramMap.get("_n") == null) return null;
		return Integer.parseInt(paramMap.get("_n"));
	}

	public String get(int n) {
		return paramMap.get("_" + n);
	}

	public int number() {
		return paramNumber;
	}

	public static AshCommandParams parse(String[] paramStrings, char[] fp, char[] vp, Integer pNumMax, Integer pNumMin) throws ParamException {
		AshCommandParams params = new AshCommandParams();
		for (int i = 0; i < paramStrings.length; i++) {
			String p = paramStrings[i];
			if (p == null || "".equals(p)) continue;
			if (p.startsWith("-")) {
				if (allNumbers(p.substring(1))) {
					// -100
					params.setInteger(Integer.parseInt(p.substring(1)));
					continue;
				} else {
					// -abc
					for (char c : p.substring(1).toCharArray()) {
						if (inChars(c, fp)) {
							// -a
							params.set(c);
							continue;
						} else if (inChars(c, vp)) {
							// -a b
							if (i == paramStrings.length - 1) throw new ParamException("-" + c + "缺少参数");
							params.set(c, paramStrings[++i]);
							continue;
						} else {
							// -?
							throw new ParamException("参数错误(-" + c + ")");
						}
					}
				}
			} else {
				if (p.startsWith("\"") && p.endsWith("\"")) {
					// "xxx"
					if (pNumMax != null && params.paramNumber >= pNumMax) throw new ParamException("参数过多(" + p + ")");
					params.set(++params.paramNumber, p.substring(1, p.length() - 1));
					continue;
				} else {
					// xxx
					if (pNumMax != null && params.paramNumber >= pNumMax) throw new ParamException("参数过多(" + p + ")");
					params.set(++params.paramNumber, p);
					continue;
				}
			}
		}
		if (pNumMin != null && params.paramNumber < pNumMin) throw new ParamException("缺少必要的参数");
		return params;
	}

	private static boolean allNumbers(String s) {
		for (char c : s.toCharArray()) {
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

	private static boolean inChars(char c, char[] cs) {
		if (cs == null) return false;
		for (char p : cs) {
			if (c == p) return true;
		}
		return false;
	}

	private static boolean inChars(char c, String s) {
		return inChars(c, s.toCharArray());
	}
}
