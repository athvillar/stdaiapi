package cn.standardai.api.ml.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class DicFilter<T> extends DynamicFilter<String, T, String> {

	public Map<String, T> dic;

	public Map<T, String> arcDic;

	@Override
	public boolean needInit() {
		return true;
	}

	protected void setDic(Map<String, T> dic) {
		this.dic = dic;
		this.arcDic = new HashMap<T, String>(this.dic.size());
		for (Entry<String, T> entry : dic.entrySet()) {
			this.arcDic.put(entry.getValue(), entry.getKey());
		}
	}
}
