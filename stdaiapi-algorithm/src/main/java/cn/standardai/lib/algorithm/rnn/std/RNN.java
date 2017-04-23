package cn.standardai.lib.algorithm.rnn.std;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.lib.algorithm.cnn.Layer.LayerType;
import cn.standardai.lib.algorithm.common.ByteUtil;
import cn.standardai.lib.algorithm.exception.StorageException;
import cn.standardai.lib.base.function.Softmax;
import cn.standardai.lib.base.function.Statistic;
import cn.standardai.lib.base.function.activate.Tanh;
import cn.standardai.lib.base.function.base.DerivableFunction;
import cn.standardai.lib.base.matrix.MatrixException;
import cn.standardai.lib.base.matrix.MatrixUtil;

public class RNN {

	private Integer volumeSize;

	private Integer hiddenSize;

	private Integer[] x;

	private Double[] y;

	private Double[][] u;

	private Double[][] v;

	private Double[][] w;

	private Map<Integer, Double[]> ss = new HashMap<Integer, Double[]>();

	private Integer stepCount = 0;

	public DerivableFunction aF = new Tanh();

	public RNN(Integer volumeSize, Integer hiddenSize) {
		this.volumeSize = volumeSize;
		this.hiddenSize = hiddenSize;
		this.y = new Double[volumeSize];
		this.u = new Double[volumeSize][hiddenSize];
		this.v = new Double[volumeSize][hiddenSize];
		this.w = new Double[hiddenSize][hiddenSize];
		initWeights();
	}

	private void initWeights() {
		// TODO Auto-generated method stub
		
	}

	public Integer step(Integer index) throws MatrixException {
		Double[] s_1 = this.ss.get(stepCount);
		stepCount++;
		// s_t = tanh(Ux_t + Ws_t-1)
		Double[] s = this.aF.getY(MatrixUtil.plus(this.u[index], MatrixUtil.multiply(this.w, s_1)));
		this.ss.put(stepCount, s);
		this.y = Softmax.getY(MatrixUtil.multiply(this.v, s));
		return Statistic.maxIndex(this.y);
	}
}
