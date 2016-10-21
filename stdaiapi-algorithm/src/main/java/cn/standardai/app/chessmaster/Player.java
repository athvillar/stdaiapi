/**
* Player.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.app.chessmaster;

import cn.standardai.lib.algorithm.ann.BPNetwork;

/**
 * 棋手
 * @author 韩晴
 *
 */
public class Player {

	private int id;

	private String name;

	private BPNetwork core;
	
	private int level;
	
	private int score;
	
	private int win;
	
	private int lose;
	
	private int withdraw;
	
	public Player(String name) {
		super();
		this.name = name;
		this.core = CoreFactory.getInstance();
		this.level = 1;
		this.score = 0;
		this.win = 0;
		this.lose = 0;
		this.withdraw = 0;
	}
	
	public int[] put(ChessBoard chessBoard) {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BPNetwork getCore() {
		return core;
	}

	public void setCore(BPNetwork core) {
		this.core = core;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public int getLose() {
		return lose;
	}

	public void setLose(int lose) {
		this.lose = lose;
	}

	public int getWithdraw() {
		return withdraw;
	}

	public void setWithdraw(int withdraw) {
		this.withdraw = withdraw;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
