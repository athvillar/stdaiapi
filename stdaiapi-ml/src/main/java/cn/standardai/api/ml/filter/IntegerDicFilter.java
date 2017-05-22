package cn.standardai.api.ml.filter;

import cn.standardai.api.ml.daohandler.DicHandler;
import cn.standardai.api.ml.run.ModelGhost;

public class IntegerDicFilter extends DicFilter<Integer> {

	@Override
	public Integer encode(String s) {
		return dic.get(s);
	}

	@Override
	public String decode(Integer t) {
		return arcDic.get(t);
	}

	@Override
	public void init(ModelGhost mg) {
		String dicName = params.get(0);
		setDic(new DicHandler(mg.getDaoHandler()).get(mg.getUserId(), dicName));
	}

	@Override
	public String getDescription() {
		return "根据数据字典，将String value转换为Integer key，以及将Integer key转换为String value。";
	}
}
