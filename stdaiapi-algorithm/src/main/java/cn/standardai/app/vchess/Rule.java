/**
* Rule.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.app.vchess;

import java.util.ArrayList;
import java.util.List;

import cn.standardai.app.vchess.InvalidStepException.ERRMSG;
import cn.standardai.app.vchess.Point.DIRECTION;
import cn.standardai.app.vchess.Point.SIDE;

/**
 * 棋盘
 * @author 韩晴
 *
 */
public class Rule {

	public static List<Point> getForbiddenPoints(Board board, SIDE side) {
		ArrayList<Point> forbiddenPoints = new ArrayList<Point>();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				Step step = new Step(i, j, side);
				try {
					put(board, step);
				} catch (InvalidStepException e) {
					forbiddenPoints.add(new Point(i, j));
				}
			}
		}
		
		return forbiddenPoints;
	}

	/**
	 * 走一步棋
	 */
	public static Board put(Board board, Step step) throws InvalidStepException {
		if (step == null) {
			return board;
		}
		// 检查该处是否有子
		if (board.getPoint(step.x, step.y).side != SIDE.NONE) {
			throw new InvalidStepException(ERRMSG.ALREADY_HAVE_PIECE, board, step);
		}
		// 新建棋盘
		Board newBoard = new Board(board);
		// 落子
		newBoard.put(step);
		// 提子
		take(newBoard, newBoard.getPoint(step.x, step.y));
		// 检查打劫禁着点
		if (board.lastBoard != null && board.lastBoard.isSame(newBoard)) {
			throw new InvalidStepException(ERRMSG.KO_VIOLATION, board, step);
		}
		// 检查是否是活棋
		if (!isLive(newBoard, newBoard.getPoint(step.x, step.y), null)) {
			throw new InvalidStepException(ERRMSG.FORBIDDEN_POINT, board, step);
		}
		// 生成棋盘链表
		newBoard.lastBoard = board;
		board.nextBoard = newBoard;

		return newBoard;
	}

	/**
	 * 按照走法组下棋
	 */
	public static Board put(Board board, List<Step> steps) {
		Board newBoard = new Board(board);
		for (Step step : steps) {
			try {
				newBoard = put(newBoard, step);
			} catch (InvalidStepException e) {
				// 捕获到非法走法时不做任何操作，继续棋局
				e.printStackTrace();
				return board;
			}
		}

		return newBoard;
	}

	/**
	 * 判断某块棋是否为活棋
	 */
	public static boolean isLive(Board board, Point point, DIRECTION direction) {

		boolean rtn = false;

		if (point.side == SIDE.NONE) {
			return true;
		}
		Point neighberPoint;
		if (direction != DIRECTION.UP) {
			neighberPoint = point.up(board);
			if (neighberPoint == null) {
				rtn = false;
			} else if (neighberPoint.side == SIDE.NONE) {
				return true;
			} else if (neighberPoint.side == point.side) {
				if (isLive(board, neighberPoint, DIRECTION.UP)) {
					return true;
				}
			}
		}
		if (direction != DIRECTION.LEFT) {
			neighberPoint = point.left(board);
			if (neighberPoint == null) {
				rtn = false;
			} else if (neighberPoint.side == SIDE.NONE) {
				return true;
			} else if (neighberPoint.side == point.side) {
				if (isLive(board, neighberPoint, DIRECTION.LEFT)) {
					return true;
				}
			}
		}
		if (direction != DIRECTION.DOWN) {
			neighberPoint = point.down(board);
			if (neighberPoint == null) {
				rtn = false;
			} else if (neighberPoint.side == SIDE.NONE) {
				return true;
			} else if (neighberPoint.side == point.side) {
				if (isLive(board, neighberPoint, DIRECTION.DOWN)) {
					return true;
				}
			}
		}
		if (direction != DIRECTION.RIGHT) {
			neighberPoint = point.right(board);
			if (neighberPoint == null) {
				rtn = false;
			} else if (neighberPoint.side == SIDE.NONE) {
				return true;
			} else if (neighberPoint.side == point.side) {
				if (isLive(board, neighberPoint, DIRECTION.RIGHT)) {
					return true;
				}
			}
		}

		return rtn;
	}

	/**
	 * 检查下了这步棋以后有哪些棋子可以从棋盘上拿走
	 */
	public static void take(Board board, Point point) {

		Point neighberPoint = point.up(board);
		if (neighberPoint != null && point.isOpposite(neighberPoint) && !isLive(board, neighberPoint, null)) {
			removeBlock(board, neighberPoint, null);
		}
		neighberPoint = point.left(board);
		if (neighberPoint != null && point.isOpposite(neighberPoint) && !isLive(board, neighberPoint, null)) {
			removeBlock(board, neighberPoint, null);
		}
		neighberPoint = point.down(board);
		if (neighberPoint != null && point.isOpposite(neighberPoint) && !isLive(board, neighberPoint, null)) {
			removeBlock(board, neighberPoint, null);
		}
		neighberPoint = point.right(board);
		if (neighberPoint != null && point.isOpposite(neighberPoint) && !isLive(board, neighberPoint, null)) {
			removeBlock(board, neighberPoint, null);
		}
	}

	/**
	 * 从棋盘上拿走一块棋
	 */
	public static void removeBlock(Board board, Point point, DIRECTION direction) {

		Point neighberPoint;
		if (direction != DIRECTION.UP) {
			neighberPoint = point.up(board);
			if (neighberPoint == null || neighberPoint.side == SIDE.NONE) {
				return;
			} else if (neighberPoint.side == point.side) {
				board.set(neighberPoint.x, neighberPoint.y, SIDE.NONE);
			}
		}
		if (direction != DIRECTION.LEFT) {
			neighberPoint = point.left(board);
			if (neighberPoint == null || neighberPoint.side == SIDE.NONE) {
				return;
			} else if (neighberPoint.side == point.side) {
				board.set(neighberPoint.x, neighberPoint.y, SIDE.NONE);
			}
		}
		if (direction != DIRECTION.DOWN) {
			neighberPoint = point.down(board);
			if (neighberPoint == null || neighberPoint.side == SIDE.NONE) {
				return;
			} else if (neighberPoint.side == point.side) {
				board.set(neighberPoint.x, neighberPoint.y, SIDE.NONE);
			}
		}
		if (direction != DIRECTION.RIGHT) {
			neighberPoint = point.right(board);
			if (neighberPoint == null || neighberPoint.side == SIDE.NONE) {
				return;
			} else if (neighberPoint.side == point.side) {
				board.set(neighberPoint.x, neighberPoint.y, SIDE.NONE);
			}
		}
	}

	/**
	 * 计算棋力
	 */
	public static double getPower(Board board, SIDE side) {

		double totalPower = 0;
		// 棋力等于每个棋子棋力之和
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (board.getPoint(i, j).side == SIDE.NONE) {
					totalPower += get1PointPower(board, side, board.getPoint(i, j));
				} else if (board.getPoint(i, j).side == side) {
					totalPower++;
				} else {
					continue;
				}
			}
		}

		return totalPower;
	}

	/**
	 * 计算一个棋子棋力
	 */
	public static int get1PointPower(Board board, SIDE side, Point point) {

		ArrayList<Point> borderPoints = new ArrayList<Point>();
		// 获得边界
		// TODO

		int totalDistance = 0;
		for (Point borderPoint : borderPoints) {
			totalDistance += (Math.abs(borderPoint.x - point.x) + Math.abs(borderPoint.y - point.y));
		}

		return 0;
	}
}
