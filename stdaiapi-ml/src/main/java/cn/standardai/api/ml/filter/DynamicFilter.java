package cn.standardai.api.ml.filter;

import java.util.HashMap;
import java.util.Map;

import cn.standardai.api.dao.base.DaoHandler;

public abstract class DynamicFilter<T1, T2, T3> extends DataFilter<T1, T2> {

	public Map<Integer, T3> params;

	public void setParam(Integer k, T3 v) {
		if (this.params == null) this.params = new HashMap<Integer, T3>();
		this.params.put(k, v);
	}

	public T3 getParam(Integer k) {
		return this.params == null ? null : this.params.get(k);
	}

	public abstract boolean needInit();

	public abstract void init(String userId, DaoHandler dh);
}
