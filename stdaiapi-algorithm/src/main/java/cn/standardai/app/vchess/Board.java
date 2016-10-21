/**
* Board.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.app.vchess;

import cn.standardai.app.vchess.Point.SIDE;

/**
 * 棋盘
 * @author 韩晴
 *
 */
public class Board {

	// 棋盘大小
	public int length;

	// 点阵
	private Point[][] point;

	// 黑棋禁着点
	//public HashSet<Point> blackForbiddenPoints;

	// 白棋禁着点
	//public HashSet<Point> whiteForbiddenPoints;

	// 点阵值(黑方)
	private double[] blackValue;

	// 点阵值(白方)
	private double[] whiteValue;
	
	// 上一步棋
	public Board lastBoard;
	
	// 下一步棋
	public Board nextBoard;
	
	// 已下步数，从1开始计数
	public int stepCount;

	/**
	 * constructor
	 */
	public Board(int length) {
		super();
		this.length = length;
		this.point = new Point[length][length];
		this.blackValue = new double[length * length];
		this.whiteValue = new double[length * length];
		//this.blackForbiddenPoints = new HashSet<Point>();
		//this.whiteForbiddenPoints = new HashSet<Point>();
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				this.point[i][j] = new Point(i, j, SIDE.NONE);
			}
		}
		this.stepCount = 0;
	}

	/**
	 * constructor
	 */
	public Board(Board board) {
		super();
		this.length = board.length;
		this.point = new Point[board.length][board.length];
		this.blackValue = new double[board.length * board.length];
		this.whiteValue = new double[board.length * board.length];
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				this.point[i][j] = new Point(i, j, board.point[i][j].side);
			}
		}
		this.stepCount = board.stepCount;
		// 设置禁着点
		//this.blackForbiddenPoints = new HashSet<Point>();
		//this.whiteForbiddenPoints = new HashSet<Point>();
		//for (Point point : board.blackForbiddenPoints) {
		////	this.blackForbiddenPoints.add(this.point[point.x][point.y]);
		//}
		//for (Point point : board.whiteForbiddenPoints) {
		//	this.whiteForbiddenPoints.add(this.point[point.x][point.y]);
		//}
	}

	/**
	 * 为棋盘上某点设置棋子或拿掉棋子
	 */
	public void put(Step step) {
		set(step.x, step.y, step.side);
		stepCount++;
	}

	/**
	 * 为棋盘上某点设置棋子或拿掉棋子
	 */
	public void set(int x, int y, SIDE side) {
		point[x][y].side = side;
		if (side == SIDE.BLACK) {
			blackValue[x + y * length] = 1;
			whiteValue[x + y * length] = -1;
			//blackForbiddenPoints.add(point[x][y]);
			//whiteForbiddenPoints.add(point[x][y]);
		} else if (side == SIDE.WHITE) {
			blackValue[x + y * length] = -1;
			whiteValue[x + y * length] = 1;
			//blackForbiddenPoints.add(point[x][y]);
			//whiteForbiddenPoints.add(point[x][y]);
		} else {
			blackValue[x + y * length] = 0;
			whiteValue[x + y * length] = 0;
			//blackForbiddenPoints.remove(point[x][y]);
			//whiteForbiddenPoints.remove(point[x][y]);
		}
	}

	/**
	 * 获取棋盘上某点
	 */
	public Point getPoint(int x, int y) {
		return point[x][y];
	}

	/**
	 * 将棋盘布局转换为double数组
	 */
	public double[] getValues(SIDE side) {
		if (side == SIDE.NONE) {
			return null;
		} else if (side == SIDE.BLACK) {
			return blackValue;
		} else {
			return whiteValue;
		}
	}

	/**
	 * 返回另一方
	 */
	public static SIDE getOppositeSide(SIDE side) {
		if (side == SIDE.BLACK) {
			return SIDE.WHITE;
		} else if (side == SIDE.WHITE) {
			return SIDE.BLACK;
		} else {
			return SIDE.NONE;
		}
	}

	/**
	 * 计算胜负
	 */
	public SIDE count() {
		// TODO
		return SIDE.BLACK;
	}

	/**
	 * 统计黑/白子数量
	 */
	public int countSide(SIDE side) {
		// TODO
		return 181;
	}

	/**
	 * 检查两盘棋是否相同
	 */
	public boolean isSame(Board targetBoard) {
		for (int i = 0; i < blackValue.length; i++) {
			if (blackValue[i] != targetBoard.blackValue[i]) {
				return false;
			}
			if (whiteValue[i] != targetBoard.whiteValue[i]) {
				return false;
			}
		}

		return true;
	}

	public Point[][] getPoint() {
		return point;
	}
}
