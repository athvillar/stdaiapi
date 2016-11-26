package cn.standardai.lib.algorithm.cnn;

import cn.standardai.lib.algorithm.exception.StorageException;

public class ReluLayer extends Layer {

	private String function;

	public ReluLayer() {
		super();
	}

	public ReluLayer(String function) {
		this.function = function;
	}

	@Override
	public void format(Layer prevLayer) throws CnnException {
		if (this.function == null) throw new CnnException("RELU层初始化异常");
		switch (this.function) {
		case "max":
			break;
		default:
			throw new CnnException("RELU层初始化异常");
		}
		this.width = prevLayer.width;
		this.height = prevLayer.height;
		this.depth = prevLayer.depth;
		this.data = new Double[this.width][this.height][this.depth];
		this.error = new Double[this.width][this.height][this.depth];
		this.initError();
	}

	@Override
	public void exec(Layer prev) {
		if ("max".equals(this.function)) {
			for (int i = 0; i < this.width; i++) {
				for (int j = 0; j < this.height; j++) {
					for (int k = 0; k < this.depth; k++) {
						this.data[i][j][k] = prev.data[i][j][k] > 0 ? prev.data[i][j][k] : 0;
					}
				}
			}
		} else {
			this.data = prev.data.clone();
		}
	}

	@Override
	public void calcPrevError(Layer prev) {
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.depth; k++) {
					prev.error[i][j][k] = this.data[i][j][k] > 0 ? this.error[i][j][k] : 0;
				}
			}
		}
	}

	@Override
	public byte getSerial() {
		return 0x04;
	}

	@Override
	public byte[] getBytes() {
		byte[] commonBytes;
		int length = Integer.BYTES + (commonBytes = super.getBytes()).length + 1;
		byte[] bytes = new byte[length];
		int index = 0;
		System.arraycopy(commonBytes.length, 0, bytes, index, Integer.BYTES);
		index += Integer.BYTES;
		System.arraycopy(commonBytes, 0, bytes, index, commonBytes.length);
		index += commonBytes.length;
		switch (this.function) {
		case "max":
			bytes[index] = 0x01;
			break;
		default:
			break;
		}
		index++;
		return bytes;
	}

	@Override
	public void load(byte[] bytes) throws StorageException {
		if (bytes == null) throw new StorageException("ReluLayer load failure");
		int index = 0, commonLength = 0;
		System.arraycopy(bytes, index, commonLength, 0, Integer.BYTES);
		index += Integer.BYTES;
		byte[] commonBytes = new byte[commonLength];
		System.arraycopy(bytes, index, commonBytes, 0, commonLength);
		super.load(commonBytes);
		index += commonLength;
		switch (bytes[index]) {
		case 0x01:
			this.function = "max";
			break;
		default:
			break;
		}
		this.data = new Double[this.width][this.height][this.depth];
		this.error = new Double[this.width][this.height][this.depth];
	}
}
