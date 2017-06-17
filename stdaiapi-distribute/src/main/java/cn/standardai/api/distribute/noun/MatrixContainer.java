package cn.standardai.api.distribute.noun;

import java.util.Map;

public class MatrixContainer extends Container<String, Matrix> {

	public Map<String, Matrix> var;

	@Override
	public Matrix get(String k) {
		return this.var.get(k);
	}

	@Override
	public void put(String k, Matrix v) {
		this.var.put(k, v);
	}

	@Override
	protected Matrix caluculate(String v) {
		Matrix.operate(get(v));
		// TODO
		return null;
	}


}
