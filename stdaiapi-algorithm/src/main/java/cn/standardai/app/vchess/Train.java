/**
* Train.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.app.vchess;

import java.lang.invoke.ConstantCallSite;
import java.util.Date;

import cn.standardai.lib.algorithm.ann.BPNetwork;

/**
 * 棋手
 * @author 韩晴
 *
 */
public class Train {

	private int length;

	private Player player;

	Board board;

	Script script;

	/**
	 * constructor
	 */
	public Train(int length, Player player, Script script) {
		super();
		this.length = length;
		this.player = player;
		this.script = script;
	}

	public void run() {

	}
	
}
