package cn.standardai.api.ml.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class DicFilter<T1, T2> extends DynamicFilter<String, T1, String> {

	public Map<String, T2> dic;

	public Map<T2, String> arcDic;

	@Override
	public boolean needInit() {
		return true;
	}

	protected void setDic(Map<String, T2> dic) {
		this.dic = dic;
		this.arcDic = new HashMap<T2, String>(this.dic.size());
		for (Entry<String, T2> entry : dic.entrySet()) {
			this.arcDic.put(entry.getValue(), entry.getKey());
		}
	}
}
