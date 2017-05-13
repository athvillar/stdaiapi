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

public class DeepLstm extends Dnn<LstmData> {

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

	public DeepLstm(int[] layerSize, int inputSize, int outputSize) {
		this.inputSize = inputSize;
		this.outputSize = outputSize;
		this.lstms = new Lstm[layerSize.length];
		this.lstms[0] = new Lstm(layerSize[0], inputSize, 0);
		this.lstms[1] = new Lstm(layerSize[1], layerSize[0], outputSize);
	}

	public void train() throws DnnException, MatrixException {

		long startTime = new Date().getTime();
		List<Integer> indice = initIndice(getTrainDataCnt());
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
				train1(batchIndice, watch);
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

	public void train1(Integer[] indice, boolean watch) throws DnnException, MatrixException {

		Double[] trainLoss = MatrixUtil.create(indice.length, 0.0);
		LstmDCache dCacheTrain1 = new LstmDCache();
		LstmDCache dCacheTrain2 = new LstmDCache();
		for (int trainDataIndex = 0; trainDataIndex < indice.length; trainDataIndex++) {
			// 1 sample in batch
			LstmData trainData1 = getTrainData(indice[trainDataIndex]);
			// Forward lstm1
			List<LstmCache> cacheTrain1 = new ArrayList<LstmCache>();
			Double[] hOldTrain1 = MatrixUtil.create(lstms[0].layerSize, 0);
			Double[] cOldTrain1 = MatrixUtil.create(lstms[0].layerSize, 0);
			for (int j = 0; j < trainData1.x.length; j++) {
				LstmCache cacheTemp = lstms[0].forward(trainData1.x[j], hOldTrain1, cOldTrain1);
				cacheTrain1.add(cacheTemp);
				hOldTrain1 = cacheTemp.h.clone();
				cOldTrain1 = cacheTemp.c.clone();
			}

			// Forward lstm2
			List<LstmCache> cacheTrain2 = new ArrayList<LstmCache>();
			Double[] hOldTrain2 = MatrixUtil.create(lstms[1].layerSize, 0);
			Double[] cOldTrain2 = MatrixUtil.create(lstms[1].layerSize, 0);
			for (int j = 0; j < cacheTrain1.size(); j++) {
				LstmCache cacheTemp = lstms[1].forward(cacheTrain1.get(j).h, hOldTrain2, cOldTrain2);
				cacheTrain2.add(cacheTemp);
				hOldTrain2 = cacheTemp.h.clone();
				cOldTrain2 = cacheTemp.c.clone();
			}

			if (watch) {
				// Loss for train, cost = - ∑x ∑j (yj * ln(aj) + (1 - yj) * ln(1 - aj)) / n
				for (int j = 0; j < trainData1.y.length; j++) {
					// 对每一个输出y
					for (int k = 0; k < outputSize; k++) {
						if (trainData1.y[j] == k) {
							trainLoss[trainDataIndex] += Math.log10(cacheTrain2.get(cacheTrain2.size() - trainData1.y.length + j).a[k]);
						} else {
							trainLoss[trainDataIndex] += Math.log10(1 - cacheTrain2.get(j).a[k]);
						}
					}
				}
				trainLoss[trainDataIndex] *= -1;
			}

			// back propagation lstm2
			dCacheTrain2.dcNext = MatrixUtil.create(lstms[1].layerSize, 0);
			dCacheTrain2.dhNext = MatrixUtil.create(lstms[1].layerSize, 0);
			Double[][] dhCacheTrain = new Double[cacheTrain2.size()][];
			for (int j = cacheTrain2.size() - 1; j >= 0; j--) {
				if (cacheTrain2.size() - j - 1 < trainData1.y.length) {
					lstms[1].backward(trainData1.y[j - cacheTrain2.size() + trainData1.y.length], dCacheTrain2, cacheTrain2.get(j), null);
				} else {
					lstms[1].backward(null, dCacheTrain2, cacheTrain2.get(j), null);
				}
				dhCacheTrain[j] = dCacheTrain2.dxUpper;
			}

			// back propagation lstm1
			dCacheTrain1.dcNext = MatrixUtil.create(lstms[0].layerSize, 0);
			dCacheTrain1.dhNext = MatrixUtil.create(lstms[0].layerSize, 0);
			for (int j = cacheTrain1.size() - 1; j >= 0; j--) {
				if (cacheTrain1.size() - j - 1 < trainData1.y.length) {
					lstms[0].backward(trainData1.y[j - cacheTrain1.size() + trainData1.y.length], dCacheTrain1, cacheTrain1.get(j), dhCacheTrain[j]);
				} else {
					lstms[0].backward(null, dCacheTrain1, cacheTrain1.get(j), dhCacheTrain[j]);
				}
			}
		}
		lstms[1].normalizeD(dCacheTrain2, indice.length);
		lstms[1].adjectParam(dCacheTrain2);
		lstms[0].normalizeD(dCacheTrain1, indice.length);
		lstms[0].adjectParam(dCacheTrain1);

		if (watch) {
			Double[] testLoss = MatrixUtil.create(getTestDataCnt(), 0.0);
			for (int testIndex = 0; testIndex < getTestDataCnt(); testIndex++) {
				// 1 sample in test data
				LstmData testData1 = getTestData(testIndex);

				// Forward lstm1
				List<LstmCache> cacheTest1 = new ArrayList<LstmCache>();
				Double[] hOldTest1 = MatrixUtil.create(lstms[0].layerSize, 0);
				Double[] cOldTest1 = MatrixUtil.create(lstms[0].layerSize, 0);
				for (int j = 0; j < testData1.x.length; j++) {
					LstmCache cacheTemp = lstms[0].forward(testData1.x[j], hOldTest1, cOldTest1);
					cacheTest1.add(cacheTemp);
					hOldTest1 = cacheTemp.h.clone();
					cOldTest1 = cacheTemp.c.clone();
				}

				// Forward lstm2
				List<LstmCache> cacheTest2 = new ArrayList<LstmCache>();
				Double[] hOldTest2 = MatrixUtil.create(lstms[1].layerSize, 0);
				Double[] cOldTest2 = MatrixUtil.create(lstms[1].layerSize, 0);
				for (int j = 0; j < cacheTest1.size(); j++) {
					LstmCache cacheTemp = lstms[1].forward(cacheTest1.get(j).h, hOldTest2, cOldTest2);
					cacheTest2.add(cacheTemp);
					hOldTest2 = cacheTemp.h.clone();
					cOldTest2 = cacheTemp.c.clone();
				}

				// Loss for test, cost = - ∑x ∑j (yj * ln(aj) + (1 - yj) * ln(1 - aj)) / n
				for (int j = 0; j < testData1.y.length; j++) {
					// 对每一个输出y
					for (int k = 0; k < outputSize; k++) {
						if (testData1.y[j] == k) {
							testLoss[testIndex] += Math.log10(cacheTest2.get(cacheTest2.size() - testData1.y.length + j).a[k]);
						} else {
							testLoss[testIndex] += Math.log10(1 - cacheTest2.get(j).a[k]);
						}
					}
				}
				testLoss[testIndex] *= -1;
			}

			double totalTrainLoss = MatrixUtil.sum(trainLoss) / indice.length;
			double totalTestLoss = MatrixUtil.sum(testLoss) / getTestDataCnt();
			synchronized (this.indicator) {
				if (this.containCatalog("trainLoss")) {
					record("trainLoss", epochCount, totalTrainLoss);
				}
				if (this.containCatalog("testLoss")) {
					record("testLoss", epochCount, totalTestLoss);
				}
				this.indicator.notify();
			}
			System.out.println("Epoch: " + epochCount + ", Train loss: " + totalTrainLoss + ", Test loss: " + totalTestLoss);
		}

		return;
	}

	public Integer[] predict(Double[][] xs) throws DnnException {

		// Forward lstm1
		List<LstmCache> cache1 = new ArrayList<LstmCache>();
		Double[] hOld1 = MatrixUtil.create(lstms[0].layerSize, 0);
		Double[] cOld1 = MatrixUtil.create(lstms[0].layerSize, 0);
		for (int j = 0; j < xs.length; j++) {
			LstmCache cacheTemp = lstms[0].forward(xs[j], hOld1, cOld1);
			cache1.add(cacheTemp);
			hOld1 = cacheTemp.h.clone();
			cOld1 = cacheTemp.c.clone();
		}

		// Forward lstm2
		Double[] hOld2 = MatrixUtil.create(lstms[1].layerSize, 0);
		Double[] cOld2 = MatrixUtil.create(lstms[1].layerSize, 0);
		Integer[] result = new Integer[cache1.size()];
		for (int i = 0; i < cache1.size(); i++) {
			LstmCache cacheTemp = lstms[1].forward(cache1.get(i).h, hOld2, cOld2);
			hOld2 = cacheTemp.h.clone();
			cOld2 = cacheTemp.c.clone();
			Roulette r = new Roulette(cacheTemp.a);
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
		this.epoch = epoch;
		for (int i = 0; i < lstms.length; i++) lstms[i].setEpoch(epoch);
	}

	public void setTrainSecond(Long trainSecond) {
		this.trainSecond = trainSecond;
		for (int i = 0; i < lstms.length; i++) lstms[i].setTrainSecond(trainSecond);
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
		for (int i = 0; i < lstms.length; i++) lstms[i].setBatchSize(batchSize);
	}

	public void setWatchEpoch(Integer watchEpoch) {
		this.watchEpoch = watchEpoch;
		for (int i = 0; i < lstms.length; i++) lstms[i].setWatchEpoch(watchEpoch);
	}
}
