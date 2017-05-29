package cn.standardai.lib.algorithm.base;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.standardai.lib.algorithm.exception.DnnException;
import cn.standardai.lib.algorithm.exception.UsageException;
import cn.standardai.lib.base.matrix.MatrixException;

public abstract class Dnn<T extends DnnData> implements Monitorable, Trainable {

	public enum ValueType { Single, Multiple };

	public final String lock = "lock";

	protected T[] data;

	public ValueType dataResultType;

	private int trainDataCnt;

	private int testDataCnt;

	private int verifyDataCnt;

	protected Integer epoch = null;

	protected Integer batchSize = null;

	protected Integer watchEpoch = null;

	protected Long trainMillisecond = null;

	protected Integer testLossIncreaseTolerance = null;

	private int[] diverseDataRate = { 8, 1, 1 };

	public Map<String, Map<Integer, Double>> indicator = new HashMap<String, Map<Integer, Double>>();

	public void addIndicator(String catalog) {
		this.indicator.put(catalog, new HashMap<Integer, Double>());
	}

	public boolean containCatalog(String catalog) {
		return this.indicator.containsKey(catalog);
	}

	public void finish() throws UsageException {
		// TODO nothing added, reserved, now just for finish indicator
		this.indicator.put("final", new HashMap<Integer, Double>());
	}

	public void record(String catalog, Integer epoch, Double value) throws UsageException {
		Map<Integer, Double> catalogMap = this.indicator.get(catalog);
		if (catalogMap == null) throw new UsageException("无此监控项目(" + catalog + ")");
		catalogMap.put(epoch, value);
	}

	public Map<Integer, Double> getValues(String catalog) throws UsageException {
		return this.indicator.get(catalog);
	}

	public Double getValue(String catalog, Integer epoch) {
		Map<Integer, Double> catalogMap = this.indicator.get(catalog);
		if (catalogMap == null) return null;
		return catalogMap.get(epoch);
	}

	public void mountData(T[] data) {
		this.data = data;
		diverseData();
	}

	public void setDiverseDataRate(int[] diverseDataRate) {
		if (diverseDataRate == null || diverseDataRate.length != 3) return;
		this.diverseDataRate = diverseDataRate;
	}

	private void diverseData() {
		int sum = this.diverseDataRate[0] + this.diverseDataRate[1] + this.diverseDataRate[2];
		if (sum == 0) {
			this.testDataCnt = 0;
			this.verifyDataCnt = 0;
			this.trainDataCnt = this.data.length;
		} else {
			this.testDataCnt = this.data.length * this.diverseDataRate[1] / sum;
			this.verifyDataCnt = this.data.length * this.diverseDataRate[2] / sum;
			this.trainDataCnt = this.data.length - testDataCnt - verifyDataCnt;
		}
	}

	public T getTrainData(int index) {
		return this.data[index];
	}

	public T getTestData(int index) {
		return this.data[trainDataCnt + index];
	}

	public T getVerifyData(int index) {
		return this.data[trainDataCnt + testDataCnt + index];
	}

	public int getTrainDataCnt() {
		return this.trainDataCnt;
	}

	public int getTestDataCnt() {
		return this.testDataCnt;
	}

	public int getVerifyDataCnt() {
		return this.verifyDataCnt;
	}

	public void setEpoch(Integer epoch) {
		if (epoch != null) this.epoch = epoch;
	}

	public void setTrainSecond(Integer trainSecond) {
		if (trainSecond != null) this.trainMillisecond = trainSecond * 1000L;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public void setTestLossIncreaseTolerance(Integer testLossIncreaseTolerance) {
		this.testLossIncreaseTolerance = testLossIncreaseTolerance;
	}

	public void setWatchEpoch(Integer watchEpoch) {
		this.watchEpoch = watchEpoch;
	}

	public abstract byte[] getBytes();

	public abstract void train() throws DnnException, MatrixException;

	protected List<Integer> initIndice(int length) {
		List<Integer> indice = new LinkedList<Integer>();
		for (int i = 0; i < length; i++) {
			indice.add(i);
		}
		return indice;
	}

	protected Integer[] getNextBatchIndex(List<Integer> indice, Integer number) {
		if (number == null) number = indice.size();
		Integer[] batchIndice = new Integer[Math.min(indice.size(), number)];
		for (int i = 0; i < batchIndice.length; i++) {
			int randNumber = new Double(Math.random() * indice.size()).intValue();
			batchIndice[i] = indice.get(randNumber);
			indice.remove(randNumber);
		}
		return batchIndice;
	}
}
