package cn.standardai.app.vchess;

import java.util.concurrent.Callable;

import cn.standardai.app.vchess.Point.SIDE;

public class PlayerAgent implements Callable<Object> {

	private Player player;

	private Board board;

	private SIDE side;

	public PlayerAgent(Player player, Board board, SIDE side) {
		this.player = player;
		this.board = board;
		this.side = side;
	}

	@Override
	public Object call() throws Exception {
		return player.get1Move(board, side);
	}
}
