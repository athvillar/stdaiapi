package cn.standardai.lib.algorithm.cnn;

import cn.standardai.lib.algorithm.common.ByteUtil;
import cn.standardai.lib.algorithm.common.Storable;
import cn.standardai.lib.algorithm.exception.StorageException;

public class FilterFactory implements Storable {

	public static int BYTES = Integer.BYTES * 2;

	protected Integer width;

	protected Integer height;

	public FilterFactory(Integer width, Integer height) {
		this.width = width;
		this.height = height;
	}

	public Filter getInstance(Integer depth, Integer divider) {
		return new Filter(this.width, this.height, depth, divider);
	}

	@Override
	public byte[] getBytes() {
		byte[] bytes = new byte[BYTES];
		int index = 0;
		ByteUtil.putInt(bytes, this.width, index);
		index += Integer.BYTES;
		ByteUtil.putInt(bytes, this.height, index);
		return bytes;
	}

	@Override
	public void load(byte[] bytes) throws StorageException {
		if (bytes == null || bytes.length != BYTES) throw new StorageException("FilterFactory load failure");
		int index = 0;
		this.width = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		this.height = ByteUtil.getInt(bytes, index);
	}
}
