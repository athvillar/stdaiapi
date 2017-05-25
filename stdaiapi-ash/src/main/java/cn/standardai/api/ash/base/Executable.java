package cn.standardai.api.ash.base;

import cn.standardai.api.ash.bean.AshParam;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;

public interface Executable {

	public AshReply exec() throws AshException;

	public AshParam getParam();

	public void setParam(String[] params) throws AshException;

	public void readParam() throws AshException;

	public void setUserId(String userId);

	public void setToken(String token);

	public int getDialogIndex();
}
