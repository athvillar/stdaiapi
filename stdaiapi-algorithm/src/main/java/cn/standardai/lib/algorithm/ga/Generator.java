/**
* Generator.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.ga;

/**
 * 生成器
 * @author hanq
 * 
 */
public abstract class Generator {

	// 生成规则
	public static enum GeneratorType {CROSS_CODE};

	// 交叉规则
	public static enum CrossRule {CROSS, HALF, FLOAT, RANDOM};

	// 亲类个数
	public int parentsNum;

	// 交叉规则
	public CrossRule crossRule;

	// 基因长度
	public int geneLen;

	/**
	 * 构造函数
	 * @param GeneratorType
	 * 生成规则
	 */
	public Generator(GeneratorType generatorType) {
		super();
	}

	/**
	 * generate
	 * @param parents
	 * 父个体
	 * @return 子个体
	 */
	public abstract GAUnit[] generate(GAUnit[] parents);
}
