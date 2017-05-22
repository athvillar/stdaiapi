package cn.standardai.lib.algorithm.rnn.lstm;

import cn.standardai.lib.algorithm.common.ByteUtil;
import cn.standardai.lib.algorithm.exception.DnnException;
import cn.standardai.lib.base.function.Softmax;
import cn.standardai.lib.base.function.activate.Sigmoid;
import cn.standardai.lib.base.function.activate.Tanh;
import cn.standardai.lib.base.function.base.DerivableFunction;
import cn.standardai.lib.base.matrix.MatrixException;
import cn.standardai.lib.base.matrix.MatrixUtil;
import cn.standardai.lib.base.matrix.VectorUtil;

public class Lstm {

	public int layerSize;

	public int inputSize;

	public int outputSize;

	// grad threshold
	private double dth = 1;

	private double η = 1;

	public DerivableFunction σ = new Sigmoid();

	public DerivableFunction tanh = new Tanh();

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
		// TODO
		this.w_f = MatrixUtil.random(layerSize + inputSize, layerSize, -1.0 / Math.sqrt((layerSize + inputSize) * layerSize), 1.0 / Math.sqrt((layerSize + inputSize) * layerSize));
		//this.w_f = MatrixUtil.random(layerSize + inputSize, layerSize, 0, 1.0 / Math.sqrt((layerSize + inputSize)));
		this.b_f = MatrixUtil.create(layerSize, 0);
		this.w_i = MatrixUtil.random(layerSize + inputSize, layerSize, -1.0 / Math.sqrt((layerSize + inputSize) * layerSize), 1.0 / Math.sqrt((layerSize + inputSize) * layerSize));
		//this.w_i = MatrixUtil.random(layerSize + inputSize, layerSize, 0, 1.0 / Math.sqrt((layerSize + inputSize)));
		this.b_i = MatrixUtil.create(layerSize, 0);
		this.w_c = MatrixUtil.random(layerSize + inputSize, layerSize, -1.0 / Math.sqrt((layerSize + inputSize) * layerSize), 1.0 / Math.sqrt((layerSize + inputSize) * layerSize));
		//this.w_c = MatrixUtil.random(layerSize + inputSize, layerSize, 0, 1.0 / Math.sqrt((layerSize + inputSize)));
		this.b_c = MatrixUtil.create(layerSize, 0);
		this.w_o = MatrixUtil.random(layerSize + inputSize, layerSize, -1.0 / Math.sqrt((layerSize + inputSize) * layerSize), 1.0 / Math.sqrt((layerSize + inputSize) * layerSize));
		//this.w_o = MatrixUtil.random(layerSize + inputSize, layerSize, 0, 1.0 / Math.sqrt((layerSize + inputSize)));
		this.b_o = MatrixUtil.create(layerSize, 0);
		this.w_y = MatrixUtil.random(layerSize, outputSize, -1.0 / Math.sqrt(outputSize * layerSize), 1.0 / Math.sqrt(layerSize * outputSize));
		//this.w_y = MatrixUtil.random(layerSize, inputSize, -1.0 / Math.sqrt(inputSize * layerSize), 1.0 / Math.sqrt(layerSize * inputSize));
		//this.w_y = MatrixUtil.random(layerSize, inputSize, 0, 1.0 / Math.sqrt(layerSize));
		this.b_y = MatrixUtil.create(outputSize, 0);
	}

	public void setDth(Double dth) {
		if (dth != null) this.dth = dth;
	}

	public void setLearningRate(Double η) {
		if (η != null) this.η = η;
	}

	public void normalizeD(LstmDCache dCache, int size) throws MatrixException {
		dCache.dwf = MatrixUtil.devide(dCache.dwf, new Double(size));
		dCache.dbf = MatrixUtil.devide(dCache.dbf, new Double(size));
		dCache.dwi = MatrixUtil.devide(dCache.dwi, new Double(size));
		dCache.dbi = MatrixUtil.devide(dCache.dbi, new Double(size));
		dCache.dwo = MatrixUtil.devide(dCache.dwo, new Double(size));
		dCache.dbo = MatrixUtil.devide(dCache.dbo, new Double(size));
		dCache.dwc = MatrixUtil.devide(dCache.dwc, new Double(size));
		dCache.dbc = MatrixUtil.devide(dCache.dbc, new Double(size));
		dCache.dwy = MatrixUtil.devide(dCache.dwy, new Double(size));
		dCache.dby = MatrixUtil.devide(dCache.dby, new Double(size));
	}

	public void adjustParam(LstmDCache dCache) throws MatrixException {
		w_f = AdjustParam(w_f, dCache.dwf);
		b_f = AdjustParam(b_f, dCache.dbf);
		w_i = AdjustParam(w_i, dCache.dwi);
		b_i = AdjustParam(b_i, dCache.dbi);
		w_o = AdjustParam(w_o, dCache.dwo);
		b_o = AdjustParam(b_o, dCache.dbo);
		w_c = AdjustParam(w_c, dCache.dwc);
		b_c = AdjustParam(b_c, dCache.dbc);
		w_y = AdjustParam(w_y, dCache.dwy);
		b_y = AdjustParam(b_y, dCache.dby);
	}

	public LstmCache forward(Double[] x_t, Double[] hOld, Double[] cOld) throws DnnException {

		try {
			LstmCache cache1 = new LstmCache();

			// Concatenate input
			cache1.X = MatrixUtil.concatenate(hOld, x_t);

			// Step 1, forget gate layer
			// f_t = sigmoid(w_f . [h_t-1, x_t] + b_f)
			cache1.hf = this.σ.getY(MatrixUtil.plus(MatrixUtil.multiply(cache1.X, w_f), b_f));

			// Step 2, input gate layer
			// i_t = sigmoid(w_i . [h_t-1, x_t] + b_i)
			cache1.hi = this.σ.getY(MatrixUtil.plus(MatrixUtil.multiply(cache1.X, w_i), b_i));
			// c__t = tanh(w_c . [h_t-1, x_t] + b_c)
			cache1.hc = this.tanh.getY(MatrixUtil.plus(MatrixUtil.multiply(cache1.X, w_c), b_c));
			// c_t = f_t * c_t-1 + i_t * c__t
			cache1.c = MatrixUtil.plus(MatrixUtil.elementMultiply(cache1.hf, cOld), MatrixUtil.elementMultiply(cache1.hi, cache1.hc));
			cache1.cOld = cOld.clone();

			// Step 3, output
			// o_t = sigmoid(w_o . [h_t-1, x_t] + b_o)
			cache1.ho = this.σ.getY(MatrixUtil.plus(MatrixUtil.multiply(cache1.X, w_o), b_o));
			// h_t = o_t * tanh(c_t)
			cache1.h = MatrixUtil.elementMultiply(cache1.ho, this.tanh.getY(cache1.c));
			// y = w_y . [h_t, x_t] + b_y
			Double[] y = MatrixUtil.plus(MatrixUtil.multiply(cache1.h, w_y), b_y);
			// probability = softmax(y)
			cache1.a = Softmax.getY(y);

			return cache1;

		} catch (MatrixException e) {
			throw new DnnException(e.getMessage());
		}
	}

	public void backward(Integer y1, Double[] y, LstmDCache dCache, LstmCache cache1, Double[] dhUpper) throws DnnException {

		try {
			Double[][] dwy = null;
			Double[] dby = null;
			Double[] dh = null;
			if (dhUpper == null) {
				Double[] dy = null;
				if (y1 != null) {
					dy = new Double[cache1.a.length];
					for (int i = 0; i < dy.length; i++) {
						dy[i] = cache1.a[i];
					}
					dy[y1] -= 1;
				} else if (y != null) {
					dy = new Double[cache1.a.length];
					for (int i = 0; i < dy.length; i++) {
						dy[i] = cache1.a[i] - y[i];
					}
				} else {
					dy = MatrixUtil.create(cache1.a.length, 0.0);
				}
				dwy = MatrixUtil.multiplyTC(cache1.h, dy);
				dby = dy.clone();
				dh = MatrixUtil.plus(MatrixUtil.multiplyCT(dy, w_y), dCache.dhNext);
			} else {
				dwy = MatrixUtil.create(this.layerSize, this.outputSize, 0.0);
				dby = MatrixUtil.create(this.outputSize, 0.0);
				dh = dhUpper.clone();
			}

			// Gradient for ho in h = ho * tanh(c)
			Double[] dho = MatrixUtil.elementMultiply(this.tanh.getY(cache1.c), dh, this.σ.getDerivativeY(cache1.ho));

			// Gradient for c in h = ho * tanh(c)
			Double[] dc = MatrixUtil.elementMultiply(cache1.ho, dh, this.tanh.getDerivativeX(cache1.c));
			dc = MatrixUtil.plus(dc, dCache.dcNext);

			// Gradient for hf in c = hf * c_old + hi * hc
			Double[] dhf = MatrixUtil.elementMultiply(cache1.cOld, dc, this.σ.getDerivativeY(cache1.hf));

			// Gradient for hi in c = hf * c_old + hi * hc
			Double[] dhi = MatrixUtil.elementMultiply(cache1.hc, dc, this.σ.getDerivativeY(cache1.hi));

			// Gradient for hc in c = hf * c_old + hi * hc
			Double[] dhc = MatrixUtil.elementMultiply(cache1.hi, dc, this.tanh.getDerivativeY(cache1.hc));

			// Gate gradient
			Double[][] dwf = MatrixUtil.multiplyTC(cache1.X, dhf);
			Double[] dbf = dhf.clone();
			Double[] dXf = MatrixUtil.multiplyCT(dhf, w_f);

			Double[][] dwi = MatrixUtil.multiplyTC(cache1.X, dhi);
			Double[] dbi = dhi.clone();
			Double[] dXi = MatrixUtil.multiplyCT(dhi, w_i);

			Double[][] dwo = MatrixUtil.multiplyTC(cache1.X, dho);
			Double[] dbo = dho.clone();
			Double[] dXo = MatrixUtil.multiplyCT(dho, w_o);

			Double[][] dwc = MatrixUtil.multiplyTC(cache1.X, dhc);
			Double[] dbc = dhc.clone();
			Double[] dXc = MatrixUtil.multiplyCT(dhc, w_c);

			Double[] dX = MatrixUtil.plus(dXf, dXi, dXo, dXc);

			dCache.dhNext = VectorUtil.subVector(dX, layerSize);
			dCache.dxUpper = VectorUtil.subVector(dX, layerSize, inputSize);
			// Gradient for c_old in c = hf * c_old + hi * hc
			dCache.dcNext = MatrixUtil.elementMultiply(cache1.hf, dc);

			// Upgrade weights & biases
			dCache.dwf = (dCache.dwf == null) ? dwf : MatrixUtil.plus(dCache.dwf, dwf);
			dCache.dbf = (dCache.dbf == null) ? dbf : MatrixUtil.plus(dCache.dbf, dbf);
			dCache.dwi = (dCache.dwi == null) ? dwi : MatrixUtil.plus(dCache.dwi, dwi);
			dCache.dbi = (dCache.dbi == null) ? dbi : MatrixUtil.plus(dCache.dbi, dbi);
			dCache.dwo = (dCache.dwo == null) ? dwo : MatrixUtil.plus(dCache.dwo, dwo);
			dCache.dbo = (dCache.dbo == null) ? dbo : MatrixUtil.plus(dCache.dbo, dbo);
			dCache.dwc = (dCache.dwc == null) ? dwc : MatrixUtil.plus(dCache.dwc, dwc);
			dCache.dbc = (dCache.dbc == null) ? dbc : MatrixUtil.plus(dCache.dbc, dbc);
			dCache.dwy = (dCache.dwy == null) ? dwy : MatrixUtil.plus(dCache.dwy, dwy);
			dCache.dby = (dCache.dby == null) ? dby : MatrixUtil.plus(dCache.dby, dby);

		} catch (MatrixException e) {
			throw new DnnException(e.getMessage());
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
	}

	public int getByteLength() {
		return Integer.BYTES * 3 +
				Double.BYTES * ((layerSize + inputSize) * layerSize * 4 + (layerSize * outputSize) + layerSize * 4 + outputSize) +
				Integer.BYTES * (2 * 5 + 5) + Integer.BYTES;
	}

	public byte[] getBytes() {

		int length = Integer.BYTES * 3 +
				Double.BYTES * ((layerSize + inputSize) * layerSize * 4 + (layerSize * outputSize) + layerSize * 4 + outputSize) +
				Integer.BYTES * (2 * 5 + 5);
		byte[] bytes = new byte[length];
		int index = 0;
		index += ByteUtil.putInt(bytes, layerSize, index);
		index += ByteUtil.putInt(bytes, inputSize, index);
		index += ByteUtil.putInt(bytes, outputSize, index);
		index += ByteUtil.putDoubles(bytes, w_f, index);
		index += ByteUtil.putDoubles(bytes, b_f, index);
		index += ByteUtil.putDoubles(bytes, w_i, index);
		index += ByteUtil.putDoubles(bytes, b_i, index);
		index += ByteUtil.putDoubles(bytes, w_c, index);
		index += ByteUtil.putDoubles(bytes, b_c, index);
		index += ByteUtil.putDoubles(bytes, w_o, index);
		index += ByteUtil.putDoubles(bytes, b_o, index);
		index += ByteUtil.putDoubles(bytes, w_y, index);
		index += ByteUtil.putDoubles(bytes, b_y, index);

		return bytes;
	}

	public static Lstm getInstance(byte[] bytes) {

		int index = 0;
		//int length = ByteUtil.getInt(bytes, index);
		//index += Integer.BYTES;
		int layerSize = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		int inputSize = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		int outputSize = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;

		Lstm lstm = new Lstm(layerSize, inputSize, outputSize);
		lstm.w_f = ByteUtil.getDouble2s(bytes, index);
		index += lstm.w_f.length * lstm.w_f[0].length * Double.BYTES + 2 * Integer.BYTES;
		lstm.b_f = ByteUtil.getDouble1s(bytes, index);
		index += lstm.b_f.length * Double.BYTES + Integer.BYTES;
		lstm.w_i = ByteUtil.getDouble2s(bytes, index);
		index += lstm.w_i.length * lstm.w_i[0].length * Double.BYTES + 2 * Integer.BYTES;
		lstm.b_i = ByteUtil.getDouble1s(bytes, index);
		index += lstm.b_i.length * Double.BYTES + Integer.BYTES;
		lstm.w_c = ByteUtil.getDouble2s(bytes, index);
		index += lstm.w_c.length * lstm.w_c[0].length * Double.BYTES + 2 * Integer.BYTES;
		lstm.b_c = ByteUtil.getDouble1s(bytes, index);
		index += lstm.b_c.length * Double.BYTES + Integer.BYTES;
		lstm.w_o = ByteUtil.getDouble2s(bytes, index);
		index += lstm.w_o.length * lstm.w_o[0].length * Double.BYTES + 2 * Integer.BYTES;
		lstm.b_o = ByteUtil.getDouble1s(bytes, index);
		index += lstm.b_o.length * Double.BYTES + Integer.BYTES;
		lstm.w_y = ByteUtil.getDouble2s(bytes, index);
		index += lstm.w_y.length * lstm.w_y[0].length * Double.BYTES + 2 * Integer.BYTES;
		lstm.b_y = ByteUtil.getDouble1s(bytes, index);
		index += lstm.b_y.length * Double.BYTES + Integer.BYTES;

		return lstm;
	}
}
