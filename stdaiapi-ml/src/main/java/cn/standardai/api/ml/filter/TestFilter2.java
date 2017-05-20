package cn.standardai.api.ml.filter;

public class TestFilter2 extends DataFilter<Integer, Double> {

	@Override
	public Double encode(Integer s) {
		return 1.0;
	}

	@Override
	public Integer decode(Double t) {
		// TODO Auto-generated method stub
		return null;
	}
}
