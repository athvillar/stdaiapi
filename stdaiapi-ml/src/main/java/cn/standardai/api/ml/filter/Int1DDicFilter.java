package cn.standardai.api.ml.filter;

import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.ml.daohandler.DicHandler;

public class Int1DDicFilter extends DicFilter<String[], Integer[], Integer> {

	@Override
	public Integer[] encode(String[] s) {
		Integer[] is = new Integer[s.length];
		for (int i = 0; i < is.length; i++) {
			is[i] = dic.get(s[i]);
		}
		return is;
	}

	@Override
	public String[] decode(Integer[] t) {
		String[] ss = new String[t.length];
		for (int i = 0; i < ss.length; i++) {
			ss[i] = arcDic.get(t[i]);
		}
		return ss;
	}

	@Override
	public void init(String userId, DaoHandler dh) {
		String dicName = params.get(0);
		setDic(new DicHandler(dh).get(userId, dicName));
	}

	@Override
	public String getDescription() {
		return "根据数据字典，将String value数组转换为Integer key数组，以及将Integer key数组转换为String value数组。";
	}
}
