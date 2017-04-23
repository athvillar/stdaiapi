/**
* BPNetwork.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.ann;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import cn.standardai.lib.algorithm.exception.AnnException;
import cn.standardai.lib.base.function.Statistic;
import cn.standardai.lib.base.function.activate.Sigmoid;
import cn.standardai.lib.base.function.base.DerivableFunction;
import cn.standardai.lib.base.function.base.PartialDerivableFunction;

/**
 * BP神经网络
 * @author 韩晴
 *
 */
public class BPNetwork implements Cloneable {

	// 学习算法
	private static enum LearnAlgorithm {STANDARD, MOMENTUM, VARIABLE_LEARNING, SELF_ADAPTION_VL, GRADIENT_FACTOR};

	// 结束条件
	private static enum FinishCondition {CONVERGENCY, MAX_TRAIN};

	// 结束条件
	private FinishCondition finishCondition = FinishCondition.MAX_TRAIN;
	
	// 训练样例集合
	private List<Sample> trainingSamples;

	// 网络输入
	private double[] x;

	// 网络输出
	private double[] y;

	// 层
	private Layer[] layers;

	// 节点输出，第i层第j节点
	private double[][] o;

	// 节点权值，第i层第j节点，上一层第k输入节点
	private double[][][] win;

	// 节点权值，第i层第j节点，下一层第k输出节点
	private double[][][] wout;

	// 节点阈值，第i层第j节点
	private double[][] b;

	// 误差项，第i层第j节点
	private double[][] δ;

	// w梯度项，第i层第j节点
	private double[][] nablaw;

	// b梯度项，第i层第j节点
	private double[][] nablab;

	// 激励函数
	private DerivableFunction aF;

	// Cost函数
	private PartialDerivableFunction cF;

	// 最大训练次数
	private int maxTrain;

	// batch size
	private int batchSize;

	// 训练次数
	private int epochCnt = 0;

	// 学习率
	private double η;

	// 初始化最小权重
	private double minW;

	// 初始化最大权重
	private double maxW;
	
	// 输出流
	private PrintStream ps;

	/**
	 * constructor
	 * @param neuronNumber
	 * 节点个数数组
	 * @param ita
	 * 学习率
	 * @param maxTrain
	 * 最大训练次数
	 */
	public BPNetwork(int[] neuronNumber, double η, double minW, double maxW,
			DerivableFunction af, PartialDerivableFunction cf) {

		super();

		// 建立节点及层
		Layer[] layers = new Layer[neuronNumber.length];
		for (int i = 0; i < neuronNumber.length; i++) {
			Layer layer = new Layer();
			BPNeuron[] neurons = new BPNeuron[neuronNumber[i]];
			for (int j = 0; j < neuronNumber[i]; j++) {
				neurons[j] = new BPNeuron();
			}
			layer.setNeurons(neurons);
			layers[i] = layer;
		}

		// 定义变量
		int maxNeuronNum;
		if (Statistic.max(neuronNumber) != null) {
			maxNeuronNum = Statistic.max(neuronNumber);
			x = new double[neuronNumber[0]];
			y = new double[neuronNumber[neuronNumber.length - 1]];
			win = new double[neuronNumber.length][maxNeuronNum][maxNeuronNum];
			wout = new double[neuronNumber.length][maxNeuronNum][maxNeuronNum];
			b = new double[neuronNumber.length][maxNeuronNum];
			o = new double[neuronNumber.length][maxNeuronNum];
			δ = new double[neuronNumber.length][maxNeuronNum];
			nablaw = new double[neuronNumber.length][maxNeuronNum];
			nablab = new double[neuronNumber.length][maxNeuronNum];
		}

		this.minW = minW;
		this.maxW = maxW;
		this.layers = layers;
		this.η = η;
		this.aF = af;
		this.cF = cf;

		// 初始化
		init();
	}

	public BPNetwork(int[] neuronNumber, double η, double minW, double maxW,
			DerivableFunction af, PartialDerivableFunction cf, int maxTrain, int batchSize) {

		this(neuronNumber, η, minW, maxW, af, cf);
		this.maxTrain = maxTrain;
		this.batchSize = batchSize;
	}

	/**
	 * 初始化
	 */
	public void init() {
		// 初始化权值和阈值
		// 遍历层
		for (int i = 0; i < layers.length; i++) {
			// 遍历节点
			for (int j = 0; j < layers[i].getNeurons().length; j++) {
				// 若不是输出层
				if (i != layers.length - 1) {
					// 遍历下一层节点
					for (int k = 0; k < layers[i + 1].getNeurons().length; k++) {
						// 随机初始化权值
						wout[i][j][k] = Math.random() * (maxW - minW) + minW;
						win[i + 1][k][j] = wout[i][j][k];
					}
				}

				// 随机初始化阈值
				b[i][j] = Math.random() * (maxW - minW) + minW;
			}
		}
	}

	/**
	 * 训练网络
	 * @param input
	 * 输入向量数组
	 * @param expectation
	 * 期望输出数组
	 */
	public void train(double[][] input, double[][] expectation) throws AnnException {

		// 检查训练样例合法性
		if (!checkTrainingExample(input, expectation)) {
			throw new AnnException(AnnException.ERRMSG.TRAINING_EXAMPLE_ERR);
		}

		// 导入训练样例
		importTrainingSamples(input, expectation);

		do {
			// 1 epoch
			epochCnt++;
			int correctCount = 0;
			List<Integer> idx = new LinkedList<Integer>();
			int trainingSetSize = this.trainingSamples.size();
			for (int i = 0; i < trainingSetSize; i++) idx.add(i);
			do {
				// 1 batch
				List<Sample> batchSamples = pickBatch(idx, this.batchSize);
				// 初始化梯度
				initNabla();
				// 对于每一个训练样例
				for (int index = 0; index < batchSamples.size(); index++) {
					// 1 training sample
					// 输入训练样例
					inputTrainingExample(batchSamples.get(index));
					// 输出前向传播
					forwardPropagation();
					// 误差反向传播
					backPropagation();
				}
				// 根据误差调整权值
				adjustWeight(batchSamples.size());
			} while (idx.size() > 0);
			//System.out.println("correct rate:" + 1.0 * correctCount / this.trainingSamples.size());
		} while (!canFinish());
	}

	private void initNabla() {
		for (int i = layers.length - 1; i > 0; i--) {
			for (int j = 0; j < layers[i].getN(); j++) {
				nablaw[i][j] = 0.0;
				nablab[i][j] = 0.0;
			}
		}
	}

	/**
	 * 检查训练样例合法性
	 * @param input
	 * 输入向量
	 * @param expectation
	 * 期望输出
	 * @return
	 * 是否合法
	 */
	private boolean checkTrainingExample(double[][] input, double[][] expectation) {
		// 检查训练样例个数
		if (input.length != expectation.length) {
			return false;
		}

		return true;
	}

	/**
	 * 导入训练样例
	 * @param input
	 * 输入向量
	 * @param expectation
	 * 期望输出
	 * @param samples
	 * 训练样例集合
	 */
	private void importTrainingSamples(double[][] input, double[][] expectation) {

		this.trainingSamples = new LinkedList<Sample>();

		for (int i = 0; i < input.length; i++) {
			Sample sample = new Sample(input[i], expectation[i]);
			this.trainingSamples.add(sample);
		}
	}

	/**
	 * 选择训练样例进入batch
	 */
	private List<Sample> pickBatch(List<Integer> idx, int batchSize) {

		List<Sample> batchSamples = new LinkedList<Sample>();

		for (int i = 0; i < batchSize && idx.size() > 0; i++) {
			// 随机选出训练数据
			int index = (int)Math.floor((Math.random() * idx.size()));
			batchSamples.add(this.trainingSamples.get(idx.get(index)));
			idx.remove(index);
		}

		return batchSamples;
	}

	/**
	 * 将训练样例输入网络
	 * @param input
	 * 输入向量
	 * @param expectation
	 * 期望输出
	 */
	private void inputTrainingExample(Sample sample) {
		// 将训练样例输入网络
		x = sample.getInput();
		// 建立Cost函数
		cF.setParam(sample.getExpectation());
	}

	/**
	 * 计算网络输出
	 */
	public double[] predict(double[] input) {
		// 将测试样例输入网络
		x = input;
		// 输出前向传播
		forwardPropagation();

		return y;
	}

	/**
	 * 计算网络输出
	 */
	public int[] monoPredict(double[] input) {
		// 将测试样例输入网络
		x = input;
		// 输出前向传播
		forwardPropagation();

		int[] output = new int[y.length];
		int maxIndex = Statistic.maxIndex(y);
		for (int i = 0; i < output.length; i++) {
			output[i] = 0;
		}
		output[maxIndex] = 1;

		return output;
	}

	/**
	 * 计算网络输出
	 */
	private void forwardPropagation() {
		// 计算网络输出
		for (int i = 0; i < layers.length; i++) {
			// 计算一层
			for (int j = 0; j < layers[i].getNeurons().length; j++) {
				// 计算一个节点
				if (i == 0) {
					// 输入层，节点输出即网络输入
					o[i][j] = x[j];
				} else {
					// 隐藏层，计算节点输出
					o[i][j] = layers[i].getNeurons()[j].getOutput(o[i - 1], win[i][j], b[i][j], aF);
				}
			}
		}

		// 网络输出即最后一层输出
		for (int i = 0; i < y.length; i++) {
			y[i] = o[layers.length - 1][i];
		}
	}

	/**
	 * 误差反向传播
	 */
	private void backPropagation() {
		// 计算误差
		for (int i = layers.length - 1; i > 0; i--) {
			// 判断是否为输出层
			if (i == layers.length - 1) {
				// 计算输出层误差
				for (int j = 0; j < layers[i].getN(); j++) {
					// 第j个节点误差 = 激励函数的导数 * Cost函数的偏导数
					δ[i][j] = aF.getDerivativeY(o[i][j]) * cF.getDerivativeX(o[i], j);
					// 计算w的梯度
					nablaw[i][j] += δ[i][j] * o[i - 1][j];
					// 计算b的梯度
					nablab[i][j] += δ[i][j];
				}
			} else {
				// 计算隐藏层误差
				for (int j = 0; j < layers[i].getN(); j++) {
					// 第j个节点误差 = 节点输出的导数 * 下一层误差加权之和
					δ[i][j] = aF.getDerivativeY(o[i][j]) * getSumOfMultiply(wout[i][j], δ[i + 1], layers[i + 1].getN());
					// 计算w的梯度
					nablaw[i][j] += δ[i][j] * o[i - 1][j];
					// 计算b的梯度
					nablab[i][j] += δ[i][j];
				}
			}
		}
	}

	/**
	 * 调整权值
	 */
	public void adjustWeight(int batchSize) {
		// 遍历层
		for (int i = 0; i < layers.length; i++) {
			// 遍历节点
			for (int j = 0; j < layers[i].getNeurons().length; j++) {
				// 若不是输出层
				if (i != layers.length - 1) {
					// 遍历下一层节点
					for (int k = 0; k < layers[i + 1].getNeurons().length; k++) {
						// 调整权值
						//wout[i][j][k] -= η * δ[i + 1][k] * o[i][j];
						//win[i + 1][k][j] = wout[i][j][k];
						wout[i][j][k] -= η * nablaw[i + 1][j] / batchSize;
						win[i + 1][k][j] = wout[i][j][k];
					}
				}

				// 调整阈值
				//b[i][j] -= η * δ[i][j];
				b[i][j] -= η * nablab[i][j] / batchSize;
			}
		}
	}

	/**
	 * 判断结束条件
	 * @return
	 * 是否结束算法
	 */
	private boolean canFinish() {
		switch (finishCondition) {
		case CONVERGENCY:
			// 对每一个中心点
			// TODO
			return true;
		case MAX_TRAIN:
			// 达到最大尝试次数，算法结束
			return (epochCnt >= maxTrain);
		default:
			return false;
		}
	}

	/**
	 * 获得乘积之和
	 * @param multiplier1
	 * 乘数1数组
	 * @param multiplier2
	 * 乘数2数组
	 * @param multiplierNum
	 * 乘数个数
	 * @return
	 * 求和结果
	 */
	public double getSumOfMultiply(double[] multiplier1, double[] multiplier2, int multiplierNum) {
		double result = 0;
		for (int i = 0; i < multiplierNum; i++) {
			result += multiplier1[i] * multiplier2[i];
		}

		return result;
	}

	public void printInitParam() {
		if (ps == null) {
			ps = System.out;
		}
		ps.print("Number of neurons: ");
		for (int i = 0; i < layers.length; i++) {
			ps.print(" " + layers[i].getNeurons().length);
		}
		ps.println();
		ps.println("Learn rate: " + η);
		ps.println("Max training count: " + maxTrain);
		ps.println("Minimal weight: " + minW);
		ps.println("Maximal weight: " + maxW);
		ps.println("Sigmoid function factor: " + ((Sigmoid)aF).k);
	}

	public PrintStream getPs() {
		return ps;
	}

	public void setPs(PrintStream ps) {
		this.ps = ps;
	}

	public void setMaxTrain(int maxTrain) {
		this.maxTrain = maxTrain;
	}
}
