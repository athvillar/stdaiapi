/**
* Point.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.app.chessmaster;

/**
 * 点
 * @author 韩晴
 *
 */
public class Point {
	// 黑白
	public static enum SIDE {BLACK, WRITE, NONE};
	// 方向
	public static enum DIRECTION {UP, DOWN, LEFT, RIGHT};

	public int x;

	public int y;
	
	public SIDE side;
	
	public Point up(Board board) {
		if (this.y == board.length - 1) {
			return null;
		}
		return board.point[this.x][this.y + 1];
	}
	
	public Point down(Board board) {
		if (this.y == 0) {
			return null;
		}
		return board.point[this.x][this.y - 1];
	}
	
	public Point left(Board board) {
		if (this.x == 0) {
			return null;
		}
		return board.point[this.x - 1][this.y];
	}
	
	public Point right(Board board) {
		if (this.x == board.length - 1) {
			return null;
		}
		return board.point[this.x + 1][this.y];
	}
	
	public boolean isOpposite(Point targetPoint) {
		if (this.side == SIDE.NONE || targetPoint.side == SIDE.NONE) {
			return false;
		}
		if (this.side == targetPoint.side) {
			return false;
		}
		return true;
	}
}
