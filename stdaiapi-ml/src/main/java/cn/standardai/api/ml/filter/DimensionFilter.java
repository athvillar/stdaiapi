package cn.standardai.api.ml.filter;

import cn.standardai.api.ml.run.ModelGhost;

public abstract class DimensionFilter<T1, T2> extends DataFilter<T1, T2> {

	public boolean needInit() {
		return false;
	}

	public void init(ModelGhost mg) {
		return;
	}
}
