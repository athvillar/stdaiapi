package cn.standardai.app.simplechess;

import cn.standardai.lib.algorithm.mdp.MDPAction;
import cn.standardai.lib.algorithm.mdp.MDPStatus;

public class SimpleChessStatus extends MDPStatus {

	public Integer n;

	public Integer[] status;

	public SimpleChessStatus(Integer n) {
		this.n = n;
		this.status = new Integer[n * n];
		for (int i = 0; i < this.status.length; i++) {
			this.status[i] = 0;
		}
	}

	@Override
	public void transition(MDPAction action) {
		if (((SimpleChessAction)action).x * this.n + ((SimpleChessAction)action).y >= this.status.length) return;
		if (this.status[((SimpleChessAction)action).x * this.n + ((SimpleChessAction)action).y] != 0) return;
		this.status[((SimpleChessAction)action).x * this.n + ((SimpleChessAction)action).y] = 1;
	}

	@Override
	public Double reward() {
		// TODO Auto-generated method stub
		return null;
	}
}
