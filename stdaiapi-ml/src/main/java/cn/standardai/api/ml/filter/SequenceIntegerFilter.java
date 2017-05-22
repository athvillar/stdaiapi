package cn.standardai.api.ml.filter;

import java.util.HashMap;

import cn.standardai.api.ml.run.ModelGhost;

public class SequenceIntegerFilter extends DicFilter<Integer> {

	@Override
	public Integer encode(String s) {
		if (dic.containsKey(s)) return dic.get(s);
		int idx = dic.size();
		dic.put(s, idx);
		arcDic.put(idx, s);
		return idx;
	}

	@Override
	public String decode(Integer t) {
		if (arcDic.containsKey(t)) return arcDic.get(t);
		return "?";
	}

	@Override
	public void init(ModelGhost mg) {
		this.dic = new HashMap<String, Integer>();
		this.arcDic = new HashMap<Integer, String>();
	}
}
