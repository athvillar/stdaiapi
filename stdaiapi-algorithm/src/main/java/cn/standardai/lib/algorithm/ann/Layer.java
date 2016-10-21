/**
* Layer.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.ann;

/**
 * 层
 * @author 韩晴
 *
 */
public class Layer {

	// 本层神经元
	private BPNeuron[] neurons;

	// 本层神经元个数
	private int n;

	/**
	 * constructor
	 */
	public Layer() {
		super();
	}

	/**
	 * constructor
	 * @param neurons
	 * 本层神经元
	 */
	public Layer(BPNeuron[] neurons) {
		super();
		this.neurons = neurons;
		this.n = neurons.length;
	}

	public BPNeuron[] getNeurons() {
		return neurons;
	}

	public void setNeurons(BPNeuron[] neurons) {
		this.neurons = neurons;
		this.n = neurons.length;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}
}	
