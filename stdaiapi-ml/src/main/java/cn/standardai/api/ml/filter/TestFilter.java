package cn.standardai.api.ml.filter;

public class TestFilter extends DataFilter<Double, Double[]> {

	@Override
	public Double[] encode(Double s) {
		return new Double[]{2.2,3.3,4.4};
	}

	@Override
	public Double decode(Double[] t) {
		// TODO Auto-generated method stub
		return null;
	}
}
