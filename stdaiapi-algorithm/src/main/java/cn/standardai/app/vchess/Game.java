/**
* Game.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.app.vchess;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.standardai.app.vchess.Point.SIDE;
import cn.standardai.app.vchess.Step.STEPSTATUS;

/**
 * 围棋游戏
 * @author 韩晴
 *
 */
public class Game {

	private int length;

	private Player player1;

	private Player player2;

	private Player currentPlayer;

	private Board board;

	private Script script;

	/**
	 * constructor
	 */
	public Game(int length, Player player1, Player player2) {
		super();
		this.length = length;
		this.player1 = player1;
		this.player2 = player2;
	}

	/**
	 * 对局
	 */
	public void run() {

		// 创建线程池 
		ExecutorService pool = Executors.newFixedThreadPool(1);
		// 初始化棋盘
		board = new Board(length);
		// 初始化棋谱
		script = new Script();
		// 计步器
		int stepCount = 0;
		// 走法
		Step step = null;
		// 上一部走法走法
		Step lastStep = null;

		// 开始下棋
		STEPSTATUS stepStatus = STEPSTATUS.VALID;
		STEPSTATUS lastStepStatus = STEPSTATUS.VALID;
		while (true) {
			stepCount++;
			// 创建player代理线程 
			Callable playerAgent = new PlayerAgent(getPlayer(stepCount), board, getSide(stepCount));
			// 启动一个选手线程，选手开始下一步棋 
			Future f = pool.submit(playerAgent);

			try {
				// 获得选手落子结果，设定超时时间
				step = (Step)f.get(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// 线程中断
				f.cancel(true);
				continue;
			} catch (ExecutionException e) {
				// 线程出错
				f.cancel(true);
				continue;
			} catch (TimeoutException e) {
				// 线程超时
				f.cancel(true);
				continue;
			}

			// 双方均无子可下，结束
			if (stepCount != 1 && step == null && lastStep == null) {
				break;
			}
			// 记录上一步走法
			lastStep = step;

			try {
				// 将落子反映到棋局上
				board = Rule.put(board, step);
				// 记入棋谱
				script.add(step);
			} catch (InvalidStepException e) {
				continue;
			}
		}

		// 计算胜负
		int blackCount = 0;//Rule.getWeight(SIDE.BLACK);
		int whiteCount =  0;//Rule.getWeight(SIDE.WHITE);
		
		if (blackCount > whiteCount) {
			setWinner(this.player1);
			setLoser(this.player2);
		} else {
			setWinner(this.player2);
			setLoser(this.player1);
		}

		// 保存棋局及结果
		DBUtil.insert(script);
		DBUtil.update(this.player1);
		DBUtil.update(this.player2);

        // 关闭线程池 
        pool.shutdown(); 
	}

	private void setWinner(Player player) {
		
	}

	private void setLoser(Player player) {
		
	}

	/**
	 * 取得当前棋手
	 */
	private Player getPlayer(int stepCount) {
		if (stepCount % 2 == 1) {
			return player1;
		} else {
			return player2;
		}
	}

	/**
	 * 取得当前黑白
	 */
	private SIDE getSide(int stepCount) {
		if (stepCount % 2 == 1) {
			return SIDE.BLACK;
		} else {
			return SIDE.WHITE;
		}
	}
}
