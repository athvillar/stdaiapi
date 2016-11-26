package cn.standardai.lib.algorithm.cnn;

import com.alibaba.fastjson.JSONArray;

import cn.standardai.lib.algorithm.common.ByteUtil;
import cn.standardai.lib.algorithm.exception.StorageException;

public class InputLayer extends Layer {

	public InputLayer() {
		super();
	}

	public InputLayer(Integer width, Integer height, Integer depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	@Override
	public void format(Layer prevLayer) throws CnnException {
		if (this.width != this.height) throw new CnnException("数据格式异常");
		this.data = new Double[width][height][depth];
	}

	public void setData(JSONArray data1) {
		Double max = Double.NEGATIVE_INFINITY;
		Double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < data1.size(); i++) {
			JSONArray data2 = data1.getJSONArray(i);
			for (int j = 0; j < data2.size(); j++) {
				JSONArray data3 = data2.getJSONArray(j);
				for (int k = 0; k < data3.size(); k++) {
					if (data3.getDouble(k) > max) max = data3.getDouble(k);
					if (data3.getDouble(k) < min) min = data3.getDouble(k);
				}
			}
		}
		if (this.data.length == data1.size()) {
			for (int i = 0; i < data1.size(); i++) {
				JSONArray data2 = data1.getJSONArray(i);
				for (int j = 0; j < data2.size(); j++) {
					JSONArray data3 = data2.getJSONArray(j);
					for (int k = 0; k < data3.size(); k++) {
						this.data[i][j][k] = this.aF.getY((data3.getDouble(k) - min) / (max - min));
					}
				}
			}
		} else {
			for (int i = 0; i < data1.size(); i++) {
				JSONArray data2 = data1.getJSONArray(i);
				for (int j = 0; j < data2.size(); j++) {
					JSONArray data3 = data2.getJSONArray(j);
					for (int k = 0; k < data3.size(); k++) {
						this.data[k][j][i] = this.aF.getY((data3.getDouble(k) - min) / (max - min));
					}
				}
			}
		}
	}

	public void setData(Integer[][][] data1) {
		Double max = Double.NEGATIVE_INFINITY;
		Double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < data1.length; i++) {
			Integer[][] data2 = data1[i];
			for (int j = 0; j < data2.length; j++) {
				Integer[] data3 = data2[j];
				for (int k = 0; k < data3.length; k++) {
					if (data3[k] > max) max = data3[k].doubleValue();
					if (data3[k] < min) min = data3[k].doubleValue();
				}
			}
		}
		for (int i = 0; i < data1.length; i++) {
			Integer[][] data2 = data1[i];
			for (int j = 0; j < data2.length; j++) {
				Integer[] data3 = data2[j];
				for (int k = 0; k < data3.length; k++) {
					//System.out.print("i="+i+",j="+j+",k="+k+"");
					//this.data[i][j][k] = this.activateFunction.getY(data3[k].doubleValue() - min);
					//this.data[i][j][k] = (data3[k].doubleValue() - min) / (max - min);
					this.data[i][j][k] = this.aF.getY((data3[k].doubleValue() - min) / (max - min));
					//this.data[i][j][k] = data3[k].doubleValue();
				}
			}
		}
		System.out.print("");
	}

	@Override
	public byte getSerial() {
		return 0x05;
	}

	@Override
	public byte[] getBytes() {
		byte[] commonBytes;
		int length = Integer.BYTES + (commonBytes = super.getBytes()).length;
		byte[] bytes = new byte[length];
		int index = 0;
		ByteUtil.putInt(bytes, commonBytes.length, index);
		index += Integer.BYTES;
		System.arraycopy(commonBytes, 0, bytes, index, commonBytes.length);
		index += commonBytes.length;
		return bytes;
	}

	@Override
	public void load(byte[] bytes) throws StorageException {
		if (bytes == null) throw new StorageException("InputLayer load failure");
		int index = 0, commonLength = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		byte[] commonBytes = new byte[commonLength];
		System.arraycopy(bytes, index, commonBytes, 0, commonLength);
		super.load(commonBytes);
		this.data = new Double[this.width][this.height][this.depth];
	}
}
