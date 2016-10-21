/**
* Species.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.ga;

import java.io.PrintStream;

import cn.standardai.lib.base.function.Order;
import cn.standardai.lib.base.function.Random;
import cn.standardai.lib.base.function.Statistic;

/**
 * 物种类
 * @author 韩晴
 *
 */
public class Species {

	// 结束条件
	private static enum FinishCondition {MAX_GENERATION};

	// 结束条件
	private FinishCondition finishCondition = FinishCondition.MAX_GENERATION;

	// 基因长度
	private int geneLen;

	// 突变率百分比，0 - 100
	private double mutateRate;

	// 最大训练次数
	private int maxGenerationNum;

	// 种群个体
	GAUnit[] units;

	// 选择器
	private Selector selector;

	// 生成器
	private Generator generator;

	// 训练次数
	private int generationNum = 0;

	// 最佳种群个体
	private GAUnit[] bestUnits;

	public GAUnit[] getBestUnits() {
		return bestUnits;
	}

	// 保存最佳个体flag
	private boolean saveBest = false;

	// 输出流
	private PrintStream ps;

	public Species(
			GAUnit[] units,
			int geneLen,
			double mutateRate,
			int maxGenerationNum,
			Selector selector,
			Generator generator) {
		this.units = units;
		this.geneLen = geneLen;
		this.mutateRate = mutateRate;
		this.maxGenerationNum = maxGenerationNum;
		this.selector = selector;
		this.generator = generator;
	}

	/**
	 * 训练种群
	 */
	public void run() {
		// 训练直到满足退出条件
		do {
			ps.println("--------Generation " + generationNum + " START-------------------------");
			generationNum++;
			// 个体工作并得出适应度
			work();
			// 计算得分并生成下一代
			propagate();
			ps.println("--------Generation " + generationNum + " END-------------------------");
			ps.println();
		} while (!canFinish());
	}

	/**
	 * 判断结束条件
	 * @return
	 * 是否结束算法
	 */
	private boolean canFinish() {
		switch (finishCondition) {
		// TODO 增加结束条件
		case MAX_GENERATION:
			// 达到最大尝试次数，算法结束
			return (generationNum >= maxGenerationNum);
		default:
			return false;
		}
	}

	/**
	 * 个体工作并得出适应度
	 */
	private void work() {
		for (int i = 0; i < units.length; i++) {
			ps.println("Generation " + generationNum + " unit " + i);
			units[i].work();
		}
	}

	/**
	 * 生成下一代
	 * @param parents
	 * @return
	 */
	public void propagate() {

		// 计算个体得分
		int[] fitnesses = new int[units.length];
		for (int i = 0; i < units.length; i++) {
			fitnesses[i] = units[i].getFitness();
		}
		ps.print("Generation: " + generationNum + "; ");
		ps.print("AVG: " + Statistic.avg(fitnesses) + "; ");
		ps.println("MAX: " + Statistic.max(fitnesses));

		/*
		System.out.print("population:" + "\t");
		for (int i = 0; i < units.length; i++) {
			System.out.print(Converter.bytes2BinaryString(units[i].getGene()));
			System.out.print("\t");
		}
		System.out.println();

		System.out.print("score:" + "\t\t");
		for (int i = 0; i < fitnesses.length; i++) {
			System.out.print(fitnesses[i] + "\t");
			System.out.print("\t");
		}
		System.out.println();
		*/

		// 保存最佳个体
		if (saveBest) {
			// 本代适应度排序后的个体索引
			int[] orderedIndex = Order.getOrderedIndex(fitnesses, Order.OrderMethod.DESC);
			// 最佳个体库中适应度
			int[] bestUnitsFitness = new int[bestUnits.length];
			for (int i = 0; i < bestUnitsFitness.length; i++) {
				if (bestUnits[i] != null) {
					bestUnitsFitness[i] = bestUnits[i].getFitness();
				} else {
					bestUnitsFitness[i] = Integer.MIN_VALUE;
				}
			}
			int[] orderedBestUnitsFitness = Order.getOrderedIndex(bestUnitsFitness, Order.OrderMethod.ASC);
			
			int j = 0;
			for (int i = 0; i < orderedIndex.length; i++) {
				if (j < orderedBestUnitsFitness.length && fitnesses[orderedIndex[i]] >= bestUnitsFitness[orderedBestUnitsFitness[j]]) {
					// 将此个体放到最佳个体库中
					try {
						bestUnits[orderedBestUnitsFitness[j]] = (GAUnit)units[orderedIndex[i]].clone();
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					j++;
				} else {
					break;
				}
			}
		}
		// 生成下一代
		selector.setFitnesses(units, fitnesses);
		GAUnit[] children = new GAUnit[units.length];
		for (int i = 0, j = 0; i < children.length; i += j) {
			// 选择父代
			GAUnit[] parents = selector.next(generator.parentsNum);

			/*
			System.out.print("parents:");
			System.out.print("\t");
			for (int in = 0; in < parents.length; in++) {
				System.out.print(Converter.bytes2BinaryString(parents[in].getGene()));
				System.out.print("\t");
			}
			System.out.println();
			*/

			// 生成子代
			GAUnit[] newborn = generator.generate(parents);

			/*
			System.out.print("children:");
			System.out.print("\t");
			for (int in = 0; in < newborn.length; in++) {
				System.out.print(Converter.bytes2BinaryString(newborn[in].getGene()));
				System.out.print("\t");
			}
			System.out.println();
			System.out.println();
			*/
			
			// 复制到children数组
			for (j = 0; j < newborn.length; j++) {
				if (i + j < children.length) {
					children[i + j] = newborn[j];
				} else {
					break;
				}
			}
		}

		// 突变
		// 遍历子代
		for (int i = 0; i < children.length; i++) {
			// 重新设置输出流
			children[i].setPs(getPs());
			// 对每一个子代个体，确定遍历基因位置
			int[] mutateGeneIndex = Random.randomIndex(geneLen, mutateRate);
			if (mutateGeneIndex != null && mutateGeneIndex.length != 0) {
				children[i].mutate(mutateGeneIndex);
			}
		}

		// 将父代替换为子代
		units = children;
	}

	public PrintStream getPs() {
		return ps;
	}

	public void setPs(PrintStream ps) {
		this.ps = ps;
	}

	public void saveBest(int saveNum) {
		saveBest = true;
		bestUnits = new GAUnit[saveNum];
	}
}
