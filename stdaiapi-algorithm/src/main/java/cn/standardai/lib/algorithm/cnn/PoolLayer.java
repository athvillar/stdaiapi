package cn.standardai.lib.algorithm.cnn;

import cn.standardai.lib.algorithm.common.ByteUtil;
import cn.standardai.lib.algorithm.exception.StorageException;

public class PoolLayer extends Layer {

	protected String method;

	protected Integer spatial;

	protected Integer stride;

	public PoolLayer() {
		super();
	}

	public PoolLayer(String method, Integer spatial, Integer stride) {
		this.method = method;
		this.spatial = spatial;
		this.stride = stride;
	}

	@Override
	public void format(Layer prev) throws CnnException {
		if (this.method == null) throw new CnnException("POOL层初始化异常");
		switch (this.method) {
		case "max":
		case "avg":
			break;
		default:
			throw new CnnException("POOL层初始化异常");
		}
		if ((prev.width - this.spatial) % this.stride != 0)
			throw new CnnException("Pool层初始化异常");
		this.width = (prev.width - this.spatial) / this.stride + 1;
		this.height = (prev.height - this.spatial) / this.stride + 1;
		this.depth = prev.depth;
		this.data = new Double[this.width][this.height][this.depth];
		this.error = new Double[this.width][this.height][this.depth];
		this.initError();
	}

	@Override
	public void exec(Layer prev) {
		if ("max".equals(this.method)) {
			for (int i = 0; i < this.width; i++) {
				for (int j = 0; j < this.height; j++) {
					for (int k = 0; k < this.depth; k++) {
						this.data[i][j][k] = Double.NEGATIVE_INFINITY;
						for (int  i2= 0; i2 < this.spatial; i2++) {
							for (int j2 = 0; j2 < this.spatial; j2++) {
								if (prev.data[i * this.stride + i2][j * this.stride + j2][k] > this.data[i][j][k]) {
									this.data[i][j][k] = this.aF.getY(prev.data[i * this.stride + i2][j * this.stride + j2][k]);
									//System.out.println("big ["+(i * this.stride + i2)+"]["+(j * this.stride + j2)+"]["+k+"]:"+prev.data[i * this.stride + i2][j * this.stride + j2][k]);
								} else {
									//System.out.println("small ["+(i * this.stride + i2)+"]["+(j * this.stride + j2)+"]["+k+"]:"+prev.data[i * this.stride + i2][j * this.stride + j2][k]);
									//System.out.println(prev.data[i * this.stride + i2][j * this.stride + j2][k] > Double.NEGATIVE_INFINITY);
								}
							}
						}
					}
				}
			}
			//System.out.print("");
		} else if ("avg".equals(this.method)) {
			for (int i = 0; i < this.width; i++) {
				for (int j = 0; j < this.height; j++) {
					for (int k = 0; k < this.depth; k++) {
						Double sum = 0.0;
						for (int  i2= 0; i2 < this.spatial; i2++) {
							for (int j2 = 0; j2 < this.spatial; j2++) {
								sum += prev.data[i * this.stride + i2][j * this.stride + j2][k];
							}
						}
						this.data[i][j][k] = this.aF.getY(sum / this.spatial / this.spatial);
					}
				}
			}
		}
	}

	@Override
	public void calcPrevError(Layer prev) {
		super.calcPrevError(prev);
		switch (this.method) {
		case "max":
			for (int i = 0; i < this.width; i++) {
				for (int j = 0; j < this.height; j++) {
					for (int k = 0; k < this.depth; k++) {
						for (int  i2 = 0; i2 < this.spatial; i2++) {
							for (int j2 = 0; j2 < this.spatial; j2++) {
								//System.out.print("i="+i+",j="+j+",k="+k+",i2="+i2+",j2="+j2+":");
								//System.out.println("prev.data["+(i * this.stride + i2) + "][" + (j * this.stride + j2)+"]["+k+"]="+prev.data[i * this.stride + i2][j * this.stride + j2][k]);
								//System.out.print("this.data="+this.data[i][j][k]+"thus:+=");
								if (prev.data[i * this.stride + i2][j * this.stride + j2][k].equals(this.data[i][j][k])) {
									//System.out.println("1.0");
									// TODO ? prev.error[i * this.stride + i2][j * this.stride + j2][k] += this.error[i][j][k];
									prev.error[i * this.stride + i2][j * this.stride + j2][k] += this.error[i][j][k] *
											prev.aF.getDerivativeY(prev.data[i * this.stride + i2][j * this.stride + j2][k]);
								} else {
									//System.out.println("0.0");
									//prev.error[i * this.stride + i2][j * this.stride + j2][k] += 0.0;
								}
							}
						}
					}
				}
			}
			break;
		case "avg":
			for (int i = 0; i < this.width; i++) {
				for (int j = 0; j < this.height; j++) {
					for (int k = 0; k < this.depth; k++) {
						for (int  i2 = 0; i2 < this.spatial; i2++) {
							for (int j2 = 0; j2 < this.spatial; j2++) {
								// TODO ? prev.error[i2 * this.stride + i2][j2 * this.stride + j2][k] +=
								//		this.error[i][j][k] / this.spatial / this.spatial;
								prev.error[i2 * this.stride + i2][j2 * this.stride + j2][k] +=
										this.error[i][j][k] / this.spatial / this.spatial *
										prev.aF.getDerivativeY(prev.data[i * this.stride + i2][j * this.stride + j2][k]);
							}
						}
					}
				}
			}
			break;
		}
	}

	@Override
	public byte getSerial() {
		return 0x03;
	}

	@Override
	public byte[] getBytes() {
		byte[] commonBytes;
		int length = Integer.BYTES + (commonBytes = super.getBytes()).length + 1 + 2 * Integer.BYTES;
		byte[] bytes = new byte[length];
		int index = 0;
		ByteUtil.putInt(bytes, commonBytes.length, index);
		index += Integer.BYTES;
		System.arraycopy(commonBytes, 0, bytes, index, commonBytes.length);
		index += commonBytes.length;
		switch (this.method) {
		case "max":
			bytes[index] = 0x01;
			break;
		case "avg":
			bytes[index] = 0x02;
			break;
		default:
			break;
		}
		index++;
		ByteUtil.putInt(bytes, this.spatial, index);
		index += Integer.BYTES;
		ByteUtil.putInt(bytes, this.stride, index);
		index += Integer.BYTES;
		return bytes;
	}

	@Override
	public void load(byte[] bytes) throws StorageException {
		if (bytes == null) throw new StorageException("PoolLayer load failure");
		int index = 0, commonLength = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		byte[] commonBytes = new byte[commonLength];
		System.arraycopy(bytes, index, commonBytes, 0, commonLength);
		super.load(commonBytes);
		index += commonLength;
		switch (bytes[index]) {
		case 0x01:
			this.method = "max";
			break;
		case 0x02:
			this.method = "avg";
			break;
		default:
			break;
		}
		index++;
		this.spatial = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		this.stride = ByteUtil.getInt(bytes, index);
		this.data = new Double[this.width][this.height][this.depth];
		this.error = new Double[this.width][this.height][this.depth];
	}
}
