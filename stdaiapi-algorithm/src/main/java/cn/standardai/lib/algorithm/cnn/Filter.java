package cn.standardai.lib.algorithm.cnn;

import cn.standardai.lib.algorithm.common.ByteUtil;
import cn.standardai.lib.algorithm.common.Storable;
import cn.standardai.lib.algorithm.exception.StorageException;

public class Filter implements Storable {

	// TODO all public
	public Integer width;

	public Integer height;

	public Integer depth;

	public Double w[][][];

	public Double b;

	public Filter(Integer width, Integer height, Integer depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.w = new Double[width][height][depth];
		this.b = (Math.random() - 0.5) / 100;
	}

	public Filter(Integer width, Integer height, Integer depth, Integer divider) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.w = new Double[width][height][depth];
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.depth; k++) {
					//this.w[i][j][k] = (Math.random() - 0.5) * 4 / this.width / this.height / this.depth;
					this.w[i][j][k] = (Math.random() - 0.5) * 4 / this.width / this.height / this.depth / divider;
					//this.w[i][j][k] = new Double(i);
				}
			}
		}
		//this.b = 2.0;
		//this.b = (Math.random() - 0.5);
		this.b = (Math.random() - 0.5) / 100;
	}

	@Override
	public byte[] getBytes() {
		int length = 3 * Integer.BYTES + Double.BYTES * (this.width * this.height * this.depth + 1);
		byte[] bytes = new byte[length];
		int index = 0;
		ByteUtil.putInt(bytes, this.width, index);
		index += Integer.BYTES;
		ByteUtil.putInt(bytes, this.height, index);
		index += Integer.BYTES;
		ByteUtil.putInt(bytes, this.depth, index);
		index += Integer.BYTES;
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.depth; k++) {
					ByteUtil.putDouble(bytes, this.w[i][j][k], index);
					index += Double.BYTES;
				}
			}
		}
		ByteUtil.putDouble(bytes, this.b, index);
		return bytes;
	}

	@Override
	public void load(byte[] bytes) throws StorageException {
		if (bytes == null) throw new StorageException("Filter load failure");
		int index = 0;
		index += Integer.BYTES;
		this.width = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		this.height = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		this.depth = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		this.w = new Double[this.width][this.height][this.depth];
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.depth; k++) {
					this.w[i][j][k] = ByteUtil.getDouble(bytes, index);
					index += Double.BYTES;
				}
			}
		}
		this.b = ByteUtil.getDouble(bytes, index);
	}
}
