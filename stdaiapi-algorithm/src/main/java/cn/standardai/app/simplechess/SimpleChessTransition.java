package cn.standardai.app.simplechess;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.standardai.app.vchess.Point.SIDE;
import cn.standardai.app.vchess.Step.STEPSTATUS;
import cn.standardai.lib.algorithm.mdp.MDP;
import cn.standardai.lib.algorithm.mdp.MDPAction;
import cn.standardai.lib.algorithm.mdp.MDPStatus;

public class SimpleChessTransition {

	private int length;

	/**
	 * constructor
	 */
	public SimpleChessTransition() {
	}
}
