package cn.standardai.api.ml.filter;

public class IntegerDicFilter extends DicFilter<Integer> {

	@Override
	public Integer encode(String s) {
		return dic.get(s);
	}

	@Override
	public String decode(Integer t) {
		return arcDic.get(t);
	}
}
