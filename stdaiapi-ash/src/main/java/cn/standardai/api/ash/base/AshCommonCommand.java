package cn.standardai.api.ash.base;

import cn.standardai.api.ash.bean.AshParam;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;

public abstract class AshCommonCommand extends AshCommand implements Executable {

	public char[] fp = null;

	public String[] vp = null;

	public Integer pNumMax = null;

	public Integer pNumMin = null;

	public AshParam param;

	public AshReply reply = new AshReply();

	public abstract void invoke() throws AshException;

	@Override
	public AshReply exec() throws AshException {
		invoke();
		return this.reply;
	}

	@Override
	public Executable getExecutor() throws AshException {
		return this;
	}

	protected void setParamRules(char[] fp, String[] vp, Integer pNumMax, Integer pNumMin) {
		// -x
		this.fp = fp;
		// -x x
		this.vp = vp;
		// xx xx xx
		this.pNumMax = pNumMax;
		this.pNumMin = pNumMin;
	}

	public void setParam(String[] param) throws AshException {
		this.param = AshParam.parse(param, fp, vp, pNumMax, pNumMin);
	}

	public AshParam getParam() {
		return this.param;
	}
}
