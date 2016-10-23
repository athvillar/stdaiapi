/**
* AnnTrainer.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.app.contracaptcha;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;

import cn.standardai.lib.algorithm.ann.BPNetwork;
import cn.standardai.lib.algorithm.exception.AnnException;
import cn.standardai.lib.algorithm.ga.Generator.CrossRule;
import cn.standardai.lib.algorithm.ga.Generator.GeneratorType;
import cn.standardai.lib.algorithm.ga.Selector.ChooseMethod;
import cn.standardai.lib.algorithm.ga.Species;
import cn.standardai.lib.algorithm.ga.SpeciesFactory;
import cn.standardai.lib.base.tool.Converter;

/**
 * 神经网络训练类
 * @author 韩晴
 *
 */
public class SingleTrainer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			trainFigure();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 训练网络
	 * @throws AnnException
	 */
	public static void trainFigure() throws Exception {

		// 输出位置
		File f = new File("C:\\work\\train.txt");
		PrintStream ps = new PrintStream(f);

		// 制作样本
		Sample trainSample = AnnTrainer.makeSample("C:\\work\\standardai\\captchaforsingletest\\train\\destiny");
		Sample testSample = AnnTrainer.makeSample("C:\\work\\standardai\\captchaforsingletest\\test\\destiny");

		double[][] trainInput = trainSample.getInput();
		double[][] trainExp = trainSample.getExpectation();

		double[][] testInput = testSample.getInput();
		double[][] testExp = testSample.getExpectation();

		// 根据基因创建网络
		int[] layerNums = {360, 50, 10};

		double eta = 3;
		int maxTrain = 3000;
		double minW = -0.5;
		double maxW = 0.5;
		double sigmoidK = 1;

		// 建立网络
		// TODO
		//BPNetwork bpnet = new BPNetwork(layerNums, eta, maxTrain, minW, maxW, sigmoidK);
		BPNetwork bpnet = null;
		bpnet.setPs(ps);
		ps.println("--------BPNetwork INFO--------");
		bpnet.printInitParam();

		// 训练
		try {
			bpnet.train(trainInput, trainExp);
		} catch (AnnException e) {
			e.printStackTrace();
		}

		// 测试
		int score = 0;
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
				score++;
			}
		}
		ps.println("Fitness : " + score);
		ps.println("END");
		ps.close();

		// 制作样本
		//Sample verifySample = SingleTrainer.makeSample("C:\\work\\standardai\\captcha\\verify\\destiny");

		// 验证
		//verify(bpnet, verifySample);
	}

	private static void verify(BPNetwork bpnet, Sample verifySample) throws Exception {

		File f = new File("C:\\work\\verify.txt");
		PrintStream ps = new PrintStream(f);
		bpnet.setPs(ps);
		bpnet.printInitParam();

		// 测试数据
		/*
		double[][] testInput = {
				// set 2
				{0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0},//1
				{0,0,0,0,0,0,1,1,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,1,0,0,0,0,1,1,1,0,0,0,0,0,0},//2
				{0,0,0,0,0,0,0,1,1,0,0,1,0,1,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,0,1,0,0,1,1,1,0},//3
				{0,0,0,0,0,0,1,0,1,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0},//4
				{0,0,0,0,0,0,1,1,1,0,0,1,0,0,0,0,1,0,0,0,0,0,1,1,0,0,0,0,1,0,0,0,0,1,0,0,1,1,1,0},//5
				{0,0,0,0,0,0,0,1,1,0,0,1,0,0,0,0,1,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0},//6
				{0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0},//7
				{0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,1,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0},//8
				{0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,1,0,0,1,1,1,0,0,0,0,0,0},//9
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,0,0}//0
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
		double[][] verifyInput = verifySample.getInput();
		double[][] verifyExp = verifySample.getExpectation();

		// 测试
		int fitness = 0;
		for (int i = 0; i < verifyInput.length; i++) {
			int[] predict = bpnet.monoPredict(verifyInput[i]);
			// 输出预测
			boolean matchFlg = true;
			for (int j = 0; j < verifyExp[i].length; j++) {
				if (verifyExp[i][j] != predict[j]) {
					matchFlg = false;
					break;
				}
			}
			if (matchFlg) {
				fitness += 100;
			}
		}
		ps.println("Fitness : " + fitness);
		ps.close();
	}

	public static Sample makeSample(String dir) {

		File root = new File(dir);
		File[] files = root.listFiles();
		Sample sample = new Sample();
		double[][] input = new double[files.length][];
		double[][] exp = new double[files.length][];

		int sampleIndex = 0;
		for (File file : files) {
			if (!file.isDirectory()) {
				// 读取图片文件
				BufferedImage image;
				try {
					image = ImageIO.read(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
				int width = image.getWidth();
				int height = image.getHeight();

				double[] input1 = new double[width * height];
				for (int i = 0 ; i < height ; i++) {
					for (int j = 0 ; j < width; j++) {
						int rgb = image.getRGB(j, i);
						String str = Integer.toHexString(rgb);
				        int r = Integer.parseInt(str.substring(2,4),16);
				        int g = Integer.parseInt(str.substring(4,6),16);
				        int b = Integer.parseInt(str.substring(6,8),16);
				        if (r + g + b > 360) {
							input1[i * width + j] = 0.0;
				        } else {
							input1[i * width + j] = 1.0;
				        }
					}
				}
				input[sampleIndex] = input1;

				double[] exp1 = new double[10];
				for (int i = 0; i < exp1.length; i++) {
					exp1[i] = 0;
				}
				int expNum = Integer.parseInt(file.getName().substring(0, file.getName().indexOf('_')));
				exp1[expNum] = 1;
				exp[sampleIndex] = exp1;
			}
			sampleIndex++;
		}

		sample.setInput(input);
		sample.setExpectation(exp);

		return sample;
	}
}
