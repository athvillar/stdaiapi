package cn.standardai.app.simplechess;

import cn.standardai.lib.algorithm.mdp.MDPAction;

public class SimpleChessAction extends MDPAction {

	public Integer x;

	public Integer y;

	public SimpleChessAction(Integer x, Integer y) {
		this.x = x;
		this.y = y;
	}
}
