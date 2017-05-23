package cn.standardai.api.ml.filter;

import cn.standardai.api.dao.base.DaoHandler;

public abstract class DimensionFilter<T1, T2> extends DynamicFilter<T1, T2, String> {

	public boolean needInit() {
		return false;
	}

	public void init(String userId, DaoHandler dh) {
		return;
	}
}
