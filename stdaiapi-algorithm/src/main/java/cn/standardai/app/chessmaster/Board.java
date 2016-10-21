/**
* Board.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.app.chessmaster;

import cn.standardai.app.chessmaster.Point.SIDE;

/**
 * 棋盘
 * @author 韩晴
 *
 */
public class Board {

	// 棋盘大小
	public int length;

	// 点阵
	public Point[][] point;
	
	// 上一步棋
	public Board lastBoard;
	
	// 下一步棋
	public Board nextBoard;

	/**
	 * constructor
	 */
	public Board(int length) {
		super();
		this.length = length;
		this.point = new Point[length][length];
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				this.point[i][j].side = SIDE.NONE;
			}
		}
	}

	/**
	 * constructor
	 */
	public Board(int length, Point[][] point) {
		super();
		this.length = length;
		this.point = new Point[length][length];
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				this.point[i][j].side = point[i][j].side;
			}
		}
	}
}
