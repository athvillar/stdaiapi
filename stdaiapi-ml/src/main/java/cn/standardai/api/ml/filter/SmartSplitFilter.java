package cn.standardai.api.ml.filter;

import java.util.HashMap;

import cn.standardai.api.dao.base.DaoHandler;

public class SmartSplitFilter extends DicFilter<String, String[], Integer> {

	@Override
	public String[] encode(String s) {
		String[] ss;
		if (s.contains(" ")) {
			ss = s.split(" ");
		} else {
			char[] cs = s.toCharArray();
			ss = new String[cs.length];
			for (int i = 0; i < ss.length; i++) {
				ss[i] = "" + cs[i] + " ";
				//ss[i] = "" + cs[i] + " ";
			}
		}
		Integer[] is = new Integer[ss.length];
		for (int i = 0; i < is.length; i++) {
			if (dic.containsKey(ss[i])) {
				is[i] = dic.get(ss[i]);
				continue;
			}
			int idx = dic.size();
			dic.put(ss[i], idx);
			arcDic.put(idx, ss[i]);
		}
		return ss;
	}

	@Override
	public String decode(String[] t) {
		String s = "";
		for (int i = 0; i < t.length; i++) {
			if (t[i].length() == 2 && t[i].charAt(1) == ' ') {
				s += t[i].substring(0, 1);
			} else {
				s += t[i] + " ";
			}
		}
		return s;
	}

	@Override
	public void init(String userId, DaoHandler dh) {
		this.dic = new HashMap<String, Integer>();
		this.arcDic = new HashMap<Integer, String>();
	}

	@Override
	public String getDescription() {
		return "先按照空格split，若没有则逐字split，根据数据中字符串出现的顺序，依次将其转换为从0开始的Integer数字。";
	}
}
