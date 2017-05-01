package cn.standardai.lib.algorithm.rnn.lstm;

import java.util.ArrayList;
import java.util.List;

import cn.standardai.lib.base.function.Roulette;
import cn.standardai.lib.base.function.Softmax;
import cn.standardai.lib.base.function.activate.Sigmoid;
import cn.standardai.lib.base.function.activate.Tanh;
import cn.standardai.lib.base.function.base.DerivableFunction;
import cn.standardai.lib.base.matrix.MatrixException;
import cn.standardai.lib.base.matrix.MatrixUtil;
import cn.standardai.lib.base.matrix.VectorUtil;

public class Lstm {

	private int epochCount = 0;

	private int stepCount = 0;

	// grad threshold
	private double dth = 10;

	private double lastLoss = 0;

	private double η = 1;

	public DerivableFunction σ = new Sigmoid();

	public DerivableFunction tanh = new Tanh();

	public int layerSize;

	public int inputSize;

	public int outputSize;

	// 参数
	public Double[][] w_f;

	public Double[] b_f;

	public Double[][] w_i;

	public Double[] b_i;

	public Double[][] w_c;

	public Double[] b_c;

	public Double[][] w_o;

	public Double[] b_o;

	public Double[][] w_y;

	public Double[] b_y;

	public Lstm(int layerSize, int inputSize, int outputSize) {
		this.layerSize = layerSize;
		this.inputSize = inputSize;
		this.outputSize = outputSize;
		this.w_f = MatrixUtil.random(layerSize + inputSize, layerSize, -1, 1);
		this.b_f = MatrixUtil.random(layerSize, -1, 1);
		this.w_i = MatrixUtil.random(layerSize + inputSize, layerSize, -1, 1);
		this.b_i = MatrixUtil.random(layerSize, -1, 1);
		this.w_c = MatrixUtil.random(layerSize + inputSize, layerSize, -1, 1);
		this.b_c = MatrixUtil.random(layerSize, -1, 1);
		this.w_o = MatrixUtil.random(layerSize + inputSize, layerSize, -1, 1);
		this.b_o = MatrixUtil.random(layerSize, -1, 1);
		this.w_y = MatrixUtil.random(layerSize, inputSize, -1, 1);
		this.b_y = MatrixUtil.random(inputSize, -1, 1);
	}

	public void train(Double[][] xs, Integer[] ys, int epoch) throws LstmException {
		if (xs.length != ys.length) throw new LstmException("训练数据长度错误");
		//this.cF.setParam(ys);
		do {
			epochCount++;
			train1(xs, ys);
		} while (epochCount < epoch);
	}

	public Integer[] predict(Double[][] xs, int step) throws LstmException {

		Double[] h_t1 = MatrixUtil.create(layerSize, 0);
		Double[] c_t1 = MatrixUtil.create(layerSize, 0);
		for (int i = 0; i < xs.length - 1; i++) {
			LstmCache cache1 = forward(xs[i], h_t1, c_t1);
			h_t1 = cache1.h_t;
			c_t1 = cache1.c_t;
		}

		Integer[] result = new Integer[step];
		Double[] xPredict = xs[xs.length - 1];
		for (int i = 0; i < step; i++) {

			LstmCache cache1 = forward(xPredict, h_t1, c_t1);
			//MatrixUtil.print(cache1.a);

			h_t1 = cache1.h_t;
			c_t1 = cache1.c_t;
			xPredict = cache1.a;

			Roulette r = new Roulette(cache1.a);
			result[i] = r.getY();
			//System.out.print(r.getY());
		}

		return result;
	}

	public void train1(Double[][] xs, Integer[] ys) throws LstmException {

		Double[] loss = MatrixUtil.create(outputSize, 0);
		List<LstmCache> cache = new ArrayList<LstmCache>();

		Double[] h_t1 = MatrixUtil.create(layerSize, 0);
		Double[] c_t1 = MatrixUtil.create(layerSize, 0);
		for (int i = 0; i < ys.length; i++) {
			stepCount++;
			LstmCache cache1 = forward(xs[i], h_t1, c_t1);
			cache.add(cache1);
			h_t1 = cache1.h_t;
			c_t1 = cache1.c_t;

			// cost = - ∑x ∑j (yj * ln(aj) + (1 - yj) * ln(1 - aj)) / n
			for (int j = 0; j < loss.length; j++) {
				if (ys[i] == j) {
					loss[j] += Math.log10(cache1.a[j]);
				} else {
					loss[j] += Math.log10(1 - cache1.a[j]);
				}
			}
			//MatrixUtil.print(loss);
		}

		double totalLoss = 0;
		for (int i = 0; i < loss.length; i++) {
			loss[i] /= -ys.length;
			totalLoss += loss[i];
		}
		if (epochCount % 5 == 0) {
			System.out.println("epoch " + epochCount + ", Loss: " + totalLoss);
		}

		adjustLearningRate(totalLoss);

		LstmDCache dCache = new LstmDCache();
		dCache.dcNext = MatrixUtil.create(layerSize, 0);
		dCache.dhNext = MatrixUtil.create(layerSize, 0);
		for (int i = cache.size() - 1; i >= 0; i--) {
			backward(ys[i], dCache, cache.get(i));
		}
	}

	private void adjustLearningRate(double totalLoss) {

		if (totalLoss >= lastLoss) {
			η *= 1.2;
			if (η > 1) η = 1;
		} else {
			η *= 0.95;
		}
		lastLoss = totalLoss;
	}

	public LstmCache forward(Double[] x_t, Double[] h_t1, Double[] c_t1) throws LstmException {

		try {
			LstmCache cache1 = new LstmCache();

			// Concatenate input
			cache1.X = MatrixUtil.concatenate(h_t1, x_t);

			// Step 1, forget gate layer
			// f_t = sigmoid(w_f . [h_t-1, x_t] + b_f)
			cache1.f_t = this.σ.getY(MatrixUtil.plus(MatrixUtil.multiply(cache1.X, w_f), b_f));

			// Step 2, input gate layer
			// i_t = sigmoid(w_i . [h_t-1, x_t] + b_i)
			cache1.i_t = this.σ.getY(MatrixUtil.plus(MatrixUtil.multiply(cache1.X, w_i), b_i));
			// c__t = tanh(w_c . [h_t-1, x_t] + b_c)
			cache1.c__t = this.tanh.getY(MatrixUtil.plus(MatrixUtil.multiply(cache1.X, w_c), b_c));
			// c_t = f_t * c_t-1 + i_t * c__t
			cache1.c_t = MatrixUtil.plus(MatrixUtil.elementMultiply(cache1.f_t, c_t1), MatrixUtil.elementMultiply(cache1.i_t, cache1.c__t));
			cache1.c_t1 = c_t1;

			// Step 3, output
			// o_t = sigmoid(w_o . [h_t-1, x_t] + b_o)
			cache1.o_t = this.σ.getY(MatrixUtil.plus(MatrixUtil.multiply(cache1.X, w_o), b_o));
			// h_t = o_t * tanh(c_t)
			cache1.h_t = MatrixUtil.elementMultiply(cache1.o_t, this.tanh.getY(cache1.c_t));
			// y = w_y . [h_t, x_t] + b_y
			Double[] y_t = MatrixUtil.plus(MatrixUtil.multiply(cache1.h_t, w_y), b_y);
			// probability = softmax(y)
			cache1.a = Softmax.getY(y_t);

			return cache1;

		} catch (MatrixException e) {
			throw new LstmException(e.getMessage());
		}
	}

	public void backward(int y, LstmDCache dCache, LstmCache cache1) throws LstmException {

		try {
			Double[] dy = new Double[cache1.a.length];
			for (int i = 0; i < dy.length; i++) {
				dy[i] = cache1.a[i];
			}
			dy[y] -= 1;

			Double[][] dwy = MatrixUtil.multiplyTC(cache1.h_t, dy);
			Double[] dby = dy;
			Double[] dh = MatrixUtil.plus(MatrixUtil.multiplyCT(dy, w_y), dCache.dhNext);

			// Gradient for ho in h = ho * tanh(c)
			Double[] dho = MatrixUtil.elementMultiply(this.tanh.getY(cache1.c_t), dh);
			dho = MatrixUtil.elementMultiply(this.σ.getDerivativeX(cache1.o_t), dho);

			// Gradient for c in h = ho * tanh(c)
			Double[] dc = MatrixUtil.elementMultiply(MatrixUtil.elementMultiply(cache1.o_t, dh),
					this.tanh.getDerivativeX(cache1.c_t));
			dc = MatrixUtil.plus(dc, dCache.dcNext);

			// Gradient for hf in c = hf * c_old + hi * hc
			Double[] dhf = MatrixUtil.elementMultiply(cache1.c_t1, dc);
			dhf = MatrixUtil.elementMultiply(this.σ.getDerivativeX(cache1.f_t), dhf);

			// Gradient for hi in c = hf * c_old + hi * hc
			Double[] dhi = MatrixUtil.elementMultiply(cache1.c__t, dc);
			dhi = MatrixUtil.elementMultiply(this.σ.getDerivativeX(cache1.i_t), dhi);

			// Gradient for hc in c = hf * c_old + hi * hc
			Double[] dhc = MatrixUtil.elementMultiply(cache1.i_t, dc);
			dhc = MatrixUtil.elementMultiply(this.tanh.getDerivativeX(cache1.c__t), dhc);

			// Gate gradient
			Double[][] dwf = MatrixUtil.multiplyTC(cache1.X, dhf);
			Double[] dbf = dhf;
			Double[] dXf = MatrixUtil.multiplyCT(dhf, w_f);

			Double[][] dwi = MatrixUtil.multiplyTC(cache1.X, dhi);
			Double[] dbi = dhi;
			Double[] dXi = MatrixUtil.multiplyCT(dhi, w_i);

			Double[][] dwo = MatrixUtil.multiplyTC(cache1.X, dho);
			Double[] dbo = dho;
			Double[] dXo = MatrixUtil.multiplyCT(dho, w_o);

			Double[][] dwc = MatrixUtil.multiplyTC(cache1.X, dhc);
			Double[] dbc = dhc;
			Double[] dXc = MatrixUtil.multiplyCT(dhc, w_c);

			Double[] dX = MatrixUtil.plus(dXf, dXi, dXo, dXc);

			dCache.dhNext = VectorUtil.subVector(dX, layerSize);
			// Gradient for c_old in c = hf * c_old + hi * hc
			dCache.dcNext = MatrixUtil.elementMultiply(cache1.f_t, dc);

			// Upgrade weights & biases
			w_f = AdjustParam(w_f, dwf);
			b_f = AdjustParam(b_f, dbf);
			w_i = AdjustParam(w_i, dwi);
			b_i = AdjustParam(b_i, dbi);
			w_o = AdjustParam(w_o, dwo);
			b_o = AdjustParam(b_o, dbo);
			w_c = AdjustParam(w_c, dwc);
			b_c = AdjustParam(b_c, dbc);
			w_y = AdjustParam(w_y, dwy);
			b_y = AdjustParam(b_y, dby);
		} catch (MatrixException e) {
			throw new LstmException(e.getMessage());
		}
	}

	private Double[] AdjustParam(Double[] p, Double[] dp) throws MatrixException {
		double l1Norm = MatrixUtil.l1Norm(dp);
		if (l1Norm > dth) {
			dp = MatrixUtil.multiply(dp, dth / l1Norm);
		}
		return MatrixUtil.minus(p, MatrixUtil.multiply(dp, η));
	}

	private Double[][] AdjustParam(Double[][] p, Double[][] dp) throws MatrixException {
		double l2Norm = MatrixUtil.l2Norm(dp);
		if (l2Norm > dth) {
			dp = MatrixUtil.multiply(dp, dth / l2Norm);
		}
		return MatrixUtil.minus(p, MatrixUtil.multiply(dp, η));
		//return MatrixUtil.plus(p, dp);
	}
}
