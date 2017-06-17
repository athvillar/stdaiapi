package cn.standardai.node.organ;

import cn.standardai.node.exec.Man;

public abstract class Organ implements Runnable {

	protected Man owner;

	public Organ(Man owner) {
		super();
		this.owner = owner;
	}
}
