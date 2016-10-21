/**
* Step.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.app.vchess;

import cn.standardai.app.vchess.Point.SIDE;

/**
 * step
 * @author 韩晴
 *
 */
public class Step {

	public static enum STEPSTATUS {VALID, INVALID, NULL};

	public int x;

	public int y;
	
	// 黑方白方
	public SIDE side;
	
	// 步数
	public int stepCount;
	
	// 此步概率
	public double prob;
	
	public Step(int x, int y, SIDE side) {
		this.x = x;
		this.y = y;
		this.side = side;
	}
	
	public Step(int x, int y, SIDE side, int stepCount, double prob) {
		this.x = x;
		this.y = y;
		this.side = side;
		this.stepCount = stepCount;
		this.prob = prob;
	}
}
