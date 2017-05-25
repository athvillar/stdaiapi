package cn.standardai.api.ml.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class DicFilter<T1, T2, T3> extends DynamicFilter<T1, T2, String> {

	public Map<String, T3> dic;

	public Map<T3, String> arcDic;

	@Override
	public boolean needInit() {
		return true;
	}

	protected void setDic(Map<String, T3> dic) {
		this.dic = dic;
		this.arcDic = new HashMap<T3, String>(this.dic.size());
		for (Entry<String, T3> entry : dic.entrySet()) {
			this.arcDic.put(entry.getValue(), entry.getKey());
		}
	}
}
