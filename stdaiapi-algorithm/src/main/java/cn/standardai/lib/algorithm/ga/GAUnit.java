/**
* GAUnit.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.ga;

import java.io.PrintStream;

import cn.standardai.lib.base.tool.Converter;

/**
 * 遗传单元
 * @author 韩晴
 *
 */
public abstract class GAUnit implements Cloneable {

	// 基因
	private byte[] gene;

	// 适应度
	private int fitness;

	// 输出流
	private PrintStream ps;

	/**
	 * constructor
	 */
	public GAUnit() {
		super();
	}

	/**
	 * constructor
	 * @param geneLen
	 * 基因长度
	 */
	public GAUnit(int geneLen) {
		super();

		this.gene = new byte[(geneLen - 1) / 8 + 1];
		for (int i = 0; i < this.gene.length; i++) {
			// 随机生成基因
			// TODO NEED TEST
			this.gene[i] = (byte)((int)(Math.random() * 256 - 128));
			//System.out.println(geneByte);
		}
		this.fitness = 0;
	}

	/**
	 * constructor
	 * @param gene
	 * 基因
	 */
	public GAUnit(byte[] gene) {
		super();
		this.gene = gene;
		this.fitness = 0;
	}

	/**
	 * 个体根据基因工作
	 */
	public abstract void work();

	/**
	 * 变异
	 * @param mutateGeneIndex
	 * 变异基因索引
	 */
	public void mutate(int[] mutateGeneIndex) {
		// 生成变异算子
		byte[] mutateModifier = new byte[gene.length];
		for (int i = 0; i < mutateGeneIndex.length; i++) {
			mutateModifier[mutateGeneIndex[i] / 8] |= (byte)Math.pow(2, mutateGeneIndex[i] % 8);
		}
		//System.out.print("mutate:\t");
		//System.out.print(Converter.bytes2BinaryString(gene) + "\t");
		//System.out.print(Converter.bytes2BinaryString(mutateModifier) + "\t");
		// 异或运算
		for (int i = 0; i < gene.length; i++) {
			gene[i] ^= mutateModifier[i];
		}
		//System.out.println(Converter.bytes2BinaryString(gene));
	}

	public byte[] getGene() {
		return gene;
	}

	/**
	 * 获得字符串化的基因
	 * @return 字符串化的基因
	 */
	public String getStrGene() {
		return Converter.bytes2BinaryString(this.gene);
	}

	public void setGene(byte[] gene) {
		this.gene = gene;
	}

	public int getFitness() {
		return fitness;
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	public PrintStream getPs() {
		return ps;
	}

	public void setPs(PrintStream ps) {
		this.ps = ps;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
