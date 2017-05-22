package cn.standardai.api.ml.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ml.exception.JSONFormatException;

public class DnnTrainSetting {

	private int[] diverseDataRate;

	private Double dth;

	private Double learningRate;

	private Integer epoch;

	private Integer trainSecond;

	private Integer batchSize;

	private Integer watchEpoch;

	private Integer testLossIncreaseTolerance;

	public Double getDth() {
		return dth;
	}

	public void setDth(Double dth) {
		this.dth = dth;
	}

	public Double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(Double learningRate) {
		this.learningRate = learningRate;
	}

	public Integer getEpoch() {
		return epoch;
	}

	public void setEpoch(Integer epoch) {
		this.epoch = epoch;
	}

	public Integer getTrainSecond() {
		return trainSecond;
	}

	public void setTrainSecond(Integer trainSecond) {
		this.trainSecond = trainSecond;
	}

	public Integer getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public Integer getWatchEpoch() {
		return watchEpoch;
	}

	public void setWatchEpoch(Integer watchEpoch) {
		this.watchEpoch = watchEpoch;
	}

	public Integer getTestLossIncreaseTolerance() {
		return testLossIncreaseTolerance;
	}

	public void setTestLossIncreaseTolerance(Integer testLossIncreaseTolerance) {
		this.testLossIncreaseTolerance = testLossIncreaseTolerance;
	}

	public int[] getDiverseDataRate() {
		return diverseDataRate;
	}

	public void setDiverseDataRate(int[] diverseDataRate) {
		this.diverseDataRate = diverseDataRate;
	}
	
	/*
	 *   "train":{
	 *     "diverseDataRate": [8,1,1],
	 *     "dth":1,
	 *     "learningRate":0.07,
	 *     "epoch":8000,
	 *     "trainSecond": 3600,
	 *     "batchSize": 100,
	 *     "watchEpoch":1,
	 *     "testLossIncreaseTolerance":3
	 *   }
	 */
	public static DnnTrainSetting parse(JSONObject json) throws JSONFormatException {

		DnnTrainSetting ts = new DnnTrainSetting();

		JSONArray diverseDataRate = json.getJSONArray("diverseDataRate");
		if (diverseDataRate != null) {
			int[] rates = new int[3];
			for (int i = 0; i < rates.length; i++) {
				if (i < diverseDataRate.size()) {
					rates[i] = diverseDataRate.getIntValue(i);
				} else {
					rates[i] = 0;
				}
			}
			ts.diverseDataRate = rates;
		}
		ts.dth = json.getDouble("dth");
		ts.learningRate = json.getDouble("learningRate");
		ts.epoch = json.getInteger("epoch");
		ts.trainSecond = json.getInteger("trainSecond");
		ts.batchSize = json.getInteger("batchSize");
		ts.watchEpoch = json.getInteger("watchEpoch");
		ts.testLossIncreaseTolerance = json.getInteger("testLossIncreaseTolerance");
		return ts;
	}
}
