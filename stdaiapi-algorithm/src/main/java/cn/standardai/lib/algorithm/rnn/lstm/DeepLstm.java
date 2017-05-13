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

	private Integer epoch = 1;

	private Long trainMillisecond = null;

	private Integer batchSize = null;

	private Integer watchEpoch = null;

	private Integer testLossIncreaseTolerance = null;

	private Integer terminator = null;

	public DerivableFunction σ = new Sigmoid();

	public DerivableFunction tanh = new Tanh();

	public DeepLstm(int[] layerSize, int inputSize, int outputSize) {
		this.inputSize = inputSize;
		this.outputSize = outputSize;
		this.lstms = new Lstm[layerSize.length];
		for (int i = 0; i < this.lstms.length; i++) {
			int layerSizeTemp = layerSize[i];
			int inputSizeTemp;
			int outputSizeTemp = 0;
			if (i == 0) {
				inputSizeTemp = inputSize;
			} else {
				inputSizeTemp = layerSize[i - 1];
			}
			if (i == this.lstms.length - 1) {
				outputSizeTemp = outputSize;
			}
			this.lstms[i] = new Lstm(layerSizeTemp, inputSizeTemp, outputSizeTemp);
		}
	}

	public void train() throws DnnException, MatrixException {

		long startTime = new Date().getTime();
		List<Integer> indice = initIndice(getTrainDataCnt());
		boolean needBreak = false;
		double lastTestLoss = Double.MAX_VALUE;
		int testLossIncreaseTime = 0;
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
				double testLoss = trainBatch(batchIndice, watch);
				if (trainMillisecond != null && (new Date().getTime() - startTime) >= trainMillisecond) {
					needBreak = true;
					break;
				}
				if (testLossIncreaseTolerance != null && testLoss != 0.0) {
					if (testLoss > lastTestLoss) {
						testLossIncreaseTime++;
						if (testLossIncreaseTime > testLossIncreaseTolerance) {
							needBreak = true;
							break;
						}
					} else {
						testLossIncreaseTime = 0;
					}
					lastTestLoss = testLoss;
				}
				watch = false;
			}
			if (needBreak) break;
			if (epoch != null && epochCount >= epoch) break;
		}
		// Finish indicator, tell monitor to stop monitoring
		synchronized (this.indicator) {
			finish();
			this.indicator.notify();
		}
	}

	private double trainBatch(Integer[] indice, boolean watch) throws DnnException, MatrixException {

		Double[] trainLoss = MatrixUtil.create(indice.length, 0.0);
		LstmDCache[] dCaches4Train = new LstmDCache[lstms.length];
		for (int i = 0; i < dCaches4Train.length; i++) dCaches4Train[i] = new LstmDCache();
		for (int trainDataIndex = 0; trainDataIndex < indice.length; trainDataIndex++) {
			// 1 sample in batch
			LstmData trainData1 = getTrainData(indice[trainDataIndex]);

			// forward
			List<List<LstmCache>> caches4Train = new ArrayList<List<LstmCache>>();
			for (int lstmIndex = 0; lstmIndex < lstms.length; lstmIndex++) {
				List<LstmCache> caches1Layer4Train = new ArrayList<LstmCache>();
				Double[] hOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
				Double[] cOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
				if (lstmIndex == 0) {
					// 1st lstm layer's forward
					for (int inputIndex = 0; inputIndex < trainData1.x.length; inputIndex++) {
						// 1 input
						LstmCache cacheTemp = lstms[lstmIndex].forward(trainData1.x[inputIndex], hOld, cOld);
						caches1Layer4Train.add(cacheTemp);
						hOld = cacheTemp.h.clone();
						cOld = cacheTemp.c.clone();
					}
				} else {
					// others' forward
					for (int inputIndex = 0; inputIndex < caches4Train.get(lstmIndex - 1).size(); inputIndex++) {
						// 1 input
						LstmCache cacheTemp = lstms[lstmIndex].forward(caches4Train.get(lstmIndex - 1).get(inputIndex).h, hOld, cOld);
						caches1Layer4Train.add(cacheTemp);
						hOld = cacheTemp.h.clone();
						cOld = cacheTemp.c.clone();
					}
				}
				caches4Train.add(caches1Layer4Train);
			}

			if (watch) {
				List<LstmCache> cacheLastLayer = caches4Train.get(caches4Train.size() - 1);
				// Loss for train, cost = - ∑x ∑j (yj * ln(aj) + (1 - yj) * ln(1 - aj)) / n
				for (int j = 0; j < trainData1.y.length; j++) {
					// 对每一个输出y
					for (int k = 0; k < outputSize; k++) {
						if (trainData1.y[j] == k) {
							trainLoss[trainDataIndex] += Math.log10(cacheLastLayer.get(cacheLastLayer.size() - trainData1.y.length + j).a[k]);
						} else {
							trainLoss[trainDataIndex] += Math.log10(1 - cacheLastLayer.get(j).a[k]);
						}
					}
				}
				trainLoss[trainDataIndex] *= -1;
			}

			// back propagation
			Double[][][] dhCacheTrain = new Double[lstms.length][][];
			for (int lstmIndex = lstms.length - 1; lstmIndex >= 0; lstmIndex--) {
				List<LstmCache> caches1Layer4Train = caches4Train.get(lstmIndex);
				dhCacheTrain[lstmIndex] = new Double[caches1Layer4Train.size()][];
				dCaches4Train[lstmIndex].dcNext = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
				dCaches4Train[lstmIndex].dhNext = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
				if (lstmIndex == lstms.length - 1) {
					// last lstm layer's back propagation
					for (int j = caches1Layer4Train.size() - 1; j >= 0; j--) {
						if (caches1Layer4Train.size() - j - 1 < trainData1.y.length) {
							lstms[lstmIndex].backward(trainData1.y[j - caches1Layer4Train.size() + trainData1.y.length], dCaches4Train[lstmIndex], caches1Layer4Train.get(j), null);
						} else {
							lstms[lstmIndex].backward(null, dCaches4Train[lstmIndex], caches1Layer4Train.get(j), null);
						}
						dhCacheTrain[lstmIndex][j] = dCaches4Train[lstmIndex].dxUpper;
					}
				} else {
					// others' back propagation
					for (int j = caches1Layer4Train.size() - 1; j >= 0; j--) {
						lstms[lstmIndex].backward(null, dCaches4Train[lstmIndex], caches1Layer4Train.get(j), dhCacheTrain[lstmIndex + 1][j]);
						dhCacheTrain[lstmIndex][j] = dCaches4Train[lstmIndex].dxUpper;
					}
				}
			}
		}

		// adjust parameters
		for (int lstmIndex = lstms.length - 1; lstmIndex >= 0; lstmIndex--) {
			lstms[lstmIndex].normalizeD(dCaches4Train[lstmIndex], indice.length);
			lstms[lstmIndex].adjustParam(dCaches4Train[lstmIndex]);
		}

		if (watch) {
			Double[] testLoss = MatrixUtil.create(getTestDataCnt(), 0.0);
			for (int testDataIndex = 0; testDataIndex < getTestDataCnt(); testDataIndex++) {
				// 1 sample in test data
				LstmData testData1 = getTestData(testDataIndex);

				List<List<LstmCache>> caches4Test = new ArrayList<List<LstmCache>>();
				for (int lstmIndex = 0; lstmIndex < lstms.length; lstmIndex++) {
					List<LstmCache> caches1Layer4Test = new ArrayList<LstmCache>();
					Double[] hOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
					Double[] cOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
					if (lstmIndex == 0) {
						// 1st lstm layer's forward
						for (int inputIndex = 0; inputIndex < testData1.x.length; inputIndex++) {
							// 1 input
							LstmCache cacheTemp = lstms[lstmIndex].forward(testData1.x[inputIndex], hOld, cOld);
							caches1Layer4Test.add(cacheTemp);
							hOld = cacheTemp.h.clone();
							cOld = cacheTemp.c.clone();
						}
					} else {
						// others' forward
						for (int inputIndex = 0; inputIndex < caches4Test.get(lstmIndex - 1).size(); inputIndex++) {
							// 1 input
							LstmCache cacheTemp = lstms[lstmIndex].forward(caches4Test.get(lstmIndex - 1).get(inputIndex).h, hOld, cOld);
							caches1Layer4Test.add(cacheTemp);
							hOld = cacheTemp.h.clone();
							cOld = cacheTemp.c.clone();
						}
					}
					caches4Test.add(caches1Layer4Test);
				}

				List<LstmCache> cacheLastLayer = caches4Test.get(caches4Test.size() - 1);
				// Loss for test, cost = - ∑x ∑j (yj * ln(aj) + (1 - yj) * ln(1 - aj)) / n
				for (int j = 0; j < testData1.y.length; j++) {
					// 对每一个输出y
					for (int k = 0; k < outputSize; k++) {
						if (testData1.y[j] == k) {
							testLoss[testDataIndex] += Math.log10(cacheLastLayer.get(cacheLastLayer.size() - testData1.y.length + j).a[k]);
						} else {
							testLoss[testDataIndex] += Math.log10(1 - cacheLastLayer.get(j).a[k]);
						}
					}
				}
				testLoss[testDataIndex] *= -1;
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
			return totalTestLoss;
		}

		return 0.0;
	}

	public Integer[] predict(Double[][] xs) throws DnnException {

		List<List<LstmCache>> caches4Predict = new ArrayList<List<LstmCache>>();
		for (int lstmIndex = 0; lstmIndex < lstms.length; lstmIndex++) {
			List<LstmCache> caches1Layer4Predict = new ArrayList<LstmCache>();
			Double[] hOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
			Double[] cOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
			if (lstmIndex == 0) {
				// 1st lstm layer's forward
				for (int inputIndex = 0; inputIndex < xs.length; inputIndex++) {
					// 1 input
					LstmCache cacheTemp = lstms[lstmIndex].forward(xs[inputIndex], hOld, cOld);
					caches1Layer4Predict.add(cacheTemp);
					hOld = cacheTemp.h.clone();
					cOld = cacheTemp.c.clone();
				}
			} else {
				// others' forward
				for (int inputIndex = 0; inputIndex < caches4Predict.get(lstmIndex - 1).size(); inputIndex++) {
					// 1 input
					LstmCache cacheTemp = lstms[lstmIndex].forward(caches4Predict.get(lstmIndex - 1).get(inputIndex).h, hOld, cOld);
					caches1Layer4Predict.add(cacheTemp);
					hOld = cacheTemp.h.clone();
					cOld = cacheTemp.c.clone();
				}
			}
			caches4Predict.add(caches1Layer4Predict);
		}

		List<LstmCache> cacheLast = caches4Predict.get(caches4Predict.size() - 1);
		Integer[] result = new Integer[cacheLast.size()];
		for (int i = 0; i < result.length; i++) {
			Roulette r = new Roulette(cacheLast.get(i).a);
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
		this.epochCount = 0;
	}

	public void setDth(Double dth) {
		for (int i = 0; i < lstms.length; i++) lstms[i].setDth(dth);
	}

	public void setLearningRate(Double η) {
		for (int i = 0; i < lstms.length; i++) lstms[i].setLearningRate(η);
	}

	public void setEpoch(Integer epoch) {
		if (epoch != null) this.epoch = epoch;
	}

	public void setTrainSecond(Integer trainSecond) {
		this.trainMillisecond = trainSecond * 1000L;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public void setWatchEpoch(Integer watchEpoch) {
		this.watchEpoch = watchEpoch;
	}

	public void setTestLossIncreaseTolerance(Integer testLossIncreaseTolerance) {
		this.testLossIncreaseTolerance = testLossIncreaseTolerance;
	}

	public void setTerminator(Integer terminator) {
		this.terminator = terminator;
	}
}
