package cn.standardai.lib.algorithm.rnn.lstm;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cn.standardai.lib.algorithm.base.Dnn;
import cn.standardai.lib.algorithm.exception.DnnException;
import cn.standardai.lib.base.function.Roulette;
import cn.standardai.lib.base.function.activate.Sigmoid;
import cn.standardai.lib.base.function.activate.Tanh;
import cn.standardai.lib.base.function.base.DerivableFunction;
import cn.standardai.lib.base.matrix.MatrixException;
import cn.standardai.lib.base.matrix.MatrixUtil;

public class DeepLstm extends Dnn {

	private Lstm[] lstms;

	private int epochCount = 0;

	public int inputSize;

	public int outputSize;

	private Integer epoch;

	private Long trainSecond;

	private Integer batchSize;

	private Integer watchEpoch;

	public DerivableFunction σ = new Sigmoid();

	public DerivableFunction tanh = new Tanh();

	public DeepLstm(int[] layerSize, int inputSize, int outputSize, int middleSize) {
		this.lstms = new Lstm[layerSize.length];
		this.lstms[0] = new Lstm(layerSize[0], inputSize, middleSize);
		this.lstms[1] = new Lstm(layerSize[1], middleSize, outputSize);
	}

	public void train(LstmData[] data) throws DnnException, MatrixException {

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
				train1(data, batchIndice, watch);
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

	public void train1(LstmData[] data, Integer[] indice, boolean watch) throws DnnException, MatrixException {

		Double[] loss = MatrixUtil.create(indice.length, 0.0);
		LstmDCache dCache = new LstmDCache();
		for (int i = 0; i < indice.length; i++) {
			// 1 sample in batch
			LstmData data1 = data[indice[i]];
			// Forward lstm1
			List<LstmCache> cache1 = new ArrayList<LstmCache>();
			Double[] hOld1 = MatrixUtil.create(lstms[0].layerSize, 0);
			Double[] cOld1 = MatrixUtil.create(lstms[0].layerSize, 0);
			for (int j = 0; j < data1.x.length; j++) {
				LstmCache cache11 = lstms[0].forward(data1.x[j], hOld1, cOld1);
				cache1.add(cache11);
				hOld1 = cache11.h.clone();
				cOld1 = cache11.c.clone();
			}

			// Forward lstm2
			List<LstmCache> cache2 = new ArrayList<LstmCache>();
			Double[] hOld2 = MatrixUtil.create(lstms[1].layerSize, 0);
			Double[] cOld2 = MatrixUtil.create(lstms[1].layerSize, 0);
			for (int j = 0; j < cache1.size(); j++) {
				LstmCache cache21 = lstms[1].forward(cache1.get(j).h, hOld2, cOld2);
				cache2.add(cache21);
				hOld2 = cache21.h.clone();
				cOld2 = cache21.c.clone();
			}

			// cost = - ∑x ∑j (yj * ln(aj) + (1 - yj) * ln(1 - aj)) / n
			for (int j = 0; j < data1.y.length; j++) {
				// 对每一个输出y
				for (int k = 0; k < outputSize; k++) {
					if (data1.y[j] == k) {
						loss[i] += Math.log10(cache2.get(cache2.size() - data1.y.length + j).a[k]);
					} else {
						loss[i] += Math.log10(1 - cache2.get(j).a[k]);
					}
				}
			}
			//loss[i] /= -data1.y.length;
			loss[i] *= -1;

			// back propagation lstm2
			dCache.dcNext = MatrixUtil.create(lstms[1].layerSize, 0);
			dCache.dhNext = MatrixUtil.create(lstms[1].layerSize, 0);
			for (int j = cache2.size() - 1; j >= 0; j--) {
				if (cache2.size() - j - 1 < data1.y.length) {
					lstms[1].backward(data1.y[j - cache2.size() + data1.y.length], dCache, cache2.get(j));
				} else {
					lstms[1].backward(null, dCache, cache2.get(j));
				}
			}

			// back propagation lstm1
			dCache.dcNext = MatrixUtil.create(lstms[0].layerSize, 0);
			dCache.dhNext = MatrixUtil.create(lstms[0].layerSize, 0);
			for (int j = cache1.size() - 1; j >= 0; j--) {
				if (cache1.size() - j - 1 < data1.y.length) {
					lstms[0].backward(data1.y[j - cache1.size() + data1.y.length], dCache, cache1.get(j));
				} else {
					lstms[0].backward(null, dCache, cache1.get(j));
				}
			}
		}
		double totalLoss = MatrixUtil.sum(loss) / indice.length;
		lstms[1].normalizeD(dCache, indice.length);
		lstms[1].adjectParam(dCache);
		lstms[0].normalizeD(dCache, indice.length);
		lstms[0].adjectParam(dCache);

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

		return;
	}

	public Integer[] predict(Double[][] xs, int step) throws DnnException {

		// Forward lstm1
		List<LstmCache> cache1 = new ArrayList<LstmCache>();
		Double[] hOld1 = MatrixUtil.create(lstms[0].layerSize, 0);
		Double[] cOld1 = MatrixUtil.create(lstms[0].layerSize, 0);
		for (int j = 0; j < xs.length - 1; j++) {
			LstmCache cache11 = lstms[0].forward(xs[j], hOld1, cOld1);
			cache1.add(cache11);
			hOld1 = cache11.h.clone();
			cOld1 = cache11.c.clone();
		}

		// Forward lstm2
		Double[] hOld2 = MatrixUtil.create(lstms[1].layerSize, 0);
		Double[] cOld2 = MatrixUtil.create(lstms[1].layerSize, 0);
		Integer[] result = new Integer[step];
		for (int i = 0; i < cache1.size(); i++) {
			LstmCache cache21 = lstms[1].forward(cache1.get(i).h, hOld2, cOld2);
			hOld2 = cache21.h.clone();
			cOld2 = cache21.c.clone();
			Roulette r = new Roulette(cache21.a);
			result[i] = r.getY();
		}

		return result;
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
		for (int i = 0; i < lstms.length; i++) lstms[i].reset();
	}

	public void setDth(Double dth) {
		for (int i = 0; i < lstms.length; i++) lstms[i].setDth(dth);
	}

	public void setLearningRate(Double η) {
		for (int i = 0; i < lstms.length; i++) lstms[i].setLearningRate(η);
	}

	public void setEpoch(Integer epoch) {
		for (int i = 0; i < lstms.length; i++) lstms[i].setEpoch(epoch);
	}

	public void setTrainSecond(Long trainSecond) {
		for (int i = 0; i < lstms.length; i++) lstms[i].setTrainSecond(trainSecond);
	}

	public void setBatchSize(Integer batchSize) {
		for (int i = 0; i < lstms.length; i++) lstms[i].setBatchSize(batchSize);
	}

	public void setWatchEpoch(Integer watchEpoch) {
		for (int i = 0; i < lstms.length; i++) lstms[i].setWatchEpoch(watchEpoch);
	}
}
