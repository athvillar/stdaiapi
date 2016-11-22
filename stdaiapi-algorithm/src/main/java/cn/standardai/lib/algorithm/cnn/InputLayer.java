package cn.standardai.lib.algorithm.cnn;

import com.alibaba.fastjson.JSONArray;

import cn.standardai.lib.base.function.activate.Sigmoid;

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
}
