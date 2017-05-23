package cn.standardai.api.ml.filter;

import cn.standardai.api.dao.base.DaoHandler;

public abstract class MatrixFilter<T1, T2> extends DataFilter<T1, T2> {

	public boolean needInit() {
		return false;
	}

	public void init(String userId, DaoHandler dh) {
		return;
	}
}
