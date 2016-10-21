/**
* Train.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.app.chessmaster;

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

	ChessBoard chessBoard;

	ChessManual chessManual;

	/**
	 * constructor
	 */
	public Train(int length, Player player, ChessManual chessManual) {
		super();
		this.length = length;
		this.player = player;
		this.chessManual = chessManual;
	}

	public void run() {

		// 初始化
		int finalResult;
		chessBoard = new ChessBoard(length);
		chessManual = new ChessManual();

		// 开始下棋
		int step = 0;
		while (chessManual.hasNext()) {
			// Add player learn
			if (chessManual.)
			player.learn(chessBoard, chessManual);
		}

		// 保存训练
		DBUtil.update(player);
	}
	
	private int oneStep(Player player) {

		int[] cross = new int[2];
		Date startTime = new Date();

		cross = player.put(chessBoard);
		// 循环直到输入合法
		while (chessBoard.isValid(cross) == ChessConstants.STEP_INVALID) {
			Date endTime = new Date();
			if (endTime.getTime() - startTime.getTime() > 1) {
				// 超时判负
				return ChessConstants.STEP_INVALID;
			}
			cross = player.retry(chessBoard);
		}

		if (cross == null) {
			chessManual.add(null);
			return ChessConstants.STEP_NOMOVE;
		} else {
			chessManual.add(cross);
			chessBoard.add(cross);
			return ChessConstants.STEP_VALID;
		}
	}
}
