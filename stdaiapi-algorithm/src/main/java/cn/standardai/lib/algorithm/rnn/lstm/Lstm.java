package cn.standardai.lib.algorithm.rnn.lstm;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cn.standardai.lib.algorithm.base.Dnn;
import cn.standardai.lib.algorithm.common.ByteUtil;
import cn.standardai.lib.algorithm.exception.DnnException;
import cn.standardai.lib.algorithm.rnn.lstm.LstmData.Delay;
import cn.standardai.lib.base.function.Roulette;
import cn.standardai.lib.base.function.Softmax;
import cn.standardai.lib.base.function.activate.Sigmoid;
import cn.standardai.lib.base.function.activate.Tanh;
import cn.standardai.lib.base.function.base.DerivableFunction;
import cn.standardai.lib.base.matrix.MatrixException;
import cn.standardai.lib.base.matrix.MatrixUtil;
import cn.standardai.lib.base.matrix.VectorUtil;

public class Lstm extends Dnn {

	private int epochCount = 0;

	private double lastLoss = Double.MAX_VALUE;

	public int layerSize;

	public int inputSize;

	public int outputSize;

	// grad threshold
	private double dth = 1;

	private double η = 1;

	private double dη = 1;

	private double maxη = 1;

	private double gainThreshold = 1;

	private Integer epoch;

	private Long trainSecond;

	private Integer batchSize;

	private Integer watchEpoch;

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
		this.w_y = MatrixUtil.random(layerSize, inputSize, -1.0 / Math.sqrt(inputSize * layerSize), 1.0 / Math.sqrt(layerSize * inputSize));
		//this.w_y = MatrixUtil.random(layerSize, inputSize, 0, 1.0 / Math.sqrt(layerSize));
		this.b_y = MatrixUtil.create(inputSize, 0);
	}

	public void setParam(Double dth, Double η, Double dη, Double maxη,
			Double gainThreshold, Integer epoch, Long trainSecond, Integer batchSize, Integer watchEpoch) {
		if (dth != null) this.dth = dth;
		if (η != null) this.η = η;
		if (dη != null) this.dη = dη;
		if (maxη != null) this.maxη = η;
		if (gainThreshold != null) this.gainThreshold = gainThreshold;
		this.epoch = epoch;
		this.trainSecond = trainSecond;
		this.batchSize = batchSize;
		this.watchEpoch = watchEpoch;
		if (this.epoch == null && this.trainSecond == null) this.epoch = 1;
	}

	public void setDth(Double dth) {
		if (dth != null) this.dth = dth;
	}

	public void setLearningRate(Double η) {
		if (η != null) this.η = η;
	}

	public void setEpoch(Integer epoch) {
		this.epoch = epoch;
	}

	public void setTrainSecond(Long trainSecond) {
		this.trainSecond = trainSecond;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public void setWatchEpoch(Integer watchEpoch) {
		this.watchEpoch = watchEpoch;
	}

	public void train(LstmData[] data) throws DnnException, MatrixException {

		double gain;
		long startTime = new Date().getTime();
		List<Integer> indice = initIndice(data.length);
		while (true) {
			epochCount++;
			boolean watch = false;
			if (watchEpoch != null && epochCount % watchEpoch == 0) {
				watch = true;
			}
			List<Integer> indiceCopy = new LinkedList<Integer>();
			indiceCopy.addAll(indice);
			while (indiceCopy.size() != 0) {
				Integer[] batchIndice = getNextBatchIndex(indiceCopy, batchSize);
				gain = train1(data, batchIndice, watch);
				watch = false;
			}
			if (epoch != null && epochCount >= epoch) break;
			if (trainSecond != null && (new Date().getTime() - startTime) >= trainSecond) break;
		}
		// Finish indicator, tell monitor to stop monitoring
		synchronized (this.indicator) {
			finish();
			this.indicator.notify();
		}
	}

	public double train1(LstmData[] data, Integer[] indice, boolean watch) throws DnnException, MatrixException {

		Double[] loss = MatrixUtil.create(indice.length, 0.0);
		LstmDCache dCache = new LstmDCache();
		for (int i = 0; i < indice.length; i++) {
			// 1 sample in batch
			LstmData data1 = data[indice[i]];
			// Forward
			List<LstmCache> cache = new ArrayList<LstmCache>();
			Double[] hOld = MatrixUtil.create(layerSize, 0);
			Double[] cOld = MatrixUtil.create(layerSize, 0);
			for (int j = 0; j < data1.x.length; j++) {
				LstmCache cache1 = forward(data1.x[j], hOld, cOld);
				cache.add(cache1);
				hOld = cache1.h.clone();
				cOld = cache1.c.clone();
			}
			if (data1.yDelay == Delay.YES) {
				for (int j = 0; j < data1.y.length - 1; j++) {
					LstmCache cache1 = forward(MatrixUtil.create(inputSize, 0.0), hOld, cOld);
					cache.add(cache1);
					hOld = cache1.h.clone();
					cOld = cache1.c.clone();
				}
			}

			// cost = - ∑x ∑j (yj * ln(aj) + (1 - yj) * ln(1 - aj)) / n
			for (int j = 0; j < data1.y.length; j++) {
				// 对每一个输出y
				for (int k = 0; k < outputSize; k++) {
					if (data1.y[j] == k) {
						loss[i] += Math.log10(cache.get(cache.size() - data1.y.length + j).a[k]);
					} else {
						loss[i] += Math.log10(1 - cache.get(j).a[k]);
					}
				}
			}
			//loss[i] /= -data1.y.length;
			loss[i] *= -1;

			dCache.dcNext = MatrixUtil.create(layerSize, 0);
			dCache.dhNext = MatrixUtil.create(layerSize, 0);
			for (int j = cache.size() - 1; j >= 0; j--) {
				if (cache.size() - j - 1 < data1.y.length) {
					backward(data1.y[j - cache.size() + data1.y.length], dCache, cache.get(j));
				} else {
					backward(null, dCache, cache.get(j));
				}
			}
			/*
			for (int j = data1.y.length - 1; j >= 0; j--) {
				backward(data1.y[j], dCache, cache.get(cache.size() - data1.y.length + j));
			}
			for (int j = cache.size() - data1.y.length - 1; j >= 0; j--) {
				backward(null, dCache, cache.get(j));
			}
			*/
		}
		double totalLoss = MatrixUtil.sum(loss) / indice.length;
		normalizeD(dCache, indice.length);
		adjectParam(dCache);

		if (watch) {
			//adjustLearningRate(totalLoss);
			//MatrixUtil.print(w_i[1]);
			synchronized (this.indicator) {
				if (this.containCatalog("loss")) {
					record("loss", epochCount, totalLoss);
				}
				this.indicator.notify();
			}
			System.out.println("Epoch: " + epochCount + ", Loss: " + totalLoss);
		}

		double gain = lastLoss - totalLoss;
		lastLoss = totalLoss;

		return gain;
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

	public void adjectParam(LstmDCache dCache) throws MatrixException {
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

	public void train(Double[][] xs, Integer[] ys) throws DnnException, MatrixException {
		if (xs.length != ys.length) throw new DnnException("训练数据长度错误");
		double gain;
		do {
			epochCount++;
			gain = train1(xs, ys);
		} while (epochCount < epoch || (epoch == -1 && gain >= 0));
		// Finish indicator, tell monitor to stop monitoring
		synchronized (this.indicator) {
			finish();
			this.indicator.notify();
		}
	}

	public double train1(Double[][] xs, Integer[] ys) throws DnnException, MatrixException {

		Double[] loss = MatrixUtil.create(outputSize, 0);
		List<LstmCache> cache = new ArrayList<LstmCache>();

		Double[] hOld = MatrixUtil.create(layerSize, 0);
		Double[] cOld = MatrixUtil.create(layerSize, 0);
		for (int i = 0; i < ys.length; i++) {
			LstmCache cache1 = forward(xs[i], hOld, cOld);
			cache.add(cache1);
			hOld = cache1.h.clone();
			cOld = cache1.c.clone();

			// cost = - ∑x ∑j (yj * ln(aj) + (1 - yj) * ln(1 - aj)) / n
			for (int j = 0; j < loss.length; j++) {
				if (ys[i] == j) {
					loss[j] += Math.log10(cache1.a[j]);
				} else {
					loss[j] += Math.log10(1 - cache1.a[j]);
				}
			}
			//MatrixUtil.print(cache1.a);
		}

		double totalLoss = 0;
		for (int i = 0; i < loss.length; i++) {
			//loss[i] /= -ys.length;
			loss[i] *= -1;
			totalLoss += loss[i];
		}

		if (watchEpoch != null && epochCount % watchEpoch == 0) {
			//adjustLearningRate(totalLoss);
			//MatrixUtil.print(w_i[1]);
			synchronized (this.indicator) {
				if (this.containCatalog("loss")) {
					record("loss", epochCount, totalLoss);
				}
				this.indicator.notify();
			}
			System.out.println("Epoch: " + epochCount + ", Loss: " + totalLoss);
		}

		LstmDCache dCache = new LstmDCache();
		dCache.dcNext = MatrixUtil.create(layerSize, 0);
		dCache.dhNext = MatrixUtil.create(layerSize, 0);
		for (int i = cache.size() - 1; i >= 0; i--) {
			backward(ys[i], dCache, cache.get(i));
		}
		adjectParam(dCache);

		double gain = lastLoss - totalLoss;
		lastLoss = totalLoss;

		return gain;
	}

	public Integer[] predict(Double[][] xs, int step) throws DnnException {

		Double[] h_t1 = MatrixUtil.create(layerSize, 0);
		Double[] c_t1 = MatrixUtil.create(layerSize, 0);
		for (int i = 0; i < xs.length - 1; i++) {
			LstmCache cache1 = forward(xs[i], h_t1, c_t1);
			h_t1 = cache1.h.clone();
			c_t1 = cache1.c.clone();
		}

		Integer[] result = new Integer[step];
		Double[] xPredict = xs[xs.length - 1];
		for (int i = 0; i < step; i++) {

			LstmCache cache1 = forward(xPredict, h_t1, c_t1);

			h_t1 = cache1.h.clone();
			c_t1 = cache1.c.clone();
			xPredict = cache1.a.clone();

			Roulette r = new Roulette(cache1.a);
			result[i] = r.getY();
		}

		return result;
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

	public void backward(Integer y, LstmDCache dCache, LstmCache cache1) throws DnnException {

		try {
			Double[] dy = null;
			if (y == null) {
				dy = MatrixUtil.create(cache1.a.length, 0.0);
				//dy = new Double[cache1.a.length];
				//for (int i = 0; i < dy.length; i++) {
				//	dy[i] = cache1.a[i] - 1;
				//}
			} else {
				dy = new Double[cache1.a.length];
				for (int i = 0; i < dy.length; i++) {
					dy[i] = cache1.a[i];
				}
				dy[y] -= 1;
			}

			Double[][] dwy = MatrixUtil.multiplyTC(cache1.h, dy);
			Double[] dby = dy.clone();
			Double[] dh = MatrixUtil.plus(MatrixUtil.multiplyCT(dy, w_y), dCache.dhNext);

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

	private void adjustLearningRate(double totalLoss) {

		if (totalLoss > lastLoss) {
			// Bad
			//η *= (totalLoss / lastLoss / 0.999);
			//η *= 1.2;
			//if (η > 0.1) η = 0.1;
			//η /= dη;
			//η *= dη;
		} else if (totalLoss < lastLoss) {
			// Good
			//η *= dη;
			//η *= (totalLoss / lastLoss * 0.999);
			if (totalLoss / lastLoss > gainThreshold) {
				//η /= dη;
			} else {
				//η *= dη;
			}
		}
		if (η > maxη) η = maxη;
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

	public static byte[] getBytes(Lstm lstm) {

		int length = Integer.BYTES * 3 +
				Double.SIZE * ((lstm.layerSize + lstm.inputSize) * lstm.layerSize * 4 +
				(lstm.layerSize + lstm.inputSize) + lstm.layerSize * 4 + lstm.inputSize);
		byte[] bytes = new byte[length];
		int index = 0;
		index += ByteUtil.putInt(bytes, lstm.layerSize, index);
		index += ByteUtil.putInt(bytes, lstm.inputSize, index);
		index += ByteUtil.putInt(bytes, lstm.outputSize, index);
		index += ByteUtil.putDoubles(bytes, lstm.w_f, index);
		index += ByteUtil.putDoubles(bytes, lstm.b_f, index);
		index += ByteUtil.putDoubles(bytes, lstm.w_i, index);
		index += ByteUtil.putDoubles(bytes, lstm.b_i, index);
		index += ByteUtil.putDoubles(bytes, lstm.w_c, index);
		index += ByteUtil.putDoubles(bytes, lstm.b_c, index);
		index += ByteUtil.putDoubles(bytes, lstm.w_o, index);
		index += ByteUtil.putDoubles(bytes, lstm.b_o, index);
		index += ByteUtil.putDoubles(bytes, lstm.w_y, index);
		index += ByteUtil.putDoubles(bytes, lstm.b_y, index);

		return bytes;
	}

	public static Lstm getInstance(byte[] bytes) {

		int index = 0;
		int length = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		int layerSize = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		int inputSize = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		int outputSize = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;

		Lstm lstm = new Lstm(layerSize, inputSize, outputSize);
		lstm.w_f = ByteUtil.getDouble2s(bytes, index);
		index += lstm.w_f.length * lstm.w_f[0].length * Double.BYTES;
		lstm.b_f = ByteUtil.getDouble1s(bytes, index);
		index += lstm.w_f.length * lstm.w_f[0].length * Double.BYTES;
		lstm.w_i = ByteUtil.getDouble2s(bytes, index);
		index += lstm.w_f.length * lstm.w_f[0].length * Double.BYTES;
		lstm.b_i = ByteUtil.getDouble1s(bytes, index);
		index += lstm.w_f.length * lstm.w_f[0].length * Double.BYTES;
		lstm.w_c = ByteUtil.getDouble2s(bytes, index);
		index += lstm.w_f.length * lstm.w_f[0].length * Double.BYTES;
		lstm.b_c = ByteUtil.getDouble1s(bytes, index);
		index += lstm.w_f.length * lstm.w_f[0].length * Double.BYTES;
		lstm.w_o = ByteUtil.getDouble2s(bytes, index);
		index += lstm.w_f.length * lstm.w_f[0].length * Double.BYTES;
		lstm.b_o = ByteUtil.getDouble1s(bytes, index);
		index += lstm.w_f.length * lstm.w_f[0].length * Double.BYTES;
		lstm.w_y = ByteUtil.getDouble2s(bytes, index);
		index += lstm.w_f.length * lstm.w_f[0].length * Double.BYTES;
		lstm.b_y = ByteUtil.getDouble1s(bytes, index);
		index += lstm.w_f.length * lstm.w_f[0].length * Double.BYTES;

		return lstm;
	}

	private List<Integer> initIndice(int length) {
		List<Integer> indice = new LinkedList<Integer>();
		for (int i = 0; i < length; i++) {
			indice.add(i);
		}
		return indice;
	}

	private Integer[] getNextBatchIndex(List<Integer> indice, Integer number) {
		if (number == null) number = indice.size();
		Integer[] batchIndice = new Integer[Math.min(indice.size(), number)];
		for (int i = 0; i < batchIndice.length; i++) {
			int randNumber = new Double(Math.random() * indice.size()).intValue();
			batchIndice[i] = indice.get(randNumber);
			indice.remove(randNumber);
		}
		return batchIndice;
	}

	public void reset() {
		this.epochCount = 0;
	}
}
