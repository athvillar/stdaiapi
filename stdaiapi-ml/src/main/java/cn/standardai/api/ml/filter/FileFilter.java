package cn.standardai.api.ml.filter;

import cn.standardai.api.ml.run.ModelGhost;

public abstract class FileFilter<T> extends DataFilter<String, T> {

	protected String filePath;

	protected String fileName;

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean needInit() {
		return false;
	}

	public void init(ModelGhost mg) {
		return;
	}
}
