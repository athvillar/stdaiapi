package cn.standardai.lib.algorithm.common;

import cn.standardai.lib.algorithm.exception.StorageException;

public interface Storable {

	public static int BYTES = 0;

	public byte[] getBytes();

	public void load(byte[] bytes) throws StorageException;
}
