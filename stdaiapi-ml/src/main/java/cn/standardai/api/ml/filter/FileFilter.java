package cn.standardai.api.ml.filter;

public abstract class FileFilter<T> extends DataFilter<String, T> {

	protected String filePath;

	protected String fileName;

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
