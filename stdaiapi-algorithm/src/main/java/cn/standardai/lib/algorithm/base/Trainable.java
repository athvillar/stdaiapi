package cn.standardai.lib.algorithm.base;

public interface Trainable {

	public void setDth(Double dth);

	public void setLearningRate(Double η);

	public void setEpoch(Integer epoch);

	public void setTrainSecond(Integer trainSecond);

	public void setBatchSize(Integer batchSize);

	public void setWatchEpoch(Integer watchEpoch);

	public void setTestLossIncreaseTolerance(Integer testLossIncreaseTolerance);
}
