/**
* Game.java
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
public class Game {

	private int length;

	private Player player1;

	private Player player2;

	ChessBoard chessBoard;

	ChessManual chessManual;

	/**
	 * constructor
	 */
	public Game(int length, Player player1, Player player2) {
		super();
		this.length = length;
		this.player1 = player1;
		this.player2 = player2;
	}

	public void run() {

		// 初始化
		chessBoard = new ChessBoard(length);
		chessManual = new ChessManual();

		// 开始下棋
		int step = 0;
		while (true) {
			int result;
			if (step % 2 == 0) {
				result = oneStep(player1);
			} else {
				result = oneStep(player2);
			}

			if (result == ChessConstants.STEP_VALID) {
				lastStep = ChessConstants.STEP_VALID;
				step++;
				if (player)
			} else if (result == ChessConstants.STEP_INVALID) {
				if (step % 2 == 0) {
					setLoser(player1);
					// TODO add player2 learn
				} else {
					setLoser(player2);
				}
				break;
			} else {
				if (lastStep == ChessConstants.STEP_NOMOVE) {
					step++;
					break;
				} else {
					lastStep = ChessConstants.STEP_NOMOVE;
					step++;
					continue;
				}
			}
		}

		// 计算胜负
		int winner = chessBoard.count();
		if (winner == ChessConstants.BLACK) {
			setLoser(player2);
		} else if (finalResult == ChessConstants.WHITE) {
			setLoser(player1);
		} else {
			withdraw();
		}

		// 保存棋局及结果
		DBUtil.insert(chessManual);
		DBUtil.update(player1);
		DBUtil.update(player2);
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
