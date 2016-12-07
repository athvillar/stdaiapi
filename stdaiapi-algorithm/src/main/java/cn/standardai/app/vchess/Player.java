/**
* Player.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.app.vchess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.standardai.app.vchess.Point.SIDE;
import cn.standardai.lib.algorithm.ann.BPNetwork;
import cn.standardai.lib.base.function.Normalizer;

/**
 * 棋手
 * @author 韩晴
 *
 */
public class Player {

	public int id;

	public String name;

	public BPNetwork core;
	
	public double minimumForeseeProb;
	
	public int level;
	
	public int score;
	
	public int win;
	
	public int lose;
	
	public int withdraw;
	
	public Player(String name, double minimumForeseeProb) {
		super();
		this.name = name;
		this.core = CoreFactory.getInstance();
		this.minimumForeseeProb = minimumForeseeProb;
		this.level = 1;
		this.score = 0;
		this.win = 0;
		this.lose = 0;
		this.withdraw = 0;
	}

	/**
	 * 走一步棋
	 */
	public Step get1Move(Board board, SIDE side) {

		// 递归调用获得下一步走法组合的方法
		ArrayList<ArrayList<Step>> stepGroups = getPotentialMoveGroups(board, side, 1);

		// 计算最佳走法
		ArrayList<Step> steps = getBestMoveGroup(board, side, stepGroups);
		if (steps != null && steps.size() > 0) {
			return steps.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 重新走一步棋
	 */
	public Step retry(Board board, SIDE side) {
		// TODO
		return get1Move(board, side);
	}

	/**
	 * 从所有可能走法中选择最佳走法，也就是得分期望最大的走法
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Step> getBestMoveGroup(Board board, SIDE side, ArrayList<ArrayList<Step>> moveGroups) {

		// 对于每一种可能的走法，模拟走之后的棋局
		ArrayList<Map<String, Object>> moveMaps = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < moveGroups.size(); i++) {
			Map<String, Object> stepGroupMap = new HashMap<String, Object>();
			stepGroupMap.put("moveGroup", moveGroups.get(i));
			stepGroupMap.put("board", Rule.put(board, moveGroups.get(i)));
			moveMaps.add(stepGroupMap);
		}

		double maxExpScore = Double.NEGATIVE_INFINITY;
		int bestIndex = 0;
		for (int i = 0; i < moveMaps.size(); i++) {
			ArrayList<Step> stepGroup = (ArrayList<Step>)moveMaps.get(i).get("moveGroup");
			double prob = 1;
			// 统计该种走法的概率
			for (int j = 0; j < stepGroup.size(); j++) {
				prob *= stepGroup.get(j).prob;
			}
			// 计算得分
			score = ((Board)moveMaps.get(i).get("board")).countSide(side) - board.countSide(side);
			double expScore = prob * score;
			if (expScore > maxExpScore) {
				maxExpScore = expScore;
				bestIndex = i;
			}
		}
		return (ArrayList<Step>)moveMaps.get(bestIndex).get("moveGroup");
	}

	/**
	 * 根据神经网络，寻找概率大于某一最小概率的走法组，包含对方下法的一系列走法
	 * @param
	 * board 当前棋盘
	 * side 哪方走棋
	 * priorMoves 之前的走法
	 * priorProb 之前的概率
	 */
	public ArrayList<ArrayList<Step>> getPotentialMoveGroups(Board board, SIDE side, double priorProb) {

		// 取得下一步所有候选走法
		ArrayList<Step> newSteps = getPotentialMoves(board, side, priorProb);
		// 若没有下一步走法，则以本步为搜索终点
		if (newSteps == null || newSteps.size() == 0) {
			return null;
		}

		// 若有下一步走法，检验其合法性，并将其加入到走法队列里
		ArrayList<ArrayList<Step>> potentialMoveGroups = new ArrayList<ArrayList<Step>>();
		for (int i = 0; i < newSteps.size(); i++) {
			Board newBoard = null;
			try {
				// 检验走法合法性
				newBoard = Rule.put(board, newSteps.get(i));
			} catch (InvalidStepException e) {
				e.printStackTrace();
				newBoard = board;
			}

			// 递归取得再下一步走法组
			ArrayList<ArrayList<Step>> nextPotentialMoveGroups = getPotentialMoveGroups(newBoard, Board.getOppositeSide(side), priorProb * newSteps.get(i).prob);
			if (nextPotentialMoveGroups == null || nextPotentialMoveGroups.size() == 0) {
				ArrayList<Step> moveGroup = new ArrayList<Step>();
				// 将本走法放入队列
				moveGroup.add(newSteps.get(i));
				// 若下一步棋没有可以下的，以本步棋为最终结果
				potentialMoveGroups.add(moveGroup);
			} else {
				// 对于下一步棋的每一种走法组
				for (int j = 0; j < nextPotentialMoveGroups.size(); j++) {
					ArrayList<Step> moveGroup = new ArrayList<Step>();
					// 将本 走法放入队列
					moveGroup.add(newSteps.get(i));
					// 将下一步走法组放入候选走法组队列
					moveGroup.addAll(nextPotentialMoveGroups.get(j));
					potentialMoveGroups.add(moveGroup);
				}
			}
		}

		return potentialMoveGroups;
	}

	/**
	 * 根据神经网络，寻找概率大于某一最小概率的走法
	 */
	public ArrayList<Step> getPotentialMoves(Board board, SIDE side, double priorProb) {
		// 找出所有走法
		double[] allSteps = core.predict(board.getValues(side));
		/*
		List<Point> forbiddenPoints = Rule.getForbiddenPoints(board, side);
		// 初始化mask
		int[] mask = new int[allSteps.length];
		for (int i = 0; i < mask.length; i++) {
			mask[i] = 1;
		}
		// 禁着点mask设为0
		for (int i = 0; i < board.length; i++) {
			mask[forbiddenPoints.get(i).x + forbiddenPoints.get(i).x * board.length] = 0;
		}
		*/
		// 概率归一化
		allSteps = Normalizer.toProbability(allSteps);
		// 取得大于最小预测概率的所有走法
		ArrayList<Step> potentialSteps = new ArrayList<Step>();
		for (int i = 0; i < allSteps.length; i++) {
			if (allSteps[i] * priorProb >= minimumForeseeProb) {
				// 若大于最小概率，则将此步列为候选走法
				Step newStep = new Step(i % board.length, i / board.length, side, board.stepCount + 1, allSteps[i]);
				potentialSteps.add(newStep);
			}
		}

		return potentialSteps;
	}
}
