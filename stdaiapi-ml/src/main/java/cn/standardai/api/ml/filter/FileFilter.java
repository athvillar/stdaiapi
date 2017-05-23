package cn.standardai.api.ml.filter;

import cn.standardai.api.dao.base.DaoHandler;

public abstract class FileFilter<T> extends DataFilter<String, T> {

	protected String filePath;

	protected String fileName;

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean needInit() {
		return false;
	}

	public void init(String userId, DaoHandler dh) {
		return;
	}
}
