package cn.standardai.api.ash.base;

import cn.standardai.api.ash.bean.AshParam;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;

public abstract class Action implements Executable {

	public char[] fp = null;

	public String[] vp = null;

	public Integer pNumMax = null;

	public Integer pNumMin = null;

	public AshCommand comm;

	public AshResource res;

	public AshParam param;

	public AshReply reply = new AshReply();

	protected void setParamRules(char[] fp, String[] vp, Integer pNumMax, Integer pNumMin) {
		// -x
		this.fp = fp;
		// -x x
		this.vp = vp;
		// xx xx xx
		this.pNumMax = pNumMax;
		this.pNumMin = pNumMin;
	}

	protected void setVp(String[][] dialog) {
		this.vp = new String[dialog.length];
		for (int i = 0; i < this.vp.length; i++) {
			this.vp[i] = dialog[i][0];
		}
	}

	public void setParam(String[] param) throws AshException {
		this.param = AshParam.parse(param, fp, vp, pNumMax, pNumMin);
	}

	public AshParam getParam() {
		return this.param;
	}
}
