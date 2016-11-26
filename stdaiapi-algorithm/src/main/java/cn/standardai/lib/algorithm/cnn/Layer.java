package cn.standardai.lib.algorithm.cnn;

import java.util.HashMap;
import java.util.Map;

import cn.standardai.lib.algorithm.common.ByteUtil;
import cn.standardai.lib.algorithm.common.Storable;
import cn.standardai.lib.algorithm.exception.StorageException;
import cn.standardai.lib.base.function.activate.Self;
import cn.standardai.lib.base.function.activate.Sigmoid;
import cn.standardai.lib.base.function.base.DerivableFunction;

public abstract class Layer implements Storable {

	public enum LayerType {

		input((byte)0x05), conv((byte)0x02), relu((byte)0x04), pool((byte)0x03), fc((byte)0x01);

		Byte type;

		private LayerType(Byte type) {
			this.type = type;
		}

		private static final Map<Byte, LayerType> mappings = new HashMap<Byte, LayerType>(5);

		static {
			for (LayerType layer : values()) {
				mappings.put(layer.type, layer);
			}
		}

		public static LayerType resolve(Byte type) {
			return (type != null ? mappings.get(type) : null);
		}
	}

	// TODO all public
	public Integer width;

	public Integer height;

	public Integer depth;

	public Double[][][] data;

	public Double[][][] error;

	public DerivableFunction aF;

	public static LayerType parseType(String type) {
		if (type == null) return null;
		switch (type) {
		case "input":
		case "INPUT":
			return LayerType.input;
		case "conv":
		case "CONV":
			return LayerType.conv;
		case "relu":
		case "RELU":
			return LayerType.relu;
		case "pool":
		case "POOL":
			return LayerType.pool;
		case "fc":
		case "FC":
			return LayerType.fc;
		default:
			return null;
		}
	}

	public void exec(Layer prevLayer) {
		return;
	}

	public void format(Layer prevLayer) throws CnnException {
		return;
	}

	public void calcError() {
		return;
	}

	public void calcPrevError(Layer prev) {
		//prev.initError();
		return;
	}

	public void initError() {
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.depth; k++) {
					this.error[i][j][k] = 0.0;
				}
			}
		}
		return;
	}

	public void print() {
		System.out.println(this.getClass());
		printData();
		printError();
		printFilter();
	}

	public void printData() {
		System.out.println(this.getClass());
		System.out.println("data:");
		for (int k = 0; k < this.depth; k++) {
			for (int j = 0; j < this.height; j++) {
				for (int i = 0; i < this.width; i++) {
					System.out.print(data[i][j][k] + "\t|");
				}
				System.out.println();
			}
			System.out.println("---------------------------------------------");
		}
	}

	public void printError() {
		if (this.error == null) return;
		System.out.println(this.getClass());
		System.out.println("error:");
		for (int k = 0; k < this.depth; k++) {
			for (int j = 0; j < this.height; j++) {
				for (int i = 0; i < this.width; i++) {
					System.out.print(error[i][j][k] + "\t|");
				}
				System.out.println();
			}
			System.out.println("---------------------------------------------");
		}
	}

	public void printFilter() {
		System.out.println(this.getClass());
		System.out.println("filters:none");
		return;
	}

	public void upgrade(Layer prev, int batchNum) {
		return;
	}

	public void setAF(String aF) {
		if (aF == null) {
			this.aF = new Self();
		} else if ("sigmoid".equals(aF)) {
			this.aF = new Sigmoid(1);
		} else {
			this.aF = new Self();
		}
	}

	public abstract byte getSerial();

	public static Layer getInstance(byte serial) {
		switch (LayerType.resolve(serial)) {
		case conv:
			return new ConvLayer();
		case fc:
			return new FCLayer();
		case input:
			return new InputLayer();
		case pool:
			return new PoolLayer();
		case relu:
			return new ReluLayer();
		default:
			return null;
		}
	}

	@Override
	public byte[] getBytes() {
		byte[] aFBytes;
		byte[] bytes = new byte[3 * Integer.BYTES + 1 + (aFBytes = aF.getBytes()).length];
		int index = 0;
		ByteUtil.putInt(bytes, this.width, index);
		index += Integer.BYTES;
		ByteUtil.putInt(bytes, this.height, index);
		index += Integer.BYTES;
		ByteUtil.putInt(bytes, this.depth, index);
		index += Integer.BYTES;
		bytes[index] = (byte)aFBytes.length;
		index++;
		System.arraycopy(aFBytes, 0, bytes, index, aFBytes.length);
		return bytes;
	}

	@Override
	public void load(byte[] bytes) throws StorageException {
		if (bytes == null) throw new StorageException("Layer load failure");
		int index = 0;
		this.width = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		this.height = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		this.depth = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		byte[] aFBytes = new byte[bytes[index++]];
		System.arraycopy(bytes, index, aFBytes, 0, aFBytes.length);
		this.aF = DerivableFunction.getInstance(aFBytes);
	}
}
