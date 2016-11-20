package cn.standardai.lib.algorithm.cnn;

import com.alibaba.fastjson.JSONArray;

public class InputLayer extends Layer {

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

	@Override
	public void exec(Double[][][] data) {
		this.data = data.clone();
	}

	public void setData(JSONArray data1) {
		for (int i = 0; i < data1.size(); i++) {
			JSONArray data2 = data1.getJSONArray(i);
			for (int j = 0; j < data2.size(); j++) {
				JSONArray data3 = data2.getJSONArray(i);
				for (int k = 0; k < data3.size(); k++) {
					this.data[k][j][i] = data3.getDouble(k);
				}
			}
		}
	}
}
