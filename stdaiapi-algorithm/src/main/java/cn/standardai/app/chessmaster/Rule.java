/**
* Rule.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.app.chessmaster;

import cn.standardai.app.chessmaster.InvalidStepException.ERRMSG;
import cn.standardai.app.chessmaster.Point.DIRECTION;
import cn.standardai.app.chessmaster.Point.SIDE;

/**
 * 棋盘
 * @author 韩晴
 *
 */
public class Rule {

	public static Board put(Board board, Step step) throws InvalidStepException {
		// 检查该处是否有子
		if (board.point[step.x][step.y] != null) {
			throw new InvalidStepException(ERRMSG.ALREADY_HAVE_PIECE);
		}
		// 新建棋盘
		Board newBoard = new Board(board.length, board.point);
		// 落子
		newBoard.point[step.x][step.y].side = step.side;
		// 提子
		take(newBoard, newBoard.point[step.x][step.y]);
		// 检查打劫禁着点
		if (board.lastBoard != null && board.lastBoard.point.hashCode() == newBoard.point.hashCode()) {
			throw new InvalidStepException(ERRMSG.KO_VIOLATION);
		}
		// 检查是否是活棋
		if (!isLive(newBoard, newBoard.point[step.x][step.y], null)) {
			throw new InvalidStepException(ERRMSG.FORBIDDEN_POINT);
		}
		// 生成棋盘链表
		newBoard.lastBoard = board;
		board.nextBoard = newBoard;

		return newBoard;
	}
	
	public static boolean isLive(Board board, Point point, DIRECTION direction) throws InvalidStepException {
		if (point.side == SIDE.NONE) {
			throw new InvalidStepException(ERRMSG.NONE_POINTER);
		}
		Point neighberPoint;
		if (direction != DIRECTION.UP) {
			neighberPoint = point.up(board);
			if (neighberPoint.side == SIDE.NONE) {
				return true;
			} else if (neighberPoint.side == point.side) {
				if (isLive(board, neighberPoint, DIRECTION.UP)) {
					return true;
				}
			}
		}
		if (direction != DIRECTION.LEFT) {
			neighberPoint = point.left(board);
			if (neighberPoint.side == SIDE.NONE) {
				return true;
			} else if (neighberPoint.side == point.side) {
				if (isLive(board, neighberPoint, DIRECTION.LEFT)) {
					return true;
				}
			}
		}
		if (direction != DIRECTION.DOWN) {
			neighberPoint = point.down(board);
			if (neighberPoint.side == SIDE.NONE) {
				return true;
			} else if (neighberPoint.side == point.side) {
				if (isLive(board, neighberPoint, DIRECTION.DOWN)) {
					return true;
				}
			}
		}
		if (direction != DIRECTION.RIGHT) {
			neighberPoint = point.right(board);
			if (neighberPoint.side == SIDE.NONE) {
				return true;
			} else if (neighberPoint.side == point.side) {
				if (isLive(board, neighberPoint, DIRECTION.RIGHT)) {
					return true;
				}
			}
		}

		return false;
	}
	
	public static void take(Board board, Point point) throws InvalidStepException {

		Point neighberPoint = point.up(board);
		if (point.isOpposite(neighberPoint) && !isLive(board, neighberPoint, null)) {
			removeBlock(board, neighberPoint, null);
		}
		neighberPoint = point.left(board);
		if (point.isOpposite(neighberPoint) && !isLive(board, neighberPoint, null)) {
			removeBlock(board, neighberPoint, null);
		}
		neighberPoint = point.down(board);
		if (point.isOpposite(neighberPoint) && !isLive(board, neighberPoint, null)) {
			removeBlock(board, neighberPoint, null);
		}
		neighberPoint = point.right(board);
		if (point.isOpposite(neighberPoint) && !isLive(board, neighberPoint, null)) {
			removeBlock(board, neighberPoint, null);
		}
	}
	
	public static void removeBlock(Board board, Point point, DIRECTION direction) throws InvalidStepException {

		if (point.side == SIDE.NONE) {
			throw new InvalidStepException(ERRMSG.NONE_POINTER);
		}
		Point neighberPoint;
		if (direction != DIRECTION.UP) {
			neighberPoint = point.up(board);
			if (neighberPoint.side == SIDE.NONE) {
				return;
			} else if (neighberPoint.side == point.side) {
				neighberPoint.side = SIDE.NONE;
			}
		}
		if (direction != DIRECTION.LEFT) {
			neighberPoint = point.left(board);
			if (neighberPoint.side == SIDE.NONE) {
				return;
			} else if (neighberPoint.side == point.side) {
				neighberPoint.side = SIDE.NONE;
			}
		}
		if (direction != DIRECTION.DOWN) {
			neighberPoint = point.down(board);
			if (neighberPoint.side == SIDE.NONE) {
				return;
			} else if (neighberPoint.side == point.side) {
				neighberPoint.side = SIDE.NONE;
			}
		}
		if (direction != DIRECTION.RIGHT) {
			neighberPoint = point.right(board);
			if (neighberPoint.side == SIDE.NONE) {
				return;
			} else if (neighberPoint.side == point.side) {
				neighberPoint.side = SIDE.NONE;
			}
		}
	}
}
