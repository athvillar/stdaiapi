/**
* CCUnit.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.app.contracaptcha;

import cn.standardai.lib.algorithm.ann.BPNetwork;
import cn.standardai.lib.algorithm.exception.AnnException;
import cn.standardai.lib.algorithm.ga.GAUnit;
import cn.standardai.lib.base.tool.Converter;

/**
 * 遗传单元
 * @author 韩晴
 *
 */
public class CCUnit extends GAUnit {

	private BPNetwork bpnet;

	public BPNetwork getBpnet() {
		return bpnet;
	}

	/**
	 * constructor
	 */
	public CCUnit(Integer geneLen) {
		super(geneLen);
	}

	public CCUnit(byte[] gene) {
		super(gene);
	}

	/**
	 * 个体根据基因工作
	 */
	public void work() {

		// 制作样本
		Sample trainSample = AnnTrainer.makeSample("C:\\work\\standardai\\captcha\\train\\destiny");
		Sample testSample = AnnTrainer.makeSample("C:\\work\\standardai\\captcha\\test\\destiny");

		// 得到基因
		String strGene = getStrGene();

		// GENE INDEX
		int index = 0;
		// 隐层层数
		int hiddenLayerNum = Converter.binaryString2Int(strGene.substring(index, index += 3)) % 5 + 1;
		// 根据基因创建网络
		int[] layerNums = new int[hiddenLayerNum + 2];
		// 输入层节点数
		layerNums[0] = 360;
		for (int i = 1; i <= hiddenLayerNum; i++) {
			layerNums[i] = Converter.binaryString2Int(strGene.substring(index, index += 1)) + 1;
		}
		// 输出层节点数
		layerNums[layerNums.length - 1] = 10;

		index = 59;
		double eta = (double)Converter.binaryString2Int(strGene.substring(index, index += 5)) / 100;
		int maxTrain = Converter.binaryString2Int(strGene.substring(index, index += 6)) * 100;
		double minW = -(double)Converter.binaryString2Int(strGene.substring(index, index += 5)) / 100;
		double maxW = (double)Converter.binaryString2Int(strGene.substring(index, index += 5)) / 100;
		double sigmoidK = (double)Converter.binaryString2Int(strGene.substring(index, index += 5)) / 10;

		// 建立网络
		bpnet = new BPNetwork(layerNums, eta, maxTrain, minW, maxW, sigmoidK);
		bpnet.setPs(getPs());
		getPs().println("--------BPNetwork INFO--------");
		bpnet.printInitParam();

		// 训练数据
		/*
		double[][] trainInput = {
				// set 1
				{0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0},//1
				{0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,1,1,1,0,0,0,0,0,0},//2
				{0,0,1,0,0,0,1,0,1,0,0,0,0,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,1,0,0,1,1,0,0,0,0,0,0,0},//3
				{0,0,0,0,0,0,1,1,1,0,0,0,0,1,0,0,0,0,1,0,0,1,1,1,0,0,0,0,1,0,0,0,0,1,0,0,1,1,1,0},//3
				{0,0,0,0,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0},//4
				{0,0,0,0,0,0,1,1,1,0,0,1,0,0,0,0,1,0,0,0,0,1,1,1,0,0,0,0,1,0,0,1,1,1,0,0,0,0,0,0},//5
				{0,0,0,0,0,0,1,1,1,0,0,1,0,0,0,0,1,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,0,0},//6
				{0,0,0,0,0,0,1,1,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0},//7
				{0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,0,0},//8
				{0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,1,0,0,1,1,1,0,0,0,0,0,0},//9
				{0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,0,1,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,0,0}//0
		};
		double[][] trainExp = {
				{0,0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0,1,0},
				{0,0,0,0,0,0,0,1,0,0},
				{0,0,0,0,0,0,0,1,0,0},
				{0,0,0,0,0,0,1,0,0,0},
				{0,0,0,0,0,1,0,0,0,0},
				{0,0,0,0,1,0,0,0,0,0},
				{0,0,0,1,0,0,0,0,0,0},
				{0,0,1,0,0,0,0,0,0,0},
				{0,1,0,0,0,0,0,0,0,0},
				{1,0,0,0,0,0,0,0,0,0}
		};
		*/
		double[][] trainInput = trainSample.getInput();
		double[][] trainExp = trainSample.getExpectation();

		// 训练
		try {
			bpnet.train(trainInput, trainExp);
		} catch (AnnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 测试数据
		/*
		double[][] testInput = {
				// set 3
				{0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0},//1
				{0,1,1,1,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,1,1,1,1,0,1,0,0,0,0,1,1,1,1,0,0,0,0,0},//2
				{0,0,0,0,0,1,1,1,1,0,0,0,0,1,0,0,0,0,1,0,1,1,1,1,0,0,0,0,1,0,0,0,0,1,0,1,1,1,1,0},//3
				{0,0,0,0,0,0,1,0,0,1,0,1,0,0,1,0,1,0,0,1,0,1,0,0,1,0,1,1,1,1,0,0,0,0,1,0,0,0,0,1},//4
				{0,0,0,0,0,1,1,1,1,0,1,0,0,0,0,1,0,0,0,0,1,1,1,1,0,0,0,0,1,0,0,0,0,1,0,0,1,1,1,0},//5
				{0,0,0,0,0,0,1,1,1,0,0,1,0,0,0,0,1,1,1,1,0,1,0,0,1,0,1,0,0,1,0,1,0,0,1,0,1,1,1,1},//6
				{0,0,0,0,0,1,1,1,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0},//7
				{0,0,0,0,0,1,1,1,1,0,1,0,0,1,0,1,1,1,1,0,1,0,0,1,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0},//8
				{0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,1,0,0,1,1,1,0,0,0,0,0},//9
				{0,0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}//0
		};
		double[][] testExp = {
				{0,0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0,1,0},
				{0,0,0,0,0,0,0,1,0,0},
				{0,0,0,0,0,0,1,0,0,0},
				{0,0,0,0,0,1,0,0,0,0},
				{0,0,0,0,1,0,0,0,0,0},
				{0,0,0,1,0,0,0,0,0,0},
				{0,0,1,0,0,0,0,0,0,0},
				{0,1,0,0,0,0,0,0,0,0},
				{1,0,0,0,0,0,0,0,0,0}
		};
		*/
		double[][] testInput = testSample.getInput();
		double[][] testExp = testSample.getExpectation();

		// 测试
		for (int i = 0; i < testInput.length; i++) {
			int[] predict = bpnet.monoPredict(testInput[i]);
			// 输出预测
			boolean matchFlg = true;
			for (int j = 0; j < testExp[i].length; j++) {
				if (testExp[i][j] != predict[j]) {
					matchFlg = false;
					break;
				}
			}
			if (matchFlg) {
				this.setFitness(getFitness() + 100);
			}
		}
		this.getPs().println("Fitness : " + this.getFitness());
		getPs().println();
	}
}
