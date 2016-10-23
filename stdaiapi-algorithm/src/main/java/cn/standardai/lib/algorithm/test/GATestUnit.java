/**
* GATestUnit.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.test;

import cn.standardai.app.contracaptcha.AnnTrainer;
import cn.standardai.app.contracaptcha.Sample;
import cn.standardai.lib.algorithm.ann.BPNetwork;
import cn.standardai.lib.algorithm.exception.AnnException;
import cn.standardai.lib.algorithm.ga.GAUnit;
import cn.standardai.lib.base.tool.Converter;

/**
 * 遗传单元
 * @author 韩晴
 *
 */
public class GATestUnit extends GAUnit {

	/**
	 * constructor
	 */
	public GATestUnit(Integer geneLen) {
		super(geneLen);
	}

	/**
	 * 个体根据基因工作
	 */
	public void work() {

		String strGene = getStrGene();
		this.getPs().println("strGene : " + strGene);
		for (char c : strGene.toCharArray()) {
			if (c == '1') {
				this.setFitness(getFitness() + 100);
			}
 		}
	}
}
