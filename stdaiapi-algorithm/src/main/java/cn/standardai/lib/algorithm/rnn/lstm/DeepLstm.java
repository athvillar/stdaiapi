package cn.standardai.lib.algorithm.rnn.lstm;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.lib.algorithm.base.Dnn;
import cn.standardai.lib.algorithm.common.ByteUtil;
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

	private boolean delay = false;

	private Boolean selfConnect = false;

	private Integer terminator = null;

	public DerivableFunction σ = new Sigmoid();

	public DerivableFunction tanh = new Tanh();

	public DeepLstm(Lstm[] lstms) {
		this.lstms = lstms;
		this.inputSize = lstms[0].inputSize;
		this.outputSize = lstms[lstms.length - 1].outputSize;
	}

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
		epochCount = 0;
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
				if (lstmIndex == 0 && lstmIndex == lstms.length - 1) {
					// 1-layer's forward
					forward(lstmIndex, trainData1.x, caches1Layer4Train);
					if (this.delay) {
						forward(lstmIndex, MatrixUtil.create(trainData1.getYLength() - 1, lstms[lstmIndex].inputSize, 0.0), caches1Layer4Train);
					}
				} else if (lstmIndex == lstms.length - 1) {
					// last layer's forward
					forward(lstmIndex, caches4Train.get(lstmIndex - 1), caches1Layer4Train);
					if (this.delay) {
						if (this.selfConnect) {
							Double[][] ys = makeYs(trainData1.y1, lstms[lstmIndex].outputSize);
							forward(lstmIndex, MatrixUtil.subMatrix(ys, ys.length - 1, lstms[lstmIndex].outputSize, 1), caches1Layer4Train);
						} else {
							forward(lstmIndex, MatrixUtil.create(trainData1.getYLength() - 1, lstms[lstmIndex].inputSize, 0.0), caches1Layer4Train);
						}
					}
				} else if (lstmIndex == 0) {
					// 1st lstm layer's forward
					forward(lstmIndex, trainData1.x, caches1Layer4Train);
				} else {
					// others' forward
					forward(lstmIndex, caches4Train.get(lstmIndex - 1), caches1Layer4Train);
				}
				caches4Train.add(caches1Layer4Train);
			}

			if (watch) {
				List<LstmCache> cacheLastLayer = caches4Train.get(caches4Train.size() - 1);
				// Loss for train, cost = - ∑x ∑j (yj * ln(aj) + (1 - yj) * ln(1 - aj)) / n
				for (int j = 0; j < trainData1.getYLength(); j++) {
					// 对每一个输出y
					if (trainData1.y1 != null) {
						for (int k = 0; k < outputSize; k++) {
							if (trainData1.y1[j] == k) {
								trainLoss[trainDataIndex] += Math.log(cacheLastLayer.get(cacheLastLayer.size() - trainData1.getYLength() + j).a[k]);
							} else {
								trainLoss[trainDataIndex] += Math.log(1 - cacheLastLayer.get(cacheLastLayer.size() - trainData1.getYLength() + j).a[k]);
							}
						}
					} else {
						for (int k = 0; k < outputSize; k++) {
							trainLoss[trainDataIndex] += (trainData1.y[j][k] * Math.log(cacheLastLayer.get(cacheLastLayer.size() - trainData1.getYLength() + j).a[k]) +
									(1 - trainData1.y[j][k]) * Math.log((1 - cacheLastLayer.get(cacheLastLayer.size() - trainData1.getYLength() + j).a[k])));
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
						if (caches1Layer4Train.size() - j - 1 < trainData1.getYLength()) {
							lstms[lstmIndex].backward(trainData1.y1 == null ? null : trainData1.y1[j - caches1Layer4Train.size() + trainData1.getYLength()],
									trainData1.y == null ? null : trainData1.y[j - caches1Layer4Train.size() + trainData1.getYLength()],
									dCaches4Train[lstmIndex], caches1Layer4Train.get(j), null);
						} else {
							lstms[lstmIndex].backward(null, null, dCaches4Train[lstmIndex], caches1Layer4Train.get(j), dCaches4Train[lstmIndex].dhNext);
						}
						dhCacheTrain[lstmIndex][j] = dCaches4Train[lstmIndex].dxUpper;
					}
				} else {
					// others' back propagation
					for (int j = caches1Layer4Train.size() - 1; j >= 0; j--) {
						lstms[lstmIndex].backward(null, null, dCaches4Train[lstmIndex], caches1Layer4Train.get(j), dhCacheTrain[lstmIndex + 1][j]);
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
					if (lstmIndex == 0 && lstmIndex == lstms.length - 1) {
						// 1-layer's forward
						forward(lstmIndex, testData1.x, caches1Layer4Test);
						if (this.delay) {
							forward(lstmIndex, MatrixUtil.create(testData1.getYLength() - 1, lstms[lstmIndex].inputSize, 0.0), caches1Layer4Test);
						}
					} else if (lstmIndex == lstms.length - 1) {
						// last layer's forward
						forward(lstmIndex, caches4Test.get(lstmIndex - 1), caches1Layer4Test);
						if (this.delay) {
							if (this.selfConnect) {
								Double[][] ys = makeYs(testData1.y1, lstms[lstmIndex].outputSize);
								forward(lstmIndex, MatrixUtil.subMatrix(ys, ys.length - 1, lstms[lstmIndex].outputSize, 1), caches1Layer4Test);
							} else {
								forward(lstmIndex, MatrixUtil.create(testData1.getYLength() - 1, lstms[lstmIndex].inputSize, 0.0), caches1Layer4Test);
							}
						}
					} else if (lstmIndex == 0) {
						// 1st lstm layer's forward
						forward(lstmIndex, testData1.x, caches1Layer4Test);
					} else {
						// others' forward
						forward(lstmIndex, caches4Test.get(lstmIndex - 1), caches1Layer4Test);
					}
					caches4Test.add(caches1Layer4Test);
				}

				List<LstmCache> cacheLastLayer = caches4Test.get(caches4Test.size() - 1);
				// Loss for test, cost = - ∑x ∑j (yj * ln(aj) + (1 - yj) * ln(1 - aj)) / n
				for (int j = 0; j < testData1.getYLength(); j++) {
					// 对每一个输出y
					if (testData1.y1 != null) {
						for (int k = 0; k < outputSize; k++) {
							if (testData1.y1[j] == k) {
								testLoss[testDataIndex] += Math.log(cacheLastLayer.get(cacheLastLayer.size() - testData1.getYLength() + j).a[k]);
							} else {
								testLoss[testDataIndex] += Math.log(1 - cacheLastLayer.get(cacheLastLayer.size() - testData1.getYLength() + j).a[k]);
							}
						}
					} else {
						for (int k = 0; k < outputSize; k++) {
							testLoss[testDataIndex] += testData1.y[j][k] * Math.log(cacheLastLayer.get(cacheLastLayer.size() - testData1.getYLength() + j).a[k]) +
									(1 - testData1.y[j][k]) * Math.log((1 - cacheLastLayer.get(cacheLastLayer.size() - testData1.getYLength() + j).a[k]));
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
			//System.out.println("Epoch: " + epochCount + ", Train loss: " + totalTrainLoss + ", Test loss: " + totalTestLoss);
			return totalTestLoss;
		}

		return 0.0;
	}

	private Double[][] makeYs(Integer[] y1, int yDimension) {
		Double[][] ys = new Double[y1.length][yDimension];
		for (int i = 0; i < y1.length; i++) {
			for (int j = 0; j < yDimension; j++) {
				if (j == y1[i]) {
					ys[i][j] = 1.0;
				} else {
					ys[i][j] = 0.0;
				}
			}
		}
		return ys;
	}

	private void forward(int lstmIndex, List<LstmCache> cachesLastLayer, List<LstmCache> cachesThisLayer) throws DnnException {
		Double[] hOld;
		Double[] cOld;
		for (int inputIndex = 0; inputIndex < cachesLastLayer.size(); inputIndex++) {
			// 1 input
			if (cachesThisLayer == null || cachesThisLayer.size() == 0) {
				hOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
				cOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
			} else {
				hOld = cachesThisLayer.get(cachesThisLayer.size() - 1).h.clone();
				cOld = cachesThisLayer.get(cachesThisLayer.size() - 1).c.clone();
			}
			LstmCache cacheTemp = lstms[lstmIndex].forward(cachesLastLayer.get(inputIndex).h, hOld, cOld);
			cachesThisLayer.add(cacheTemp);
		}
		return;
	}

	private void forward(int lstmIndex, Double[][] input, List<LstmCache> caches1Layer) throws DnnException {
		Double[] hOld;
		Double[] cOld;
		for (int inputIndex = 0; inputIndex < input.length; inputIndex++) {
			// 1 input
			if (caches1Layer == null || caches1Layer.size() == 0) {
				hOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
				cOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
			} else {
				hOld = caches1Layer.get(caches1Layer.size() - 1).h.clone();
				cOld = caches1Layer.get(caches1Layer.size() - 1).c.clone();
			}
			LstmCache cacheTemp = lstms[lstmIndex].forward(input[inputIndex], hOld, cOld);
			caches1Layer.add(cacheTemp);
		}
		return;
	}

	private void forwardTillTerminate(int lstmIndex, Double[][] input, List<LstmCache> caches1Layer, Integer terminator) throws DnnException {
		Double[] hOld;
		Double[] cOld;
		for (int inputIndex = 0; inputIndex < input.length; inputIndex++) {
			// 1 input
			if (caches1Layer == null || caches1Layer.size() == 0) {
				hOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
				cOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
			} else {
				hOld = caches1Layer.get(caches1Layer.size() - 1).h.clone();
				cOld = caches1Layer.get(caches1Layer.size() - 1).c.clone();
			}
			LstmCache cacheTemp = lstms[lstmIndex].forward(input[inputIndex], hOld, cOld);
			caches1Layer.add(cacheTemp);
			if (inputIndex == input.length - 1) {
				Roulette r = new Roulette(cacheTemp.a);
				if (r.getY() == terminator) {
					return;
				}
			}
		}
		while (true) {
			if (caches1Layer == null || caches1Layer.size() == 0) {
				hOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
				cOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
			} else {
				hOld = caches1Layer.get(caches1Layer.size() - 1).h.clone();
				cOld = caches1Layer.get(caches1Layer.size() - 1).c.clone();
			}
			LstmCache cacheTemp;
			if (this.selfConnect) {
				cacheTemp = lstms[lstmIndex].forward(caches1Layer.get(caches1Layer.size() - 1).a, hOld, cOld);
			} else {
				cacheTemp = lstms[lstmIndex].forward(MatrixUtil.create(lstms[lstmIndex].inputSize, 0.0), hOld, cOld);
			}
			caches1Layer.add(cacheTemp);

			Roulette r = new Roulette(cacheTemp.a);
			if (r.getY() == terminator) {
				return;
			}
		}
	}

	private void forwardTillTerminate(int lstmIndex, List<LstmCache> cachesLastLayer, List<LstmCache> cachesThisLayer, Integer terminator) throws DnnException {
		Double[] hOld;
		Double[] cOld;
		for (int inputIndex = 0; inputIndex < cachesLastLayer.size(); inputIndex++) {
			// 1 input
			if (cachesThisLayer == null || cachesThisLayer.size() == 0) {
				hOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
				cOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
			} else {
				hOld = cachesThisLayer.get(cachesThisLayer.size() - 1).h.clone();
				cOld = cachesThisLayer.get(cachesThisLayer.size() - 1).c.clone();
			}
			LstmCache cacheTemp = lstms[lstmIndex].forward(cachesLastLayer.get(inputIndex).h, hOld, cOld);
			cachesThisLayer.add(cacheTemp);
			if (inputIndex == cachesLastLayer.size() - 1) {
				Roulette r = new Roulette(cacheTemp.a);
				if (r.getY() == terminator) {
					return;
				}
			}
		}
		while (true) {
			if (cachesThisLayer == null || cachesThisLayer.size() == 0) {
				hOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
				cOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
			} else {
				hOld = cachesThisLayer.get(cachesThisLayer.size() - 1).h.clone();
				cOld = cachesThisLayer.get(cachesThisLayer.size() - 1).c.clone();
			}
			LstmCache cacheTemp;
			if (this.selfConnect) {
				cacheTemp = lstms[lstmIndex].forward(cachesThisLayer.get(cachesThisLayer.size() - 1).a, hOld, cOld);
			} else {
				cacheTemp = lstms[lstmIndex].forward(MatrixUtil.create(lstms[lstmIndex].inputSize, 0.0), hOld, cOld);
			}
			cachesThisLayer.add(cacheTemp);

			Roulette r = new Roulette(cacheTemp.a);
			if (r.getY() == terminator) {
				return;
			}
		}
	}

	private void forwardAtSteps(int lstmIndex, Double[][] input, List<LstmCache> caches1Layer, Integer steps) throws DnnException {
		Double[] hOld;
		Double[] cOld;
		for (int inputIndex = 0; inputIndex < input.length; inputIndex++) {
			// 1 input
			if (caches1Layer == null || caches1Layer.size() == 0) {
				hOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
				cOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
			} else {
				hOld = caches1Layer.get(caches1Layer.size() - 1).h.clone();
				cOld = caches1Layer.get(caches1Layer.size() - 1).c.clone();
			}
			LstmCache cacheTemp = lstms[lstmIndex].forward(input[inputIndex], hOld, cOld);
			caches1Layer.add(cacheTemp);
		}
		for (int i = 0; i < steps - 1; i++) {
			if (caches1Layer == null || caches1Layer.size() == 0) {
				hOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
				cOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
			} else {
				hOld = caches1Layer.get(caches1Layer.size() - 1).h.clone();
				cOld = caches1Layer.get(caches1Layer.size() - 1).c.clone();
			}
			LstmCache cacheTemp;
			if (this.selfConnect) {
				cacheTemp = lstms[lstmIndex].forward(caches1Layer.get(caches1Layer.size() - 1).a, hOld, cOld);
			} else {
				cacheTemp = lstms[lstmIndex].forward(MatrixUtil.create(lstms[lstmIndex].inputSize, 0.0), hOld, cOld);
			}
			caches1Layer.add(cacheTemp);
		}
	}

	private void forwardAtSteps(int lstmIndex, List<LstmCache> cachesLastLayer, List<LstmCache> cachesThisLayer, Integer steps) throws DnnException {
		Double[] hOld;
		Double[] cOld;
		for (int inputIndex = 0; inputIndex < cachesLastLayer.size(); inputIndex++) {
			// 1 input
			if (cachesThisLayer == null || cachesThisLayer.size() == 0) {
				hOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
				cOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
			} else {
				hOld = cachesThisLayer.get(cachesThisLayer.size() - 1).h.clone();
				cOld = cachesThisLayer.get(cachesThisLayer.size() - 1).c.clone();
			}
			LstmCache cacheTemp = lstms[lstmIndex].forward(cachesLastLayer.get(inputIndex).h, hOld, cOld);
			cachesThisLayer.add(cacheTemp);
		}
		for (int i = 0; i < steps - 1; i++) {
			if (cachesThisLayer == null || cachesThisLayer.size() == 0) {
				hOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
				cOld = MatrixUtil.create(lstms[lstmIndex].layerSize, 0);
			} else {
				hOld = cachesThisLayer.get(cachesThisLayer.size() - 1).h.clone();
				cOld = cachesThisLayer.get(cachesThisLayer.size() - 1).c.clone();
			}
			LstmCache cacheTemp;
			if (this.selfConnect) {
				cacheTemp = lstms[lstmIndex].forward(cachesThisLayer.get(cachesThisLayer.size() - 1).a, hOld, cOld);
			} else {
				cacheTemp = lstms[lstmIndex].forward(MatrixUtil.create(lstms[lstmIndex].inputSize, 0.0), hOld, cOld);
			}
			cachesThisLayer.add(cacheTemp);
		}
	}

	public Integer[] predictY(Double[][] xs, Integer terminator, Integer steps) throws DnnException {

		if (terminator == null) terminator = this.terminator;
		List<List<LstmCache>> caches4Predict = new ArrayList<List<LstmCache>>();
		for (int lstmIndex = 0; lstmIndex < lstms.length; lstmIndex++) {
			List<LstmCache> caches1Layer4Predict = new ArrayList<LstmCache>();
			if (lstmIndex == 0 && lstmIndex == lstms.length - 1) {
				// 1-layer's forward
				if (terminator != null) {
					forwardTillTerminate(lstmIndex, xs, caches1Layer4Predict, terminator);
				} else if (steps != null) {
					forwardAtSteps(lstmIndex, xs, caches1Layer4Predict, steps);
				} else {
					forward(lstmIndex, xs, caches1Layer4Predict);
				}
			} else if (lstmIndex == lstms.length - 1) {
				// last layer's forward
				if (terminator != null) {
					forwardTillTerminate(lstmIndex, caches4Predict.get(lstmIndex - 1), caches1Layer4Predict, terminator);
				} else if (steps != null) {
					forwardAtSteps(lstmIndex, caches4Predict.get(lstmIndex - 1), caches1Layer4Predict, steps);
				} else {
					forward(lstmIndex, caches4Predict.get(lstmIndex - 1), caches1Layer4Predict);
				}
			} else if (lstmIndex == 0) {
				// 1st lstm layer's forward
				forward(lstmIndex, xs, caches1Layer4Predict);
			} else {
				// others' forward
				forward(lstmIndex, caches4Predict.get(lstmIndex - 1), caches1Layer4Predict);
			}
			caches4Predict.add(caches1Layer4Predict);
		}

		List<LstmCache> cacheLast = caches4Predict.get(caches4Predict.size() - 1);
		if (this.delay) {
			Integer[] result = new Integer[cacheLast.size() - xs.length + 1];
			for (int i = 0; i < result.length; i++) {
				Roulette r = new Roulette(cacheLast.get(xs.length + i - 1).a);
				result[i] = r.getY();
			}
			return result;
		} else {
			Integer[] result = new Integer[cacheLast.size()];
			for (int i = 0; i < result.length; i++) {
				Roulette r = new Roulette(cacheLast.get(i).a);
				result[i] = r.getY();
			}
			return result;
		}
	}

	public Double[][] predictYs(Double[][] xs, Integer steps) throws DnnException {

		List<List<LstmCache>> caches4Predict = new ArrayList<List<LstmCache>>();
		for (int lstmIndex = 0; lstmIndex < lstms.length; lstmIndex++) {
			List<LstmCache> caches1Layer4Predict = new ArrayList<LstmCache>();
			if (lstmIndex == 0 && lstmIndex == lstms.length - 1) {
				// 1-layer's forward
				if (steps != null) {
					forwardAtSteps(lstmIndex, xs, caches1Layer4Predict, steps);
				} else {
					forward(lstmIndex, xs, caches1Layer4Predict);
				}
			} else if (lstmIndex == lstms.length - 1) {
				// last layer's forward
				if (steps != null) {
					forwardAtSteps(lstmIndex, caches4Predict.get(lstmIndex - 1), caches1Layer4Predict, steps);
				} else {
					forward(lstmIndex, caches4Predict.get(lstmIndex - 1), caches1Layer4Predict);
				}
			} else if (lstmIndex == 0) {
				// 1st lstm layer's forward
				forward(lstmIndex, xs, caches1Layer4Predict);
			} else {
				// others' forward
				forward(lstmIndex, caches4Predict.get(lstmIndex - 1), caches1Layer4Predict);
			}
			caches4Predict.add(caches1Layer4Predict);
		}

		List<LstmCache> cacheLast = caches4Predict.get(caches4Predict.size() - 1);
		if (this.delay) {
			Double[][] result = new Double[cacheLast.size() - xs.length + 1][];
			for (int i = 0; i < result.length; i++) {
				result[i] = cacheLast.get(xs.length + i - 1).a;
			}
			return result;
		} else {
			Double[][] result = new Double[cacheLast.size()][];
			for (int i = 0; i < result.length; i++) {
				result[i] = cacheLast.get(i).a;
			}
			return result;
		}
	}

	public void setDelay(boolean delay) {
		this.delay = delay;
	}

	public void setTerminator(Integer terminator) {
		this.terminator = terminator;
	}

	public void setSelfConnect(boolean selfConnect) throws DnnException {
		this.selfConnect = selfConnect;
	}

	public void setSelfConnect() throws DnnException {
		if (this.lstms.length < 2) throw new DnnException("层数过少，无法设置层连接");
		if (this.lstms[lstms.length - 2].outputSize != this.lstms[lstms.length - 2].inputSize) {
			this.lstms[lstms.length - 2] = new Lstm(this.lstms[lstms.length - 1].outputSize,
					this.lstms[lstms.length - 2].inputSize,
					this.lstms[lstms.length - 2].outputSize);
			this.lstms[lstms.length - 1] = new Lstm(this.lstms[lstms.length - 1].layerSize,
					this.lstms[lstms.length - 1].outputSize,
					this.lstms[lstms.length - 1].outputSize);
		}
	}

	public static DeepLstm getInstance(JSONObject structure) throws DnnException {

		JSONArray layerSizeJ = structure.getJSONArray("layerSize");
		Integer inputSize = structure.getInteger("inputSize");
		Integer outputSize = structure.getInteger("outputSize");
		Boolean delay = structure.getBoolean("delay");
		if (layerSizeJ == null || inputSize == null || outputSize == null) {
			throw new DnnException("缺少必要的初始化参数");
		}
		if (delay == null) delay = false;

		int[] layerSize = new int[layerSizeJ.size()];
		for (int i = 0; i < layerSize.length; i++) {
			layerSize[i] = layerSizeJ.getInteger(i);
		}
		DeepLstm dLstm = new DeepLstm(layerSize, inputSize, outputSize);
		dLstm.setDelay(delay);

		return dLstm;
	}

	public static DeepLstm getInstance(byte[] structure) throws DnnException {

		int idx = 0;
		boolean delay = ByteUtil.getBoolean(structure, idx++);
		boolean selfConnect = ByteUtil.getBoolean(structure, idx++);
		int lstmSize = ByteUtil.getInt(structure, idx);
		idx += Integer.BYTES;
		Lstm[] lstm = new Lstm[lstmSize];

		for (int i = 0; i < lstm.length; i++) {
			int lstmLength = ByteUtil.getInt(structure, idx);
			idx += Integer.BYTES;
			byte[] lstmBytes = new byte[lstmLength];
			System.arraycopy(structure, idx, lstmBytes, 0, lstmLength);
			idx += lstmLength;
			lstm[i] = Lstm.getInstance(lstmBytes);
		}

		DeepLstm deepLstm = new DeepLstm(lstm);
		deepLstm.setDelay(delay);
		deepLstm.setSelfConnect(selfConnect);
		return deepLstm;
	}

	@Override
	public byte[] getBytes() {
		int totalLength = 0;
		for (int i = 0; i < lstms.length; i++) {
			totalLength += lstms[i].getByteLength();
		}
		byte[] bytes = new byte[totalLength + Integer.BYTES * lstms.length + Integer.BYTES + 2];
		int idx = 0;
		idx += ByteUtil.putBoolean(bytes, this.delay, idx);
		idx += ByteUtil.putBoolean(bytes, this.selfConnect, idx);
		idx += ByteUtil.putInt(bytes, lstms.length, idx);
		for (int i = 0; i < lstms.length; i++) {
			byte[] lstm1Bytes = lstms[i].getBytes();
			idx += ByteUtil.putInt(bytes, lstm1Bytes.length, idx);
			System.arraycopy(lstm1Bytes, 0, bytes, idx, lstm1Bytes.length);
			idx += lstm1Bytes.length;
		}
		return bytes;
	}

	public void setDth(Double dth) {
		for (int i = 0; i < lstms.length; i++) lstms[i].setDth(dth);
	}

	public void setLearningRate(Double η) {
		for (int i = 0; i < lstms.length; i++) lstms[i].setLearningRate(η);
	}
}
