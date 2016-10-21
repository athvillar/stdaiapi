/**
* CrossCodeGenerator.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.ga;

import java.util.Arrays;

import cn.standardai.lib.base.tool.Converter;

/**
 * CrossCode生成器
 * @author hanq
 * 
 */
public class CrossCodeGenerator extends Generator {

	// 交叉算子
	private byte[][] crossCode;

	/**
	 * constructor
	 * @param parentsNum
	 * 父个体个数
	 * @param crossRule
	 * 交叉规则
	 * @param geneLen
	 * 基因长度
	 */
	public CrossCodeGenerator(int parentsNum, CrossRule crossRule, int geneLen) {
		super(GeneratorType.CROSS_CODE);
		this.parentsNum = parentsNum;
		this.crossRule = crossRule;
		this.geneLen = geneLen;
	}

	/**
	 * generate
	 * @param parents
	 * 父个体
	 * @return 子个体
	 */
	public GAUnit[] generate(GAUnit[] parents) {
		// 生成CrossCode
		if (crossCode == null && parents != null && parents.length != 0) {
			makeCrossCode();
		}

		GAUnit[] children = new GAUnit[parentsNum];
		for (int i = 0; i < children.length; i++) {
			byte[] tempGene = new byte[crossCode[0].length];
			for (int j = 0; j < tempGene.length; j++) {
				for (int k = 0; k < parents.length; k++) {
					tempGene[j] += parents[(k + i) % parents.length].getGene()[j] & crossCode[k][j];
				}
			}
			children[i] = GAUnitFactory.getInstance(parents[0].getClass(), tempGene);
		}

		return children;
	}

	private void makeCrossCode() {

		// 存放01字符串的中间变量
		StringBuffer[] sbCrossCodes = new StringBuffer[parentsNum];
		for (int i = 0; i < sbCrossCodes.length; i++) {
			sbCrossCodes[i] = new StringBuffer();
		}

		// 根据cross code生成规则选择生成方法
		switch (crossRule) {
		case CROSS:
			// 01交叉cross code
			// 生成父个体个数个cross code
			for (int i = 0; i < parentsNum; i++) {
				for (int j = 0; j < geneLen; j++) {
					if (j % parentsNum == i) {
						sbCrossCodes[i].append("1");
					} else {
						sbCrossCodes[i].append("0");
					}
				}
			}
			break;
		case HALF:
			// 01连续cross code
			// 生成父个体个数个cross code
			for (int i = 0; i < parentsNum; i++) {
				for (int j = 0; j < geneLen; j++) {
					if ((j < ((geneLen - 1) / parentsNum + 1) * (i + 1)) &&
						(j >= ((geneLen - 1) / parentsNum + 1) * (i))) {
						sbCrossCodes[i].append("1");
					} else {
						sbCrossCodes[i].append("0");
					}
				}
			}
			break;
		case FLOAT:
			// 浮点型cross code
			int[] floatIndex = new int[parentsNum];
			floatIndex[0] = 0;
			for (int i = 1; i < floatIndex.length; i++) {
				floatIndex[i] = (int)Math.floor(Math.random() * (geneLen - floatIndex[i - 1]) + floatIndex[i - 1]);
			}
			int i, k;
			for (k = 0; k < floatIndex.length; k++) {
				for (i = 0; i < parentsNum; i++) {
					if (k != floatIndex.length - 1) {
						if (k % floatIndex.length == i) {
							sbCrossCodes[i].append(getString('1', floatIndex[k + 1] - floatIndex[k]));
						} else {
							sbCrossCodes[i].append(getString('0', floatIndex[k + 1] - floatIndex[k]));
						}
					} else {
						if (k % floatIndex.length == i) {
							sbCrossCodes[i].append(getString('1', geneLen - floatIndex[k]));
						} else {
							sbCrossCodes[i].append(getString('0', geneLen - floatIndex[k]));
						}
					}
				}
			}
			break;
		case RANDOM:
			// 随机cross code
			// 生成父个体个数个cross code
			for (int j = 0; j < geneLen; j++) {
				int index = (int)Math.floor(Math.random() * parentsNum);
				for (i = 0; i < parentsNum; i++) {
					if (i == index) {
						sbCrossCodes[i].append("1");
					} else {
						sbCrossCodes[i].append("0");
					}
				}
			}
			break;
		}

		// 生成cross code
		crossCode = new byte[parentsNum][(geneLen - 1) / 8 + 1];
		for (int i = 0; i < crossCode.length; i++) {
			crossCode[i] = Converter.binaryString2Bytes(sbCrossCodes[i].toString());
			/*
			char[] c = sbCrossCodes[i].toString().toCharArray();
			for (int j = 0; j < crossCode[i].length; j++) {
				for (int k = j * 8; k < j * 8 + 8; k++) {
					if (k < c.length && c[k] == '1') {
						crossCode[i][j] += Math.pow(2, k - j * 8);
					}
				}
			}
			*/
		}
	}
	
	private String getString(char c, int len) {
		char[] b = new char[len];
		Arrays.fill(b, c);
		return new String(b);
	}
}
