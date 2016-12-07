package cn.standardai.lib.algorithm.mdp;

public abstract class MDPStatus {

	public abstract void transition(MDPAction action);

	public abstract Double reward();
}
